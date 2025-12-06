package com.hcmute.fit.toeicrise.dtos.requests.minitest;

import com.hcmute.fit.toeicrise.dtos.requests.useranswer.UserAnswerRequest;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MiniTestRequest {
    List<UserAnswerRequest> userAnswerRequests;
}
