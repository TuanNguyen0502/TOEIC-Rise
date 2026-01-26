package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.tag.TagRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.tag.TagDashboardResponse;
import com.hcmute.fit.toeicrise.dtos.responses.tag.TagResponse;
import com.hcmute.fit.toeicrise.dtos.responses.minitest.TagByPartResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Tag;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.models.mappers.TagMapper;
import com.hcmute.fit.toeicrise.repositories.PartRepository;
import com.hcmute.fit.toeicrise.repositories.TagRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.TagSpecification;
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
    public PageResponse getAllTagsForDashboard(int page, int pageSize, String sortBy, String direction, String tagName) {
        String searchName = (tagName == null || tagName.trim().isEmpty()) ? null : tagName;
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Page<TagDashboardResponse> tags = tagRepository.findAllTagStatistics(searchName, pageable).map(tagMapper::mapToTagDashboardResponse);
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

    @Override
    public void checkExistsIds(Set<Long> tagIds) {
        if (tagRepository.countByIdIn(tagIds) < tagIds.size())
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Tag");
    }

    @Override
    public void createTagIfNotExists(TagRequest tagRequest) {
        tagRepository.findByName(tagRequest.getName())
                .orElseGet(() -> {
                    Tag newTag = Tag.builder().name(tagRequest.getName()).build();
                    return tagRepository.save(newTag);
                });
        throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Tag name: " + tagRequest.getName());
    }

    @Override
    public void updateTag(Long tagId, TagRequest tagRequest) {
        Tag existingTag = tagRepository.findById(tagId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Tag ID: " + tagId));

        Optional<Tag> tagWithName = tagRepository.findByName(tagRequest.getName());
        if (tagWithName.isPresent() && !tagWithName.get().getId().equals(tagId)) {
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Tag name: " + tagRequest.getName());
        }

        existingTag.setName(tagRequest.getName());
        tagRepository.save(existingTag);
    }
}