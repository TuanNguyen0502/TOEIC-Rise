package com.hcmute.fit.toeicrise.dtos.requests.flashcard;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlashcardItemListRequest {
    @NotEmpty(message = MessageConstant.FLASHCARD_ITEM_PROGRESS_NOT_EMPTY)
    private List<FlashcardItemProgressRequest> items;
}
