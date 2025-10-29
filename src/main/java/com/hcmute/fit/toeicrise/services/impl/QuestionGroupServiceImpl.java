package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.utils.CloudinaryUtil;
import com.hcmute.fit.toeicrise.dtos.requests.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.requests.QuestionGroupUpdateRequest;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Part;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.entities.Test;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.QuestionGroupMapper;
import com.hcmute.fit.toeicrise.repositories.QuestionGroupRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionGroupService;
import com.hcmute.fit.toeicrise.dtos.responses.PartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.QuestionGroupResponse;
import com.hcmute.fit.toeicrise.dtos.responses.QuestionResponse;
import com.hcmute.fit.toeicrise.models.mappers.PartMapper;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionGroupServiceImpl implements IQuestionGroupService {
    private final QuestionGroupRepository questionGroupRepository;
    private final IQuestionService questionService;
    private final CloudinaryUtil cloudinaryUtil;
    private final QuestionGroupMapper questionGroupMapper;
    private final PartMapper partMapper;

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
    }

    @Override
    public QuestionGroup getQuestionGroup(Long questionGroupId) {
        return questionGroupRepository.findById(questionGroupId).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question group"));
    }

    @Override
    public QuestionGroupResponse getQuestionGroupResponse(Long questionGroupId) {
        List<QuestionResponse> questions = questionService.getQuestionsByQuestionGroupId(questionGroupId);
        return questionGroupMapper.toResponse(getQuestionGroup(questionGroupId), questions);
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
        boolean isListening = isListeningPart(part);
        boolean isPart1 = isPart1(part);
        boolean hasImageFile = image != null && !image.isEmpty();
        boolean hasImageUrl = imageUrl != null && !imageUrl.isBlank();

        // Non-listening parts should not have images
        if (!isListening && (hasImageFile || hasImageUrl)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Image should not be provided for non-listening parts.");
        }
        // Part 1 requires an image
        if (isPart1 && !hasImageFile && !hasImageUrl) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Image is required for part 1.");
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
        if (passage != null && !passage.isBlank()) {
            if (isListeningPart(part) || part.getName().contains("5")) {
                throw new AppException(ErrorCode.INVALID_REQUEST, "Passage should not be provided for listening parts or part 5.");
            } else {
                throw new AppException(ErrorCode.INVALID_REQUEST, "Passage is required for parts 6 and 7.");
            }
        } else {
            if (part.getName().contains("6") || part.getName().contains("7")) {
                throw new AppException(ErrorCode.INVALID_REQUEST, "Passage is required for parts 6 and 7.");
            }
        }
    }

    private boolean isListeningPart(Part part) {
        return part.getName().contains("1") ||
                part.getName().contains("2") ||
                part.getName().contains("3") ||
                part.getName().contains("4");
    }

    private boolean isPart1(Part part) {
        return part.getName().contains("1");
    }
}