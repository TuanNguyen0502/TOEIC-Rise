package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {
    @Override
    @NonNull
    @EntityGraph(attributePaths = {"tags"})
    Page<Question> findAll(@Nullable Specification<Question> specification, @NonNull Pageable pageable);
}
