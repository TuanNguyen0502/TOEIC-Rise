package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.dtos.requests.ChatRequest;
import com.hcmute.fit.toeicrise.dtos.requests.TitleRequest;
import com.hcmute.fit.toeicrise.dtos.responses.ChatbotResponse;
import com.hcmute.fit.toeicrise.services.interfaces.IChatService;
import com.hcmute.fit.toeicrise.services.interfaces.IChatTitleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import jakarta.annotation.PostConstruct;
import java.time.Duration;

@RestController
@RequestMapping("/learner/chatbot")
@RequiredArgsConstructor
@Slf4j
public class ChatbotController {
    private final IChatService chatService;
    private final IChatTitleService chatTitleService;

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
        return ResponseEntity.ok(chatService.getChatHistory(conversationId));
    }

    @PostMapping(path = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatbotResponse> chat(@RequestBody ChatRequest chatRequest) {
        return chatService.chat(chatRequest)
                .delayElements(Duration.ofMillis(50));
    }

    @PostMapping(path = "/title", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> createChatTitle(@RequestBody TitleRequest titleRequest) {
        return chatService.generateConversationTitle(titleRequest);
    }
}
