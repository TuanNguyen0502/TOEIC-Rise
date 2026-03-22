package com.hcmute.fit.toeicrise.controllers.staff;

import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.requests.blog.post.BlogPostCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.blog.post.BlogPostUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.blog.post.BlogPostDetailForStaffResponse;
import com.hcmute.fit.toeicrise.models.enums.EBlogPostStatus;
import com.hcmute.fit.toeicrise.services.interfaces.IBlogPostService;
import jakarta.validation.Valid;
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

    @GetMapping("/{id}")
    public ResponseEntity<BlogPostDetailForStaffResponse> getBlogPostDetailForStaff(@PathVariable Long id) {
        return ResponseEntity.ok(blogPostService.getBlogPostDetailForStaff(id));
    }

    @PostMapping("/{category-slug}")
    public ResponseEntity<?> createBlogPost(@PathVariable("category-slug") String categorySlug, @Valid @RequestBody BlogPostCreateRequest request) {
        String email = SecurityUtils.getCurrentUser();
        blogPostService.createBlogPost(email, categorySlug, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBlogPost(@PathVariable Long id, @Valid @RequestBody BlogPostUpdateRequest request) {
        String email = SecurityUtils.getCurrentUser();
        blogPostService.updateBlogPost(email, id, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> changeBlogPostStatus(@PathVariable Long id, @RequestParam EBlogPostStatus status) {
        String email = SecurityUtils.getCurrentUser();
        blogPostService.changeStatus(email, id, status);
        return ResponseEntity.ok().build();
    }
}
