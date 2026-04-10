package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.DictationTranscript;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictationTranscriptRepository extends JpaRepository<DictationTranscript, Long> {

     List<DictationTranscript> findAllByQuestionGroupIdIn(List<Long> questionGroupIds);
     void deleteByQuestionGroupIdIn(List<Long> questionGroupIds);
     List<DictationTranscript> findByQuestionGroupIdIn(List<Long> questionGroupIds);
}
