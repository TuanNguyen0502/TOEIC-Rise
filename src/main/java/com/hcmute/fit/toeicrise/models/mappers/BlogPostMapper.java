package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.dtos.responses.blog.post.BlogPostDetailForLearnerResponse;
import com.hcmute.fit.toeicrise.dtos.responses.blog.post.BlogPostDetailForStaffResponse;
import com.hcmute.fit.toeicrise.dtos.responses.blog.post.BlogPostResponse;
import com.hcmute.fit.toeicrise.models.entities.BlogPost;
import org.mapstruct.Mapper;

import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface BlogPostMapper {
    default BlogPostResponse blogPostToBlogPostResponse(BlogPost blogPost) {
        return BlogPostResponse.builder()
                .id(blogPost.getId())
                .title(blogPost.getTitle())
                .slug(blogPost.getSlug())
                .summary(blogPost.getSummary())
                .thumbnailUrl(blogPost.getThumbnailUrl())
                .authorName(blogPost.getAuthor().getFullName())
                .categoryName(blogPost.getCategory().getName())
                .categorySlug(blogPost.getCategory().getSlug())
                .views(blogPost.getViews())
                .updatedAt(blogPost.getUpdatedAt().format(DateTimeFormatter.ofPattern(Constant.DATE_TIME_PATTERN)))
                .build();
    }

    default BlogPostDetailForLearnerResponse toBlogPostDetailForLearnerResponse(BlogPost blogPost) {
        return BlogPostDetailForLearnerResponse.builder()
                .id(blogPost.getId())
                .title(blogPost.getTitle())
                .slug(blogPost.getSlug())
                .summary(blogPost.getSummary())
                .content(blogPost.getContent())
                .thumbnailUrl(blogPost.getThumbnailUrl())
                .authorName(blogPost.getAuthor().getFullName())
                .categoryName(blogPost.getCategory().getName())
                .categorySlug(blogPost.getCategory().getSlug())
                .views(blogPost.getViews())
                .updatedAt(blogPost.getUpdatedAt().format(DateTimeFormatter.ofPattern(Constant.DATE_TIME_PATTERN)))
                .build();
    }

    default BlogPostDetailForStaffResponse toBlogPostDetailForStaffResponse(BlogPost blogPost) {
        return BlogPostDetailForStaffResponse.builder()
                .id(blogPost.getId())
                .title(blogPost.getTitle())
                .slug(blogPost.getSlug())
                .summary(blogPost.getSummary())
                .content(blogPost.getContent())
                .thumbnailUrl(blogPost.getThumbnailUrl())
                .status(blogPost.getStatus())
                .views(blogPost.getViews())
                .createdAt(blogPost.getCreatedAt().format(DateTimeFormatter.ofPattern(Constant.DATE_TIME_PATTERN)))
                .updatedAt(blogPost.getUpdatedAt().format(DateTimeFormatter.ofPattern(Constant.DATE_TIME_PATTERN)))
                .authorId(blogPost.getAuthor().getId())
                .authorName(blogPost.getAuthor().getFullName())
                .authorEmail(blogPost.getAuthor().getAccount().getEmail())
                .categoryId(blogPost.getCategory().getId())
                .categoryName(blogPost.getCategory().getName())
                .categorySlug(blogPost.getCategory().getSlug())
                .categoryIsActive(blogPost.getCategory().getIsActive())
                .build();
    }
}
