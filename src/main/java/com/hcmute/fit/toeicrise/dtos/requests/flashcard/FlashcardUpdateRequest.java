package com.hcmute.fit.toeicrise.dtos.requests.flashcard;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.models.enums.EFlashcardAccessType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlashcardUpdateRequest {
    @NotBlank(message = MessageConstant.FLASHCARD_NAME_NOT_BLANK)
    @Pattern(regexp = Constant.FLASHCARD_NAME_PATTERN, message = MessageConstant.FLASHCARD_NAME_INVALID)
    private String name;

    private String description;

    @NotNull(message = MessageConstant.FLASHCARD_ACCESS_TYPE_NOT_NULL)
    private EFlashcardAccessType accessType;

    private List<FlashcardItemUpdateRequest> items;
}
