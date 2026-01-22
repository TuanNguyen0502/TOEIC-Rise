package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.utils.FileUtil;
import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.requests.test.TestRequest;
import com.hcmute.fit.toeicrise.dtos.responses.*;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.LearnerTestResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.PartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.TestDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.TestResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.*;
import com.hcmute.fit.toeicrise.dtos.requests.test.TestUpdateRequest;
import com.hcmute.fit.toeicrise.models.enums.*;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.models.mappers.PartMapper;
import com.hcmute.fit.toeicrise.models.mappers.TestMapper;
import com.hcmute.fit.toeicrise.repositories.TestRepository;
import com.hcmute.fit.toeicrise.repositories.TestSetRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.TestSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.*;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.util.*;

import static com.hcmute.fit.toeicrise.commons.utils.CodeGeneratorUtils.extractGroupNumber;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestServiceImpl implements ITestService {
    private final TestRepository testRepository;
    private final IPartService partService;
    private final TestSetRepository testSetRepository;
    private final IQuestionService questionService;
    private final IQuestionGroupService questionGroupService;
    private final PartMapper partMapper;
    private final TestMapper testMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse getAllTests(String name, ETestStatus status, int page, int size, String sortBy, String direction) {
        Specification<Test> specification = (_, _, cb) -> cb.conjunction();
        return getTestResponses(name, status, page, size, sortBy, direction, specification);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse getTestsByTestSetId(Long testSetId, String name, ETestStatus status, int page, int size, String sortBy, String direction) {
        Specification<Test> specification = (_, _, cb) -> cb.conjunction();
        specification = specification.and(TestSpecification.testSetIdEquals(testSetId));
        return getTestResponses(name, status, page, size, sortBy, direction, specification);
    }

    @Override
    @Transactional
    public TestResponse updateTest(Long id, TestUpdateRequest testUpdateRequest) {
        Test existingTest = getTestById(id);
        testRepository.findByName(testUpdateRequest.getName()).ifPresent(
                testWithSameName -> {
                    if (!testWithSameName.getId().equals(existingTest.getId()))
                        throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Test's name");
                }
        );
        existingTest.setName(testUpdateRequest.getName());
        existingTest.setStatus(ETestStatus.PENDING);
        Test updatedTest = testRepository.save(existingTest);
        log.info("Test updated successfully with id: {}", updatedTest.getId());
        return testMapper.toResponse(updatedTest);
    }

    @Override
    @Transactional
    public boolean changeTestStatusById(Long id, ETestStatus status) {
        Test test = getTestById(id);
        test.setStatus(status);
        testRepository.save(test);
        log.info("Test updated status successfully with id: {}", id);
        return true;
    }

    @Async
    @Override
    public void deleteTestsByTestSetId(Long testSetId) {
        int updatedCount = testRepository.updateStatusByTestSetId(testSetId, ETestStatus.DELETED);
        if (updatedCount == 0){
            log.debug("No tests found for test set ID: {}", testSetId);
            return;
        }
        log.info("Test updated status deleted successfully with id: {}", testSetId);
    }

    @Async
    @Override
    public void changeTestsStatusToPendingByTestSetId(Long testSetId) {
        int updatedCount = testRepository.updateStatusByTestSetId(testSetId, ETestStatus.PENDING);
        if (updatedCount == 0){
            log.debug("Not found for test set ID: {}", testSetId);
            return;
        }
        log.info("Test updated status to pending successfully with id: {}", testSetId);
    }

    @Override
    @Transactional(readOnly = true)
    public TestDetailResponse getTestDetailById(Long id) {
        Test test = getTestById(id);
        List<PartResponse> partResponses = questionGroupService.getQuestionGroupsByTestIdGroupByPart(id);
        return testMapper.toDetailResponse(test, partResponses);
    }

    private PageResponse getTestResponses(String name, ETestStatus status, int page, int size, String sortBy, String direction, Specification<Test> specification) {
        if (name != null && !name.trim().isEmpty())
            specification = specification.and(TestSpecification.nameContains(name));
        if (status != null)
            specification = specification.and(TestSpecification.statusEquals(status));

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TestResponse> testResponses = testRepository.findAll(specification, pageable).map(testMapper::toResponse);
        return pageResponseMapper.toPageResponse(testResponses);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void importTest(MultipartFile file, TestRequest request) {
        if (!FileUtil.isValidFile(file))
            throw new AppException(ErrorCode.INVALID_FILE_FORMAT);
        TestSet testSet = testSetRepository.findById(request.getTestSetId()).orElseThrow(() ->
                new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test set"));
        if (testRepository.existsByName(request.getTestName()))
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Test's name");

        List<QuestionExcelRequest> questionExcelRequests = readFile(file);
        Test test = createTest(request.getTestName(), testSet);
        processQuestions(test, questionExcelRequests);
        log.info("Test imported successfully with {} questions", questionExcelRequests.size());
    }

    @Override
    public Test createTest(String testName, TestSet testSet) {
        Test test = Test.builder()
                .name(testName)
                .status(ETestStatus.PENDING)
                .testSet(testSet)
                .numberOfLearnerTests(0L).build();
        log.info("Test created successfully");
        return testRepository.save(test);
    }

    @Override
    public List<QuestionExcelRequest> readFile(MultipartFile file) {
        List<QuestionExcelRequest> questionExcelRequests;
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getLastRowNum() < 1)
                throw new AppException(ErrorCode.INVALID_FILE_FORMAT);

            int maxRows = 202;
            int lastRowNum = Math.min(sheet.getLastRowNum(), maxRows);
            questionExcelRequests = new ArrayList<>(lastRowNum);

            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                try {
                    QuestionExcelRequest questionExcelRequest = testMapper.mapRowToDTO(row);
                    if (questionExcelRequest != null)
                        questionExcelRequests.add(questionExcelRequest);
                } catch (Exception e) {
                    log.warn("Error parsing file: {}", e.getMessage());
                }
            }
            if (sheet.getLastRowNum() > maxRows)
                log.warn("File has more than {} rows, only processing first {} rows", maxRows, maxRows);
        } catch (IOException e) {
            log.error("Error reading file: {}", e.getMessage());
            throw new AppException(ErrorCode.FILE_READ_ERROR);
        }
        return questionExcelRequests;
    }

    @Override
    public void processQuestions(Test test, List<QuestionExcelRequest> questions) {
        if (questions == null || questions.isEmpty()) {
            log.warn("No questions to process for test ID: {}", test.getId());
            return;
        }

        Map<Integer, List<QuestionExcelRequest>> groupedQuestions = groupQuestionsByKey(questions);
        int processedGroups = 0;
        for (Map.Entry<Integer, List<QuestionExcelRequest>> entry : groupedQuestions.entrySet()) {
            try {
                processQuestionGroup(test, entry.getValue());
                processedGroups++;
                if (processedGroups % 10 == 0)
                    log.debug("Processed {}/{} question groups", processedGroups, groupedQuestions.size());
            } catch (Exception e) {
                log.error("Failed to process question group {}: {}", entry.getKey(), e.getMessage(), e);
            }
        }
    }

    @Override
    public void processQuestionGroup(Test test, List<QuestionExcelRequest> groupQuestions) {
        try {
            QuestionExcelRequest firstQuestion = groupQuestions.getFirst();
            Part part = partService.getPartById(firstQuestion.getPartNumber());
            QuestionGroup questionGroup = questionGroupService.createQuestionGroup(test, part, firstQuestion);

            questionService.createQuestionBatch(groupQuestions, questionGroup);
            log.debug("Processed {} questions in group for test ID: {}", groupQuestions.size(), test.getId());
        } catch (ConstraintViolationException e) {
            log.warn("Failed to import Question Group. Error: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error processing question group: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.FILE_READ_ERROR);
        }
    }

    @Override
    public PageResponse searchTestsByName(com.hcmute.fit.toeicrise.dtos.requests.test.PageRequest request) {
        Specification<Test> testSpecification = (_, _, cb) -> cb.conjunction();
        testSpecification = testSpecification.and(TestSpecification.testSetIdsIn(request.getSort()));

        if (request.getName() != null && !request.getName().isEmpty())
            testSpecification = testSpecification.and(TestSpecification.nameContains(request.getName()));
        testSpecification = testSpecification.and(TestSpecification.statusEquals(ETestStatus.APPROVED));
        testSpecification = testSpecification.and(TestSpecification.testSetStatusEquals(ETestSetStatus.IN_USE));
        Sort sort = Sort.by(Sort.Direction.fromString(EDirection.DES.getValue()), ESort.CREATED_AT.getValue());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<LearnerTestResponse> testResponses = testRepository.findAll(testSpecification, pageable).map(testMapper::toLearnerTestResponse);
        return pageResponseMapper.toPageResponse(testResponses);
    }

    @Override
    public LearnerTestDetailResponse getLearnerTestDetailById(Long id) {
        return testMapper.toLearnerTestDetailResponse(testRepository.findListTagByIdOrderByPartName(id, ETestStatus.APPROVED.name()), partMapper);
    }

    @Override
    public Long totalTest() {
        return testRepository.count();
    }

    private Test getTestById(Long testId){
        return testRepository.findByIdWithTestSet(testId).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test"));
    }

    private Map<Integer, List<QuestionExcelRequest>> groupQuestionsByKey(List<QuestionExcelRequest> questionExcelRequests){
        List<QuestionExcelRequest> sortedQuestions = questionExcelRequests.stream().sorted(
                Comparator.comparing(QuestionExcelRequest::getNumberOfQuestions,
                        Comparator.nullsLast(Comparator.naturalOrder()))
        ).toList();

        Map<Integer, List<QuestionExcelRequest>> groupedQuestions = new HashMap<>();
        for (QuestionExcelRequest question : sortedQuestions) {
            Integer groupKey = Optional.ofNullable(extractGroupNumber(question.getQuestionGroupId()))
                    .orElse(-question.getNumberOfQuestions());
            groupedQuestions.computeIfAbsent(groupKey, _ -> new ArrayList<>()).add(question);
        }
        return groupedQuestions;
    }
}
