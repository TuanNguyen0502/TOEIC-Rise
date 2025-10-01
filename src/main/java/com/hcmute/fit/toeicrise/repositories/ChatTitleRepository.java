package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.ChatTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatTitleRepository extends JpaRepository<ChatTitle, Long> {
    List<ChatTitle> findAllByUser_Id(Long userId);

    boolean existsByConversationIdAndUser_Id(String conversationId, Long userId);
}
