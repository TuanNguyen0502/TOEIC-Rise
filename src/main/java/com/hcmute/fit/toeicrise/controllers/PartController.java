package com.hcmute.fit.toeicrise.controllers;

import com.hcmute.fit.toeicrise.services.interfaces.IPartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/parts")
@RequiredArgsConstructor
public class PartController {
    private final IPartService partService;

    @GetMapping()
    public ResponseEntity<?> getParts() {
        return ResponseEntity.ok(partService.getAllParts());
    }
}