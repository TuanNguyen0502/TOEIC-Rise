package com.hcmute.fit.toeicrise.controllers.staff;

import com.hcmute.fit.toeicrise.dtos.requests.tag.TagRequest;
import com.hcmute.fit.toeicrise.services.interfaces.ITagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staff/tags")
@RequiredArgsConstructor
public class TagController {

    private final ITagService tagService;

    @GetMapping
    public ResponseEntity<?> getAllTags(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(defaultValue = "") String tagName) {
        return ResponseEntity.ok(tagService.getAllTags(page, size, tagName));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getAllTagsForDashboard(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(defaultValue = "name") String sortBy,
                                                    @RequestParam(defaultValue = "ASC") String direction,
                                                    @RequestParam(defaultValue = "") String tagName) {
        return ResponseEntity.ok(tagService.getAllTagsForDashboard(page, size, sortBy, direction, tagName));
    }

    @PostMapping("")
    public ResponseEntity<?> createTag(@RequestBody TagRequest tagRequest) {
        tagService.createTagIfNotExists(tagRequest);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{tagId}")
    public ResponseEntity<?> updateTag(@PathVariable Long tagId, @RequestBody TagRequest tagRequest) {
        tagService.updateTag(tagId, tagRequest);
        return ResponseEntity.ok().build();
    }
}
