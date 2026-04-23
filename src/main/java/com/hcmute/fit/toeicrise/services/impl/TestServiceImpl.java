package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.utils.FileUtil;
import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.requests.question.SpeakingQuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.requests.question.WritingQuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.requests.test.TestRequest;
import com.hcmute.fit.toeicrise.dtos.responses.*;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.speaking.LearnerSpeakingPartDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.speaking.LearnerSpeakingTestDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.writing.LearnerWritingPartDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.writing.LearnerWritingTestDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.LearnerTestResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.PartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.TestDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.TestResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.speaking.SpeakingPartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.speaking.SpeakingTestDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.writing.WritingPartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.writing.WritingTestDetailResponse;
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
    public PageResponse getAllTestsByType(ETestType type, String name, ETestStatus status, int page, int size, String sortBy, String direction) {
        Specification<Test> specification = (_, _, cb) -> cb.conjunction();
        specification = specification.and(TestSpecification.typeEquals(type));
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
        if (updatedCount == 0) {
            log.debug("No tests found for test set ID: {}", testSetId);
            return;
        }
        log.info("Test updated status deleted successfully with id: {}", testSetId);
    }

    @Async
    @Override
    public void changeTestsStatusToPendingByTestSetId(Long testSetId) {
        int updatedCount = testRepository.updateStatusByTestSetId(testSetId, ETestStatus.PENDING);
        if (updatedCount == 0) {
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

    @Transactional(readOnly = true)
    @Override
    public SpeakingTestDetailResponse getSpeakingTestDetailById(Long id) {
        Test test = getTestById(id);
        List<SpeakingPartResponse> partResponses = questionGroupService.getSpeakingQuestionGroupsByTestIdGroupByPart(id);
        return testMapper.toSpeakingTestDetailResponse(test, partResponses);
    }

    @Transactional(readOnly = true)
    @Override
    public WritingTestDetailResponse getWritingTestDetailById(Long id) {
        Test test = getTestById(id);
        List<WritingPartResponse> partResponses = questionGroupService.getWritingQuestionGroupsByTestIdGroupByPart(id);
        return testMapper.toWritingTestDetailResponse(test, partResponses);
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
        Test test = createTest(request.getTestName(), ETestType.LISTENING_AND_READING, testSet);
        processQuestions(test, questionExcelRequests);
        log.info("Test imported successfully with {} questions", questionExcelRequests.size());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void importSpeakingTest(MultipartFile file, TestRequest request) {
        if (!FileUtil.isValidFile(file))
            throw new AppException(ErrorCode.INVALID_FILE_FORMAT);
        TestSet testSet = testSetRepository.findById(request.getTestSetId()).orElseThrow(() ->
                new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test set"));
        if (testRepository.existsByName(request.getTestName()))
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Test's name");

        List<SpeakingQuestionExcelRequest> questionExcelRequests = readSpeakingFile(file);
        Test test = createTest(request.getTestName(), ETestType.SPEAKING, testSet);
        processSpeakingQuestions(test, questionExcelRequests);
        log.info("Test imported successfully with {} questions", questionExcelRequests.size());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void importWritingTest(MultipartFile file, TestRequest request) {
        if (!FileUtil.isValidFile(file))
            throw new AppException(ErrorCode.INVALID_FILE_FORMAT);
        TestSet testSet = testSetRepository.findById(request.getTestSetId()).orElseThrow(() ->
                new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test set"));
        if (testRepository.existsByName(request.getTestName()))
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Test's name");

        List<WritingQuestionExcelRequest> questionExcelRequests = readWritingFile(file);
        Test test = createTest(request.getTestName(), ETestType.WRITING, testSet);
        processWritingQuestions(test, questionExcelRequests);
        log.info("Test imported successfully with {} questions", questionExcelRequests.size());
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
    public PageResponse searchTestsByTypeAndName(ETestType type, com.hcmute.fit.toeicrise.dtos.requests.test.PageRequest request) {
        Specification<Test> testSpecification = (_, _, cb) -> cb.conjunction();
        testSpecification = testSpecification.and(TestSpecification.testSetIdsIn(request.getSort()));
        testSpecification = testSpecification.and(TestSpecification.typeEquals(type));

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

    @Override
    public Test getTestById(Long testId) {
        return testRepository.findByIdWithTestSet(testId).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test"));
    }

    @Override
    public void incrementNumberOfLearnersSubmit(Test test) {
        test.setNumberOfLearnerTests(test.getNumberOfLearnerTests() + 1);
        testRepository.save(test);
    }

    @Override
    public Test getTestByIdAndStatus(Long testId, ETestStatus status) {
        return testRepository.findByIdAndStatus(testId, status).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test"));
    }

    @Override
    public LearnerSpeakingTestDetailResponse getSpeakingTestDetailResponseForExam(Long testId, List<Long> parts) {
        Test test = getApprovedTestEntity(testId);
        if (test.getType() != ETestType.SPEAKING)
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test");

        List<LearnerSpeakingPartDetailResponse> partDetailResponses = questionGroupService.getLearnerSpeakingPartsByTestIdGroupByParts(testId, parts);
        return testMapper.toLearnerSpeakingTestDetailResponse(test, partDetailResponses);
    }

    @Override
    public LearnerWritingTestDetailResponse getWritingTestDetailResponseForExam(Long testId, List<Long> parts) {
        Test test = getApprovedTestEntity(testId);
        if (test.getType() != ETestType.WRITING)
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test");

        List<LearnerWritingPartDetailResponse> partDetailResponses = questionGroupService.getLearnerWritingPartsByTestIdGroupByParts(testId, parts);
        return testMapper.toLearnerWritingTestDetailResponse(test, partDetailResponses);
    }

    private Test getApprovedTestEntity(Long testId) {
        Test test = testRepository.findById(testId).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test"));
        if (test.getStatus() != ETestStatus.APPROVED)
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test");
        return test;
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

    private Test createTest(String testName, ETestType type, TestSet testSet) {
        Test test = Test.builder()
                .name(testName)
                .status(ETestStatus.PENDING)
                .type(type)
                .testSet(testSet)
                .numberOfLearnerTests(0L).build();
        log.info("Test created successfully");
        return testRepository.save(test);
    }

    private Map<Integer, List<QuestionExcelRequest>> groupQuestionsByKey(List<QuestionExcelRequest> questionExcelRequests) {
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

    private Map<Integer, List<SpeakingQuestionExcelRequest>> groupSpeakingQuestionsByKey(List<SpeakingQuestionExcelRequest> questionExcelRequests) {
        List<SpeakingQuestionExcelRequest> sortedQuestions = questionExcelRequests.stream().sorted(
                Comparator.comparing(SpeakingQuestionExcelRequest::getNumberOfQuestions,
                        Comparator.nullsLast(Comparator.naturalOrder()))
        ).toList();

        Map<Integer, List<SpeakingQuestionExcelRequest>> groupedQuestions = new HashMap<>();
        for (SpeakingQuestionExcelRequest question : sortedQuestions) {
            Integer groupKey = Optional.ofNullable(extractGroupNumber(question.getQuestionGroupId()))
                    .orElse(-question.getNumberOfQuestions());
            groupedQuestions.computeIfAbsent(groupKey, _ -> new ArrayList<>()).add(question);
        }
        return groupedQuestions;
    }

    private Map<Integer, List<WritingQuestionExcelRequest>> groupWritingQuestionsByKey(List<WritingQuestionExcelRequest> questionExcelRequests) {
        List<WritingQuestionExcelRequest> sortedQuestions = questionExcelRequests.stream().sorted(
                Comparator.comparing(WritingQuestionExcelRequest::getNumberOfQuestions,
                        Comparator.nullsLast(Comparator.naturalOrder()))
        ).toList();

        Map<Integer, List<WritingQuestionExcelRequest>> groupedQuestions = new HashMap<>();
        for (WritingQuestionExcelRequest question : sortedQuestions) {
            Integer groupKey = Optional.ofNullable(extractGroupNumber(question.getQuestionGroupId()))
                    .orElse(-question.getNumberOfQuestions());
            groupedQuestions.computeIfAbsent(groupKey, _ -> new ArrayList<>()).add(question);
        }
        return groupedQuestions;
    }

    private void processSpeakingQuestionGroup(Test test, List<SpeakingQuestionExcelRequest> groupQuestions) {
        try {
            SpeakingQuestionExcelRequest firstQuestion = groupQuestions.getFirst();
            String partName = EPart.getSpeakingPart(firstQuestion.getPartNumber());
            Part part = partService.getPartByName(partName);
            QuestionGroup questionGroup = questionGroupService.createQuestionGroup(test, part, firstQuestion);

            questionService.createSpeakingQuestionBatch(groupQuestions, questionGroup);
            log.debug("Processed {} questions in group for test ID: {}", groupQuestions.size(), test.getId());
        } catch (ConstraintViolationException e) {
            log.warn("Failed to import Question Group. Error: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error processing question group: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.FILE_READ_ERROR);
        }
    }

    private void processWritingQuestionGroup(Test test, List<WritingQuestionExcelRequest> groupQuestions) {
        try {
            WritingQuestionExcelRequest firstQuestion = groupQuestions.getFirst();
            String partName = EPart.getWritingPart(firstQuestion.getPartNumber());
            Part part = partService.getPartByName(partName);
            QuestionGroup questionGroup = questionGroupService.createQuestionGroup(test, part, firstQuestion);

            questionService.createWritingQuestionBatch(groupQuestions, questionGroup);
            log.debug("Processed {} questions in group for test ID: {}", groupQuestions.size(), test.getId());
        } catch (ConstraintViolationException e) {
            log.warn("Failed to import Question Group. Error: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error processing question group: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.FILE_READ_ERROR);
        }
    }

    private void processSpeakingQuestions(Test test, List<SpeakingQuestionExcelRequest> questions) {
        if (questions == null || questions.isEmpty()) {
            log.warn("No questions to process for test ID: {}", test.getId());
            return;
        }

        Map<Integer, List<SpeakingQuestionExcelRequest>> groupedQuestions = groupSpeakingQuestionsByKey(questions);
        int processedGroups = 0;
        for (Map.Entry<Integer, List<SpeakingQuestionExcelRequest>> entry : groupedQuestions.entrySet()) {
            try {
                processSpeakingQuestionGroup(test, entry.getValue());
                processedGroups++;
                if (processedGroups % 10 == 0)
                    log.debug("Processed {}/{} question groups", processedGroups, groupedQuestions.size());
            } catch (Exception e) {
                log.error("Failed to process question group {}: {}", entry.getKey(), e.getMessage(), e);
            }
        }
    }

    private void processWritingQuestions(Test test, List<WritingQuestionExcelRequest> questions) {
        if (questions == null || questions.isEmpty()) {
            log.warn("No questions to process for test ID: {}", test.getId());
            return;
        }

        Map<Integer, List<WritingQuestionExcelRequest>> groupedQuestions = groupWritingQuestionsByKey(questions);
        int processedGroups = 0;
        for (Map.Entry<Integer, List<WritingQuestionExcelRequest>> entry : groupedQuestions.entrySet()) {
            try {
                processWritingQuestionGroup(test, entry.getValue());
                processedGroups++;
                if (processedGroups % 10 == 0)
                    log.debug("Processed {}/{} question groups", processedGroups, groupedQuestions.size());
            } catch (Exception e) {
                log.error("Failed to process question group {}: {}", entry.getKey(), e.getMessage(), e);
            }
        }
    }

    private List<SpeakingQuestionExcelRequest> readSpeakingFile(MultipartFile file) {
        List<SpeakingQuestionExcelRequest> questionExcelRequests;
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getLastRowNum() < 1)
                throw new AppException(ErrorCode.INVALID_FILE_FORMAT);

            int maxRows = 15;
            int lastRowNum = Math.min(sheet.getLastRowNum(), maxRows);
            questionExcelRequests = new ArrayList<>(lastRowNum);

            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                try {
                    SpeakingQuestionExcelRequest questionExcelRequest = testMapper.mapRowToSpeakingDTO(row);
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

    private List<WritingQuestionExcelRequest> readWritingFile(MultipartFile file) {
        List<WritingQuestionExcelRequest> questionExcelRequests;
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getLastRowNum() < 1)
                throw new AppException(ErrorCode.INVALID_FILE_FORMAT);

            int maxRows = 15;
            int lastRowNum = Math.min(sheet.getLastRowNum(), maxRows);
            questionExcelRequests = new ArrayList<>(lastRowNum);

            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                try {
                    WritingQuestionExcelRequest questionExcelRequest = testMapper.mapRowToWritingDTO(row);
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
}
