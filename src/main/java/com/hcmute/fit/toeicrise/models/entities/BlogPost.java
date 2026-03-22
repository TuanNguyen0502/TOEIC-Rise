package com.hcmute.fit.toeicrise.models.entities;

import com.hcmute.fit.toeicrise.models.enums.EBlogPostStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "blog_posts")
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPost extends BaseEntity {
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id")
    private BlogCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EBlogPostStatus status;

    @Column(name = "views", nullable = false)
    private Integer views;
}
