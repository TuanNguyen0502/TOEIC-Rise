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
                .thumbnailUrl(blogPost.getThumbnailUrl())
                .authorName(blogPost.getAuthor().getFullName())
                .categoryName(blogPost.getCategory().getName())
                .categorySlug(blogPost.getCategory().getSlug())
                .views(blogPost.getViews())
                .createdAt(blogPost.getCreatedAt())
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
        blogDocument.setThumbnailUrl(blogPost.getThumbnailUrl());
        blogDocument.setCategoryName(blogPost.getCategory().getName());
        blogDocument.setCategorySlug(blogPost.getCategory().getSlug());
        blogDocument.setViews(blogPost.getViews());
        blogDocument.setCreatedAt(blogPost.getCreatedAt());
        blogSearchRepository.save(blogDocument);
    }

    @Async
    @Override
    public void deleteDocumentById(Long id) {
        blogSearchRepository.deleteById(id);
    }
}
