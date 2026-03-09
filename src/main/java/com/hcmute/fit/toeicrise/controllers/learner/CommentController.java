package com.hcmute.fit.toeicrise.controllers.learner;


import com.hcmute.fit.toeicrise.dtos.requests.comment.CommentRequest;
import com.hcmute.fit.toeicrise.dtos.requests.comment.EditCommentRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.services.interfaces.ICommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/learner/comments")
@RequiredArgsConstructor
public class CommentController {
    private final ICommentService commentService;

    @PostMapping("")
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentRequest commentRequest) {
        commentService.createComment(commentRequest);
        return ResponseEntity.ok().build();
    }

    @PutMapping("{commentId}")
    public ResponseEntity<?> editComment(
            @PathVariable Long commentId,
            @Valid @RequestBody EditCommentRequest commentRequest
    ) {
        commentService.editComment(commentId, commentRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("test/{testId}")
    public ResponseEntity<PageResponse> getCommentsByTestId(
            @PathVariable Long testId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse response = commentService.getCommentsByTestId(testId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<PageResponse> getMoreReply(
            @PathVariable Long commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        PageResponse response = commentService.getMoreCommentsByCommentId(commentId, page, size);
        return ResponseEntity.ok(response);
    }



    @DeleteMapping("{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }
}
