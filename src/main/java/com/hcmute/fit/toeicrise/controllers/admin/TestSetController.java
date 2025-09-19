package com.hcmute.fit.toeicrise.controllers.admin;

import com.hcmute.fit.toeicrise.services.interfaces.ITestSetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/test-sets")
@RequiredArgsConstructor
public class TestSetController {
    private final ITestSetService testSetService;

    @GetMapping("")
    public ResponseEntity<?> getAllTestSets(@RequestParam(required = false) String name,
                                            @RequestParam(required = false) String status,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(defaultValue = "updatedAt") String sortBy,
                                            @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(testSetService.getAllTestSets(
                name, status, page, size, sortBy, direction
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTestSetDetailById(@PathVariable Long id,
                                                  @RequestParam(required = false) String name,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(defaultValue = "updatedAt") String sortBy,
                                                  @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(testSetService.getTestSetDetailById(
                id, name, status, page, size, sortBy, direction)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTestSetById(@PathVariable Long id) {
        testSetService.deleteTestSetById(id);
        return ResponseEntity.ok("Test set deleted successfully");
    }
}
