package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.chatbot.*;
import com.hcmute.fit.toeicrise.dtos.requests.flashcard.SentenceCreateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.analysis.AnalysisResultResponse;
import com.hcmute.fit.toeicrise.dtos.responses.chatbot.ChatbotAnalysisResponse;
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
import com.hcmute.fit.toeicrise.repositories.QuestionRepository;
import com.hcmute.fit.toeicrise.repositories.UserAnswerRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IChatService;
import com.hcmute.fit.toeicrise.services.interfaces.IChatTitleService;
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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
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
    private final ChatClient.Builder chatClientBuilder;
    private final ChatModel chatModel;
    private final QuestionRepository questionRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final ChatMemoryRepository chatMemoryRepository;
    private final ChatbotSystemPromptServiceImpl chatbotSystemPromptService;
    private final QAndASystemPromptServiceImpl qAndASystemPromptService;
    private final ExplanationGenerationSystemPromptServiceImpl explanationGenerationSystemPromptService;
    private final SentenceAssessmentSystemPromptServiceImpl sentenceAssessmentSystemPromptService;
    private final BlogSummarizationSystemPromptServiceImpl blogSummarizationSystemPromptService;
    private final IChatTitleService chatTitleService;
    private final ChatbotMapper chatbotMapper;
    private final TemplateEngine templateEngine;

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
                .system(getActiveChatbotSystemPrompt())
                .stream()
                .content();

        return content.map(contentText -> chatbotMapper.toChatbotResponse(
                contentText,
                messageId,
                chatRequest.getConversationId(),
                MessageType.ASSISTANT.name()
        ));
    }

    public Flux<ChatbotResponse> chat(ChatRequest chatRequest, String systemPrompt) {
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
                .system(systemPrompt)
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
    public Flux<ChatbotResponse> chat(TestingSystemPromptChatbotRequest request) {
        return Flux.defer(() -> {
            if (request.getConversationId() == null || request.getConversationId().isEmpty()) {
                request.setConversationId(UUID.randomUUID().toString());
            }
            String messageId = UUID.randomUUID().toString();
            return chatClient.prompt()
                    .advisors(advisorSpec -> {
                        advisorSpec.param(ChatMemory.CONVERSATION_ID, request.getConversationId());
                        advisorSpec.param("messageId", messageId);
                    })
                    .user(request.getMessage())
                    .system(request.getSystemPromptContent())
                    .stream()
                    .content()
                    .map(contentText -> chatbotMapper.toChatbotResponse(
                            contentText,
                            messageId,
                            request.getConversationId(),
                            MessageType.ASSISTANT.name()
                    ));
        }).subscribeOn(Schedulers.boundedElastic());
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
                .system(getActiveChatbotSystemPrompt())
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
                        ### DỮ LIỆU ĐẦU VÀO:\s
                        1. Tin nhắn của người dùng:
                        %s\s
                        2. Passage (đoạn văn nếu có):
                        %s\s
                        3. Transcript (nghe hiểu nếu có):
                        %s\s
                        4. Nội dung câu hỏi:
                        %s\s
                        5. Các lựa chọn:
                        %s\s
                        6. Đáp án đúng:
                        %s\s
                        7. Giải thích đáp án đúng:
                        %s\s
                        8. Đáp án người dùng đã chọn (nếu có):
                        %s\s
                        9. Tags / Chủ điểm kiến thức:
                        %s\s
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
                                .build(),
                        getActiveQAndASystemPrompt())
        );
    }

    @Override
    public Flux<ChatbotResponse> chatAboutQuestion(TestingSystemPromptQAndAnswerRequest request) {
        Mono<String> promptMono;

        if (request.getConversationId() == null || request.getConversationId().isEmpty()) {
            promptMono = Mono.fromCallable(() -> {
                Question question = questionRepository.findRandomQuestionByPartName(request.getPartName())
                        .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST, "No question found for part: " + request.getPartName()));
                QuestionGroup questionGroup = question.getQuestionGroup();

                String options = String.join(", ", question.getOptions());
                String tags = question.getTags().stream()
                        .map(Tag::getName)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("N/A");
                return """
                        ### DỮ LIỆU ĐẦU VÀO:\s
                        1. Tin nhắn của người dùng:
                        %s\s
                        2. Passage (đoạn văn nếu có):
                        %s\s
                        3. Transcript (nghe hiểu nếu có):
                        %s\s
                        4. Nội dung câu hỏi:
                        %s\s
                        5. Các lựa chọn:
                        %s\s
                        6. Đáp án đúng:
                        %s\s
                        7. Giải thích đáp án đúng:
                        %s\s
                        8. Đáp án người dùng đã chọn (nếu có):
                        %s\s
                        9. Tags / Chủ điểm kiến thức:
                        %s\s
                        """
                        .formatted(request.getMessage(),
                                questionGroup.getPassage(),
                                questionGroup.getTranscript(),
                                question.getContent(),
                                options,
                                question.getCorrectOption(),
                                question.getExplanation(),
                                "N/A",
                                tags);
            }).subscribeOn(Schedulers.boundedElastic());
        } else {
            promptMono = Mono.just(request.getMessage());
        }

        return promptMono.flatMapMany(prompt ->
                chat(ChatRequest.builder()
                                .conversationId(request.getConversationId())
                                .message(prompt)
                                .build(),
                        request.getSystemPromptContent())
        );
    }

    @Override
    public ChatbotAnalysisResponse chatAnalysisData(ChatAnalysisRequest chatRequest) {
        AnalysisResultResponse response = chatRequest.getAnalysisResult();
        Context context = new Context();
        context.setVariable("analysisData", response);
        String prompts = templateEngine.process("analysis-result", context);

        return chatClient.prompt(prompts)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, UUID.randomUUID().toString()))
                .call()
                .entity(ChatbotAnalysisResponse.class);
    }

    @Override
    public Flux<ChatbotResponse> generateExplanation(GenerateExplanationRequest request) {
        return Flux.defer(() -> {
            ChatClient cleanClient = chatClientBuilder.build();
            // Toàn bộ logic tạo prompt nằm bên trong này để đảm bảo tính Lazy
            String conversationId = UUID.randomUUID().toString();
            String messageId = UUID.randomUUID().toString();
            String options = String.join(", ", request.getOptions());

            String userPrompt = """
                    ### DỮ LIỆU ĐẦU VÀO:\s
                    1. Passage (Đoạn văn đọc hiểu): %s\s
                    2. Transcript: %s\s
                    3. Nội dung câu hỏi: %s\s
                    4. Các lựa chọn: %s\s
                    5. Đáp án đúng: %s\s
                    """.formatted(
                    request.getPassage(),
                    request.getTranscript(),
                    request.getContent(),
                    options,
                    request.getCorrectOption()
            );

            return cleanClient.prompt()
                    .user(userPrompt)
                    .system(getActiveExplanationGenerationSystemPrompt())
                    .stream()
                    .content()
                    .map(contentText -> chatbotMapper.toChatbotResponse(
                            contentText,
                            messageId,
                            conversationId,
                            MessageType.ASSISTANT.name()
                    ));
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<ChatbotResponse> generateExplanation(TestingSystemPromptExplanationGenerationRequest request) {
        return Flux.defer(() -> {
            ChatClient cleanClient = chatClientBuilder.build();
            String conversationId = UUID.randomUUID().toString();
            String messageId = UUID.randomUUID().toString();

            return Mono.fromCallable(() -> {
                        Question question = questionRepository.findRandomQuestionByPartName(request.getPartName())
                                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST, "No question found for part: " + request.getPartName()));
                        QuestionGroup questionGroup = question.getQuestionGroup();
                        String options = String.join(", ", question.getOptions());

                        return """
                                ### DỮ LIỆU ĐẦU VÀO:\s
                                1. Passage (Đoạn văn đọc hiểu): %s\s
                                2. Transcript: %s\s
                                3. Nội dung câu hỏi: %s\s
                                4. Các lựa chọn: %s\s
                                5. Đáp án đúng: %s\s
                                """.formatted(
                                questionGroup.getPassage(),
                                questionGroup.getTranscript(),
                                question.getContent(),
                                options,
                                question.getCorrectOption()
                        );
                    })
                    .subscribeOn(Schedulers.boundedElastic())
                    .flatMapMany(userPrompt ->
                            cleanClient.prompt()
                                    .user(userPrompt)
                                    .system(getActiveExplanationGenerationSystemPrompt())
                                    .stream()
                                    .content()
                                    .map(contentText -> chatbotMapper.toChatbotResponse(
                                            contentText,
                                            messageId,
                                            conversationId,
                                            MessageType.ASSISTANT.name()
                                    ))
                    );
        });
    }

    @Override
    public Flux<ChatbotResponse> generateBlogPostSummary(BlogPostSummaryRequest request) {
        return Flux.defer(() -> {
            ChatClient cleanClient = chatClientBuilder.build();
            // Toàn bộ logic tạo prompt nằm bên trong này để đảm bảo tính Lazy
            String conversationId = UUID.randomUUID().toString();
            String messageId = UUID.randomUUID().toString();

            String userPrompt = """
                    ### DỮ LIỆU ĐẦU VÀO:\s
                    1. Title: %s\s
                    2. Content: %s\s
                    """.formatted(
                    request.getTitle(),
                    request.getContent()
            );

            return cleanClient.prompt()
                    .user(userPrompt)
                    .system(getBlogSummarizationSystemPrompt())
                    .stream()
                    .content()
                    .map(contentText -> chatbotMapper.toChatbotResponse(
                            contentText,
                            messageId,
                            conversationId,
                            MessageType.ASSISTANT.name()
                    ));
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public String generateFeedbackForWritingTestAnswer(String answerText, String partName, String passage, InputStream imageInputStream, String contentType) {
        String systemPrompt = """
                Bạn là giám khảo chấm thi TOEIC Writing tại TOEIC Rise.\s
                Nhiệm vụ của bạn là đánh giá chi tiết bài làm của người học dựa trên dữ liệu đầu vào (Part, Passage, Ảnh nếu có) và đoạn văn người học viết.
                
                TIÊU CHÍ ĐÁNH GIÁ (Dựa trên chuẩn ETS):
                1. Part 1: Câu phải sử dụng đúng 2 từ khóa yêu cầu, đúng ngữ pháp và mô tả chính xác hành động/sự vật trong ảnh.
                2. Part 2: Email phải phản hồi đầy đủ các yêu cầu trong đề bài, sử dụng văn phong công sở (formal/semi-formal) và có cấu trúc rõ ràng (Chào hỏi - Nội dung chính - Kết thúc).
                3. Part 3: Bài nghị luận phải có bố cục 3 phần, lập luận chặt chẽ, có ví dụ minh họa và sử dụng đa dạng các từ nối cũng như cấu trúc câu phức.
                
                YÊU CẦU PHẢN HỒI: Trình bày theo cấu trúc sau:
                
                1. Đánh giá chi tiết:
                - Nhận xét về độ chính xác ngữ pháp, cách chia thì và cấu trúc câu.
                - Đánh giá mức độ phù hợp của từ vựng đối với ngữ cảnh (ưu tiên Business English).
                - Chỉ ra các lỗi sai cụ thể (nếu có) và giải thích nguyên nhân bằng tiếng Việt.
                - Nhận xét về việc đáp ứng đầy đủ các yêu cầu của đề bài (Task Completion).
                
                2. Phiên bản bài làm tối ưu (Model Answer):
                - Dựa trên ý tưởng gốc của người học, hãy viết lại một phiên bản hoàn thiện hơn.
                - Phiên bản này phải: Sửa hết các lỗi ngữ pháp, nâng cấp từ vựng lên trình độ chuyên nghiệp hơn, đảm bảo tính liên kết giữa các câu và bám sát yêu cầu của bài thi TOEIC.
                - Dịch nghĩa phiên bản tối ưu sang tiếng Việt.
                
                3. Từ vựng & Cấu trúc gợi ý:
                - Gợi ý 2-3 từ vựng chuyên ngành hoặc cụm từ (collocations) thường gặp trong chủ đề này.
                - Gợi ý 1-2 cấu trúc ngữ pháp nâng cao (ví dụ: câu điều kiện, mệnh đề quan hệ rút gọn, đảo ngữ...) để giúp người học cải thiện trình độ.
                
                LƯU Ý QUAN TRỌNG:
                - Ngôn ngữ: Tiếng Việt (trừ phần Model Answer).
                - Giọng văn: Khách quan, chuyên nghiệp, mang tính xây dựng.
                - Trình bày dưới dạng text thuần túy (plain text), sử dụng các dấu đầu dòng (-).
                - KHÔNG chào hỏi, không dẫn dắt rườm rà, không có câu kết.
                - Nếu bài làm của người học hoàn toàn không liên quan đến đề bài hoặc ảnh, hãy nêu rõ lý do và yêu cầu họ thực hiện lại đúng yêu cầu.'""";
        String userPrompt = """
                ### DỮ LIỆU ĐẦU VÀO:\s
                1. Answer Text: %s\s
                2. Part: %s\s
                3. Passage: %s\s
                """.formatted(
                answerText,
                partName,
                passage
        );

        return ChatClient.create(chatModel)
                .prompt()
                .system(systemPrompt)
                .user(user -> user
                        .text(userPrompt)
                        .media(MimeTypeUtils.parseMimeType(contentType), new InputStreamResource(imageInputStream)))
                .call()
                .content();
    }

    private String getActiveChatbotSystemPrompt() {
        SystemPromptDetailResponse response = chatbotSystemPromptService.getActiveSystemPrompt();
        return response.getContent();
    }

    private String getActiveQAndASystemPrompt() {
        SystemPromptDetailResponse response = qAndASystemPromptService.getActiveSystemPrompt();
        return response.getContent();
    }

    private String getActiveExplanationGenerationSystemPrompt() {
        SystemPromptDetailResponse response = explanationGenerationSystemPromptService.getActiveSystemPrompt();
        return response.getContent();
    }

    private String getSentenceAssessmentSystemPrompt() {
        SystemPromptDetailResponse response = sentenceAssessmentSystemPromptService.getActiveSystemPrompt();
        return response.getContent();
    }

    private String getBlogSummarizationSystemPrompt() {
        SystemPromptDetailResponse response = blogSummarizationSystemPromptService.getActiveSystemPrompt();
        return response.getContent();
    }

    @Override
    public Flux<ChatbotResponse> chatAboutSentenceStream(SentenceCreateRequest sentenceCreateRequest) {
        return Flux.defer(() -> {
            ChatClient cleanClient = chatClientBuilder.build();
            String conversationId = UUID.randomUUID().toString();
            String messageId = UUID.randomUUID().toString();

            return Mono.fromCallable(() -> """
                            ### DỮ LIỆU ĐẦU VÀO:\s
                            1. Sentence: %s\s
                            2. Keyword: %s\s
                            """.formatted(
                            sentenceCreateRequest.getSentence(),
                            sentenceCreateRequest.getKeyword()
                    ))
                    .subscribeOn(Schedulers.boundedElastic())
                    .flatMapMany(userPrompt ->
                            cleanClient.prompt()
                                    .user(userPrompt)
                                    .system(getSentenceAssessmentSystemPrompt())
                                    .stream()
                                    .content()
                                    .map(contentText -> chatbotMapper.toChatbotResponse(
                                            contentText,
                                            messageId,
                                            conversationId,
                                            MessageType.ASSISTANT.name()
                                    ))
                    );
        });
    }

}
