package com.hcmute.fit.toeicrise.controllers.staff;

import com.hcmute.fit.toeicrise.services.interfaces.ITagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/staff/tags")
@RequiredArgsConstructor
public class TagController {
    private final ITagService tagService;

    @GetMapping
    public ResponseEntity<?> getAllTags(@RequestParam(defaultValue = "0") int size,
                                        @RequestParam(defaultValue = "10") int page,
                                        @RequestParam String tagName) {
        return ResponseEntity.ok(tagService.getAllTags(size, page, tagName));
    }
}
