package com.hcmute.fit.toeicrise.controllers.admin;

import com.hcmute.fit.toeicrise.services.interfaces.IChatbotRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/chatbot-ratings")
@RequiredArgsConstructor
public class ChatbotRatingController {
    private final IChatbotRatingService chatbotRatingService;
}
