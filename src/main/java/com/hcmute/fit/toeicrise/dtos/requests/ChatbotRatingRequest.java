package com.hcmute.fit.toeicrise.dtos.requests;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.models.enums.EChatbotRating;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatbotRatingRequest {
    @NotBlank(message = MessageConstant.MESSAGE_ID_NOT_BLANK)
    private String messageId;

    @NotNull(message = MessageConstant.ECHATBOT_RATING_NOT_NULL)
    private EChatbotRating rating;
}
