package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.blog.BlogCategoryResponse;

import java.util.List;

public interface IBlogCategoryService {
    List<BlogCategoryResponse> getAllBlogCategories();

    List<BlogCategoryResponse> getBlogCategoriesForStaffDropdown(String keyword);
}
