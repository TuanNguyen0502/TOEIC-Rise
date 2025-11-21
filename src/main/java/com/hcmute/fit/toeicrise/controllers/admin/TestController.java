package com.hcmute.fit.toeicrise.controllers.admin;

import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import com.hcmute.fit.toeicrise.services.interfaces.ITestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/tests")
@RequiredArgsConstructor
public class TestController {
    private final ITestService testService;

    @PatchMapping("/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id, @Valid @RequestParam ETestStatus status) {
        return ResponseEntity.ok(testService.changeTestStatusById(id, status));
    }
}