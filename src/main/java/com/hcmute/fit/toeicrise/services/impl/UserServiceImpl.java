package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.utils.CloudinaryUtil;
import com.hcmute.fit.toeicrise.dtos.requests.ProfileUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.UserCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.UserUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.ProfileResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.EAuthProvider;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.UserMapper;
import com.hcmute.fit.toeicrise.repositories.AccountRepository;
import com.hcmute.fit.toeicrise.repositories.RoleRepository;
import com.hcmute.fit.toeicrise.repositories.UserRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
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

        user.setFullName(request.getFullName());
        user.setGender(request.getGender());
        userRepository.save(user);
    }

    @Override
    public void createUser(UserCreateRequest request) {
        // Check for duplicate email
        if (isDuplicateEmail(request.getEmail())) {
            throw new AppException(ErrorCode.DUPLICATE_EMAIL);
        }
        // Validate password and confirm password match
        if (isValidPassword(request.getPassword(), request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        // Create Account entity
        Account account = new Account();
        account.setEmail(request.getEmail());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setIsActive(true);
        account.setAuthProvider(EAuthProvider.LOCAL);
        // Create associated User entity
        User user = new User();
        user.setRole(roleRepository.findByName(request.getRole()));
        user.setAccount(account);
        user.setFullName(request.getFullName());
        user.setGender(request.getGender());
        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            cloudinaryUtil.validateImageFile(request.getAvatar());
            user.setAvatar(cloudinaryUtil.uploadFile(request.getAvatar()));
        } else {
            user.setAvatar(cloudinaryUtil.getDefaultAvatarUrl());
        }

        // Link the User entity to the Account
        account.setUser(user);
        accountRepository.save(account);
    }

    @Override
    public void updateUser(Long id, UserUpdateRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Account"));
        User user = account.getUser();

        // Validate password and confirm password match
        if (isValidPassword(request.getPassword(), request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setIsActive(request.isActive());
        user.setRole(roleRepository.findByName(request.getRole()));
        user.setFullName(request.getFullName());
        user.setGender(request.getGender());
        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            cloudinaryUtil.validateImageFile(request.getAvatar());
            user.setAvatar(user.getAvatar().equals(cloudinaryUtil.getDefaultAvatarUrl())
                    ? cloudinaryUtil.uploadFile(request.getAvatar())
                    : cloudinaryUtil.updateFile(request.getAvatar(), user.getAvatar()));
        }
        accountRepository.save(account);
    }

    private boolean isValidPassword(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    private boolean isDuplicateEmail(String email) {
        return accountRepository.findByEmail(email).isPresent();
    }
}
