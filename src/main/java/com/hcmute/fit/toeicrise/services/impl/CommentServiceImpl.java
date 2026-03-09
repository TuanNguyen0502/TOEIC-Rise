package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.requests.comment.CommentRequest;
import com.hcmute.fit.toeicrise.dtos.requests.comment.EditCommentRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.comment.CommentResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Comment;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.Test;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.CommentMapper;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.repositories.CommentRepository;
import com.hcmute.fit.toeicrise.repositories.QuestionRepository;
import com.hcmute.fit.toeicrise.repositories.TestRepository;
import com.hcmute.fit.toeicrise.repositories.UserRepository;
import com.hcmute.fit.toeicrise.services.interfaces.ICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements ICommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final CommentMapper commentMapper;
    private final PageResponseMapper pageResponseMapper;

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

        if (commentRequest.questionId() != null) {
            Question question = questionRepository.findById(commentRequest.questionId())
                    .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question"));
            comment.setTaggedQuestion(question);
        }

        if (commentRequest.parentId() != null) {
            Comment targetComment = commentRepository.findById(commentRequest.parentId())
                    .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Comment"));
            Comment actualParent = (targetComment.getParent() != null)
                    ? targetComment.getParent()
                    : targetComment;
            comment.setParent(actualParent);
        }
        commentRepository.save(comment);

    }

    @Override
    public PageResponse getCommentsByTestId(Long testId, int page, int size) {
        String currentEmail = SecurityUtils.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Comment> rootPage = commentRepository.findRootCommentsByTestId(testId, pageable);
        List<Comment> roots = rootPage.getContent();

        if (roots.isEmpty()) {
            return pageResponseMapper.toPageResponse(rootPage, List.of());
        }

        List<Long> rootIds = roots.stream().map(Comment::getId).toList();

        List<Comment> allReplies = commentRepository.findAllRepliesByParentIds(rootIds);
        Map<Long, List<Comment>> repliesGroupByParent = allReplies.stream()
                .collect(Collectors.groupingBy(c -> c.getParent().getId()));

        Map<Long, Long> countsMap = commentRepository.countRepliesByParentIds(rootIds)
                .stream().collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        List<CommentResponse> dtoList = roots.stream().map(root -> {
            CommentResponse dto = enrichCommentDto(root, currentEmail);
            dto.setTotalReplies(countsMap.getOrDefault(root.getId(), 0L));

            List<Comment> subReplies = repliesGroupByParent.getOrDefault(root.getId(), List.of());
            List<CommentResponse> replyDos = subReplies.stream()
                    .limit(5)
                    .map(r -> enrichCommentDto(r, currentEmail))
                    .toList();

            Pageable replyPageable = PageRequest.of(0, 5);
            Page<CommentResponse> fakePage = new PageImpl<>(replyDos, replyPageable, dto.getTotalReplies());
            dto.setReplies(pageResponseMapper.toPageResponse(fakePage, replyDos));

            return dto;
        }).toList();

        return pageResponseMapper.toPageResponse(rootPage, dtoList);
    }

    @Override
    public PageResponse getMoreCommentsByCommentId(Long commentId, int page, int size) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Comment"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<Comment> replyPage = commentRepository.findByParentId(commentId, pageable);

        String currentEmail = SecurityUtils.getCurrentUser();
        List<CommentResponse> dtoList = replyPage.stream()
                .map(c -> enrichCommentDto(c, currentEmail))
                .toList();

        return pageResponseMapper.toPageResponse(replyPage, dtoList);

    }

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Comment"));
        if (!isOwner(comment)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Comment");
        }
        commentRepository.delete(comment);
    }

    @Override
    public void editComment(Long commentId, EditCommentRequest commentRequest) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Comment"));
        if (!isOwner(comment)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Comment");
        }

        comment.setContent(commentRequest.content());
        commentRepository.save(comment);
    }

    private CommentResponse enrichCommentDto(Comment comment, String email) {
        CommentResponse dto = commentMapper.toResponse(comment);

        dto.setOwner(comment.getUser().getAccount().getEmail().equals(email));
        dto.setEdited(comment.getUpdatedAt() != null &&
                comment.getUpdatedAt().isAfter(comment.getCreatedAt().plusSeconds(1)));

        return dto;
    }

    private boolean isOwner(Comment comment) {
        String email = SecurityUtils.getCurrentUser();
        return comment.getUser().getAccount().getEmail().equals(email);
    }

}
