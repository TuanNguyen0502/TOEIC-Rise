package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.*;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TestResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestDetailResponse;
import com.hcmute.fit.toeicrise.models.entities.Test;
import com.hcmute.fit.toeicrise.models.entities.TestSet;
import com.hcmute.fit.toeicrise.dtos.requests.TestUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.TestDetailResponse;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface ITestService {
    PageResponse getAllTests(String name, ETestStatus status, int page, int size, String sortBy, String direction);

    PageResponse getTestsByTestSetId(Long testSetId, String name, ETestStatus status, int page, int size, String sortBy, String direction);

    TestResponse updateTest(Long id, TestUpdateRequest testUpdateRequest);

    boolean deleteTestById(Long id);

    @Async
    void deleteTestsByTestSetId(Long testSetId);

    TestDetailResponse getTestDetailById(Long id);

    void importTest(MultipartFile file, TestRequest testRequest);

    Test createTest(String testName, TestSet testSet);

    List<QuestionExcelRequest> readFile(MultipartFile file);

    void processQuestions(Test test, List<QuestionExcelRequest> questions);

    void processQuestionGroup(Test test, List<QuestionExcelRequest> groupQuestions);

    boolean isValidFile(MultipartFile file);

    PageResponse searchTestsByName(PageRequest request);

    LearnerTestDetailResponse getLearnerTestDetailById(Long id);
}