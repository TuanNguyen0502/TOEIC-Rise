package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {
    List<Question> findAllByQuestionGroup_Id(Long questionGroupId);
    @Query("SELECT q" +
            " FROM Question q INNER JOIN q.tags WHERE q.id =:id AND q.questionGroup.id =:questionGroupId")
    Optional<Question> findByIdAndQuestionGroup_Id(Long id, Long questionGroupId);
    @Modifying
    @Query(value = "DELETE FROM questions_tags qt WHERE qt.question_id = :questionId", nativeQuery = true)
    void deleteTagsByQuestionId(Long questionId);
}
