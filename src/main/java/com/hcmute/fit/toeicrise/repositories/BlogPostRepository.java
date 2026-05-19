package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long>, JpaSpecificationExecutor<BlogPost> {
    List<BlogPost> findAllByCategory_Id(Long categoryId);

    Optional<BlogPost> findBySlug(String slug);

    Boolean existsBySlug(String slug);

    @Query(value = "SELECT * FROM blog_posts b WHERE b.status = 'PUBLISHED' " +
            "AND MATCH(b.title, b.summary) AGAINST(:keyword IN NATURAL LANGUAGE MODE) " +
            "ORDER BY MATCH(b.title, b.summary) AGAINST(:keyword) DESC",
            countQuery = "SELECT count(*) FROM blog_posts b WHERE b.status = 'PUBLISHED' " +
                    "AND MATCH(b.title, b.summary) AGAINST(:keyword IN NATURAL LANGUAGE MODE)",
            nativeQuery = true)
    Page<BlogPost> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query(value =
            "( " +
                    "  SELECT *, MATCH(title, summary) AGAINST(:currentTitle IN NATURAL LANGUAGE MODE) AS score " +
                    "  FROM blog_posts " +
                    "  WHERE status = 'PUBLISHED' AND id != :currentPostId " +
                    "  AND MATCH(title, summary) AGAINST(:currentTitle IN NATURAL LANGUAGE MODE) " +
                    ") " +
                    "UNION " +
                    "( " +
                    "  SELECT *, 0.1 AS score " +
                    "  FROM blog_posts " +
                    "  WHERE status = 'PUBLISHED' AND id != :currentPostId " +
                    "  AND category_id = :categoryId " +
                    ") " +
                    "ORDER BY score DESC",
            nativeQuery = true)
    List<BlogPost> findRelatedBlogsSmart(
            @Param("currentTitle") String currentTitle,
            @Param("categoryId") Long categoryId,
            @Param("currentPostId") Long currentPostId,
            Pageable pageable
    );
}
