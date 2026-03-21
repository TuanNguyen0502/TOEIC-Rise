package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.blog.post.BlogPostResponse;
import com.hcmute.fit.toeicrise.models.entities.BlogPost;
import com.hcmute.fit.toeicrise.models.mappers.BlogPostMapper;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.repositories.BlogPostRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.BlogPostSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.IBlogPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlogPostServiceImpl implements IBlogPostService {
    private final BlogPostRepository blogPostRepository;
    private final BlogPostMapper blogPostMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    public PageResponse getBlogPostsByCategory(String categorySlug, int page, int size) {
        Specification<BlogPost> spec = (_, _, cb) -> cb.conjunction();
        spec = spec.and(BlogPostSpecification.byCategorySlug(categorySlug));
        Sort sort = Sort.by(Sort.Direction.fromString("DESC"), "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BlogPostResponse> blogPostResponses = blogPostRepository.findAll(spec, pageable)
                .map(blogPostMapper::blogPostToBlogPostResponse);
        return pageResponseMapper.toPageResponse(blogPostResponses);
    }
}
