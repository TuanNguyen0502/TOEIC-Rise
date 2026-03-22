package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.blog.category.BlogCategoryCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.blog.category.BlogCategoryUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.blog.category.BlogCategoryDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.blog.category.BlogCategoryResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IBlogCategoryService {
    PageResponse getAllBlogCategoriesForStaff(String name, String slug, Boolean isActive, int page, int size, String sortBy, String direction);

    List<BlogCategoryResponse> getAllBlogCategoriesForLearner();

    BlogCategoryDetailResponse getBlogCategoryDetailById(Long id);

    @Transactional
    void createBlogCategory(BlogCategoryCreateRequest request);

    @Transactional
    void updateBlogCategory(Long id, BlogCategoryUpdateRequest request);

    @Transactional
    void inactiveBlogCategory(Long id);
}
