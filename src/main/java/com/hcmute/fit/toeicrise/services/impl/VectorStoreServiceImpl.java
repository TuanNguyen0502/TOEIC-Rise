package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.services.interfaces.ITestService;
import com.hcmute.fit.toeicrise.services.interfaces.IVectorStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VectorStoreServiceImpl implements IVectorStoreService {
    private final VectorStore vectorStore;
    private final ITestService testService;

    @Override
    public void initTestEmbeddings() {
        // 1. Lấy danh sách Documents đã được format từ MySQL
        List<Document> documents = testService.loadTestsForVectorDB();

        TokenTextSplitter splitter = new TokenTextSplitter(
                1000, // chunk size (quan trọng nhất) - Đừng để quá 2048
                400,  // min chunk size chars
                10,   // min chunk length to embed
                10000, // max num chunks
                true   // keep separator
        );

        List<Document> splitDocuments = splitter.apply(documents);

        // 2. Lưu vào ChromaDB
        // Lưu ý: Nếu dữ liệu lớn, hãy chia nhỏ (batch) để tránh lỗi memory
        vectorStore.add(splitDocuments);

        System.out.println("Đã embed thành công " + splitDocuments.size() + " bài test.");
    }
}
