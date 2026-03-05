package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.comment.CommentRequest;

public interface ICommentService {
    void createComment(CommentRequest commentRequest);
}
