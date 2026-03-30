package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.models.entities.BlogPost;
import org.springframework.scheduling.annotation.Async;

public interface IBlogSearchService {
    @Async
    void createDocument(BlogPost blogPost);

    @Async
    void updateDocument(BlogPost blogPost);

    @Async
    void deleteDocumentById(Long id);
}
