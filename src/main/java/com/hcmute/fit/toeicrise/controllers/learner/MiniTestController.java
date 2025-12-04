package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.requests.minitest.MiniTestRequest;
import com.hcmute.fit.toeicrise.dtos.responses.minitest.TagByPartResponse;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionGroupService;
import com.hcmute.fit.toeicrise.services.interfaces.ITagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("learnerMiniTestController")
@RequestMapping("/learner/mini-tests")
@RequiredArgsConstructor
public class MiniTestController {
    private final ITagService tagService;
    private final IQuestionGroupService questionGroupService;

    @GetMapping("/tags")
    public List<TagByPartResponse> getTagsByPartId(@RequestParam Long partId) {
        return tagService.getTagsByPartId(partId);
    }

    @PostMapping("")
    public ResponseEntity<?> submitTest(@RequestBody MiniTestRequest miniTestRequest){
        return ResponseEntity.ok(questionGroupService.getMiniTestOverallResponse(miniTestRequest, SecurityUtils.getCurrentUser()));
    }
}
