package com.hcmute.fit.toeicrise.services.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.blog.post.BlogPostResponse;
import com.hcmute.fit.toeicrise.models.entities.BlogDocument;
import com.hcmute.fit.toeicrise.models.entities.BlogPost;
import com.hcmute.fit.toeicrise.models.mappers.BlogPostMapper;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.repositories.BlogSearchRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IBlogSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogSearchServiceImpl implements IBlogSearchService {
    private final ElasticsearchOperations elasticsearchOperations;
    private final BlogSearchRepository blogSearchRepository;
    private final BlogPostMapper blogPostMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    public PageResponse searchBlogs(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Xây dựng Query MultiMatch
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q
                        .multiMatch(m -> m
                                .fields("title^3", "summary^2", "content") // Tiêu đề trọng số x3, tóm tắt x2
                                .query(keyword)
                                .fuzziness("AUTO") // Tự động sửa lỗi chính tả nhẹ
                                .operator(Operator.And) // Ưu tiên kết quả chứa đủ các từ khóa
                        )
                )
                .withPageable(pageable)
                .build();

        SearchHits<BlogDocument> searchHits = elasticsearchOperations.search(query, BlogDocument.class);

        List<BlogPostResponse> content = searchHits.getSearchHits().stream()
                .map(hit -> blogPostMapper.toBlogPostResponse(hit.getContent()))
                .toList();

        Page<BlogPostResponse> blogPostResponsePage = content.isEmpty() ? Page.empty(pageable) : new org.springframework.data.domain.PageImpl<>(content, pageable, searchHits.getTotalHits());

        return pageResponseMapper.toPageResponse(blogPostResponsePage);
    }

    @Override
    public List<BlogPostResponse> getRelatedBlogs(Long currentPostId, int limit) {
        // 1. Tìm thông tin bài viết hiện tại trong ES
        BlogDocument currentDoc = elasticsearchOperations.get(currentPostId.toString(), BlogDocument.class);
        if (currentDoc == null) return Collections.emptyList();

        // 2. Xây dựng More Like This Query
        Query query = NativeQuery.builder()
                .withQuery(q -> q
                        .moreLikeThis(m -> m
                                .like(l -> l.document(d -> d.id(currentPostId.toString()).index("blog_posts")))
                                .fields("title", "summary") // Dựa trên tiêu đề và tóm tắt để tìm bài tương tự
                                .minTermFreq(1)
                                .maxQueryTerms(12)
                        )
                )
                .withPageable(PageRequest.of(0, limit))
                .build();

        SearchHits<BlogDocument> hits = elasticsearchOperations.search(query, BlogDocument.class);

        if (hits.hasSearchHits()) {
            return hits.getSearchHits().stream()
                    .map(hit -> blogPostMapper.toBlogPostResponse(hit.getContent()))
                    .toList();
        }

        // 3. Fallback: Nếu không tìm thấy bài tương tự, tìm theo categoryName
        String categoryName = currentDoc.getCategoryName();
        Query fallbackQuery = NativeQuery.builder()
                .withQuery(q -> q
                        .bool(b -> b
                                .must(m -> m.term(t -> t.field("categoryName.keyword").value(categoryName)))
                                .mustNot(mn -> mn.term(t -> t.field("id").value(currentPostId))) // Loại trừ bài hiện tại
                        )
                )
                .withPageable(PageRequest.of(0, limit))
                .build();

        SearchHits<BlogDocument> fallbackHits = elasticsearchOperations.search(fallbackQuery, BlogDocument.class);

        return fallbackHits.getSearchHits().stream()
                .map(hit -> blogPostMapper.toBlogPostResponse(hit.getContent()))
                .toList();
    }

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
                .updatedAt(blogPost.getUpdatedAt().format(DateTimeFormatter.ofPattern(Constant.DATE_TIME_PATTERN)))
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
        blogDocument.setUpdatedAt(blogPost.getUpdatedAt().format(DateTimeFormatter.ofPattern(Constant.DATE_TIME_PATTERN)));
        blogSearchRepository.save(blogDocument);
    }

    @Async
    @Override
    public void deleteDocumentById(Long id) {
        blogSearchRepository.deleteById(id);
    }
}
