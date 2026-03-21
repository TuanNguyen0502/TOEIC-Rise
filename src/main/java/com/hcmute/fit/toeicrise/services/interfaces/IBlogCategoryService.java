package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.blog.BlogCategoryCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.blog.BlogCategoryUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.blog.BlogCategoryResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IBlogCategoryService {
    List<BlogCategoryResponse> getAllBlogCategories();

    List<BlogCategoryResponse> getBlogCategoriesForStaffDropdown(String keyword);

    @Transactional
    void createBlogCategory(BlogCategoryCreateRequest request);

    @Transactional
    void updateBlogCategory(Long id, BlogCategoryUpdateRequest request);

    @Transactional
    void inactiveBlogCategory(Long id);
}
