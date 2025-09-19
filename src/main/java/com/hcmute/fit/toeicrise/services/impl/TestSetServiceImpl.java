package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.TestSet;
import com.hcmute.fit.toeicrise.models.enums.ETestSetStatus;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.repositories.TestSetRepository;
import com.hcmute.fit.toeicrise.services.interfaces.ITestSetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestSetServiceImpl implements ITestSetService {
    private final TestSetRepository testSetRepository;

    @Override
    public void deleteTestSetById(Long id) {
        TestSet testSet = testSetRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test set"));
        testSet.setStatus(ETestSetStatus.DELETED);
        testSetRepository.save(testSet);
    }
}
