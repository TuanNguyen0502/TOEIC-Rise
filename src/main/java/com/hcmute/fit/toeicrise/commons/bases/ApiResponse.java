package com.hcmute.fit.toeicrise.commons.bases;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private int code;
    private String status;
    private String message;
    private Object data;
}