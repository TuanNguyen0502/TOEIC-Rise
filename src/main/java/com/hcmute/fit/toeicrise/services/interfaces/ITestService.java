package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.responses.TestResponse;
import com.hcmute.fit.toeicrise.models.entities.Test;
import com.hcmute.fit.toeicrise.models.entities.TestSet;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ITestService {
    Page<TestResponse> getTestsByTestSetId(Long testSetId, String name, String status, int page, int size, String sortBy, String direction);
    void importTest(MultipartFile file, String testName, Long testSetId);
    Test createTest(String testName, TestSet testSet);
    List<QuestionExcelRequest> readFile(MultipartFile file);
    void processAndSaveQuestion(Test test, List<QuestionExcelRequest> questions);
}
