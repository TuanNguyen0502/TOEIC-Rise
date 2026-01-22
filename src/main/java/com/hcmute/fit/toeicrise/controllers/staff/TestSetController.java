package com.hcmute.fit.toeicrise.controllers.staff;

import com.hcmute.fit.toeicrise.models.enums.ETestSetStatus;
import com.hcmute.fit.toeicrise.services.interfaces.ITestSetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("staffTestSetController")
@RequestMapping("/staff/test-sets")
@RequiredArgsConstructor
public class TestSetController {
    private final ITestSetService testSetService;

    @GetMapping("")
    public ResponseEntity<?> getAllTestSets(@RequestParam(required = false) String name,
                                            @RequestParam(required = false) ETestSetStatus status,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(defaultValue = "updatedAt") String sortBy,
                                            @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(testSetService.getAllTestSets(
                name, status, page, size, sortBy, direction
        ));
    }
}
