package com.hcmute.fit.toeicrise.controllers.admin;

import com.hcmute.fit.toeicrise.services.interfaces.ITestSetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/test-sets")
@RequiredArgsConstructor
public class TestSetController {
    private final ITestSetService testSetService;

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTestSetById(@PathVariable Long id) {
        testSetService.deleteTestSetById(id);
        return ResponseEntity.ok("Test set deleted successfully");
    }
}
