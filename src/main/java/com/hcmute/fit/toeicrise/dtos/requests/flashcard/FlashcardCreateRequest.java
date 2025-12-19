package com.hcmute.fit.toeicrise.dtos.requests.flashcard;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.models.enums.EFlashcardAccessType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardCreateRequest {
    @NotNull(message = MessageConstant.FLASHCARD_NAME_NOT_NULL)
    @NotBlank(message = MessageConstant.FLASHCARD_NAME_NOT_BLANK)
    @Pattern(regexp = Constant.FLASHCARD_NAME_PATTERN, message = MessageConstant.FLASHCARD_NAME_INVALID)
    private String name;

    private String description;

    @NotNull(message = MessageConstant.FLASHCARD_ACCESS_TYPE_NOT_NULL)
    private EFlashcardAccessType accessType;

    private List<@Valid FlashcardItemCreateRequest> items;
}
