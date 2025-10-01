package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.services.interfaces.IChatService;
import com.hcmute.fit.toeicrise.services.interfaces.IChatTitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("learner/chatbot")
@RequiredArgsConstructor
public class ChatbotController {
    private final IChatService chatService;
    private final IChatTitleService chatTitleService;

    @GetMapping("")
    public ResponseEntity<?> getAllConversations() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(chatTitleService.getAllChatTitlesByUserId(email));
    }
}
