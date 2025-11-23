package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.services.interfaces.ITestService;
import com.hcmute.fit.toeicrise.services.interfaces.IVectorStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VectorStoreServiceImpl implements IVectorStoreService {
    private final VectorStore vectorStore;
    private final ITestService testService;
    private final RestTemplate restTemplate = new RestTemplate();

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

    @Override
    @Async
    public void deleteTestById(Object testId) {
        try {
            // BƯỚC 2: Cấu hình URL theo đúng ảnh bạn gửi
            // Mẫu: /api/v2/tenants/{tenant}/databases/{database}/collections/{collection_id}/delete

            String CHROMA_HOST = "http://localhost:8000";
            String TENANT_NAME = "TOEIC-Rise";
            String DATABASE_NAME = "TOEIC-Rise";
            String COLLECTION_NAME = "toeic-rise-collection";
            String deleteUrl = String.format("%s/api/v2/tenants/%s/databases/%s/collections/%s/delete",
                    CHROMA_HOST, TENANT_NAME, DATABASE_NAME, COLLECTION_NAME);

            // BƯỚC 3: Tạo Body theo schema
            // { "where": { "test_id": 10 } }
            Map<String, Object> whereClause = new HashMap<>();
            whereClause.put("test_id", testId); // <--- Lọc theo Metadata

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("where", whereClause);

            // BƯỚC 4: Gửi POST Request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Lưu ý: Dù là xóa nhưng API này dùng method POST
            restTemplate.postForEntity(deleteUrl, entity, String.class);
        } catch (Exception e) {
            System.err.println("❌ Exception: " + e.getMessage());
        }
    }
}
