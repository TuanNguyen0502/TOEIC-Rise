package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.Comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByTestIdAndParentIsNull(Long testId, Pageable pageable);
    Page<Comment> findByParentId(Long parentId, Pageable pageable);
    Long countByTestId(Long testId);
}
