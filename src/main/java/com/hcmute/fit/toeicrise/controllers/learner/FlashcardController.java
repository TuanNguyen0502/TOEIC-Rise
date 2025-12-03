package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardCreateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.flashcard.FlashcardDetailResponse;
import com.hcmute.fit.toeicrise.services.interfaces.IFlashcardFavouriteService;
import com.hcmute.fit.toeicrise.services.interfaces.IFlashcardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("learnerFlashcardController")
@RequestMapping("/learner/flashcards")
@RequiredArgsConstructor
public class FlashcardController {
    private final IFlashcardService flashcardService;
    private final IFlashcardFavouriteService flashcardFavouriteService;

    @GetMapping("/my")
    public PageResponse getMyFlashcards(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "favouriteCount") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") String direction
    ) {
        String email = SecurityUtils.getCurrentUser();
        return flashcardService.getAllMyFlashcards(email, name, page, size, sortBy, direction);
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

    @GetMapping("/{flashcardId}")
    public FlashcardDetailResponse getFlashcardDetail(@PathVariable("flashcardId") Long flashcardId) {
        String email = SecurityUtils.getCurrentUser();
        return flashcardService.getFlashcardDetailById(email, flashcardId);
    }

    @GetMapping("/favourite")
    public PageResponse getMyFavouriteFlashcards(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "favouriteCount") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") String direction
    ) {
        String email = SecurityUtils.getCurrentUser();
        return flashcardFavouriteService.getAllMyFavouriteFlashcards(email, name, page, size, sortBy, direction);
    }

    @PostMapping("")
    public ResponseEntity<?> createFlashcard(@Valid @RequestBody FlashcardCreateRequest flashcardCreateRequest) {
        String email = SecurityUtils.getCurrentUser();
        flashcardService.createFlashcard(email, flashcardCreateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{flashcardId}")
    public ResponseEntity<?> deleteFlashcard(@PathVariable("flashcardId") Long flashcardId) {
        String email = SecurityUtils.getCurrentUser();
        flashcardService.deleteFlashcard(email, flashcardId);
        return ResponseEntity.ok().build();
    }
}
