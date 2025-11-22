package com.hcmute.fit.toeicrise.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.fit.toeicrise.dtos.requests.chatbot.ChatAboutQuestionRequest;
import com.hcmute.fit.toeicrise.dtos.requests.chatbot.ChatAnalysisRequest;
import com.hcmute.fit.toeicrise.dtos.requests.chatbot.ChatRequest;
import com.hcmute.fit.toeicrise.dtos.requests.chatbot.TitleRequest;
import com.hcmute.fit.toeicrise.dtos.responses.analysis.AnalysisResultResponse;
import com.hcmute.fit.toeicrise.dtos.responses.chatbot.ChatbotResponse;
import com.hcmute.fit.toeicrise.dtos.responses.chatbot.SystemPromptDetailResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.entities.Tag;
import com.hcmute.fit.toeicrise.models.entities.UserAnswer;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.ChatbotMapper;
import com.hcmute.fit.toeicrise.repositories.ChatMemoryRepository;
import com.hcmute.fit.toeicrise.repositories.UserAnswerRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IChatService;
import com.hcmute.fit.toeicrise.services.interfaces.IChatTitleService;
import com.hcmute.fit.toeicrise.services.interfaces.ISystemPromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements IChatService {
    private final ChatClient chatClient;
    private final ChatModel chatModel;
    private final UserAnswerRepository userAnswerRepository;
    private final ChatMemoryRepository chatMemoryRepository;
    private final ISystemPromptService systemPromptService;
    private final IChatTitleService chatTitleService;
    private final ChatbotMapper chatbotMapper;

    @Override
    public List<ChatbotResponse> getChatHistory(String conversationId) {
        return chatMemoryRepository.getChatHistory(conversationId);
    }

    @Override
    public Flux<ChatbotResponse> chat(ChatRequest chatRequest) {
        // Ensure conversationId is set
        if (chatRequest.getConversationId() == null || chatRequest.getConversationId().isEmpty()) {
            chatRequest.setConversationId(UUID.randomUUID().toString());
        }

        // Generate a messageId before streaming starts
        String messageId = UUID.randomUUID().toString();

        Flux<String> content = chatClient.prompt()
                .advisors(advisorSpec -> {
                    advisorSpec.param(ChatMemory.CONVERSATION_ID, chatRequest.getConversationId());
                    advisorSpec.param("messageId", messageId); // Pass messageId to advisor
                })
                .user(chatRequest.getMessage())
                .system(getActiveSystemPrompt())
                .stream()
                .content();

        return content.map(contentText -> chatbotMapper.toChatbotResponse(
                contentText,
                messageId,
                chatRequest.getConversationId(),
                MessageType.ASSISTANT.name()
        ));
    }

    @Override
    public Flux<ChatbotResponse> chat(ChatRequest chatRequest, InputStream imageInputStream, String contentType) {
        // Ensure conversationId is set
        if (chatRequest.getConversationId() == null || chatRequest.getConversationId().isEmpty()) {
            chatRequest.setConversationId(UUID.randomUUID().toString());
        }

        // Generate a messageId before streaming starts
        String messageId = UUID.randomUUID().toString();

        Flux<String> content = ChatClient.create(chatModel)
                .prompt()
                .system(getActiveSystemPrompt())
                .user(user -> user
                        .text(chatRequest.getMessage())
                        .media(MimeTypeUtils.parseMimeType(contentType), new InputStreamResource(imageInputStream)))
                .advisors(advisorSpec -> {
                    advisorSpec.param(ChatMemory.CONVERSATION_ID, chatRequest.getConversationId());
                    advisorSpec.param("messageId", messageId); // Pass messageId to advisor
                })
                .stream()
                .content();

        // Collect the streaming content and save when complete
        AtomicReference<String> fullResponse = new AtomicReference<>("");

        return content
                .doOnNext(chunk -> {
                    // Accumulate the response
                    fullResponse.updateAndGet(current -> current + chunk);
                })
                .doOnComplete(() -> {
                    // Save the complete assistant message when streaming is done
                    Message assistantMessage = new AssistantMessage(fullResponse.get());
                    chatMemoryRepository.saveMessage(chatRequest.getConversationId(), assistantMessage);
                })
                .map(contentChunk -> chatbotMapper.toChatbotResponse(
                        contentChunk,
                        messageId,
                        chatRequest.getConversationId(),
                        MessageType.ASSISTANT.name()
                ));
    }

    @Override
    public String generateConversationTitle(String email, TitleRequest titleRequest) {
        String prompt = "Dựa trên tin nhắn sau của người dùng, hãy tạo một tiêu đề ngắn gọn, rõ ràng và phù hợp cho cuộc hội thoại. "
                + "Tiêu đề phải dưới 10 từ, không có dấu ngoặc kép, không thêm giải thích hoặc văn bản thừa. "
                + "Chỉ trả về tiêu đề duy nhất.\n\nTin nhắn người dùng:\n"
                + titleRequest.getMessage();
        String title = ChatClient.create(chatModel)
                .prompt()
                .system("Bạn là một trợ lý hữu ích, có nhiệm vụ tạo ra tiêu đề cuộc hội thoại ngắn gọn và phù hợp.")
                .user(prompt)
                .call()
                .content();
        if (title != null) {
            // Remove newline characters and trim whitespace
            title = title.replace("\n", " ").trim();
        }
        // Save title to database
        chatTitleService.createChatTitle(email, titleRequest.getConversationId(), title);
        return title;
    }

    @Override
    public Flux<ChatbotResponse> chatAboutQuestion(ChatAboutQuestionRequest chatAboutQuestionRequest) {
        Mono<String> promptMono;

        if (chatAboutQuestionRequest.getConversationId() == null || chatAboutQuestionRequest.getConversationId().isEmpty()) {
            promptMono = Mono.fromCallable(() -> {
                UserAnswer userAnswer = userAnswerRepository.findById(chatAboutQuestionRequest.getUserAnswerId())
                        .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question"));

                Question question = userAnswer.getQuestion();
                QuestionGroup questionGroup = question.getQuestionGroup();

                String options = String.join(", ", question.getOptions());
                String tags = question.getTags().stream()
                        .map(Tag::getName)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("N/A");
                return """
                         Bạn là trợ lý TOEIC Mentor. Nhiệm vụ của bạn là hỗ trợ người dùng giải thích, phân tích và trả lời câu hỏi TOEIC dựa trên dữ liệu cung cấp.\s
                         Hãy đọc kỹ toàn bộ thông tin và phản hồi một cách rõ ràng, chính xác và dễ hiểu.
                         Dưới đây là thông tin bạn cần xử lý:
                         1. Tin nhắn của người dùng:
                         %s
                         2. Passage (đoạn văn nếu có):
                         %s
                         3. Transcript (nghe hiểu nếu có):
                         %s
                         4. Nội dung câu hỏi:
                         %s
                         5. Các lựa chọn:
                         %s
                         6. Đáp án đúng:
                         %s
                         7. Giải thích đáp án đúng:
                         %s
                         8. Đáp án người dùng đã chọn (nếu có):
                         %s
                         9. Tags / Chủ điểm kiến thức:
                         %s
                         Yêu cầu phản hồi:
                         - Trả lời đúng trọng tâm dựa trên tin nhắn người dùng. \s
                         - Giải thích ngắn gọn câu hỏi đang kiểm tra kiến thức gì (ngữ pháp, từ vựng, suy luận, nội dung đoạn văn...). \s
                         - Phân tích và chỉ ra cách tìm đáp án đúng dựa trên dữ liệu đã cung cấp. \s
                         - Giải thích vì sao đáp án đúng là phù hợp. \s
                         - Giải thích vì sao các lựa chọn sai không phù hợp (nếu có danh sách lựa chọn). \s
                         - Nếu không có đáp án đúng (correctOption trống), hãy giúp người dùng suy luận và chọn đáp án hợp lý nhất. \s
                         - Phản hồi theo phong cách thân thiện, rõ ràng, phù hợp với người đang luyện thi TOEIC. \s
                         Lưu ý quan trọng:
                         - Chỉ sử dụng thông tin được cung cấp. \s
                         - Không tự tạo thêm dữ liệu không có trong đề bài. \s
                         - Nếu thông tin không đủ, hãy nêu ra rõ ràng và đưa ra hướng dẫn phù hợp.
                        """
                        .formatted(chatAboutQuestionRequest.getMessage(),
                                questionGroup.getPassage(),
                                questionGroup.getTranscript(),
                                question.getContent(),
                                options,
                                question.getCorrectOption(),
                                question.getExplanation(),
                                userAnswer.getAnswer() != null ? userAnswer.getAnswer() : "N/A",
                                tags);
            }).subscribeOn(Schedulers.boundedElastic());
        } else {
            promptMono = Mono.just(chatAboutQuestionRequest.getMessage());
        }

        return promptMono.flatMapMany(prompt ->
                chat(ChatRequest.builder()
                        .conversationId(chatAboutQuestionRequest.getConversationId())
                        .message(prompt)
                        .build())
        );
    }
    @Override
    public Flux<ChatbotResponse> chatAnalysisData(ChatAnalysisRequest chatRequest) {
        AnalysisResultResponse response = chatRequest.getAnalysisResult();
        Mono<String> prompts = Mono.fromCallable(() -> {
            String systemPrompt = """
                 'Bạn là TOEIC Rise – một hệ thống phân tích kết quả làm bài TOEIC thông minh. ',
                 'Vai trò của bạn là đánh giá hiệu suất của người học dựa trên dữ liệu thống kê mà hệ thống backend cung cấp. ',
                 'Bạn phải luôn duy trì vai trò này trong suốt quá trình phân tích.\\n',
                 \\n',
                 
                 'Vai trò và nhiệm vụ:\\n',
                 '1. Phân tích kết quả TOEIC\\n',
                 '+ Đánh giá hiệu suất từng Part (1–7): tỷ lệ đúng/sai, mức độ ổn định, phần yếu và mạnh.\\n',
                 '+ Phân tích theo Tag/Kỹ năng: vocabulary, grammar, inference, paraphrasing, detail, logic,...\\n',
                 '+ Phân tích tốc độ làm bài: nhanh, chậm, đoán mò, phân bổ thời gian chưa hợp lý.\\n',
                 '+ Phân tích xu hướng qua nhiều bài: tiến bộ, giảm phong độ, phần nào thay đổi nhiều nhất.\\n',
                 '\\n',
                 
                 '2. Xác định điểm mạnh và điểm yếu\\n',
                 '+ Nêu rõ kỹ năng mạnh dựa trên dữ liệu.\\n',
                 '+ Xác định kỹ năng yếu và giải thích nguyên nhân (ví dụ: paraphrasing thấp vì sai nhiều ở Part 3-4).\\n',
                 '+ Chỉ ra Part nào cần cải thiện ưu tiên.\\n',
                 '\\n',
                 
                 '3. Đưa ra khuyến nghị học tập\\n',
                 '+ Gợi ý cách luyện từng kỹ năng yếu.\\n',
                 '+ Đề xuất chiến lược làm bài theo từng Part.\\n',
                 '+ Gợi ý bài tập hoặc chủ đề học phù hợp.\\n',
                 '+ Đề xuất kế hoạch học tập ngắn hạn và dài hạn.\\n',
                 '\\n',
                 'Nguyên tắc:\\n',
                 '+ Phạm vi: chỉ hỗ trợ TOEIC và tiếng Anh liên quan đến TOEIC.\\n',
                 '+ Phong cách: thân thiện, rõ ràng, dễ hiểu, luôn khuyến khích người học.\\n',
                 '+ Tính an toàn: không cung cấp nội dung sai lệch, nhạy cảm hoặc nguy hiểm.\\n',
                 '\\n',
                 'Yêu cầu về đầu vào\\n',
                 '+ Bạn chỉ được phân tích dựa trên dữ liệu được gửi, không tự tạo dữ liệu mới.\\n',
                 '\\n',
                 
                 'Yêu cầu về đầu ra\\n',
                 '+ Phải trả về dữ liệu phân tích ở dạng JSON rõ ràng, có cấu trúc.\\n',
                 '+ Cấu trúc JSON bắt buộc:\\n',
                 '  {\\n',
                 '    "overallSummary": "...",\\n',
                 '    "strengths": [...],\\n',
                 '    "weaknesses": [...],\\n',
                 '    "partAnalysis": { ... },\\n',
                 '    "tagAnalysis": { ... },\\n',
                 '    "timingAnalysis": "...",\\n',
                 '    "trendAnalysis": "...",\\n',
                 '    "recommendations": [...],\\n',
                 '    "studyPlan": {\\n',
                 '        "shortTerm": [...],\\n',
                 '        "longTerm": [...]\\n',
                 '    }\\n',
                 '  }\\n',
                 '+ Tất cả nội dung phải dựa trên dữ liệu và bối cảnh TOEIC.\\n',
                 '\\n',
                 
                 'Phong cách phản hồi\\n',
                 '+ Ngắn gọn, chính xác, rõ ràng, mang tính chuyên môn.\\n',
                 '+ Không lan man, không lặp lại dữ liệu đầu vào.\\n',
                 '+ Giống như một giáo viên TOEIC chuyên nghiệp đang đánh giá bài.\\n',
                 '\\n',
                 
                 'Bạn luôn tuân thủ các quy tắc trên khi phân tích mọi đề thi TOEIC.'
                 'Dưới đây là dữ liệu cần phân tích:'
                 """;
            String jsonData = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(response);
            return systemPrompt + "\n" + jsonData;
        }).subscribeOn(Schedulers.boundedElastic());

        return prompts.flatMapMany(prompt ->{
            String conversationId = chatRequest.getConversationId();
            if (conversationId == null || conversationId.isEmpty()) {
                conversationId = UUID.randomUUID().toString();
            }
            return chat(ChatRequest.builder()
                    .conversationId(conversationId)
                    .message(prompt)
                    .build());
        });
    }


    private String getActiveSystemPrompt() {
        SystemPromptDetailResponse response = systemPromptService.getActiveSystemPrompt();
        return response.getContent();
    }
}
