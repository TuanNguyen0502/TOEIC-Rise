package com.hcmute.fit.toeicrise.controllers.admin;

import com.hcmute.fit.toeicrise.dtos.requests.cloudinary.CloudinarySignRequest;
import com.hcmute.fit.toeicrise.services.interfaces.ICloudinaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/admin/cloudinary")
@RequiredArgsConstructor
public class CloudinaryUploadController {
    private final ICloudinaryService cloudinaryService;

    @PostMapping("/sign-video-upload")
    public ResponseEntity<?> signVideoUpload(@Valid @RequestBody(required = false) CloudinarySignRequest request) {
        return ResponseEntity.ok(cloudinaryService.getSignature(new HashMap<>(), request));
    }
}
