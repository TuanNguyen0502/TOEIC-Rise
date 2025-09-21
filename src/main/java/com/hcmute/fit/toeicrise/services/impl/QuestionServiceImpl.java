package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.responses.QuestionResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.QuestionMapper;
import com.hcmute.fit.toeicrise.repositories.PartRepository;
import com.hcmute.fit.toeicrise.repositories.QuestionRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionService;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements IQuestionService {
    private final QuestionRepository questionRepository;
    private final PartRepository partRepository;
    private final IQuestionTagService questionTagService;
    private final QuestionMapper questionMapper;

    @Override
    public int countQuestionsByQuestionGroupId(Long questionGroupId) {
        return questionRepository.countAllByQuestionGroup_Id(questionGroupId);
    }

    @Override
    public Page<QuestionResponse> getQuestionsByTestId(Long testId, String part, int page, int size, String sortBy, String direction) {
        Specification<Question> specification = (_, _, cb) -> cb.conjunction();
        if (part != null && !part.isEmpty()) {
            if (!partRepository.existsByName(part)) {
                throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Part");
            }
            specification = specification.and((root, _, cb) -> cb.equal(root.get("part"), part));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return questionRepository.findAll(specification, pageable)
                .map(question -> {
                            List<String> tags = questionTagService.getTagsByQuestionId(question.getId());
                            return questionMapper.toQuestionResponse(question, tags);
                        }
                );
    }
}
