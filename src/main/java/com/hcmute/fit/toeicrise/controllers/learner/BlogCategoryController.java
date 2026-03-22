package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.dtos.responses.blog.category.BlogCategoryResponse;
import com.hcmute.fit.toeicrise.services.interfaces.IBlogCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("LearnerBlogCategoryController")
@RequestMapping("/blog-categories")
@RequiredArgsConstructor
public class BlogCategoryController {
    private final IBlogCategoryService blogCategoryService;

    @GetMapping("")
    public ResponseEntity<List<BlogCategoryResponse>> getAllBlogCategories() {
        return ResponseEntity.ok(blogCategoryService.getAllBlogCategories());
    }
}
