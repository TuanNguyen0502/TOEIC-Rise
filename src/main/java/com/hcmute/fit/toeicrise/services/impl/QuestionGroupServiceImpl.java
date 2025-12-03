package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.utils.CloudinaryUtil;
import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionGroupUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestPartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestQuestionGroupResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestQuestionResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.MiniTestQuestionResponse;
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
        List<QuestionGroup> questionGroups = questionGroupRepository.findByTest_IdOrderByPositionAsc(testId);

        // Group question groups by part
        Map<Part, List<QuestionGroup>> groupedByPart = questionGroups.stream()
                .collect(Collectors.groupingBy(QuestionGroup::getPart));

        // Convert the map to a list of PartResponse objects and sort by part name
        return groupedByPart.entrySet().stream()
                .map(entry -> {
                    Part part = entry.getKey();
                    List<QuestionGroup> groups = entry.getValue();

                    // Map each QuestionGroup to QuestionGroupResponse with questions
                    List<QuestionGroupResponse> questionGroupResponses = groups.stream()
                            .map(group -> {
                                // Fetch questions for this question group
                                List<QuestionResponse> questions = questionService.getQuestionsByQuestionGroupId(group.getId());
                                return questionGroupMapper.toResponse(group, questions);
                            })
                            .toList();

                    // Create and return a PartResponse
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
        System.out.println(questionGroup.getPart().getName());
        return questionGroup.getPart().getName();
    }

    @Override
    public Map<Long, String> getPartNamesByQuestionGroupIds(Set<Long> questionGroupIds) {
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
        return hasUrl ? newUrl : oldUrl;
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
        } else {
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
    public List<LearnerTestQuestionGroupResponse> getLearnerTestQuestionGroupResponsesByTags(Long partId, String tags, int numberQuestion) {
        List<Tag> tagList = tagService.parseTagsOrThrow(tags);
        Set<Long> tagIds = tagList.stream().filter(Objects::nonNull).map(Tag::getId).collect(Collectors.toSet());
        List<Question> questionList = questionService.getAllQuestionsByPartAndTags(tagIds, partId);

        Collections.shuffle(questionList);
        questionList = questionList.subList(0,Math.min(questionList.size(), numberQuestion));
        Map<QuestionGroup, List<Question>> groupEntities = questionList.stream().collect(Collectors.groupingBy(Question::getQuestionGroup));
        return groupEntities.entrySet().stream().map(
                        entry -> {
                            List<Question> questions = entry.getValue();
                            List<MiniTestQuestionResponse> learnerTestQuestionResponses = questions.stream().map(questionMapper::toMiniTestQuestionResponse).toList();
                            LearnerTestQuestionGroupResponse learnerTestQuestionGroupResponse = questionGroupMapper.toLearnerTestQuestionGroupResponse(entry.getKey());
                            learnerTestQuestionGroupResponse.setQuestions(new ArrayList<>(learnerTestQuestionResponses));
                            return learnerTestQuestionGroupResponse;
                        })
                .toList();
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