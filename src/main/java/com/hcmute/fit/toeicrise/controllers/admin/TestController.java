package com.hcmute.fit.toeicrise.controllers.admin;

import com.hcmute.fit.toeicrise.services.interfaces.ITestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/admin/tests")
@RestController
@RequiredArgsConstructor
public class TestController {
    private final ITestService testService;

    @PostMapping("/import")
    public ResponseEntity<?> importTests(@RequestParam("file") MultipartFile
                                        file, @RequestParam("testName")
    String testName, @RequestParam("testSetId") Long testSetId) {
        testService.importTest(file, testName, testSetId);
        return ResponseEntity.ok("Test imported");
    }
}