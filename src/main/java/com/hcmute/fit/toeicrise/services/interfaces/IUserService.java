package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.ProfileUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.ProfileResponse;

public interface IUserService {
    ProfileResponse getUserProfileByEmail(String email);

    void updateUserProfile(String email, ProfileUpdateRequest request);
}
