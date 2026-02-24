package com.hcmute.fit.toeicrise.controllers.staff;

import com.hcmute.fit.toeicrise.dtos.requests.chatbot.*;
import com.hcmute.fit.toeicrise.dtos.responses.chatbot.ChatbotResponse;
import com.hcmute.fit.toeicrise.services.interfaces.IChatService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController("StaffChatbotController")
@RequestMapping("/staff/chatbot")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ChatbotController {
    private final IChatService chatService;

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

    @PostMapping(path = "/generate-explanation", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatbotResponse> generateExplanation(@Valid @ModelAttribute GenerateExplanationRequest request) {
        return chatService.generateExplanation(request).delayElements(Duration.ofMillis(50));
    }

    @PostMapping(path = "/testing-chatbot-system-prompt", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatbotResponse> testingChatbotSystemPrompt(@Valid @ModelAttribute ChatForTestingSystemPromptRequest request) {
        return chatService.chat(request).delayElements(Duration.ofMillis(50));
    }
}
