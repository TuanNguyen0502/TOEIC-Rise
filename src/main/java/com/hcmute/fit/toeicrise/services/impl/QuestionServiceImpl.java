package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.responses.QuestionResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.Tag;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.QuestionMapper;
import com.hcmute.fit.toeicrise.repositories.PartRepository;
import com.hcmute.fit.toeicrise.repositories.QuestionRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.QuestionSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements IQuestionService {
    private final QuestionRepository questionRepository;
    private final PartRepository partRepository;
    private final QuestionMapper questionMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponse> getQuestionsByTestId(Long testId, String part, int page, int size, String sortBy, String direction) {
        Specification<Question> specification = QuestionSpecification.hasTestId(testId);
        if (part != null && !part.isEmpty()) {
            if (!partRepository.existsByName(part)) {
                throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Part");
            }
            specification = specification.and(QuestionSpecification.hasPart(part));
        }

        // Paging & Sorting
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Question> questions = questionRepository.findAll(specification, pageable);

        return questions.map(q -> {
            List<String> tags = q.getTags().stream()
                    .map(Tag::getName)
                    .distinct()
                    .toList();
            return questionMapper.toQuestionResponse(q, tags);
        });
    }
}
