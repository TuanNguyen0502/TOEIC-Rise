package com.hcmute.fit.toeicrise.controllers.staff;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.models.enums.EBlogPostStatus;
import com.hcmute.fit.toeicrise.services.interfaces.IBlogPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("staffBlogPostController")
@RequestMapping("/staff/blog-posts")
@RequiredArgsConstructor
public class BlogPostController {
    private final IBlogPostService blogPostService;

    @GetMapping("/{category-slug}")
    public ResponseEntity<PageResponse> getBlogPostsByCategoryForStaff(@PathVariable("category-slug") String categorySlug,
                                                                       @RequestParam(required = false) String title,
                                                                       @RequestParam(required = false) String slug,
                                                                       @RequestParam(required = false) EBlogPostStatus status,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(blogPostService.getBlogPostsByCategory(categorySlug, title, slug, status, page, size));
    }
}
