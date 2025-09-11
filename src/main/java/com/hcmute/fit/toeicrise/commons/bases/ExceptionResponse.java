package com.hcmute.fit.toeicrise.commons.bases;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hcmute.fit.toeicrise.commons.constants.Constant;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {
    private String path;
    private String status;
    private int code;
    private Object message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.DATE_TIME_PATTERN, timezone = Constant.TIMEZONE_VIETNAM)
    private LocalDateTime timestamp;
}