package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.analysis.FullTestResultResponse;
import com.hcmute.fit.toeicrise.models.entities.UserTest;
import com.hcmute.fit.toeicrise.models.enums.EDays;
import com.hcmute.fit.toeicrise.models.enums.EDirection;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import com.hcmute.fit.toeicrise.repositories.specifications.UserTestSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.IAnalysisService;
import com.hcmute.fit.toeicrise.services.interfaces.IUserTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements IAnalysisService {
    private final IUserTestService userTestService;

    @Override
    public PageResponse getAllTestHistory(EDays days, int page, int size, String email) {
        Specification<UserTest> specification = (_, _, cb) -> cb.conjunction();
        specification = specification.and(UserTestSpecification.statusEquals(ETestStatus.APPROVED));
        specification = specification.and(UserTestSpecification.createdAtBetween(days.getDays()));
        specification = specification.and(UserTestSpecification.accountHasEmail(email));
        Sort sort = Sort.by(Sort.Direction.fromString(EDirection.DES.getValue()), "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        return userTestService.getAllHistories(specification, pageable);
    }
}
