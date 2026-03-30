package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.models.entities.BlogDocument;
import com.hcmute.fit.toeicrise.models.entities.BlogPost;
import com.hcmute.fit.toeicrise.repositories.BlogSearchRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IBlogSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlogSearchServiceImpl implements IBlogSearchService {
    private final BlogSearchRepository blogSearchRepository;

    @Async
    @Override
    public void createDocument(BlogPost blogPost) {
        BlogDocument doc = BlogDocument.builder()
                .id(blogPost.getId())
                .title(blogPost.getTitle())
                .summary(blogPost.getSummary())
                .content(blogPost.getContent())
                .slug(blogPost.getSlug())
                .categoryName(blogPost.getCategory().getName())
                .categoryId(blogPost.getCategory().getId())
                .build();
        blogSearchRepository.save(doc);
    }

    @Async
    @Override
    public void updateDocument(BlogPost blogPost) {
        BlogDocument blogDocument = blogSearchRepository.findById(blogPost.getId())
                .orElse(BlogDocument.builder().id(blogPost.getId()).build());
        blogDocument.setTitle(blogPost.getTitle());
        blogDocument.setSummary(blogPost.getSummary());
        blogDocument.setContent(blogPost.getContent());
        blogDocument.setSlug(blogPost.getSlug());
        blogDocument.setCategoryName(blogPost.getCategory().getName());
        blogDocument.setCategoryId(blogPost.getCategory().getId());
        blogSearchRepository.save(blogDocument);
    }

    @Async
    @Override
    public void deleteDocumentById(Long id) {
        blogSearchRepository.deleteById(id);
    }
}
