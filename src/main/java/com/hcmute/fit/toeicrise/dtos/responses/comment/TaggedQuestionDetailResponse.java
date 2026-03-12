package com.hcmute.fit.toeicrise.dtos.responses.comment;

import com.hcmute.fit.toeicrise.dtos.responses.tag.TagResponse;

import java.util.List;

public record TaggedQuestionDetailResponse(
        Long id,
        Integer position,
        String content,
        List<String> options,
        String correctOption,
        String explanation,
        List<TagResponse> tags,
        QuestionGroupSupportResponse group
) {
}
