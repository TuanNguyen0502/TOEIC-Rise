package com.hcmute.fit.toeicrise.controllers.staff;

import com.hcmute.fit.toeicrise.models.enums.ETestSetStatus;
import com.hcmute.fit.toeicrise.models.enums.ETestSetType;
import com.hcmute.fit.toeicrise.services.interfaces.ITestSetService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("staffTestSetController")
@RequestMapping("/staff/test-sets")
@RequiredArgsConstructor
public class TestSetController {
    private final ITestSetService testSetService;

    @GetMapping("/listening-reading")
    public ResponseEntity<?> getAllListeningReadingTestSets(@RequestParam(required = false) String name,
                                                            @RequestParam(required = false) ETestSetStatus status,
                                                            @RequestParam(defaultValue = "0")
                                                            @Min(value = 0) int page,
                                                            @RequestParam(defaultValue = "10")
                                                            @Min(value = 1) @Max(value = 100) int size,
                                                            @RequestParam(defaultValue = "numberOfLearnerTests") String sortBy,
                                                            @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(testSetService.getAllTestSetsByType(ETestSetType.LISTENING_AND_READING, name, status, page, size, sortBy, direction));
    }

    @GetMapping("/speaking")
    public ResponseEntity<?> getAllSpeakingTestSets(@RequestParam(required = false) String name,
                                                    @RequestParam(required = false) ETestSetStatus status,
                                                    @RequestParam(defaultValue = "0")
                                                    @Min(value = 0) int page,
                                                    @RequestParam(defaultValue = "10")
                                                    @Min(value = 1) @Max(value = 100) int size,
                                                    @RequestParam(defaultValue = "numberOfLearnerTests") String sortBy,
                                                    @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(testSetService.getAllTestSetsByType(ETestSetType.SPEAKING, name, status, page, size, sortBy, direction));
    }

    @GetMapping("/writing")
    public ResponseEntity<?> getAllWritingTestSets(@RequestParam(required = false) String name,
                                                   @RequestParam(required = false) ETestSetStatus status,
                                                   @RequestParam(defaultValue = "0")
                                                   @Min(value = 0) int page,
                                                   @RequestParam(defaultValue = "10")
                                                   @Min(value = 1) @Max(value = 100) int size,
                                                   @RequestParam(defaultValue = "numberOfLearnerTests") String sortBy,
                                                   @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(testSetService.getAllTestSetsByType(ETestSetType.WRITING, name, status, page, size, sortBy, direction));
    }
}
