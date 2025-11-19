package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.dtos.responses.useranswer.UserAnswerDetailResponse;
import com.hcmute.fit.toeicrise.services.interfaces.IUserAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/learner/user-answers")
@RequiredArgsConstructor
public class UserAnswerController {
    private final IUserAnswerService userAnswerService;

    @GetMapping("/{userAnswerId}")
    public ResponseEntity<UserAnswerDetailResponse> getUserAnswerDetail(@PathVariable Long userAnswerId) {
        return ResponseEntity.ok(userAnswerService.getUserAnswerDetailResponse(userAnswerId));
    }
}
