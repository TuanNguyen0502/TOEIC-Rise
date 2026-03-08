package com.hcmute.fit.toeicrise.dtos.responses.comment;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    Long id;
    String content;
    String userFullName;
    String userAvatar;
    Integer taggedQuestionPosition;
    LocalDate createdAt;
    boolean isEdited;
    boolean isOwner;
    Long totalReplies;
    PageResponse replies;
}
