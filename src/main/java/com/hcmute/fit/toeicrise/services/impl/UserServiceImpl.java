package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.utils.CloudinaryUtil;
import com.hcmute.fit.toeicrise.dtos.requests.ProfileUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.ProfileResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.UserMapper;
import com.hcmute.fit.toeicrise.repositories.UserRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CloudinaryUtil cloudinaryUtil;

    @Override
    public ProfileResponse getUserProfileByEmail(String email) {
        return userRepository.findByAccount_Email(email)
                .map(user -> userMapper.toProfileResponse(email, user))
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
    }

    @Override
    public void updateUserProfile(String email, ProfileUpdateRequest request) {
        User user = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
        // Update avatar if provided
        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            // Check if the current avatar is the default one
            String avatarUrl = cloudinaryUtil.getDefaultAvatarUrl().equals(user.getAvatar()) // is default avatar
                    ? cloudinaryUtil.uploadFile(request.getAvatar()) // upload new avatar
                    : cloudinaryUtil.updateFile(request.getAvatar(), user.getAvatar()); // upload new avatar and delete old one
            user.setAvatar(avatarUrl);
        }
        user.setFullName(request.getFullName());
        user.setGender(request.getGender());
        userRepository.save(user);
    }
}
