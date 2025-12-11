package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.TagResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestQuestionGroupResponse;
import com.hcmute.fit.toeicrise.dtos.responses.minitest.TagByPartResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Tag;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.models.mappers.TagMapper;
import com.hcmute.fit.toeicrise.repositories.PartRepository;
import com.hcmute.fit.toeicrise.repositories.TagRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.TagSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionGroupService;
import com.hcmute.fit.toeicrise.services.interfaces.ITagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements ITagService {
    private final PartRepository partRepository;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    public List<Tag> getTagsFromString(String tagsString, Function<String, Tag> resolver) {
        List<Tag> tags = new ArrayList<>();
        if (!StringUtils.hasText(tagsString)) {
            return tags;
        }
        String[] tagNames = tagsString.split(";");
        for (String tagName : tagNames) {
            if (StringUtils.hasText(tagName)) {
                tags.add(resolver.apply(tagName.trim()));
            }
        }
        return tags;
    }

    @Override
    public PageResponse getAllTags(int page, int pageSize, String tagsName) {
        Specification<Tag> specification = (_, _, cb) -> cb.conjunction();
        if (StringUtils.hasText(tagsName)) {
            specification = specification.and(TagSpecification.nameContains(tagsName));
        }
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Page<TagResponse> tags = tagRepository.findAll(specification, pageable).map(tagMapper::toTagResponse);
        return pageResponseMapper.toPageResponse(tags);
    }

    @Override
    public List<TagByPartResponse> getTagsByPartId(Long partId) {
        if (!partRepository.existsById(partId)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Part");
        }
        return tagRepository.findTagsByPartId(partId).stream()
                .map(tagMapper::mapToTagByPartResponse)
                .toList();
    }

    public List<Tag> parseTagsAllowCreate(String tagsString) {
        return getTagsFromString(tagsString, name ->
                tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(name).build()))
        );
    }

    public List<Tag> parseTagsOrThrow(String tagsString) {
        return getTagsFromString(tagsString, name ->
                tagRepository.findByName(name)
                        .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND,
                                "Tag name: "+ name))
        );
    }
}