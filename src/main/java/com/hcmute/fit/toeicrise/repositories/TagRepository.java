package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.dtos.responses.tag.TagStatisticsProjection;
import com.hcmute.fit.toeicrise.models.entities.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag> {
    Optional<Tag> findByName(String name);

    @Query("SELECT DISTINCT t " +
            "FROM Part p " +
            "JOIN p.questionGroups qg " +
            "JOIN qg.questions q " +
            "JOIN q.tags t " +
            "WHERE p.id = :partId")
    List<Tag> findTagsByPartId(@Param("partId") Long partId);

    Long countByIdIn(Set<Long> ids);

    @Query(value = """
            SELECT * FROM (
                SELECT
                    t.id as id,
                    t.name as name,
                    COUNT(DISTINCT qt.question_id) as totalQuestions,
                    COUNT(ua.id) as totalAnswers,
                    IFNULL(
                        (SUM(CASE WHEN ua.is_correct = 1 THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(ua.id), 0)),
                        0
                    ) as correctionRate
                FROM tags t
                LEFT JOIN questions_tags qt ON t.id = qt.tag_id
                LEFT JOIN user_answers ua ON qt.question_id = ua.question_id
                WHERE (:tagName IS NULL OR t.name LIKE CONCAT('%', :tagName, '%'))
                GROUP BY t.id, t.name
            ) as results
            """,
            countQuery = """
                        SELECT COUNT(*) FROM tags t
                        WHERE (:tagName IS NULL OR t.name LIKE CONCAT('%', :tagName, '%'))
                    """,
            nativeQuery = true)
    Page<TagStatisticsProjection> findAllTagStatistics(@Param("tagName") String tagName, Pageable pageable);
}
