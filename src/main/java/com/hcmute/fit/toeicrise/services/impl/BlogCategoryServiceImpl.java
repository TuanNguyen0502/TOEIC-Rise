package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.blog.category.BlogCategoryCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.blog.category.BlogCategoryUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.blog.category.BlogCategoryDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.blog.category.BlogCategoryResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.BlogCategory;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.BlogCategoryMapper;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.repositories.BlogCategoryRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.BlogCategorySpecification;
import com.hcmute.fit.toeicrise.services.interfaces.IBlogCategoryService;
import com.hcmute.fit.toeicrise.services.interfaces.IBlogPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogCategoryServiceImpl implements IBlogCategoryService {
    private final IBlogPostService blogPostService;
    private final BlogCategoryRepository blogCategoryRepository;
    private final BlogCategoryMapper blogCategoryMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    public PageResponse getAllBlogCategoriesForStaff(String name, String slug, Boolean isActive, int page, int size, String sortBy, String direction) {
        Specification<BlogCategory> spec = (_, _, cb) -> cb.conjunction();
        if (name != null && !name.isBlank()) {
            spec = spec.and(BlogCategorySpecification.nameContains(name));
        }
        if (slug != null && !slug.isBlank()) {
            spec = spec.and(BlogCategorySpecification.slugContains(slug));
        }
        if (isActive != null) {
            spec = spec.and(BlogCategorySpecification.isActive(isActive));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<BlogCategoryResponse> blogCategoryResponses = blogCategoryRepository.findAll(spec, pageable)
                .map(blogCategoryMapper::toBlogCategoryResponse);
        return pageResponseMapper.toPageResponse(blogCategoryResponses);
    }

    @Override
    public List<BlogCategoryResponse> getAllBlogCategories() {
        List<BlogCategoryResponse> blogCategoryResponseList = new ArrayList<>();
        for (BlogCategory blogCategory : blogCategoryRepository.findAll()) {
            blogCategoryResponseList.add(blogCategoryMapper.toBlogCategoryResponse(blogCategory));
        }
        return blogCategoryResponseList;
    }

    @Override
    public List<BlogCategoryResponse> getBlogCategoriesForStaffDropdown(String keyword) {
        List<BlogCategoryResponse> blogCategoryResponseList = new ArrayList<>();
        if (keyword != null) {
            for (BlogCategory blogCategory : blogCategoryRepository.findAll()) {
                if (blogCategory.getName().contains(keyword)) {
                    blogCategoryResponseList.add(blogCategoryMapper.toBlogCategoryResponse(blogCategory));
                }
            }
        }
        return blogCategoryResponseList;
    }

    @Override
    public BlogCategoryDetailResponse getBlogCategoryDetailById(Long id) {
        BlogCategory blogCategory = blogCategoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Blog category with id '" + id + "'"));
        return blogCategoryMapper.toBlogCategoryDetailResponse(blogCategory);
    }

    @Transactional
    @Override
    public void createBlogCategory(BlogCategoryCreateRequest request) {
        BlogCategory blogCategory = blogCategoryRepository.findBySlug(request.getSlug()).orElse(null);
        if (blogCategory != null) {
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Blog category with slug '" + request.getSlug() + "'");
        }

        BlogCategory newBlogCategory = new BlogCategory();
        newBlogCategory.setName(request.getName());
        newBlogCategory.setSlug(request.getSlug());
        newBlogCategory.setIsActive(true);
        blogCategoryRepository.save(newBlogCategory);
    }

    @Transactional
    @Override
    public void updateBlogCategory(Long id, BlogCategoryUpdateRequest request) {
        BlogCategory blogCategory = blogCategoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Blog category with id '" + id + "'"));

        // Check if the slug is being updated and if the new slug already exists
        if (!blogCategory.getSlug().equals(request.getSlug())) {
            BlogCategory bc = blogCategoryRepository.findBySlug(request.getSlug()).orElse(null);
            if (bc != null) {
                throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Blog category with slug '" + request.getSlug() + "'");
            }
        }

        blogCategory.setName(request.getName());
        blogCategory.setSlug(request.getSlug());
        blogCategory.setIsActive(request.getActive());
        blogCategoryRepository.save(blogCategory);
    }

    @Transactional
    @Override
    public void inactiveBlogCategory(Long id) {
        BlogCategory blogCategory = blogCategoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Blog category with id '" + id + "'"));
        blogCategory.setIsActive(false);
        blogCategoryRepository.save(blogCategory);
        blogPostService.achievedBlogPostsByCategory(id);
    }
}
