package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.dtos.requests.chatbot.*;
import com.hcmute.fit.toeicrise.dtos.responses.chatbot.ChatbotResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.services.interfaces.IChatService;
import com.hcmute.fit.toeicrise.services.interfaces.IChatTitleService;
import com.hcmute.fit.toeicrise.services.interfaces.IChatbotRatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import jakarta.annotation.PostConstruct;

import java.io.InputStream;
import java.time.Duration;

@RestController
@RequestMapping("/learner/chatbot")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ChatbotController {
    private final IChatService chatService;
    private final IChatTitleService chatTitleService;
    private final IChatbotRatingService chatbotRatingService;

    // Suppress all common error sources for reactive endpoints
    @PostConstruct
    public void suppressErrors() {
        // Suppress Spring Security access denied errors
        Logger securityLogger = LoggerFactory.getLogger("org.springframework.security.authorization");
        if (securityLogger instanceof ch.qos.logback.classic.Logger) {
            ((ch.qos.logback.classic.Logger) securityLogger).setLevel(ch.qos.logback.classic.Level.OFF);
        }

        // Suppress servlet dispatcher errors
        Logger servletLogger = LoggerFactory.getLogger("org.apache.catalina.core.ContainerBase.[Tomcat].[localhost].[/].[dispatcherServlet]");
        if (servletLogger instanceof ch.qos.logback.classic.Logger) {
            ((ch.qos.logback.classic.Logger) servletLogger).setLevel(ch.qos.logback.classic.Level.OFF);
        }

        // Suppress Tomcat error page processing errors
        Logger tomcatLogger = LoggerFactory.getLogger("org.apache.catalina.core.ContainerBase.[Tomcat].[localhost]");
        if (tomcatLogger instanceof ch.qos.logback.classic.Logger) {
            ((ch.qos.logback.classic.Logger) tomcatLogger).setLevel(ch.qos.logback.classic.Level.OFF);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllConversations() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(chatTitleService.getAllChatTitlesByUserId(email));
    }

    @GetMapping("{conversationId}")
    public ResponseEntity<?> getChatHistory(@PathVariable String conversationId) {
        // Verify conversation belongs to user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        chatTitleService.checkConversationIdBelongsToUser(email, conversationId);
        return ResponseEntity.ok(chatService.getChatHistory(conversationId));
    }

    @PostMapping(path = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatbotResponse> chat(@ModelAttribute ChatRequest chatRequest) {
        if (chatRequest.getImage() == null || chatRequest.getImage().isEmpty()) {
            return chatService.chat(chatRequest).delayElements(Duration.ofMillis(50));
        } else {
            try (InputStream inputStream = chatRequest.getImage().getInputStream()) {
                return chatService.chat(chatRequest, inputStream, chatRequest.getImage().getContentType())
                        .delayElements(Duration.ofMillis(50));
            } catch (Exception e) {
                return Flux.error(new AppException(ErrorCode.IMAGE_PROCESSING_ERROR));
            }
        }
    }

    @PostMapping(path = "/chat-about-question", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatbotResponse> chatAboutQuestion(@Valid @ModelAttribute ChatAboutQuestionRequest chatAboutQuestionRequest) {
        return chatService.chatAboutQuestion(chatAboutQuestionRequest).delayElements(Duration.ofMillis(50));
    }

    @PostMapping("/generate-title")
    public String generateTitle(@RequestBody TitleRequest titleRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return chatService.generateConversationTitle(email, titleRequest);
    }

    @PostMapping("rate")
    public void rateChatbot(@RequestBody ChatbotRatingRequest chatbotRatingRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        chatbotRatingService.createChatbotRating(chatbotRatingRequest, email);
    }

    @DeleteMapping("{conversationId}")
    public ResponseEntity<?> deleteConversation(@PathVariable String conversationId) {
        // Verify conversation belongs to user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        chatTitleService.deleteChatTitle(email, conversationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("rename-title")
    public ResponseEntity<?> renameTitle(@RequestBody ChatTitleUpdateRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        chatTitleService.renameChatTitle(email, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/analysis", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<?> getAnalysis(@RequestBody ChatAnalysisRequest chatAnalysisRequest) {
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseEntity.ok(chatService.chatAnalysisData(chatAnalysisRequest).delayElements(Duration.ofMillis(50)));
    }
}
