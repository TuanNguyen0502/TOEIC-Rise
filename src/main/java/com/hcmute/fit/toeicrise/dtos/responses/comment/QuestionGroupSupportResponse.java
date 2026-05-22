package com.hcmute.fit.toeicrise.dtos.responses.comment;

public record QuestionGroupSupportResponse(
        Long id,
        String audioUrl,
        String imageUrl,
        String passage,
        String transcript,
        String partName
) {
}
