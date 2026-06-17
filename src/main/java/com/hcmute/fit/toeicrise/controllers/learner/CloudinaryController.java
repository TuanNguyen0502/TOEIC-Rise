package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.dtos.requests.cloudinary.AudioDeleteRequest;
import com.hcmute.fit.toeicrise.dtos.requests.cloudinary.AudioSavingRequest;
import com.hcmute.fit.toeicrise.services.interfaces.ICloudinaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("learnerCloudinaryController")
@RequestMapping("/learner/cloudinary")
@RequiredArgsConstructor
public class CloudinaryController {
    private final ICloudinaryService cloudinaryService;

    @PostMapping(value = "/upload-audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAudio(@Valid @ModelAttribute AudioSavingRequest request) {
        return ResponseEntity.ok(cloudinaryService.uploadAudio(request));
    }

    @DeleteMapping("/delete-audio")
    public ResponseEntity<?> deleteAudio(@Valid @RequestBody AudioDeleteRequest request) {
        cloudinaryService.deleteAudio(request);
        return ResponseEntity.ok().build();
    }
}
