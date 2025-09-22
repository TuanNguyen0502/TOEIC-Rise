package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.QuestionTag;
import com.hcmute.fit.toeicrise.models.entities.QuestionTagId;
import com.hcmute.fit.toeicrise.models.entities.Tag;
import com.hcmute.fit.toeicrise.models.mappers.QuestionTagMapper;
import com.hcmute.fit.toeicrise.repositories.QuestionTagRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionTagService;
import com.hcmute.fit.toeicrise.services.interfaces.ITagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class QuestionTagsServiceImpl implements IQuestionTagService {
    private final QuestionTagRepository questionTagRepository;
    private final ITagService tagService;
    private final QuestionTagMapper questionTagMapper;

    @Override
    public void processQuestionTags(Question question, String tagsString) {
        if (!StringUtils.hasText(tagsString)) {
            return;
        }
        String[] tags = tagsString.split(";");
        for (String tagName : tags) {
            tagName = tagName.trim();
            Tag tag =tagService.findOrCreateTag(tagName);
            QuestionTagId questionTagId = new QuestionTagId(question.getId(), tag.getId());
            QuestionTag questionTag = questionTagMapper.toEntity(questionTagId, question, tag);
            questionTagRepository.save(questionTag);
        }
    }
}