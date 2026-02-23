package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.Test;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<Test, Long>, JpaSpecificationExecutor<Test> {
    @Query("SELECT t FROM Test t LEFT JOIN FETCH t.testSet WHERE t.id = :id")
    Optional<Test> findByIdWithTestSet(@Param("id") Long id);
    Optional<Test> findByName(String name);
    @Query(value = "SELECT t.id, t.name, t.number_of_learner_tests, p.name, p.id," +
            "GROUP_CONCAT(DISTINCT tg.name ORDER BY tg.name SEPARATOR '; ') AS tags " +
            "FROM tests t " +
            "INNER JOIN question_groups qg ON qg.test_id = t.id " +
            "INNER JOIN questions q ON q.question_group_id = qg.id " +
            "INNER JOIN parts p ON qg.part_id = p.id " +
            "LEFT JOIN questions_tags qtg ON qtg.question_id = q.id " +
            "LEFT JOIN tags tg ON qtg.tag_id = tg.id " +
            "WHERE t.id =:id AND t.status = :status " +
            "GROUP BY t.id, t.name, t.number_of_learner_tests, p.name, p.id " +
            "ORDER BY p.id", nativeQuery = true)
    List<Object[]> findListTagByIdOrderByPartName(@Param("id") Long id, @Param("status") String status);
    Optional<Test> findByIdAndStatus(Long id, ETestStatus status);
    boolean existsByName(String name);
    @Modifying
    @Query("UPDATE Test t SET t.status = :status WHERE t.testSet.id = :testSetId")
    int updateStatusByTestSetId(@Param("testSetId") Long testSetId, @Param("status") ETestStatus status);
    @NotNull
    @EntityGraph(attributePaths = {"testSet"})
    Page<Test> findAll(Specification<Test> specification, @NotNull Pageable pageable);
}
