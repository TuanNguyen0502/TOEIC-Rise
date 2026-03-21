package com.hcmute.fit.toeicrise.controllers.staff;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.blog.category.BlogCategoryDetailResponse;
import com.hcmute.fit.toeicrise.services.interfaces.IBlogCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("staffBlogCategoryController")
@RequestMapping("/staff/blog-categories")
@RequiredArgsConstructor
public class BlogCategoryController {
    private final IBlogCategoryService blogCategoryService;

    @GetMapping("")
    public ResponseEntity<PageResponse> getBlogCategories(@RequestParam(required = false) String name,
                                                          @RequestParam(required = false) String slug,
                                                          @RequestParam(required = false) Boolean isActive,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(defaultValue = "updatedAt") String sortBy,
                                                          @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(blogCategoryService.getAllBlogCategoriesForStaff(name, slug, isActive, page, size, sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogCategoryDetailResponse> getBlogCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(blogCategoryService.getBlogCategoryDetailById(id));
    }
}
