package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.dtos.responses.blog.BlogPostResponse;
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
                .views(blogPost.getViews())
                .updatedAt(blogPost.getUpdatedAt().format(DateTimeFormatter.ofPattern(Constant.DATE_TIME_PATTERN)))
                .build();
    }
}
