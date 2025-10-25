package com.hcmute.fit.toeicrise.controllers.admin;

import com.hcmute.fit.toeicrise.models.enums.EChatbotRating;
import com.hcmute.fit.toeicrise.services.interfaces.IChatbotRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/chatbot-ratings")
@RequiredArgsConstructor
public class ChatbotRatingController {
    private final IChatbotRatingService chatbotRatingService;

    @GetMapping("")
    public ResponseEntity<?> getChatbotRatings(@RequestParam(required = false) EChatbotRating rating,
                                               @RequestParam(required = false) String conversationTitle,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(defaultValue = "updatedAt") String sortBy,
                                               @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(chatbotRatingService.getChatbotRatings(rating, conversationTitle, page, size, sortBy, direction));
    }

    @GetMapping("/count-like")
    public ResponseEntity<?> countLikeRating() {
        return ResponseEntity.ok(chatbotRatingService.countLikeRating());
    }

    @GetMapping("/count-dislike")
    public ResponseEntity<?> countDislikeRating() {
        return ResponseEntity.ok(chatbotRatingService.countDislikeRating());
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getChatbotRatingById(@PathVariable Long id) {
        return ResponseEntity.ok(chatbotRatingService.getChatbotRatingDetail(id));
    }
}
