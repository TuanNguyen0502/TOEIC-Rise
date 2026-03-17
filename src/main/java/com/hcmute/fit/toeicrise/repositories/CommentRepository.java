package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.Comment;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c JOIN FETCH c.user u JOIN FETCH u.account " +
            "LEFT JOIN FETCH c.taggedQuestion " +
            "WHERE c.test.id = :testId AND c.parent IS NULL")
    Page<Comment> findRootCommentsByTestId(@Param("testId") Long testId, Pageable pageable);

    @Query("SELECT c FROM Comment c JOIN FETCH c.user u JOIN FETCH u.account " +
            "LEFT JOIN FETCH c.taggedQuestion " +
            "WHERE c.parent.id IN :parentIds")
    List<Comment> findAllRepliesByParentIds(@Param("parentIds") List<Long> parentIds);

    @Query("SELECT c.parent.id, COUNT(c) FROM Comment c WHERE c.parent.id IN :parentIds GROUP BY c.parent.id")
    List<Object[]> countRepliesByParentIds(@Param("parentIds") List<Long> parentIds);

    @EntityGraph(attributePaths = {"user", "user.account", "taggedQuestion"})
    Page<Comment> findByParentId(Long parentId, Pageable pageable);
}
