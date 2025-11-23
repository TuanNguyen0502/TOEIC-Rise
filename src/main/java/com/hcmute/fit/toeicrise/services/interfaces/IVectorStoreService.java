package com.hcmute.fit.toeicrise.services.interfaces;

import org.springframework.scheduling.annotation.Async;

public interface IVectorStoreService {
    void initTestEmbeddings();

    @Async
    void deleteTestById(Object testId);
}
