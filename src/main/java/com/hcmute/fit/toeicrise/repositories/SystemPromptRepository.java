package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.SystemPrompt;
import com.hcmute.fit.toeicrise.models.enums.ESystemPromptFeatureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemPromptRepository extends JpaRepository<SystemPrompt, Long>, JpaSpecificationExecutor<SystemPrompt> {
    @Query("SELECT sp FROM SystemPrompt sp WHERE sp.featureType=:featureType AND sp.version = (SELECT MAX(s.version) FROM SystemPrompt s)")
    Optional<SystemPrompt> findLatestVersionByFeatureType(@Param("featureType") ESystemPromptFeatureType featureType);

    Optional<SystemPrompt> findFirstByIsActiveAndFeatureType(Boolean isActive, ESystemPromptFeatureType featureType);
}
