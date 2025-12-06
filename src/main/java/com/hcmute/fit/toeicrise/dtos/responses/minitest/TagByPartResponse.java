package com.hcmute.fit.toeicrise.dtos.responses.minitest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagByPartResponse {
    private Long tagId;
    private String tagName;
}
