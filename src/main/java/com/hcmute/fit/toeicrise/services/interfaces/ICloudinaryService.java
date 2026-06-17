package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.cloudinary.AudioDeleteRequest;
import com.hcmute.fit.toeicrise.dtos.requests.cloudinary.AudioSavingRequest;
import com.hcmute.fit.toeicrise.dtos.requests.cloudinary.CloudinaryImageDeleteRequest;
import com.hcmute.fit.toeicrise.dtos.requests.cloudinary.CloudinaryImageRequest;
import org.springframework.web.multipart.MultipartFile;

public interface ICloudinaryService {
    String processMediaFile(MultipartFile newFile, String newUrl, String oldUrl);

    String uploadImage(CloudinaryImageRequest request);

    void deleteImage(CloudinaryImageDeleteRequest request);

    String uploadAudio(AudioSavingRequest request);

    void deleteAudio(AudioDeleteRequest request);
}