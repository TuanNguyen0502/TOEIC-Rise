package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.BlogDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BlogSearchRepository extends ElasticsearchRepository<BlogDocument, Long> {
    Page<BlogDocument> findByTitleOrSummaryOrContent(String title, String summary, String content, Pageable pageable);
}
