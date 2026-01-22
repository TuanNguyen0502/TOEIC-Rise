package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.utils.CloudinaryUtil;
import com.hcmute.fit.toeicrise.commons.utils.ShuffleUtil;
import com.hcmute.fit.toeicrise.dtos.requests.minitest.MiniQuestionGroupRequest;
import com.hcmute.fit.toeicrise.dtos.requests.minitest.MiniTestRequest;
import com.hcmute.fit.toeicrise.dtos.requests.minitest.UserAnswerMiniTestRequest;
import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionGroupUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestPartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestQuestionGroupResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestQuestionResponse;
import com.hcmute.fit.toeicrise.dtos.responses.minitest.*;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.*;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.QuestionGroupMapper;
import com.hcmute.fit.toeicrise.models.mappers.QuestionMapper;
import com.hcmute.fit.toeicrise.repositories.QuestionGroupRepository;
import com.hcmute.fit.toeicrise.repositories.TestRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionGroupService;
import com.hcmute.fit.toeicrise.dtos.responses.test.PartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.QuestionGroupResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.QuestionResponse;
import com.hcmute.fit.toeicrise.models.mappers.PartMapper;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionService;
import com.hcmute.fit.toeicrise.services.interfaces.ITagService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionGroupServiceImpl implements IQuestionGroupService {
    private final TestRepository testRepository;
    private final QuestionGroupRepository questionGroupRepository;
    private final IQuestionService questionService;
    private final CloudinaryUtil cloudinaryUtil;
    private final QuestionGroupMapper questionGroupMapper;
    private final PartMapper partMapper;
    private final QuestionMapper questionMapper;
    private final ITagService tagService;

    @Transactional(readOnly = true)
    @Override
    public List<PartResponse> getQuestionGroupsByTestIdGroupByPart(Long testId) {
        List<QuestionGroup> questionGroups = questionGroupRepository.findByTestIdWithPart(testId);
        Set<Long> questionGroupIds = questionGroups.stream().map(QuestionGroup::getId).collect(Collectors.toSet());
        List<Question> questions = questionService.findAllQuestionByIdWithTags(questionGroupIds);
        Map<Long, List<Question>> questionsByGroupId = questions.stream().collect(
                Collectors.groupingBy(q -> q.getQuestionGroup().getId()));
        questionGroups.forEach(questionGroup -> {
            List<Question> groupQuestions = questionsByGroupId.getOrDefault(questionGroup.getId(), Collections.emptyList());
            questionGroup.setQuestions(groupQuestions);
        });
        Map<Part, List<QuestionGroup>> groupedByPart = questionGroups.stream()
                .collect(Collectors.groupingBy(QuestionGroup::getPart));

        return groupedByPart.entrySet().stream()
                .map(entry -> {
                    Part part = entry.getKey();
                    List<QuestionGroup> groups = entry.getValue();
                    List<QuestionGroupResponse> questionGroupResponses = groups.stream()
                            .map(group -> {
                                List<QuestionResponse> questionsResponse = group.getQuestions().stream()
                                        .sorted(Comparator.comparing(Question::getPosition))
                                        .map(questionMapper::toQuestionResponse)
                                        .toList();
                                return questionGroupMapper.toResponse(group, questionsResponse);
                            })
                            .toList();
                    return partMapper.toPartResponse(part, questionGroupResponses);
                })
                .sorted(Comparator.comparing(PartResponse::getName))
                .toList();
    }

    @Override
    @Transactional
    public QuestionGroup createQuestionGroup(Test test, Part part, QuestionExcelRequest questionExcelRequest) {
        QuestionGroup questionGroup = questionGroupMapper.toQuestionGroup(test, part, questionExcelRequest);
        questionGroup = questionGroupRepository.saveAndFlush(questionGroup);
        return questionGroup;
    }

    @Transactional
    @Override
    public void updateQuestionGroup(Long questionGroupId, QuestionGroupUpdateRequest request) {
        QuestionGroup questionGroup = questionGroupRepository.findById(questionGroupId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question group with ID " + questionGroupId));
        updateQuestionGroupWithEntity(questionGroup, request);
    }

    @Override
    public void updateQuestionGroupWithEntity(QuestionGroup questionGroup, QuestionGroupUpdateRequest request) {
        Part part = questionGroup.getPart();

        // Validate
        validateAudioForPart(part, request.getAudio(), request.getAudioUrl());
        validateImageForPart(part, request.getImage(), request.getImageUrl());
        validatePassageForPart(part, request.getPassage());

        // Update question group
        // Handle audio file
        questionGroup.setAudioUrl(processMediaFile(
                request.getAudio(), request.getAudioUrl(), questionGroup.getAudioUrl()));
        questionGroup.setImageUrl(processMediaFile(
                request.getImage(), request.getImageUrl(), questionGroup.getImageUrl()));
        questionGroup.setPassage(request.getPassage());
        questionGroup.setTranscript(request.getTranscript());

        questionGroupRepository.save(questionGroup);

        // Set test status to PENDING
        changeTestStatusToPending(questionGroup);
    }

    @Override
    public QuestionGroup getQuestionGroup(Long questionGroupId) {
        return questionGroupRepository.findById(questionGroupId).orElse(null);
    }

    @Override
    public QuestionGroupResponse getQuestionGroupResponse(Long questionGroupId) {
        List<QuestionResponse> questions = questionService.getQuestionsByQuestionGroupId(questionGroupId);
        return questionGroupMapper.toResponse(getQuestionGroup(questionGroupId), questions);
    }

    @Override
    public QuestionGroup getQuestionGroupEntity(Long questionGroupId) {
        return questionGroupRepository.findById(questionGroupId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question group with ID " + questionGroupId));
    }

    @Override
    public String getPartNameByQuestionGroupId(Long questionGroupId) {
        QuestionGroup questionGroup = questionGroupRepository.findById(questionGroupId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question group with ID " + questionGroupId));
        return questionGroup.getPart().getName();
    }

    @Override
    public Map<Long, String> getPartNamesByQuestionGroupIds(Set<Long> questionGroupIds) {
        if (questionGroupIds.isEmpty())
            return Collections.emptyMap();
        return questionGroupRepository.findAllById(questionGroupIds)
                .stream()
                .collect(Collectors.toMap(
                        QuestionGroup::getId,
                        qg -> qg.getPart().getName()
                ));
    }

    @Override
    public List<QuestionGroup> findAllByIdsWithQuestions(Set<Long> ids) {
        return questionGroupRepository.findAllByIdInFetchQuestions(ids);
    }

    @Override
    public void checkQuestionGroupsExistByIds(List<Long> ids) {
        Set<Long> existingIds = questionGroupRepository.findExistingIdsByIds(ids);
        for (Long id : ids) {
            if (!existingIds.contains(id)) {
                throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question group");
            }
        }
    }

    private String processMediaFile(MultipartFile newFile, String newUrl, String oldUrl) {
        boolean hasFile = newFile != null && !newFile.isEmpty();
        boolean hasUrl = newUrl != null && !newUrl.isBlank();

        if (hasFile) {
            // If there's an old file in Cloudinary, update it
            if (oldUrl != null && cloudinaryUtil.isCloudinaryUrl(oldUrl)) {
                return cloudinaryUtil.updateFile(newFile, oldUrl);
            }
            return cloudinaryUtil.uploadFile(newFile);
        }
        if (hasUrl) {
            // If the URL has changed and the old file is in Cloudinary, delete the old file
            if (oldUrl != null && cloudinaryUtil.isCloudinaryUrl(oldUrl) && !oldUrl.equals(newUrl)) {
                cloudinaryUtil.deleteFile(oldUrl);
            }
            return newUrl;
        }
        // If neither new file nor new URL is provided, delete the old file if it exists
        if (oldUrl != null && cloudinaryUtil.isCloudinaryUrl(oldUrl)) {
            cloudinaryUtil.deleteFile(oldUrl);
        }
        return null;
    }

    private void validateAudioForPart(Part part, MultipartFile audio, String audioUrl) {
        boolean isListening = isListeningPart(part);
        boolean hasAudioFile = audio != null && !audio.isEmpty();
        boolean hasAudioUrl = audioUrl != null && !audioUrl.isBlank();

        // Non-listening parts should not have audio
        if (!isListening && (hasAudioFile || hasAudioUrl)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Audio should not be provided for non-listening parts.");
        }
        // Listening parts require audio
        if (isListening && !hasAudioFile && !hasAudioUrl) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Audio is required for listening parts.");
        }
        // Validate audio file and URL
        if (hasAudioFile) {
            if (audio.getSize() > Constant.QUESTION_GROUP_AUDIO_MAX_SIZE) {
                throw new AppException(ErrorCode.INVALID_REQUEST, "Audio file size exceeds the maximum limit.");
            }
            cloudinaryUtil.validateAudioFile(audio);
        }
        if (hasAudioUrl) cloudinaryUtil.validateAudioURL(audioUrl);
    }

    private void validateImageForPart(Part part, MultipartFile image, String imageUrl) {
        boolean hasImageFile = image != null && !image.isEmpty();
        boolean hasImageUrl = imageUrl != null && !imageUrl.isBlank();
        // Specific part rules
        // Part 1 requires an image
        if (part.getName().contains("1") && (!hasImageFile && !hasImageUrl)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Image is required for part " + part.getName() + ".");
        }
        // Parts 2, 3, 5, and 6 should not have images
        if (part.getName().contains("2") || part.getName().contains("3") || part.getName().contains("5") || part.getName().contains("6")) {
            if (hasImageFile || hasImageUrl) {
                throw new AppException(ErrorCode.INVALID_REQUEST, "Image should not be provided for part " + part.getName() + ".");
            }
        }
        // Validate image file and URL
        if (hasImageFile) {
            if (image.getSize() > Constant.QUESTION_GROUP_IMAGE_MAX_SIZE) {
                throw new AppException(ErrorCode.INVALID_REQUEST, "Image file size exceeds the maximum limit.");
            }
            cloudinaryUtil.validateImageFile(image);
        }
        if (hasImageUrl) cloudinaryUtil.validateImageURL(imageUrl);
    }


    private void validatePassageForPart(Part part, String passage) {
        // Parts 6 and 7 require a passage
        // Other parts should not have a passage
        if (part.getName().contains("6") || part.getName().contains("7")) {
            if (passage == null || passage.isBlank()) {
                throw new AppException(ErrorCode.INVALID_REQUEST, "Passage is required for parts 6 and 7.");
            }
        } else if (passage != null && !passage.isBlank()) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Passage should not be provided for listening parts or part 5.");
        }
    }

    @Override
    public boolean isListeningPart(Part part) {
        return part.getName().contains("1") ||
                part.getName().contains("2") ||
                part.getName().contains("3") ||
                part.getName().contains("4");
    }

    @Transactional(readOnly = true)
    @Override
    public List<LearnerTestPartResponse> getQuestionGroupsByTestIdGroupByParts(Long testId, List<Long> partIds) {
        List<QuestionGroup> questionGroups = questionGroupRepository.findByTest_IdAndPart_IdOrderByPositionAsc(testId, partIds);
        Map<Part, List<QuestionGroup>> groupedByPart = questionGroups.stream()
                .distinct().collect(Collectors.groupingBy(QuestionGroup::getPart));
        return groupedByPart.entrySet().stream().map(entry -> {
            Part part = entry.getKey();
            List<QuestionGroup> questionGroupList = entry.getValue();
            List<LearnerTestQuestionGroupResponse> questionGroupResponses = questionGroupList
                    .stream()
                    .sorted(Comparator.comparing(QuestionGroup::getPosition))
                    .map(group -> {
                        List<LearnerTestQuestionResponse> questionResponses = group.getQuestions()
                                .stream().sorted(Comparator.comparing(Question::getPosition))
                                .map(questionMapper::toLearnerTestQuestionResponse)
                                .toList();
                        List<Object> questionAsObject = new ArrayList<>(questionResponses);
                        LearnerTestQuestionGroupResponse learnerTestQuestionGroupResponse = questionGroupMapper.toLearnerTestQuestionGroupResponse(group);
                        learnerTestQuestionGroupResponse.setQuestions(questionAsObject);
                        return learnerTestQuestionGroupResponse;
                    }).toList();
            LearnerTestPartResponse partResponse = partMapper.toLearnerTestPartResponse(part);
            partResponse.setQuestionGroups(questionGroupResponses);
            return partResponse;
        }).sorted(Comparator.comparing(LearnerTestPartResponse::getPartName)).toList();
    }

    @Override
    public MiniTestOverallResponse getMiniTestOverallResponse(MiniTestRequest request) {
        List<Long> questionIds = request.getQuestionGroups().stream()
                .flatMap(group -> group.getUserAnswerRequests().stream())
                .map(UserAnswerMiniTestRequest::getQuestionId)
                .filter(Objects::nonNull)
                .distinct().toList();

        List<Question> questions = questionService.getQuestionsWithGroupsByIds(questionIds);
        questionService.validateQuestion(questionIds, questions);
        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));
        MiniTestOverallResponse miniTestOverallResponse = calculatorAnswerMiniTest(request, questionMap);
        miniTestOverallResponse.setTotalQuestions(questions.size());
        return miniTestOverallResponse;
    }

    private MiniTestOverallResponse calculatorAnswerMiniTest(MiniTestRequest miniTestRequest, Map<Long, Question> questionMap) {
        int correctAnswers = 0;
        Map<QuestionGroup, List<MiniTestAnswerQuestionResponse>> miniTestAnswerQuestionResponses = new LinkedHashMap<>();
        int groupPosition = 1;
        long globalQuestionPosition = 1;

        for (MiniQuestionGroupRequest questionGroupRequest : miniTestRequest.getQuestionGroups()) {
            if (questionGroupRequest.getQuestionGroupId() == null)
                throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question group");

            for (UserAnswerMiniTestRequest userAnswerRequest : questionGroupRequest.getUserAnswerRequests()) {
                if (userAnswerRequest.getQuestionId() == null)
                    throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question");
                Question question = questionMap.get(userAnswerRequest.getQuestionId());
                if (question == null) throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question");
                if (!question.getQuestionGroup().getId().equals(questionGroupRequest.getQuestionGroupId()))
                    throw new AppException(ErrorCode.VALIDATION_ERROR);

                boolean isCorrect = userAnswerRequest.getAnswer() != null && question.getCorrectOption() != null
                        && question.getCorrectOption().equals(userAnswerRequest.getAnswer());
                if (isCorrect) correctAnswers++;

                MiniTestAnswerQuestionResponse miniTestAnswerQuestionResponse = questionMapper.toMiniTestAnswerQuestionResponse(question);
                miniTestAnswerQuestionResponse.setUserAnswer(userAnswerRequest.getAnswer());
                miniTestAnswerQuestionResponse.setIsCorrect(isCorrect);

                miniTestAnswerQuestionResponses.computeIfAbsent(question.getQuestionGroup(), _ -> new ArrayList<>()).add(miniTestAnswerQuestionResponse);
            }
        }
        List<MiniTestQuestionGroupAnswerResponse> groupResponses = new ArrayList<>();
        for (Map.Entry<QuestionGroup, List<MiniTestAnswerQuestionResponse>> entry : miniTestAnswerQuestionResponses.entrySet()) {
            MiniTestQuestionGroupAnswerResponse miniTestQuestionGroupResponse = questionGroupMapper.toMiniTestQuestionGroupAnswerResponse(entry.getKey());
            miniTestQuestionGroupResponse.setIndex(groupPosition++);
            for (MiniTestAnswerQuestionResponse miniTestAnswerQuestionResponse : entry.getValue()) {
                miniTestAnswerQuestionResponse.setIndex(globalQuestionPosition++);
            }
            miniTestQuestionGroupResponse.setQuestions(entry.getValue());
            groupResponses.add(miniTestQuestionGroupResponse);
        }
        return MiniTestOverallResponse.builder()
                .correctAnswers(correctAnswers)
                .questionGroups(groupResponses).build();
    }

    @Override
    public MiniTestResponse getLearnerTestQuestionGroupResponsesByTags(Long partId, Set<Long> tagIds, int numberQuestion) {
        tagService.checkExistsIds(tagIds);

        Map<QuestionGroup, List<Question>> groupEntities = getAllQuestionGroup(partId, tagIds, numberQuestion);
        groupEntities.values().forEach(questions -> questions.sort(Comparator.comparing(Question::getPosition)));
        List<MiniTestQuestionGroupResponse> miniTestQuestionGroupResponses = new ArrayList<>();
        int groupPosition = 1;
        long globalQuestionPosition = 1;

        for (Map.Entry<QuestionGroup, List<Question>> entry : groupEntities.entrySet()) {
            MiniTestQuestionGroupResponse groupResponse = questionGroupMapper.toMiniTestQuestionGroupResponse(entry.getKey());
            groupResponse.setIndex(groupPosition++);
            List<MiniTestQuestionResponse> questionResponses = new ArrayList<>();
            for (Question question : entry.getValue()) {
                MiniTestQuestionResponse questionResponse = questionMapper.toMiniTestQuestionResponse(question);
                questionResponse.setIndex(globalQuestionPosition++);
                questionResponses.add(questionResponse);
            }
            groupResponse.setQuestions(questionResponses);
            miniTestQuestionGroupResponses.add(groupResponse);
        }
        return MiniTestResponse.builder()
                .questionGroups(miniTestQuestionGroupResponses)
                .totalQuestions(globalQuestionPosition - 1).build();
    }

    private Map<QuestionGroup, List<Question>> getAllQuestionGroup(Long partId, Set<Long> tagIds, int numberQuestion) {
        List<Question> allQuestions = questionService.getAllQuestionsByPartAndTags(tagIds, partId);
        ShuffleUtil.shuffle(allQuestions);
        Map<Long, List<Question>> questionsByTag = new LinkedHashMap<>();

        for (Long tagId : tagIds) {
            List<Question> tagQuestions = allQuestions.stream().filter(
                    question -> question.getTags().stream().anyMatch(
                            tag -> tag.getId().equals(tagId))).collect(Collectors.toList());
            questionsByTag.put(tagId, tagQuestions);
        }
        List<Question> selectedQuestions = new ArrayList<>();
        Set<Long> usedQuestionIds = new HashSet<>();
        Map<Long, Integer> tagIndices = new HashMap<>();
        tagIds.forEach(tagId -> tagIndices.put(tagId, 0));

        while (selectedQuestions.size() < numberQuestion) {
            boolean addedInThisRound = false;

            for (Long tagId : tagIds) {
                if (selectedQuestions.size() >= numberQuestion)
                    break;
                List<Question> tagQuestions = questionsByTag.get(tagId);
                if (tagQuestions == null || tagQuestions.isEmpty())
                    throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question");
                Integer currentIndex = tagIndices.get(tagId);

                while (currentIndex < tagQuestions.size()) {
                    Question tagQuestion = tagQuestions.get(currentIndex);
                    currentIndex++;
                    if (!usedQuestionIds.contains(tagQuestion.getId())) {
                        selectedQuestions.add(tagQuestion);
                        usedQuestionIds.add(tagQuestion.getId());
                        addedInThisRound = true;
                        break;
                    }
                }
                tagIndices.put(tagId, currentIndex);
            }
            if (!addedInThisRound)
                break;
        }
        return selectedQuestions.stream().collect(Collectors.groupingBy(Question::getQuestionGroup, LinkedHashMap::new, Collectors.toList()));
    }

    @Async
    public void changeTestStatusToPending(QuestionGroup questionGroup) {
        Test test = questionGroup.getTest();
        if (test.getStatus() != ETestStatus.PENDING) {
            test.setStatus(ETestStatus.PENDING);
            testRepository.save(test);
        }
    }
}