package com.hcmute.fit.toeicrise.services.impl;

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

        // Validate audio file
        validateAudioFileForPart(questionGroup.getPart(), request.getAudio(), request.getAudioUrl());

        // Validate image file
        validateImageFileForPart(questionGroup.getPart(), request.getImage(), request.getImageUrl(), questionGroup.getImageUrl());

        // Validate passage
        validatePassageForPart(questionGroup.getPart(), request.getPassage());

        // Update question group
        // Handle audio file
        if (request.getAudio() != null) {
            String audioUrl;
            // Update existing audio
            if (questionGroup.getAudioUrl() != null && cloudinaryUtil.isCloudinaryUrl(questionGroup.getAudioUrl())) {
                // Update audio file in Cloudinary
                audioUrl = cloudinaryUtil.updateFile(request.getAudio(), questionGroup.getAudioUrl());
            } else {
                // Upload new audio file to Cloudinary
                audioUrl = cloudinaryUtil.uploadFile(request.getAudio());
            }
            questionGroup.setAudioUrl(audioUrl);
        } else if (request.getAudioUrl() != null && !request.getAudioUrl().isBlank()) {
            // Set audio URL directly if provided
            questionGroup.setAudioUrl(request.getAudioUrl());
        }
        // Handle image file
        if (request.getImage() != null) {
            String imageUrl;
            // Update existing image
            if (questionGroup.getImageUrl() != null && cloudinaryUtil.isCloudinaryUrl(questionGroup.getImageUrl())) {
                // Update image file in Cloudinary
                imageUrl = cloudinaryUtil.updateFile(request.getImage(), questionGroup.getImageUrl());
            } else {
                // Upload new image file to Cloudinary
                imageUrl = cloudinaryUtil.uploadFile(request.getImage());
            }
            questionGroup.setImageUrl(imageUrl);
        } else if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            // Set image URL directly if provided
            questionGroup.setImageUrl(request.getImageUrl());
        }
        questionGroup.setPassage(request.getPassage());
        questionGroup.setTranscript(request.getTranscript());
        questionGroupRepository.save(questionGroup);
    }

    private void validateAudioFileForPart(Part part, MultipartFile audio, String audioUrl) {
        if (isListeningPart(part)) {
            if (audio == null && (audioUrl == null || audioUrl.isBlank())) {
                throw new AppException(ErrorCode.INVALID_REQUEST, "Audio file is required for listening parts.");
            } else if (audio != null && !cloudinaryUtil.isAudioFileValid(audio)) {
                throw new AppException(ErrorCode.INVALID_REQUEST, "Invalid audio file format.");
            }
        } else if (!isListeningPart(part) && audio != null) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Audio file should not be provided for non-listening parts.");
        }
    }

    private void validateImageFileForPart(Part part, MultipartFile image, String imageUrl, String oldImageUrl) {
        if (!isListeningPart(part) && image != null) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Image file should not be provided for non-listening parts.");
        }
        if (oldImageUrl != null && (image == null && (imageUrl == null || imageUrl.isBlank()))) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Image file is required.");
        }
        if (image != null && !cloudinaryUtil.isImageFileValid(image)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Invalid image file format.");
        }
    }

    private void validatePassageForPart(Part part, String passage) {
        if (passage == null || passage.isBlank()) {
            if (isListeningPart(part) || part.getName().contains("5")) {
                throw new AppException(ErrorCode.INVALID_REQUEST, "Passage should not be provided for listening parts or part 5.");
            } else {
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
}