package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.requests.test.PageRequest;
import com.hcmute.fit.toeicrise.dtos.requests.test.TestRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.TestResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.speaking.SpeakingTestDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.writing.WritingTestDetailResponse;
import com.hcmute.fit.toeicrise.models.entities.Test;
import com.hcmute.fit.toeicrise.dtos.requests.test.TestUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.test.TestDetailResponse;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import com.hcmute.fit.toeicrise.models.enums.ETestType;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface ITestService {
    PageResponse getAllTestsByType(ETestType type, String name, ETestStatus status, int page, int size, String sortBy, String direction);

    PageResponse getTestsByTestSetId(Long testSetId, String name, ETestStatus status, int page, int size, String sortBy, String direction);

    TestResponse updateTest(Long id, TestUpdateRequest testUpdateRequest);

    boolean changeTestStatusById(Long id, ETestStatus status);

    @Async
    void deleteTestsByTestSetId(Long testSetId);

    @Async
    void changeTestsStatusToPendingByTestSetId(Long testSetId);

    TestDetailResponse getTestDetailById(Long id);

    @Transactional(readOnly = true)
    SpeakingTestDetailResponse getSpeakingTestDetailById(Long id);

    @Transactional(readOnly = true)
    WritingTestDetailResponse getWritingTestDetailById(Long id);

    void importTest(MultipartFile file, TestRequest testRequest);

    @Transactional(isolation = Isolation.READ_COMMITTED)
    void importSpeakingTest(MultipartFile file, TestRequest request);

    @Transactional(isolation = Isolation.READ_COMMITTED)
    void importWritingTest(MultipartFile file, TestRequest request);

    List<QuestionExcelRequest> readFile(MultipartFile file);

    void processQuestions(Test test, List<QuestionExcelRequest> questions);

    void processQuestionGroup(Test test, List<QuestionExcelRequest> groupQuestions);

    PageResponse searchTestsByTypeAndName(ETestType type, PageRequest request);

    LearnerTestDetailResponse getLearnerTestDetailById(Long id);

    Long totalTest();

    Test getTestById(Long testId);

    void incrementNumberOfLearnersSubmit(Test test);

    Test getTestByIdAndStatus(Long testId, ETestStatus status);
}