package com.hcmute.fit.toeicrise.dtos.responses.tag;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagResponse {
    private Long id;
    private String name;
}
