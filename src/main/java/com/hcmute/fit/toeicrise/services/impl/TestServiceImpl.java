package com.hcmute.fit.toeicrise.services.impl;

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
import org.springframework.ai.document.Document;
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
import java.util.stream.Collectors;

import static com.hcmute.fit.toeicrise.commons.utils.CodeGeneratorUtils.extractGroupNumber;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements ITestService {
    private final TestRepository testRepository;
    private final IPartService partService;
    private final TestSetRepository testSetRepository;
    private final IQuestionService questionService;
    private final IQuestionGroupService questionGroupService;
    private final ITagService tagService;
    private final PartMapper partMapper;
    private final TestMapper testMapper;
    private final PageResponseMapper pageResponseMapper;
    private final IVectorStoreService vectorStoreService;

    @Override
    public PageResponse getAllTests(String name, ETestStatus status, int page, int size, String sortBy, String direction) {
        Specification<Test> specification = (_, _, cb) -> cb.conjunction();
        return getTestResponses(name, status, page, size, sortBy, direction, specification);
    }

    @Override
    public PageResponse getTestsByTestSetId(Long testSetId, String name, ETestStatus status, int page, int size, String sortBy, String direction) {
        Specification<Test> specification = (_, _, cb) -> cb.conjunction();
        specification = specification.and(TestSpecification.testSetIdEquals(testSetId));
        return getTestResponses(name, status, page, size, sortBy, direction, specification);
    }

    @Override
    public TestResponse updateTest(Long id, TestUpdateRequest testUpdateRequest) {
        // Validate test ID
        Test existingTest = testRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test"));

        // Check for name uniqueness
        Test testWithSameName = testRepository.findByName(testUpdateRequest.getName()).orElse(null);
        if (testWithSameName != null && !testWithSameName.getId().equals(existingTest.getId())) {
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Test's name");
        }

        // Update fields
        existingTest.setName(testUpdateRequest.getName());
        existingTest.setStatus(ETestStatus.PENDING);
        Test updatedTest = testRepository.save(existingTest);
        return testMapper.toResponse(updatedTest);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean changeTestStatusById(Long id, ETestStatus status) {
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test"));
        test.setStatus(status);
        testRepository.save(test);
        if (status == ETestStatus.APPROVED) {
            Document document = loadTestForVectorDB(test);
            vectorStoreService.initTestEmbedding(document);
        } else {
            vectorStoreService.deleteTestById(test.getId());
        }
        return true;
    }

    @Async
    @Override
    public void deleteTestsByTestSetId(Long testSetId) {
        List<Test> tests = testRepository.findAllByTestSet_Id(testSetId);
        for (Test test : tests) {
            test.setStatus(ETestStatus.DELETED);
        }
        testRepository.saveAll(tests);
    }

    @Override
    public TestDetailResponse getTestDetailById(Long id) {
        // Validate test ID
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test"));

        // Fetch question groups and questions
        List<PartResponse> partResponses = questionGroupService.getQuestionGroupsByTestIdGroupByPart(id);
        return testMapper.toDetailResponse(test, partResponses);
    }

    private PageResponse getTestResponses(String name, ETestStatus status, int page, int size, String sortBy, String direction, Specification<Test> specification) {
        if (name != null && !name.isEmpty()) {
            specification = specification.and(TestSpecification.nameContains(name));
        }
        if (status != null) {
            specification = specification.and(TestSpecification.statusEquals(status));
        } else {
            // If status is null, get all statuses except DELETED
            specification = specification.and(TestSpecification.statusNotEquals(ETestStatus.DELETED));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TestResponse> testResponses = testRepository.findAll(specification, pageable).map(testMapper::toResponse);
        return pageResponseMapper.toPageResponse(testResponses);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void importTest(MultipartFile file, TestRequest request) {
        if (!isValidFile(file))
            throw new AppException(ErrorCode.INVALID_FILE_FORMAT);
        TestSet testSet = testSetRepository.findById(request.getTestSetId()).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test Set"));
        Test test = createTest(request.getTestName(), testSet);
        List<QuestionExcelRequest> questionExcelRequests = readFile(file);
        processQuestions(test, questionExcelRequests);
    }

    @Override
    public Test createTest(String testName, TestSet testSet) {
        Test test = new Test();
        test.setName(testName);
        test.setStatus(ETestStatus.PENDING);
        test.setTestSet(testSet);
        test.setNumberOfLearnerTests(0L);
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
    public void processQuestions(Test test, List<QuestionExcelRequest> questions) {
        List<QuestionExcelRequest> sortedQuestions = questions.stream()
                .sorted(Comparator.comparing(QuestionExcelRequest::getNumberOfQuestions, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
        Map<Integer, List<QuestionExcelRequest>> groupedQuestions = new HashMap<>();
        for (QuestionExcelRequest question : sortedQuestions) {
            Integer groupKey = Optional.ofNullable(extractGroupNumber(question.getQuestionGroupId()))
                    .orElse(-question.getNumberOfQuestions());
            groupedQuestions.computeIfAbsent(groupKey, _ -> new ArrayList<>()).add(question);
        }
        for (Map.Entry<Integer, List<QuestionExcelRequest>> entry : new ArrayList<>(groupedQuestions.entrySet())) {
            processQuestionGroup(test, entry.getValue());
        }
    }

    @Override
    public void processQuestionGroup(Test test, List<QuestionExcelRequest> groupQuestions) {
        try {
            QuestionExcelRequest firstQuestion = groupQuestions.getFirst();
            Part part = partService.getPartById(firstQuestion.getPartNumber());
            QuestionGroup questionGroup = questionGroupService.createQuestionGroup(test, part, firstQuestion);
            for (QuestionExcelRequest dto : groupQuestions) {
                List<Tag> tags = tagService.getTagsFromString(dto.getTags());
                questionService.createQuestion(dto, questionGroup, tags);
            }
        } catch (ConstraintViolationException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrorCode.FILE_READ_ERROR);
        }
    }

    @Override
    public boolean isValidFile(MultipartFile file) {
        String filePath = file.getOriginalFilename();
        if (filePath == null)
            return false;
        return filePath.endsWith(".xlsx") || filePath.endsWith(".xls") || filePath.endsWith(".xlsm");
    }

    @Override
    public PageResponse searchTestsByName(com.hcmute.fit.toeicrise.dtos.requests.test.PageRequest request) {
        Specification<Test> testSpecification = (_, _, cb) -> cb.conjunction();
        testSpecification = testSpecification.and(TestSpecification.testSetIdsIn(request.getSort()));

        if (request.getName() != null && !request.getName().isEmpty()) {
            testSpecification = testSpecification.and(TestSpecification.nameContains(request.getName()));
        }
        testSpecification = testSpecification.and(TestSpecification.statusEquals(ETestStatus.APPROVED));
        testSpecification = testSpecification.and(TestSpecification.testSetStatusEquals(ETestSetStatus.IN_USE));
        Sort sort = Sort.by(Sort.Direction.fromString(EDirection.DES.getValue()), ESort.CREATED_AT.getValue());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<LearnerTestResponse> testResponses = testRepository.findAll(testSpecification, pageable).map(testMapper::toLearnerTestResponse);
        return pageResponseMapper.toPageResponse(testResponses);
    }

    @Override
    public LearnerTestDetailResponse getLearnerTestDetailById(Long id) {
        return testMapper.toLearnerTestDetailResponse(testRepository.findListTagByIdOrderByPartName(id), partMapper);
    }

    private Document loadTestForVectorDB(Test test) {
        StringBuilder contentBuilder = new StringBuilder();

        // Xây dựng nội dung mô tả bài test (Context Window)
        contentBuilder.append("Test Name: ").append(test.getName()).append("\n");

        List<QuestionGroup> questionGroups = questionGroupService.findAllByTestId(test.getId());

        // Duyệt qua các Question Groups
        for (QuestionGroup group : questionGroups) {
            if (group.getPart() != null) {
                contentBuilder.append("Part: ").append(group.getPart().getName()).append("\n");
            }
            if (group.getTranscript() != null) {
                contentBuilder.append("Transcript: ").append(group.getTranscript()).append("\n");
            }

            // Duyệt qua các Questions
            for (Question question : group.getQuestions()) {
                contentBuilder.append("Question: ").append(question.getContent()).append("\n");
                if (question.getExplanation() != null) {
                    contentBuilder.append("Explanation: ").append(question.getExplanation()).append("\n");
                }

                // Duyệt Tags
                String tags = question.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.joining(", "));
                if (!tags.isEmpty()) {
                    contentBuilder.append("Tags: ").append(tags).append("\n");
                }
            }
        }

        // 2. Tạo Metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("test_id", test.getId());
        metadata.put("status", test.getStatus().toString());
        metadata.put("popularity", test.getNumberOfLearnerTests());

        // 3. Tạo Document của Spring AI
        return new Document(contentBuilder.toString(), metadata);
    }
}
