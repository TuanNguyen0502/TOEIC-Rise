package com.hcmute.fit.toeicrise.models.entities;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "blog_posts")
@Data
@Builder
public class BlogDocument {
    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "icu_analyzer") // Analyzer cho tiếng Việt
    private String title;

    @Field(type = FieldType.Text, analyzer = "icu_analyzer")
    private String summary;

    @Field(type = FieldType.Text, analyzer = "icu_analyzer")
    private String content;

    private String slug;
    private String categoryName;
    private Long categoryId;
}
