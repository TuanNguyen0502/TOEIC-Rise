package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.dtos.responses.minitest.TagByPartResponse;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionGroupService;
import com.hcmute.fit.toeicrise.services.interfaces.ITagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("")
    public ResponseEntity<?> getQuestionByPart(@RequestParam(defaultValue = "1") Long partId,
                                               @RequestParam String tags,
                                               @RequestParam(defaultValue = "5") int numberQuestion){
        return ResponseEntity.ok(questionGroupService.getLearnerTestQuestionGroupResponsesByTags(partId, tags, numberQuestion));
    }
}
