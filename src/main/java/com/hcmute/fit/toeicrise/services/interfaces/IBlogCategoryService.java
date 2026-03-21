package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.blog.BlogCategoryRequest;
import com.hcmute.fit.toeicrise.dtos.responses.blog.BlogCategoryResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IBlogCategoryService {
    List<BlogCategoryResponse> getAllBlogCategories();

    List<BlogCategoryResponse> getBlogCategoriesForStaffDropdown(String keyword);

    @Transactional
    void createBlogCategory(BlogCategoryRequest request);
}
