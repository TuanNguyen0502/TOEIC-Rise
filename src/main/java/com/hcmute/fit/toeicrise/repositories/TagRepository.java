package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
}
