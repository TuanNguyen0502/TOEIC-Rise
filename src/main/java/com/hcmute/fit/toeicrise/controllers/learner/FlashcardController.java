package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardResponse;
import com.hcmute.fit.toeicrise.services.interfaces.IFlashcardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("learnerFlashcardController")
@RequestMapping("/learner/flashcards")
@RequiredArgsConstructor
public class FlashcardController {
    private final IFlashcardService flashcardService;

    @GetMapping("/my")
    public List<FlashcardResponse> getMyFlashcards() {
        String email = SecurityUtils.getCurrentUser();
        return flashcardService.getAllFlashcardsByEmail(email);
    }

    @GetMapping("/public")
    public PageResponse getPublicFlashcards(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "favouriteCount") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") String direction
    ) {
        return flashcardService.getAllPublicFlashcards(name, page, size, sortBy, direction);
    }
}
