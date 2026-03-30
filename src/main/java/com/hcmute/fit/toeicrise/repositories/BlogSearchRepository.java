package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.BlogDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BlogSearchRepository extends ElasticsearchRepository<BlogDocument, Long> {
}
