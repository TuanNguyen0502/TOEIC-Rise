package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.TestSet;
import com.hcmute.fit.toeicrise.models.enums.ETestSetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestSetRepository extends JpaRepository<TestSet, Long>, JpaSpecificationExecutor<TestSet> {
    boolean existsByName(String name);
    Optional<TestSet> findByName(String name);
    @Query("SELECT t FROM TestSet t WHERE t.status= :status ORDER BY t.createdAt DESC")
    List<TestSet> getAllByStatus(@Param("status") ETestSetStatus status);
}
