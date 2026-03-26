package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.utils.CloudinaryUtil;
import com.hcmute.fit.toeicrise.dtos.requests.blog.post.BlogPostCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.blog.post.BlogPostImageDeleteRequest;
import com.hcmute.fit.toeicrise.dtos.requests.blog.post.BlogPostImageRequest;
import com.hcmute.fit.toeicrise.dtos.requests.blog.post.BlogPostUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.blog.post.BlogPostDetailForLearnerResponse;
import com.hcmute.fit.toeicrise.dtos.responses.blog.post.BlogPostDetailForStaffResponse;
import com.hcmute.fit.toeicrise.dtos.responses.blog.post.BlogPostResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.BlogCategory;
import com.hcmute.fit.toeicrise.models.entities.BlogPost;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.EBlogPostStatus;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.BlogPostMapper;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.repositories.BlogCategoryRepository;
import com.hcmute.fit.toeicrise.repositories.BlogPostRepository;
import com.hcmute.fit.toeicrise.repositories.UserRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.BlogPostSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.IBlogPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogPostServiceImpl implements IBlogPostService {
    private final CloudinaryUtil cloudinaryUtil;
    private final BlogCategoryRepository blogCategoryRepository;
    private final BlogPostRepository blogPostRepository;
    private final UserRepository userRepository;
    private final BlogPostMapper blogPostMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    public PageResponse getNewestBlogPosts(String title, int page, int size) {
        Specification<BlogPost> spec = (_, _, cb) -> cb.conjunction();
        if (title != null && !title.isBlank()) {
            spec = spec.and(BlogPostSpecification.titleContains(title));
        }

        Sort sort = Sort.by(Sort.Direction.fromString("DESC"), "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BlogPostResponse> blogPostResponses = blogPostRepository.findAll(spec, pageable)
                .map(blogPostMapper::blogPostToBlogPostResponse);
        return pageResponseMapper.toPageResponse(blogPostResponses);
    }

    @Override
    public PageResponse getBlogPostsByCategory(String categorySlug, String title, String slug, EBlogPostStatus status, int page, int size, String sortBy, String direction) {
        Specification<BlogPost> spec = (_, _, cb) -> cb.conjunction();
        spec = spec.and(BlogPostSpecification.byCategorySlug(categorySlug));
        if (title != null && !title.isBlank()) {
            spec = spec.and(BlogPostSpecification.titleContains(title));
        }
        if (slug != null && !slug.isBlank()) {
            spec = spec.and(BlogPostSpecification.slugContains(slug));
        }
        if (status != null) {
            spec = spec.and(BlogPostSpecification.isActive(status));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BlogPostResponse> blogPostResponses = blogPostRepository.findAll(spec, pageable)
                .map(blogPostMapper::blogPostToBlogPostResponse);
        return pageResponseMapper.toPageResponse(blogPostResponses);
    }

    @Override
    public PageResponse getBlogPostsByCategory(String categorySlug, String title, int page, int size) {
        Specification<BlogPost> spec = (_, _, cb) -> cb.conjunction();
        spec = spec.and(BlogPostSpecification.byCategorySlug(categorySlug));
        if (title != null && !title.isBlank()) {
            spec = spec.and(BlogPostSpecification.titleContains(title));
        }

        Sort sort = Sort.by(Sort.Direction.fromString("DESC"), "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BlogPostResponse> blogPostResponses = blogPostRepository.findAll(spec, pageable)
                .map(blogPostMapper::blogPostToBlogPostResponse);
        return pageResponseMapper.toPageResponse(blogPostResponses);
    }

    @Override
    public BlogPostDetailForStaffResponse getBlogPostDetailForStaff(Long blogPostId) {
        BlogPost blogPost = blogPostRepository.findById(blogPostId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Blog post"));
        return blogPostMapper.toBlogPostDetailForStaffResponse(blogPost);
    }

    @Override
    public BlogPostDetailForLearnerResponse getBlogPostDetailForLearner(String slug) {
        BlogPost blogPost = blogPostRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Blog post"));
        if (blogPost.getStatus() != EBlogPostStatus.PUBLISHED) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Blog post");
        }
        blogPost.setViews(blogPost.getViews() + 1);
        blogPostRepository.save(blogPost);
        return blogPostMapper.toBlogPostDetailForLearnerResponse(blogPost);
    }

    @Async
    @Override
    public void achievedBlogPostsByCategory(Long categoryId) {
        List<BlogPost> blogPosts = blogPostRepository.findAllByCategory_Id(categoryId);
        if (blogPosts.isEmpty()) {
            return;
        }
        for (BlogPost blogPost : blogPosts) {
            blogPost.setStatus(EBlogPostStatus.ARCHIVED);
        }
        blogPostRepository.saveAll(blogPosts);
    }

    @Transactional
    @Override
    public void createBlogPost(String email, String categorySlug, BlogPostCreateRequest request) {
        BlogPost bp = blogPostRepository.findBySlug(request.getSlug()).orElse(null);
        if (bp != null) {
            throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Blog post with slug '" + request.getSlug() + "'");
        }

        User author = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Staff"));
        BlogCategory blogCategory = blogCategoryRepository.findBySlug(categorySlug)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Blog category"));

        cloudinaryUtil.validateImageFile(request.getThumbnail());
        String thumbnailUrl = cloudinaryUtil.uploadFile(request.getThumbnail());

        BlogPost blogPost = new BlogPost();
        blogPost.setTitle(request.getTitle());
        blogPost.setSlug(request.getSlug());
        blogPost.setSummary(request.getSummary());
        blogPost.setContent(request.getContent());
        blogPost.setThumbnailUrl(thumbnailUrl);
        blogPost.setAuthor(author);
        blogPost.setCategory(blogCategory);
        blogPost.setStatus(EBlogPostStatus.DRAFT);
        blogPost.setViews(0);
        blogPostRepository.save(blogPost);
    }

    @Transactional
    @Override
    public void updateBlogPost(String email, Long blogPostId, BlogPostUpdateRequest request) {
        BlogPost blogPost = blogPostRepository.findById(blogPostId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Blog post"));
        User author = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Staff"));

        // Only the author of the blog post can update it
        if (!blogPost.getAuthor().getId().equals(author.getId())) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "You are not the author of this blog post");
        }
        if (!blogPost.getSlug().equals(request.getSlug())) {
            BlogPost bp = blogPostRepository.findBySlug(request.getSlug()).orElse(null);
            if (bp != null) {
                throw new AppException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Blog post with slug '" + request.getSlug() + "'");
            }
        }

        blogPost.setTitle(request.getTitle());
        blogPost.setSlug(request.getSlug());
        blogPost.setSummary(request.getSummary());
        blogPost.setContent(request.getContent());
        blogPost.setStatus(request.getStatus());
        if (request.getThumbnail() != null) {
            cloudinaryUtil.validateImageFile(request.getThumbnail());
            String thumbnailUrl = cloudinaryUtil.updateFile(request.getThumbnail(), blogPost.getThumbnailUrl());
            blogPost.setThumbnailUrl(thumbnailUrl);
        }
        if (request.getCategoryId() != null && !blogPost.getCategory().getId().equals(request.getCategoryId())) {
            BlogCategory blogCategory = blogCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Blog category"));
            blogPost.setCategory(blogCategory);
        }
        blogPostRepository.save(blogPost);
    }

    @Transactional
    @Override
    public void changeStatus(String email, Long blogPostId, EBlogPostStatus status) {
        BlogPost blogPost = blogPostRepository.findById(blogPostId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Blog post"));
        User author = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Staff"));
        BlogCategory blogCategory = blogPost.getCategory();

        if (!blogPost.getAuthor().getId().equals(author.getId())) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "You are not the author of this blog post");
        }
        if (status.equals(EBlogPostStatus.PUBLISHED) && blogCategory.getIsActive() == false) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Cannot publish blog post because the category '" + blogCategory.getName() + "' is not active");
        }
        blogPost.setStatus(status);
        blogPostRepository.save(blogPost);
    }

    @Override
    public String uploadImage(BlogPostImageRequest request) {
        cloudinaryUtil.validateImageFile(request.getImage());
        return cloudinaryUtil.uploadFile(request.getImage());
    }

    @Override
    public void deleteImage(BlogPostImageDeleteRequest request) {
        if (!cloudinaryUtil.isCloudinaryUrl(request.getImageUrl())) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Invalid image URL");
        }
        cloudinaryUtil.validateImageURL(request.getImageUrl());
        cloudinaryUtil.deleteFile(request.getImageUrl());
    }
}
