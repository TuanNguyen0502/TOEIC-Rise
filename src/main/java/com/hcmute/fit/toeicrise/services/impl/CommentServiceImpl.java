package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.requests.comment.CommentRequest;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Comment;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.Test;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.repositories.CommentRepository;
import com.hcmute.fit.toeicrise.repositories.QuestionRepository;
import com.hcmute.fit.toeicrise.repositories.TestRepository;
import com.hcmute.fit.toeicrise.repositories.UserRepository;
import com.hcmute.fit.toeicrise.services.interfaces.ICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements ICommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;

    @Override
    public void createComment(CommentRequest commentRequest) {
        String email = SecurityUtils.getCurrentUser();

        User user = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User"));

        Test test = testRepository.findById(commentRequest.testId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test"));

        Comment comment = new Comment();
        comment.setContent(commentRequest.content());
        comment.setUser(user);
        comment.setTest(test);

        if(commentRequest.questionId() != null) {
            Question question = questionRepository.findById(commentRequest.questionId())
                    .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question"));
            comment.setTaggedQuestion(question);
        }

        if (commentRequest.parentId() != null) {
            Comment targetComment = commentRepository.findById(commentRequest.parentId())
                    .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Bình luận"));

            Comment actualParent = (targetComment.getParent() != null)
                    ? targetComment.getParent()
                    : targetComment;

            comment.setParent(actualParent);
        }

        commentRepository.save(comment);

    }
}
