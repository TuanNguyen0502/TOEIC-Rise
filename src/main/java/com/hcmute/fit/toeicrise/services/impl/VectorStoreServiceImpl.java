package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.services.interfaces.ITestService;
import com.hcmute.fit.toeicrise.services.interfaces.IVectorStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
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

        // 2. Lưu vào ChromaDB
        // Lưu ý: Nếu dữ liệu lớn, hãy chia nhỏ (batch) để tránh lỗi memory
        vectorStore.add(documents);

        System.out.println("Đã embed thành công " + documents.size() + " bài test.");
    }
}
