package com.hcmute.fit.toeicrise.services.interfaces;

import org.springframework.ai.document.Document;
import org.springframework.scheduling.annotation.Async;

public interface IVectorStoreService {
    @Async
    void initTestEmbedding(Document document);

    @Async
    void deleteTestById(Object testId);
}
