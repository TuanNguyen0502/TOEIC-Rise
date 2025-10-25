package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.ProfileUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.UserCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.UserUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.ProfileResponse;
import com.hcmute.fit.toeicrise.models.enums.ERole;

public interface IUserService {
    PageResponse getAllUsers(String email, Boolean isActive, ERole role, int page, int size, String sortBy, String direction);

    ProfileResponse getUserProfileByEmail(String email);

    void updateUserProfile(String email, ProfileUpdateRequest request);

    void createUser(UserCreateRequest request);

    void updateUser(Long id, UserUpdateRequest request);

    void changeAccountStatus(Long userId);
}
