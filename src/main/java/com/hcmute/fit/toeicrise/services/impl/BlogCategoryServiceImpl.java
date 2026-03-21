package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.blog.BlogCategoryRequest;
import com.hcmute.fit.toeicrise.dtos.responses.blog.BlogCategoryResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.BlogCategory;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.BlogCategoryMapper;
import com.hcmute.fit.toeicrise.repositories.BlogCategoryRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IBlogCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogCategoryServiceImpl implements IBlogCategoryService {
    private final BlogCategoryRepository blogCategoryRepository;
    private final BlogCategoryMapper blogCategoryMapper;

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

    @Transactional
    @Override
    public void createBlogCategory(BlogCategoryRequest request) {
        BlogCategory blogCategory = blogCategoryRepository.findBySlug(request.getSlug()).orElse(null);
        if (blogCategory != null) {
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Blog category with slug '" + request.getSlug() + "'");
        }

        BlogCategory newBlogCategory = new BlogCategory();
        newBlogCategory.setName(request.getName());
        newBlogCategory.setSlug(request.getSlug());
        blogCategoryRepository.save(newBlogCategory);
    }

    @Transactional
    public void updateBlogCategory(Long id, BlogCategoryRequest request) {
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
        blogCategoryRepository.save(blogCategory);
    }
}
