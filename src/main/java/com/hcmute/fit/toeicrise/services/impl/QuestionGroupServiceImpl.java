package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.utils.CloudinaryUtil;
import com.hcmute.fit.toeicrise.commons.utils.HelperUtil;
import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionGroupUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestPartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestQuestionGroupResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestQuestionResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.*;
import com.hcmute.fit.toeicrise.models.enums.EPart;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.QuestionGroupMapper;
import com.hcmute.fit.toeicrise.models.mappers.QuestionMapper;
import com.hcmute.fit.toeicrise.repositories.QuestionGroupRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionGroupService;
import com.hcmute.fit.toeicrise.dtos.responses.test.PartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.QuestionGroupResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.QuestionResponse;
import com.hcmute.fit.toeicrise.models.mappers.PartMapper;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionGroupServiceImpl implements IQuestionGroupService {
    private final QuestionGroupRepository questionGroupRepository;
    private final IQuestionService questionService;
    private final CloudinaryUtil cloudinaryUtil;
    private final QuestionGroupMapper questionGroupMapper;
    private final PartMapper partMapper;
    private final QuestionMapper questionMapper;

    @Override
    @Transactional
    public QuestionGroup createQuestionGroup(Test test, Part part, QuestionExcelRequest questionExcelRequest) {
        QuestionGroup questionGroup = questionGroupMapper.toQuestionGroup(test, part, questionExcelRequest);
        questionGroup = questionGroupRepository.saveAndFlush(questionGroup);
        log.info("Created question group: {}", questionGroup.getId());
        return questionGroup;
    }

    @Transactional
    @Override
    public QuestionGroupResponse updateQuestionGroup(Long questionGroupId, QuestionGroupUpdateRequest request) {
        QuestionGroup questionGroup = getQuestionGroupEntity(questionGroupId);
        updateQuestionGroupWithEntity(questionGroup, request);
        log.info("Updated question group: {}", questionGroup.getId());
        List<QuestionResponse> questionResponses = questionGroup.getQuestions().stream()
                .map(questionMapper::toQuestionResponse).toList();
        return questionGroupMapper.toResponse(getQuestionGroupEntity(questionGroupId), questionResponses);
    }

    @Override
    @Transactional
    public void updateQuestionGroupWithEntity(QuestionGroup questionGroup, QuestionGroupUpdateRequest request) {
        Part part = questionGroup.getPart();

        validateAudioForPart(part, request.getAudio(), request.getAudioUrl());
        validateImageForPart(part, request.getImage(), request.getImageUrl());
        validatePassageForPart(part, request.getPassage());

        questionGroup.setAudioUrl(processMediaFile(
                request.getAudio(), request.getAudioUrl(), questionGroup.getAudioUrl()));
        questionGroup.setImageUrl(processMediaFile(
                request.getImage(), request.getImageUrl(), questionGroup.getImageUrl()));
        questionGroup.setPassage(request.getPassage());
        questionGroup.setTranscript(request.getTranscript());

        questionGroupRepository.save(questionGroup);
        log.info("Update question group successfully with ID: {}", questionGroup.getId());
        questionService.changeTestStatusToPending(questionGroup.getTest());
    }

    @Override
    public QuestionGroupResponse getQuestionGroupResponse(Long questionGroupId) {
        List<QuestionResponse> questions = questionService.getQuestionsByQuestionGroupId(questionGroupId);
        return questionGroupMapper.toResponse(getQuestionGroupEntity(questionGroupId), questions);
    }

    @Override
    public QuestionGroup getQuestionGroupEntity(Long questionGroupId) {
        return questionGroupRepository.findById(questionGroupId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question group"));
    }

    @Override
    public String getPartNameByQuestionGroupId(Long questionGroupId) {
        QuestionGroup questionGroup = getQuestionGroupEntity(questionGroupId);
        return questionGroup.getPart().getName();
    }

    @Override
    public Map<Long, String> getPartNamesByQuestionGroupIds(Set<Long> questionGroupIds) {
        if (questionGroupIds.isEmpty())
            return Collections.emptyMap();
        return questionGroupRepository.findAllByIdWithPart(questionGroupIds)
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
        Set<Long> inputIds = new HashSet<>(ids);
        Set<Long> existingIds = questionGroupRepository.findExistingIdsByIds(ids);
        List<Long> missingIds = inputIds.stream().filter(id -> !existingIds.contains(id)).toList();
        if (!missingIds.isEmpty())
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question group");
    }

    private String processMediaFile(MultipartFile newFile, String newUrl, String oldUrl) {
        boolean hasFile = newFile != null && !newFile.isEmpty();
        boolean hasUrl = newUrl != null && !newUrl.isBlank();

        if (hasFile) {
            if (oldUrl != null && cloudinaryUtil.isCloudinaryUrl(oldUrl))
                return cloudinaryUtil.updateFile(newFile, oldUrl);
            return cloudinaryUtil.uploadFile(newFile);
        }
        if (hasUrl) {
            if (oldUrl != null && cloudinaryUtil.isCloudinaryUrl(oldUrl) && !oldUrl.equals(newUrl)) {
                cloudinaryUtil.deleteFile(oldUrl);
            }
            return newUrl;
        }
        return oldUrl;
    }

    private void validateAudioForPart(Part part, MultipartFile audio, String audioUrl) {
        EPart ePart = EPart.getEPart(part.getName());
        boolean hasAudioFile = audio != null && !audio.isEmpty();
        boolean hasAudioUrl = audioUrl != null && !audioUrl.isBlank();

        if (!ePart.isRequiredAudio() && (hasAudioFile || hasAudioUrl))
            throw new AppException(ErrorCode.INVALID_REQUEST, "Audio should not be provided for non-listening parts.");
        if (ePart.isRequiredAudio() && !hasAudioFile && !hasAudioUrl)
            throw new AppException(ErrorCode.INVALID_REQUEST, "Audio is required for listening parts.");
        if (hasAudioFile) {
            if (audio.getSize() > Constant.QUESTION_GROUP_AUDIO_MAX_SIZE)
                throw new AppException(ErrorCode.FILE_SIZE_EXCEEDED);
            cloudinaryUtil.validateAudioFile(audio);
        }
        if (hasAudioUrl) cloudinaryUtil.validateAudioURL(audioUrl);
    }

    private void validateImageForPart(Part part, MultipartFile image, String imageUrl) {
        EPart ePart = EPart.getEPart(part.getName());
        boolean hasImageFile = image != null && !image.isEmpty();
        boolean hasImageUrl = imageUrl != null && !imageUrl.isBlank();

        if (ePart.isRequiredImage() && (!hasImageFile && !hasImageUrl))
            throw new AppException(ErrorCode.INVALID_REQUEST, "Image is required for part " + part.getName() + ".");
        if (!ePart.allowImage() && (hasImageFile || hasImageUrl))
            throw new AppException(ErrorCode.INVALID_REQUEST, "Image should not be provided for part " + part.getName() + ".");
        if (hasImageFile) {
            if (image.getSize() > Constant.QUESTION_GROUP_IMAGE_MAX_SIZE)
                throw new AppException(ErrorCode.IMAGE_SIZE_EXCEEDED);
            cloudinaryUtil.validateImageFile(image);
        }
        if (hasImageUrl) cloudinaryUtil.validateImageURL(imageUrl);
    }

    private void validatePassageForPart(Part part, String passage) {
        EPart ePart = EPart.getEPart(part.getName());
        if (ePart.isRequiredPassage()) {
            if (passage == null || passage.isBlank())
                throw new AppException(ErrorCode.INVALID_REQUEST, "Passage is required for parts 6 and 7.");
        }
        else if (passage != null && !passage.isBlank())
            throw new AppException(ErrorCode.INVALID_REQUEST, "Passage should not be provided for listening parts or part 5.");
    }

    @Override
    public boolean isListeningPart(Part part) {
        EPart ePart = EPart.getEPart(part.getName());
        return ePart.isRequiredAudio();
    }

    @Transactional(readOnly = true)
    @Override
    public List<LearnerTestPartResponse> getQuestionGroupsByTestIdGroupByParts(Long testId, List<Long> partIds) {
        List<QuestionGroup> questionGroups = questionGroupRepository.findByTestIdAndPartIdsWithQuestionsAndPart(testId, partIds);

        return HelperUtil.groupByPartAndMap(questionGroups, (part, groups) -> {
            List<LearnerTestQuestionGroupResponse> groupResponses = groups.stream().sorted(Comparator.comparing(QuestionGroup::getPosition))
                    .map(group -> {
                        List<LearnerTestQuestionResponse> questionResponses = group.getQuestions().stream().sorted(Comparator.comparing(Question::getPosition))
                                .map(questionMapper::toLearnerTestQuestionResponse).toList();

                        LearnerTestQuestionGroupResponse groupResponse = questionGroupMapper.toLearnerTestQuestionGroupResponse(group);
                        groupResponse.setQuestions(new ArrayList<>(questionResponses));
                        return groupResponse;
                    })
                    .toList();
            LearnerTestPartResponse partResponse = partMapper.toLearnerTestPartResponse(part);
            partResponse.setQuestionGroups(groupResponses);
            return partResponse;
        }, Comparator.comparing(LearnerTestPartResponse::getPartName));
    }

    @Transactional(readOnly = true)
    @Override
    public List<PartResponse> getQuestionGroupsByTestIdGroupByPart(Long testId) {
        List<QuestionGroup> questionGroups = questionGroupRepository.findByTestIdWithPart(testId);
        Map<Long, List<Question>> questionsByGroupId = attachQuestionsToGroups(questionGroups);

        return HelperUtil.groupByPartAndMap(questionGroups, (part, groups) -> {
            List<QuestionGroupResponse> groupResponses = groups.stream().map(group -> {
                List<Question> groupQuestions = questionsByGroupId.getOrDefault(group.getId(), Collections.emptyList());
                List<QuestionResponse> questionResponses = groupQuestions.stream().sorted(Comparator.comparing(Question::getPosition))
                        .map(questionMapper::toQuestionResponse).toList();
                return questionGroupMapper.toResponse(group, questionResponses);
            }).toList();
            return partMapper.toPartResponse(part, groupResponses);
        }, Comparator.comparing(PartResponse::getName));
    }

    public Map<Long, List<Question>> attachQuestionsToGroups(List<QuestionGroup> questionGroups){
        Set<Long> groupIds = questionGroups.stream().map(QuestionGroup::getId).collect(Collectors.toSet());
        if (groupIds.isEmpty())
            return Collections.emptyMap();

        return questionService.findAllQuestionByIdWithTags(groupIds).stream()
                .collect(Collectors.groupingBy(q -> q.getQuestionGroup().getId()));
    }
}