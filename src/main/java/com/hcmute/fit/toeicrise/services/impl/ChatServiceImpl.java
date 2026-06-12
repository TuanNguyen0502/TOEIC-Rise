package com.hcmute.fit.toeicrise.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.fit.toeicrise.commons.constants.PromptConstant;
import com.hcmute.fit.toeicrise.commons.utils.ImageUtils;
import com.hcmute.fit.toeicrise.dtos.requests.chatbot.*;
import com.hcmute.fit.toeicrise.dtos.requests.dictation.AiDictationRequest;
import com.hcmute.fit.toeicrise.dtos.requests.flashcard.SentenceCreateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.ImageResource;
import com.hcmute.fit.toeicrise.dtos.responses.analysis.AnalysisResultResponse;
import com.hcmute.fit.toeicrise.dtos.responses.chatbot.ChatbotAnalysisResponse;
import com.hcmute.fit.toeicrise.dtos.responses.chatbot.ChatbotResponse;
import com.hcmute.fit.toeicrise.dtos.responses.chatbot.SystemPromptDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.dictation.DictationGenerationResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.*;
import com.hcmute.fit.toeicrise.models.enums.EPart;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.ChatbotMapper;
import com.hcmute.fit.toeicrise.repositories.*;
import com.hcmute.fit.toeicrise.services.impl.systemprompt.*;
import com.hcmute.fit.toeicrise.services.interfaces.IChatService;
import com.hcmute.fit.toeicrise.services.interfaces.IChatTitleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements IChatService {
    private final ChatClient chatClient;
    private final ChatClient.Builder chatClientBuilder;
    private final ChatModel chatModel;
    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;
    private final QuestionGroupRepository questionGroupRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final ChatMemoryRepository chatMemoryRepository;
    private final ChatbotSystemPromptServiceImpl chatbotSystemPromptService;
    private final QAndASystemPromptServiceImpl qAndASystemPromptService;
    private final ExplanationGenerationSystemPromptServiceImpl explanationGenerationSystemPromptService;
    private final SentenceAssessmentSystemPromptServiceImpl sentenceAssessmentSystemPromptService;
    private final BlogSummarizationSystemPromptServiceImpl blogSummarizationSystemPromptService;
    private final WritingAssessmentSystemPromptServiceImpl writingAssessmentSystemPromptService;
    private final SpeakingAssessmentSystemPromptServiceImpl speakingAssessmentSystemPromptService;
    private final IChatTitleService chatTitleService;
    private final ChatbotMapper chatbotMapper;
    private final TemplateEngine templateEngine;
    private final ObjectMapper objectMapper;

    @Override
    public Flux<ChatbotResponse> chatAboutQuestion(ChatAboutQuestionRequest request) {
        return Mono.fromCallable(() -> {
                    UserAnswer userAnswer = userAnswerRepository.findById(request.getUserAnswerId())
                            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question"));

                    Question question = userAnswer.getQuestion();
                    QuestionGroup questionGroup = question.getQuestionGroup();
                    Part part = questionGroup.getPart();

                    String prompt;
                    if (request.getConversationId() == null || request.getConversationId().isEmpty()) {
                        String partName = part.getName();
                        String passage = (questionGroup.getPassage() != null && !questionGroup.getPassage().isBlank())
                                ? questionGroup.getPassage()
                                : "N/A";
                        String transcript = (questionGroup.getTranscript() != null && !questionGroup.getTranscript().isBlank())
                                ? questionGroup.getTranscript()
                                : "N/A";
                        String content = (question.getContent() != null && !question.getContent().isBlank())
                                ? question.getContent()
                                : "N/A";
                        String options = (question.getOptions() != null && !question.getOptions().isEmpty())
                                ? String.join(", ", question.getOptions())
                                : "N/A";
                        String correctOption = (question.getCorrectOption() != null && !question.getCorrectOption().isBlank())
                                ? question.getCorrectOption()
                                : "N/A";
                        String explanation = (question.getExplanation() != null && !question.getExplanation().isBlank())
                                ? question.getExplanation()
                                : "N/A";
                        String answer = (userAnswer.getAnswer() != null && !userAnswer.getAnswer().isBlank())
                                ? userAnswer.getAnswer()
                                : (userAnswer.getAnswerText() != null && !userAnswer.getAnswerText().isBlank())
                                  ? userAnswer.getAnswerText()
                                  : "N/A";
                        String tags = question.getTags().stream()
                                .map(Tag::getName)
                                .reduce((a, b) -> a + ", " + b)
                                .orElse("N/A");

                        prompt = """
                                ### DỮ LIỆU ĐẦU VÀO:
                                1. Tin nhắn của người dùng:
                                %s
                                
                                2. Part (phần thi):
                                %s
                                
                                3. Passage (đoạn văn nếu có):
                                %s
                                
                                4. Transcript (nghe hiểu nếu có):
                                %s
                                
                                5. Nội dung câu hỏi:
                                %s
                                
                                6. Các lựa chọn:
                                %s
                                
                                7. Đáp án đúng:
                                %s
                                
                                8. Giải thích đáp án đúng:
                                %s
                                
                                9. Đáp án người dùng đã chọn (nếu có):
                                %s
                                
                                10. Tags / Chủ điểm kiến thức:
                                %s
                                """.formatted(
                                request.getMessage(),
                                partName,
                                passage,
                                transcript,
                                content,
                                options,
                                correctOption,
                                explanation,
                                answer,
                                tags
                        );
                    } else {
                        prompt = request.getMessage();
                    }

                    return new ChatAboutQuestionContext(prompt, questionGroup);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(ctx -> {
                    String prompt = ctx.prompt();
                    QuestionGroup questionGroup = ctx.questionGroup();
                    if (questionGroup.getImageUrl() != null && !questionGroup.getImageUrl().isBlank()) {
                        try {
                            ImageResource resource = ImageUtils.fetchImage(questionGroup.getImageUrl());
                            InputStream is = resource.inputStream(); // keep open during streaming

                            return chat(ChatRequest.builder()
                                            .conversationId(request.getConversationId())
                                            .message(prompt)
                                            .build(),
                                    getActiveQAndASystemPrompt(),
                                    is,
                                    resource.contentType());
                        } catch (IOException e) {
                            throw new AppException(ErrorCode.INVALID_REQUEST, "Failed to fetch question image");
                        }
                    }
                    return chat(ChatRequest.builder()
                                    .conversationId(request.getConversationId())
                                    .message(prompt)
                                    .build(),
                            getActiveQAndASystemPrompt());
                });
    }

    @Override
    public Flux<ChatbotResponse> testChatAboutQuestion(TestingSystemPromptQAndAnswerRequest request) {
        return Mono.fromCallable(() -> {
                    Question question = questionRepository.findRandomQuestionByPartName(request.getPartName())
                            .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST, "No question found for part: " + request.getPartName()));
                    QuestionGroup questionGroup = question.getQuestionGroup();
                    Part part = questionGroup.getPart();

                    String prompt;
                    if (request.getConversationId() == null || request.getConversationId().isEmpty()) {
                        String partName = part.getName();
                        String passage = (questionGroup.getPassage() != null && !questionGroup.getPassage().isBlank())
                                ? questionGroup.getPassage()
                                : "N/A";
                        String transcript = (questionGroup.getTranscript() != null && !questionGroup.getTranscript().isBlank())
                                ? questionGroup.getTranscript()
                                : "N/A";
                        String content = (question.getContent() != null && !question.getContent().isBlank())
                                ? question.getContent()
                                : "N/A";
                        String options = (question.getOptions() != null && !question.getOptions().isEmpty())
                                ? String.join(", ", question.getOptions())
                                : "N/A";
                        String correctOption = (question.getCorrectOption() != null && !question.getCorrectOption().isBlank())
                                ? question.getCorrectOption()
                                : "N/A";
                        String explanation = (question.getExplanation() != null && !question.getExplanation().isBlank())
                                ? question.getExplanation()
                                : "N/A";
                        String answer = "N/A";
                        String tags = question.getTags().stream()
                                .map(Tag::getName)
                                .reduce((a, b) -> a + ", " + b)
                                .orElse("N/A");

                        prompt = """
                                ### DỮ LIỆU ĐẦU VÀO:
                                1. Tin nhắn của người dùng:
                                %s
                                
                                2. Part (phần thi):
                                %s
                                
                                3. Passage (đoạn văn nếu có):
                                %s
                                
                                4. Transcript (nghe hiểu nếu có):
                                %s
                                
                                5. Nội dung câu hỏi:
                                %s
                                
                                6. Các lựa chọn:
                                %s
                                
                                7. Đáp án đúng:
                                %s
                                
                                8. Giải thích đáp án đúng:
                                %s
                                
                                9. Đáp án người dùng đã chọn (nếu có):
                                %s
                                
                                10. Tags / Chủ điểm kiến thức:
                                %s
                                """.formatted(
                                request.getMessage(),
                                partName,
                                passage,
                                transcript,
                                content,
                                options,
                                correctOption,
                                explanation,
                                answer,
                                tags
                        );
                    } else {
                        prompt = request.getMessage();
                    }

                    return new ChatAboutQuestionContext(prompt, questionGroup);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(ctx -> {
                    String prompt = ctx.prompt();
                    QuestionGroup questionGroup = ctx.questionGroup();
                    if (questionGroup.getImageUrl() != null && !questionGroup.getImageUrl().isBlank()) {
                        try {
                            ImageResource resource = ImageUtils.fetchImage(questionGroup.getImageUrl());
                            InputStream is = resource.inputStream(); // keep open during streaming

                            return chat(ChatRequest.builder()
                                            .conversationId(request.getConversationId())
                                            .message(prompt)
                                            .build(),
                                    request.getSystemPromptContent(),
                                    is,
                                    resource.contentType());
                        } catch (IOException e) {
                            throw new AppException(ErrorCode.INVALID_REQUEST, "Failed to fetch question image");
                        }
                    }
                    return chat(ChatRequest.builder()
                                    .conversationId(request.getConversationId())
                                    .message(prompt)
                                    .build(),
                            request.getSystemPromptContent());
                });
    }

    @Override
    public Flux<ChatbotResponse> generateExplanation(GenerateExplanationRequest request) {
        return Mono.fromCallable(() -> {
                    request.setConversationId(UUID.randomUUID().toString());

                    Question question = questionRepository.findById(request.getQuestionId())
                            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question not found for id: " + request.getQuestionId()));
                    QuestionGroup questionGroup = question.getQuestionGroup();

                    String passage = (questionGroup.getPassage() != null && !questionGroup.getPassage().isBlank())
                            ? questionGroup.getPassage()
                            : "N/A";
                    String transcript = (questionGroup.getTranscript() != null && !questionGroup.getTranscript().isBlank())
                            ? questionGroup.getTranscript()
                            : "N/A";
                    String content = (question.getContent() != null && !question.getContent().isBlank())
                            ? question.getContent()
                            : "N/A";
                    String options = (question.getOptions() != null && !question.getOptions().isEmpty())
                            ? String.join(", ", question.getOptions())
                            : "N/A";
                    String correctOption = (question.getCorrectOption() != null && !question.getCorrectOption().isBlank())
                            ? question.getCorrectOption()
                            : "N/A";
                    String tags = question.getTags().stream()
                            .map(Tag::getName)
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("N/A");

                    String prompt = """
                            ### DỮ LIỆU ĐẦU VÀO:\s
                            1. Passage (Đoạn văn đọc hiểu): %s\s
                            2. Transcript: %s\s
                            3. Nội dung câu hỏi: %s\s
                            4. Các lựa chọn: %s\s
                            5. Đáp án đúng: %s\s
                            6. Tags / Chủ điểm kiến thức:  %s\s
                            """.formatted(
                            passage,
                            transcript,
                            content,
                            options,
                            correctOption,
                            tags
                    );

                    return new GenerateExplanationContext(prompt, questionGroup);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(ctx -> {
                    String prompt = ctx.prompt();
                    QuestionGroup questionGroup = ctx.questionGroup();
                    if (questionGroup.getImageUrl() != null && !questionGroup.getImageUrl().isBlank()) {
                        try {
                            ImageResource resource = ImageUtils.fetchImage(questionGroup.getImageUrl());
                            InputStream is = resource.inputStream(); // keep open during streaming

                            return chatWithoutMemory(ChatRequest.builder()
                                            .conversationId(request.getConversationId())
                                            .message(prompt)
                                            .build(),
                                    getActiveExplanationGenerationSystemPrompt(),
                                    is,
                                    resource.contentType());
                        } catch (IOException e) {
                            throw new AppException(ErrorCode.INVALID_REQUEST, "Failed to fetch question image");
                        }
                    }
                    return chatWithoutMemory(ChatRequest.builder()
                                    .conversationId(request.getConversationId())
                                    .message(prompt)
                                    .build(),
                            getActiveExplanationGenerationSystemPrompt());
                });
    }

    @Override
    public Flux<ChatbotResponse> testGenerateExplanation(TestingSystemPromptExplanationGenerationRequest request) {
        return Mono.fromCallable(() -> {
                    Question question = questionRepository.findRandomQuestionByPartName(request.getPartName())
                            .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST, "No question found for part: " + request.getPartName()));
                    QuestionGroup questionGroup = question.getQuestionGroup();

                    String passage = (questionGroup.getPassage() != null && !questionGroup.getPassage().isBlank())
                            ? questionGroup.getPassage()
                            : "N/A";
                    String transcript = (questionGroup.getTranscript() != null && !questionGroup.getTranscript().isBlank())
                            ? questionGroup.getTranscript()
                            : "N/A";
                    String content = (question.getContent() != null && !question.getContent().isBlank())
                            ? question.getContent()
                            : "N/A";
                    String options = (question.getOptions() != null && !question.getOptions().isEmpty())
                            ? String.join(", ", question.getOptions())
                            : "N/A";
                    String correctOption = (question.getCorrectOption() != null && !question.getCorrectOption().isBlank())
                            ? question.getCorrectOption()
                            : "N/A";
                    String tags = question.getTags().stream()
                            .map(Tag::getName)
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("N/A");

                    String prompt = """
                            ### DỮ LIỆU ĐẦU VÀO:\s
                            1. Passage (Đoạn văn đọc hiểu): %s\s
                            2. Transcript: %s\s
                            3. Nội dung câu hỏi: %s\s
                            4. Các lựa chọn: %s\s
                            5. Đáp án đúng: %s\s
                            6. Tags / Chủ điểm kiến thức:  %s\s
                            """.formatted(
                            passage,
                            transcript,
                            content,
                            options,
                            correctOption,
                            tags
                    );

                    return new GenerateExplanationContext(prompt, questionGroup);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(ctx -> {
                    String prompt = ctx.prompt();
                    QuestionGroup questionGroup = ctx.questionGroup();
                    String conversationId = UUID.randomUUID().toString();
                    if (questionGroup.getImageUrl() != null && !questionGroup.getImageUrl().isBlank()) {
                        try {
                            ImageResource resource = ImageUtils.fetchImage(questionGroup.getImageUrl());
                            InputStream is = resource.inputStream(); // keep open during streaming

                            return chatWithoutMemory(ChatRequest.builder()
                                            .conversationId(conversationId)
                                            .message(prompt)
                                            .build(),
                                    getActiveExplanationGenerationSystemPrompt(),
                                    is,
                                    resource.contentType());
                        } catch (IOException e) {
                            throw new AppException(ErrorCode.INVALID_REQUEST, "Failed to fetch question image");
                        }
                    }
                    return chatWithoutMemory(ChatRequest.builder()
                                    .conversationId(conversationId)
                                    .message(prompt)
                                    .build(),
                            getActiveExplanationGenerationSystemPrompt());
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
                    .system(getActiveBlogSummarizationSystemPrompt())
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
    public String generateFeedbackForWritingTestAnswerWithImage(String answerText, String partName, String passage, InputStream imageInputStream, String contentType) {
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

        ChatClient cleanClient = chatClientBuilder.build();
        return cleanClient
                .prompt()
                .system(getActiveWritingAssessmentSystemPrompt())
                .user(user -> user
                        .text(userPrompt)
                        .media(MimeTypeUtils.parseMimeType(contentType), new InputStreamResource(imageInputStream)))
                .call()
                .content();
    }

    @Override
    public String generateFeedbackForWritingTestAnswerWithoutImage(String answerText, String partName, String passage) {
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

        ChatClient cleanClient = chatClientBuilder.build();
        return cleanClient
                .prompt()
                .system(getActiveWritingAssessmentSystemPrompt())
                .user(userPrompt)
                .call()
                .content();
    }

    @Override
    public String generateFeedbackForSpeakingTestAnswerWithImage(String partName, String passage, String questionContent, InputStream imageInputStream, String imageContentType, InputStream audioInputStream, String audioContentType) {
        String userPrompt = """
                ### DỮ LIỆU ĐẦU VÀO:\s
                1. Answer Audio: (được gửi dưới dạng media)\s
                2. Part: %s\s
                3. Passage: %s\s
                4. Question: %s\s
                """.formatted(
                partName,
                passage,
                questionContent
        );

        ChatClient cleanClient = chatClientBuilder.build();
        return cleanClient
                .prompt()
                .system(getActiveSpeakingAssessmentSystemPrompt())
                .user(user -> user
                        .text(userPrompt)
                        .media(MimeTypeUtils.parseMimeType(audioContentType), new InputStreamResource(audioInputStream))
                        .media(MimeTypeUtils.parseMimeType(imageContentType), new InputStreamResource(imageInputStream))
                )
                .call()
                .content();
    }

    @Override
    public String generateFeedbackForSpeakingTestAnswerWithoutImage(String partName, String passage, String questionContent, InputStream audioInputStream, String audioContentType) {
        String userPrompt = """
                ### DỮ LIỆU ĐẦU VÀO:\s
                1. Answer Audio: (được gửi dưới dạng media)\s
                2. Part: %s\s
                3. Passage: %s\s
                4. Question: %s\s
                """.formatted(
                partName,
                passage,
                questionContent
        );

        ChatClient cleanClient = chatClientBuilder.build();
        return cleanClient
                .prompt()
                .system(getActiveSpeakingAssessmentSystemPrompt())
                .user(user -> user
                        .text(userPrompt)
                        .media(MimeTypeUtils.parseMimeType(audioContentType), new InputStreamResource(audioInputStream))
                )
                .call()
                .content();
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
                                    .system(getActiveSentenceAssessmentSystemPrompt())
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
    public List<DictationGenerationResponse> generateDictation(Long testId, Long partId) {

        if (!testRepository.existsById(testId)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test");
        }

        EPart part = EPart.getEPartByPosition(partId.intValue());
        String partName = part.getName();

        if (!part.isListening()) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Part must be a listening part");
        }

        List<QuestionGroup> groups = questionGroupRepository.findByTestIdAndPartIdsWithQuestionsAndPart(testId, List.of(partId));

        if (groups.isEmpty()) {
            return Collections.emptyList();
        }

        List<AiDictationRequest> aiRequests = groups.stream()
                .filter(g -> g.getTranscript() != null && !g.getTranscript().isBlank())
                .map(g -> AiDictationRequest.builder()
                        .questionGroupId(g.getId())
                        .transcript(g.getTranscript())
                        .build())
                .toList();

        if (aiRequests.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            String payload = objectMapper.writeValueAsString(aiRequests);

            String userMessage = """
                    Process these question groups for TOEIC Dictation.
                    Target Part: %s
                    Input data:
                    %s
                    """.formatted(partName, payload);

            ChatClient cleanClient = chatClientBuilder.build();
            return cleanClient.prompt()
                    .system(PromptConstant.DICTATION_GENERATION_SYSTEM_PROMPT)
                    .user(userMessage)
                    .call()
                    .entity(new ParameterizedTypeReference<>() {
                    });
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR, "Failed to prepare dictation request");
        } catch (Exception e) {
            throw new AppException(ErrorCode.AI_PROCESSING_ERROR, "Failed to generate dictation preview");
        }
    }

    @Override
    public List<ChatbotResponse> getChatHistory(String conversationId) {
        return chatMemoryRepository.getChatHistory(conversationId);
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
        return getChatbotResponseFlux(chatRequest, messageId, content);
    }

    @Override
    public Flux<ChatbotResponse> testChat(TestingSystemPromptChatbotRequest request) {
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

    private Flux<ChatbotResponse> chat(ChatRequest chatRequest, String systemPrompt) {
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

    private Flux<ChatbotResponse> chat(ChatRequest chatRequest, String systemPrompt, InputStream imageInputStream, String contentType) {
        // Ensure conversationId is set
        if (chatRequest.getConversationId() == null || chatRequest.getConversationId().isEmpty()) {
            chatRequest.setConversationId(UUID.randomUUID().toString());
        }

        // Generate a messageId before streaming starts
        String messageId = UUID.randomUUID().toString();

        Flux<String> content = chatClient
                .prompt()
                .system(systemPrompt)
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
        return getChatbotResponseFlux(chatRequest, messageId, content);
    }

    private Flux<ChatbotResponse> chatWithoutMemory(ChatRequest chatRequest, String systemPrompt) {
        // Generate a messageId before streaming starts
        String messageId = UUID.randomUUID().toString();

        Flux<String> content = chatClient.prompt()
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

    private Flux<ChatbotResponse> chatWithoutMemory(ChatRequest chatRequest, String systemPrompt, InputStream imageInputStream, String contentType) {
        String messageId = UUID.randomUUID().toString();

        ChatClient cleanClient = chatClientBuilder.build();
        Flux<String> content = cleanClient
                .prompt()
                .system(systemPrompt)
                .user(user -> user
                        .text(chatRequest.getMessage())
                        .media(MimeTypeUtils.parseMimeType(contentType), new InputStreamResource(imageInputStream)))
                .stream()
                .content();

        // Collect the streaming content and save when complete
        AtomicReference<String> fullResponse = new AtomicReference<>("");

        return content
                .doOnNext(chunk -> {
                    // Accumulate the response
                    fullResponse.updateAndGet(current -> current + chunk);
                })
                .map(contentChunk -> chatbotMapper.toChatbotResponse(
                        contentChunk,
                        messageId,
                        chatRequest.getConversationId(),
                        MessageType.ASSISTANT.name()
                ));
    }

    @NonNull
    private Flux<ChatbotResponse> getChatbotResponseFlux(ChatRequest chatRequest, String messageId, Flux<String> content) {
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

    private String getActiveSentenceAssessmentSystemPrompt() {
        SystemPromptDetailResponse response = sentenceAssessmentSystemPromptService.getActiveSystemPrompt();
        return response.getContent();
    }

    private String getActiveBlogSummarizationSystemPrompt() {
        SystemPromptDetailResponse response = blogSummarizationSystemPromptService.getActiveSystemPrompt();
        return response.getContent();
    }

    private String getActiveWritingAssessmentSystemPrompt() {
        SystemPromptDetailResponse response = writingAssessmentSystemPromptService.getActiveSystemPrompt();
        return response.getContent();
    }

    private String getActiveSpeakingAssessmentSystemPrompt() {
        SystemPromptDetailResponse response = speakingAssessmentSystemPromptService.getActiveSystemPrompt();
        return response.getContent();
    }

    private record ChatAboutQuestionContext(String prompt, QuestionGroup questionGroup) {
    }

    private record GenerateExplanationContext(String prompt, QuestionGroup questionGroup) {
    }
}
