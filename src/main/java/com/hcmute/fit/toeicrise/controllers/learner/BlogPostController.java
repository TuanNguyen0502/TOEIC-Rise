package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.blog.post.BlogPostDetailForLearnerResponse;
import com.hcmute.fit.toeicrise.dtos.responses.blog.post.BlogPostResponse;
import com.hcmute.fit.toeicrise.services.interfaces.IBlogPostService;
import com.hcmute.fit.toeicrise.services.interfaces.IBlogSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("LearnerBlogPostController")
@RequestMapping("/blog-posts")
@RequiredArgsConstructor
public class BlogPostController {
    private final IBlogPostService blogPostService;
    private final IBlogSearchService blogSearchService;

    @GetMapping("/newest")
    public ResponseEntity<PageResponse> getNewestBlogPosts(@RequestParam(required = false) String title,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(blogPostService.getNewestBlogPosts(title, page, size));
    }

    @GetMapping("/categories/{category}")
    public ResponseEntity<PageResponse> getBlogPostsByCategory(@PathVariable("category") String categorySlug,
                                                               @RequestParam(required = false) String title,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(blogPostService.getBlogPostsByCategory(categorySlug, title, page, size));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<BlogPostDetailForLearnerResponse> getBlogPostDetailForLearner(@PathVariable String slug) {
        return ResponseEntity.ok(blogPostService.getBlogPostDetailForLearner(slug));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse> searchBlogs(@RequestParam String keyword,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(blogSearchService.searchBlogs(keyword, page, size));
    }

    @GetMapping("/relate/{id}")
    public List<BlogPostResponse> getRelatedBlogPosts(@PathVariable Long id, @RequestParam(defaultValue = "5") int limit) {
        return blogSearchService.getRelatedBlogs(id, limit);
    }
}
