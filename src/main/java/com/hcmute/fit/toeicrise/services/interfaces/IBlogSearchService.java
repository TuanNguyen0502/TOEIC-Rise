package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.blog.post.BlogPostResponse;
import com.hcmute.fit.toeicrise.models.entities.BlogPost;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface IBlogSearchService {
    PageResponse searchBlogs(String keyword, int page, int size);

    List<BlogPostResponse> getRelatedBlogs(Long currentPostId, int limit);

    @Async
    void createDocument(BlogPost blogPost);

    @Async
    void updateDocument(BlogPost blogPost);

    @Async
    void deleteDocumentById(Long id);
}
