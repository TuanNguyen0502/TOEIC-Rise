package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.requests.minitest.MiniTestRequest;
import com.hcmute.fit.toeicrise.dtos.responses.minitest.TagByPartResponse;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionGroupService;
import com.hcmute.fit.toeicrise.services.interfaces.ITagService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

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
    public ResponseEntity<?> getQuestionByPart(@RequestParam(defaultValue = "1")
                                                   @Min(value = 1, message = MessageConstant.PART_ID_MIN)
                                                    @Max(value = 7, message = MessageConstant.PART_ID_MAX)
                                                   Long partId,
                                               @RequestParam(name = "tagIds")
                                               @Size(min = 1, max = 3, message = MessageConstant.TAGS_SIZE)
                                                   Set<Long> tagIds,
                                               @RequestParam(defaultValue = "5")
                                                       @Min(value = 5, message = MessageConstant.QUESTION_MIN)
                                                       @Max(value = 60, message = MessageConstant.QUESTION_MAX)
                                                   int numberQuestion){
        return ResponseEntity.ok(questionGroupService.getLearnerTestQuestionGroupResponsesByTags(partId, tagIds, numberQuestion));
    }

    @PostMapping("")
    public ResponseEntity<?> submitTest(@RequestBody MiniTestRequest miniTestRequest){
        return ResponseEntity.ok(questionGroupService.getMiniTestOverallResponse(miniTestRequest, SecurityUtils.getCurrentUser()));
    }
}
