package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.dtos.requests.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.responses.TestResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.*;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.TestMapper;
import com.hcmute.fit.toeicrise.repositories.TestRepository;
import com.hcmute.fit.toeicrise.repositories.TestSetRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.TestSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements ITestService {
    private final TestRepository testRepository;
    private final IPartService partService;
    private final TestSetRepository testSetRepository;
    private final IQuestionService questionService;
    private final IQuestionGroupService questionGroupService;
    private final IQuestionTagService questionTagService;
    private final TestMapper testMapper;

    @Override
    public Page<TestResponse> getTestsByTestSetId(Long testSetId, String name, String status, int page, int size, String sortBy, String direction) {
        Specification<Test> specification = (_, _, cb) -> cb.conjunction();
        specification = specification.and(TestSpecification.testSetIdEquals(testSetId));
        if (name != null && !name.isEmpty()) {
            specification = specification.and(TestSpecification.nameContains(name));
        }
        if (status != null && !status.isEmpty()) {
            if (Arrays.stream(ETestStatus.values()).noneMatch(s -> s.name().equals(status))) {
                throw new AppException(ErrorCode.VALIDATION_ERROR, "status");
            }
            specification = specification.and(TestSpecification.statusEquals(status));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return testRepository.findAll(specification, pageable)
                .map(test -> TestResponse.builder()
                        .id(test.getId())
                        .name(test.getName())
                        .status(test.getStatus().name().replace("_", " "))
                        .createdAt(test.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern(Constant.DATE_TIME_PATTERN)))
                        .updatedAt(test.getUpdatedAt().format(java.time.format.DateTimeFormatter.ofPattern(Constant.DATE_TIME_PATTERN)))
                        .build());
    }

    @Override
    @Transactional
    public void importTest(MultipartFile file, String testName, Long testSetId) {
        TestSet testSet = testSetRepository.findById(testSetId).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND,"Test Set"));
        Test test = createTest(testName, testSet);
        List<QuestionExcelRequest> questionExcelRequests = readFile(file);
        processAndSaveQuestion(test, questionExcelRequests);
    }

    @Override
    public Test createTest(String testName, TestSet testSet) {
        Test test= Test.builder()
                .name(testName)
                .status(ETestStatus.PENDING)
                .testSet(testSet)
                .build();
        return testRepository.save(test);
    }

    @Override
    public List<QuestionExcelRequest> readFile(MultipartFile file) {
        List<QuestionExcelRequest> questionExcelRequests = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                QuestionExcelRequest questionExcelRequest = testMapper.mapRowToDTO(row);
                if (questionExcelRequest != null) {
                    questionExcelRequests.add(questionExcelRequest);
                }
            }
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_READ_ERROR);
        }
        return questionExcelRequests;
    }

    @Override
    public void processAndSaveQuestion(Test test, List<QuestionExcelRequest> questions) {
        Map<Integer, QuestionGroup> groupMap = new HashMap<>();
        questions.sort(Comparator.comparing(QuestionExcelRequest::getNumberOfQuestions,
                Comparator.nullsLast(Comparator.naturalOrder())));
        Map<Integer, List<QuestionExcelRequest>> questionGroupMap = questions.stream()
                .collect(Collectors.groupingBy(
                        dto -> Optional.ofNullable(dto.getQuestionGroupId()).orElse(-dto.getNumberOfQuestions()), // Negative seq_number for single questions
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
        int groupPosition = 1;
        for (Map.Entry<Integer, List<QuestionExcelRequest>> entry : questionGroupMap.entrySet()) {
            Integer groupKey = entry.getKey();
            List<QuestionExcelRequest> groupQuestions = entry.getValue();

            try {
                QuestionExcelRequest firstQuestion = groupQuestions.get(0);
                Part part = partService.getPartById(firstQuestion.getPartNumber());
                QuestionGroup questionGroup = questionGroupService.createQuestionGroup(test, part, firstQuestion, groupPosition);
                groupMap.put(groupKey, questionGroup);
                int questionPosition = 1;
                for (QuestionExcelRequest dto : groupQuestions) {
                    Question question = questionService.createQuestion(dto, questionGroup, questionPosition);
                    questionTagService.processQuestionTags(question, dto.getTags());
                    questionPosition++;
                }

                groupPosition++;

            } catch (Exception e) {
                throw new AppException(ErrorCode.FILE_READ_ERROR);
            }
        }
    }
}
