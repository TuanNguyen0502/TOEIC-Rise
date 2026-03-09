package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardItemListRequest;
import com.hcmute.fit.toeicrise.dtos.requests.flashcard.FlashcardUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.services.interfaces.IFlashcardFavouriteService;
import com.hcmute.fit.toeicrise.services.interfaces.IFlashcardItemProgressService;
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
    private final IFlashcardItemProgressService flashcardItemProgressService;

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
        String email = SecurityUtils.getCurrentUser();
        return flashcardService.getAllPublicFlashcards(email, name, page, size, sortBy, direction);
    }

    @GetMapping("/{flashcardId}")
    public ResponseEntity<?> getFlashcardDetail(@PathVariable Long flashcardId) {
        String email = SecurityUtils.getCurrentUser();
        return ResponseEntity.ok(flashcardService.getFlashcardDetailById(email, flashcardId));
    }

    @GetMapping("/favourite")
    public PageResponse getMyFavouriteFlashcards(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
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

    @PostMapping("/favourite/{flashcardId}")
    public ResponseEntity<?> addFlashcardToFavourite(@PathVariable Long flashcardId) {
        String email = SecurityUtils.getCurrentUser();
        flashcardFavouriteService.addFavourite(email, flashcardId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{flashcardId}")
    public ResponseEntity<?> updateFlashcard(@PathVariable Long flashcardId,@Valid @RequestBody FlashcardUpdateRequest flashcardUpdateRequest) {
        return ResponseEntity.ok(flashcardService.updateFlashcard(SecurityUtils.getCurrentUser(), flashcardId, flashcardUpdateRequest));
    }

    @DeleteMapping("/{flashcardId}")
    public ResponseEntity<?> deleteFlashcard(@PathVariable Long flashcardId) {
        String email = SecurityUtils.getCurrentUser();
        flashcardService.deleteFlashcard(email, flashcardId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/favourite/{flashcardId}")
    public ResponseEntity<?> deleteFavourite(@PathVariable("flashcardId") Long favouriteFlashcardId) {
        String email = SecurityUtils.getCurrentUser();
        flashcardFavouriteService.deleteFavourite(email, favouriteFlashcardId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{flashcardId}/review")
    public ResponseEntity<?> getReview(@PathVariable Long flashcardId) {
        String email = SecurityUtils.getCurrentUser();
        return ResponseEntity.ok(flashcardService.getFlashcardItemDetailToReview(email, flashcardId));
    }

    @GetMapping("/due-items")
    public ResponseEntity<?> getDueItems() {
        String email = SecurityUtils.getCurrentUser();
        return ResponseEntity.ok(flashcardItemProgressService.getFlashcardItemDueToReview(email));
    }

    @PostMapping("/submit-review")
    public ResponseEntity<?> submitReview(@Valid @RequestBody FlashcardItemListRequest flashcardItemProgressRequestList) {
        String email = SecurityUtils.getCurrentUser();
        flashcardItemProgressService.saveFlashcardItemProgress(email, flashcardItemProgressRequestList);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/overall")
    public ResponseEntity<?> reviewWords(){
        String email = SecurityUtils.getCurrentUser();
        return ResponseEntity.ok(flashcardItemProgressService.getFlashcardReviewOverall(email));
    }
}
