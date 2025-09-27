package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.SystemPrompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemPromptRepository extends JpaRepository<SystemPrompt, Long> {
    @Query("SELECT sp FROM SystemPrompt sp WHERE sp.version = (SELECT MAX(s.version) FROM SystemPrompt s)")
    Optional<SystemPrompt> findLatestVersion();

    Optional<SystemPrompt> findFirstByIsActive(Boolean isActive);
}
