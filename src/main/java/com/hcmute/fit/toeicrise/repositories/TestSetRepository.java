package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.TestSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestSetRepository extends JpaRepository<TestSet, Long>, JpaSpecificationExecutor<TestSet> {
    boolean existsByName(String name);
    Optional<TestSet> findByName(String name);
    @Query("select t from TestSet t where t.status='IN_USE' order by t.createdAt desc")
    List<TestSet> getAllByStatus();
}
