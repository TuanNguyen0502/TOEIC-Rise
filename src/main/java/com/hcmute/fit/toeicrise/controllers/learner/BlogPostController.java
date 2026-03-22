package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.services.interfaces.IBlogPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("LearnerBlogPostController")
@RequestMapping("/blog-posts")
@RequiredArgsConstructor
public class BlogPostController {
    private final IBlogPostService blogPostService;

    @GetMapping("/{category-slug}")
    public ResponseEntity<PageResponse> getBlogPostsByCategory(@PathVariable("category-slug") String categorySlug,
                                                               @RequestParam(required = false) String title,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(blogPostService.getBlogPostsByCategory(categorySlug, title, page, size));
    }
}
