package com.hcmute.fit.toeicrise.controllers.staff;

import com.hcmute.fit.toeicrise.dtos.requests.cloudinary.CloudinaryImageDeleteRequest;
import com.hcmute.fit.toeicrise.dtos.requests.cloudinary.CloudinaryImageRequest;
import com.hcmute.fit.toeicrise.services.interfaces.ICloudinaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("staffCloudinaryController")
@RequestMapping("/staff/cloudinary")
@RequiredArgsConstructor
public class CloudinaryController {
    private final ICloudinaryService cloudinaryService;

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(@Valid @ModelAttribute CloudinaryImageRequest request) {
        return ResponseEntity.ok(cloudinaryService.uploadImage(request));
    }

    @DeleteMapping("/delete-image")
    public ResponseEntity<?> deleteImage(@Valid @RequestBody CloudinaryImageDeleteRequest request) {
        cloudinaryService.deleteImage(request);
        return ResponseEntity.ok().build();
    }
}
