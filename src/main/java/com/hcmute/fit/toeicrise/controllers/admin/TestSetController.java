package com.hcmute.fit.toeicrise.controllers.admin;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.models.enums.ETestSetStatus;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import com.hcmute.fit.toeicrise.dtos.requests.testset.TestSetRequest;
import com.hcmute.fit.toeicrise.dtos.requests.testset.UpdateTestSetRequest;
import com.hcmute.fit.toeicrise.services.interfaces.ITestSetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
                                            @RequestParam(required = false) ETestSetStatus status,
                                            @RequestParam(defaultValue = "0")
                                                @Min(value = 0) int page,
                                            @RequestParam(defaultValue = "10")
                                                @Min(value = 1) @Max(value = 100) int size,
                                            @RequestParam(defaultValue = "updatedAt") String sortBy,
                                            @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(testSetService.getAllTestSets(name, status, page, size, sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTestSetDetailById(@PathVariable Long id,
                                                  @RequestParam(required = false) String name,
                                                  @RequestParam(required = false) ETestStatus status,
                                                  @RequestParam(defaultValue = "0")
                                                      @Min(value = 0) int page,
                                                  @RequestParam(defaultValue = "10")
                                                      @Min(value = 1) @Max(value = 100) int size,
                                                  @RequestParam(defaultValue = "updatedAt") String sortBy,
                                                  @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(testSetService.getTestSetDetailById(id, name, status, page, size, sortBy, direction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTestSetById(@PathVariable Long id) {
        testSetService.deleteTestSetById(id);
        return ResponseEntity.ok(MessageConstant.TEST_SET_DELETED_SUCCESS);
    }

    @PostMapping("")
    public ResponseEntity<?> createTestSet(@Valid @RequestBody TestSetRequest testSetRequest) {
        testSetService.addTestSet(testSetRequest);
        return ResponseEntity.ok(MessageConstant.TEST_SET_CREATED_SUCCESS);
    }

    @PutMapping("")
    public ResponseEntity<?> updateTestSet(@Valid @RequestBody UpdateTestSetRequest updateTestSetRequest) {
        return ResponseEntity.ok(testSetService.updateTestSet(updateTestSetRequest));
    }
}
