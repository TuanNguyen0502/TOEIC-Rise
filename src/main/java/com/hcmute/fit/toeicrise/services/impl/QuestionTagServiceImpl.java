package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.models.entities.QuestionTag;
import com.hcmute.fit.toeicrise.models.entities.Tag;
import com.hcmute.fit.toeicrise.repositories.QuestionTagRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionTagServiceImpl implements IQuestionTagService {
    private final QuestionTagRepository questionTagRepository;

    @Override
    public List<String> getTagsByQuestionId(Long questionId) {
        List<QuestionTag> questionTags = questionTagRepository.findAllById_QuestionId(questionId);
        return questionTags.stream()
                .map(QuestionTag::getTag)
                .distinct()
                .map(Tag::getName)
                .distinct()
                .toList();
    }
}
