package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.responses.blog.BlogCategoryResponse;
import com.hcmute.fit.toeicrise.models.entities.BlogCategory;
import com.hcmute.fit.toeicrise.models.mappers.BlogCategoryMapper;
import com.hcmute.fit.toeicrise.repositories.BlogCategoryRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IBlogCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
