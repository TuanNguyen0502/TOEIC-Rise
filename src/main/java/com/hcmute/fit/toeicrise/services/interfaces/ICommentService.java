package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.comment.CommentRequest;
import com.hcmute.fit.toeicrise.dtos.requests.comment.EditCommentRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;

public interface ICommentService {
    void createComment(CommentRequest commentRequest);
    PageResponse getCommentsByTestId(Long testId, int page, int size);
    PageResponse getMoreCommentsByCommentId(Long commentId, int page, int size);
    void deleteComment(Long commentId);
    void editComment(Long commentId, EditCommentRequest commentRequest);
}
