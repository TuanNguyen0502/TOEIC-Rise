package com.hcmute.fit.toeicrise.dtos.responses.blog.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogCategoryResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    private String slug;
    private Integer numberOfPosts;
    private Boolean isActive;
}
