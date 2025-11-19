package com.hcmute.fit.toeicrise.controllers.admin;

import com.hcmute.fit.toeicrise.dtos.requests.test.TestRequest;
import com.hcmute.fit.toeicrise.dtos.requests.test.TestUpdateRequest;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import com.hcmute.fit.toeicrise.services.interfaces.ITestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/tests")
@RequiredArgsConstructor
public class TestController {
    private final ITestService testService;

    @PostMapping("/import")
    public ResponseEntity<?> importTests(@RequestPart MultipartFile file,
                                         @Valid @RequestPart TestRequest testRequest) {
        testService.importTest(file, testRequest);
        return ResponseEntity.ok("Test imported");
    }

    @GetMapping("")
    public ResponseEntity<?> getAllTests(@RequestParam(required = false) String name,
                                         @RequestParam(required = false) ETestStatus status,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(defaultValue = "updatedAt") String sortBy,
                                         @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(testService.getAllTests(
                name, status, page, size, sortBy, direction
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTestById(@PathVariable Long id) {
        return ResponseEntity.ok(testService.getTestDetailById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTest(@PathVariable Long id, @Valid @RequestBody TestUpdateRequest testUpdateRequest) {
        return ResponseEntity.ok(testService.updateTest(id, testUpdateRequest));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id, @Valid @RequestParam ETestStatus status) {
        return ResponseEntity.ok(testService.changeTestStatusById(id, status));
    }
}