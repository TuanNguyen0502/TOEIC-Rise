package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardItemRequest;
import com.hcmute.fit.toeicrise.services.interfaces.IFlashcardItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/learner/flashcard-items")
@RequiredArgsConstructor
public class FlashcardItemController {
    private final IFlashcardItemService flashcardItemService;

    @PostMapping("")
    public ResponseEntity<?> createFlashcardItem(@RequestBody FlashcardItemRequest flashcardItemRequest) {
        return ResponseEntity.ok(flashcardItemService.createFlashcardItem(flashcardItemRequest, SecurityUtils.getCurrentUser()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFlashcardItem(@PathVariable Long id) {
        return ResponseEntity.ok(flashcardItemService.getFlashcardItemDetail(id));
    }
}
