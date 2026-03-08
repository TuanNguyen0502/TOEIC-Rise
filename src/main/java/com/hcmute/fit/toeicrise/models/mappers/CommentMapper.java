package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.comment.CommentResponse;
import com.hcmute.fit.toeicrise.models.entities.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "userFullName", source = "user.fullName")
    @Mapping(target = "userAvatar", source = "user.avatar")
    @Mapping(target = "taggedQuestionPosition", source = "taggedQuestion.position")
    @Mapping(target = "isEdited", ignore = true)
    @Mapping(target = "isOwner", ignore = true)
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "totalReplies", ignore = true)
    CommentResponse toResponse(Comment comment);
}
