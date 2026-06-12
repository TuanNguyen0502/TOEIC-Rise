package com.hcmute.fit.toeicrise.controllers.staff;

import com.hcmute.fit.toeicrise.services.interfaces.IPartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/staff/parts")
@RequiredArgsConstructor
public class PartController {
    private final IPartService partService;

    @GetMapping()
    public ResponseEntity<List<String>> getAllPartNames() {
        return ResponseEntity.ok(partService.getAllPartNames());
    }
}
