package com.hcmute.fit.toeicrise.controllers.admin;

import com.hcmute.fit.toeicrise.dtos.requests.TestUpdateRequest;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import com.hcmute.fit.toeicrise.services.interfaces.ITestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/tests")
@RequiredArgsConstructor
public class TestController {
    private final ITestService testService;

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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTest(@PathVariable Long id, @RequestBody TestUpdateRequest testUpdateRequest) {
        return ResponseEntity.ok(testService.updateTest(id, testUpdateRequest));
    }
}
