package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.utils.CloudinaryUtil;
import com.hcmute.fit.toeicrise.dtos.requests.ProfileUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.user.UserCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.user.UserResetPasswordRequest;
import com.hcmute.fit.toeicrise.dtos.requests.user.UserUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.ProfileResponse;
import com.hcmute.fit.toeicrise.dtos.responses.user.UserDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.user.UserResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.EAuthProvider;
import com.hcmute.fit.toeicrise.models.enums.ERole;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.models.mappers.UserMapper;
import com.hcmute.fit.toeicrise.repositories.AccountRepository;
import com.hcmute.fit.toeicrise.repositories.RoleRepository;
import com.hcmute.fit.toeicrise.repositories.UserRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.UserSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryUtil cloudinaryUtil;
    private final UserMapper userMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    public PageResponse getAllUsers(String email, Boolean isActive, ERole role, int page, int size, String sortBy, String direction) {
        Specification<User> specification = (_, _, cb) -> cb.conjunction();
        if (email != null && !email.isEmpty()) {
            specification = specification.and(UserSpecification.emailContains(email));
        }
        if (isActive != null) {
            specification = specification.and(UserSpecification.isActiveEquals(isActive));
        }
        if (role != null) {
            specification = specification.and(UserSpecification.hasRole(role));
        }
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserResponse> userResponses = userRepository.findAll(specification, pageable).map(userMapper::toUserResponse);
        return pageResponseMapper.toPageResponse(userResponses);
    }

    @Override
    public UserDetailResponse getUserDetailById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User"));
        return userMapper.toUserDetailResponse(user);
    }

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
            if (request.getAvatar().getSize() > Constant.PROFILE_AVATAR_MAX_SIZE) {
                throw new AppException(ErrorCode.IMAGE_SIZE_EXCEEDED);
            }
            cloudinaryUtil.validateImageFile(request.getAvatar());
            user.setAvatar(user.getAvatar() == null
                    ? cloudinaryUtil.uploadFile(request.getAvatar()) // upload new avatar
                    : cloudinaryUtil.updateFile(request.getAvatar(), user.getAvatar())); // upload new avatar and delete old one);
        }
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
        if (!request.getPassword().equals(request.getConfirmPassword())) {
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
            if (request.getAvatar().getSize() > Constant.AVATAR_MAX_SIZE) {
                throw new AppException(ErrorCode.IMAGE_SIZE_EXCEEDED);
            }
            cloudinaryUtil.validateImageFile(request.getAvatar());
            user.setAvatar(cloudinaryUtil.uploadFile(request.getAvatar()));
        }

        // Link the User entity to the Account
        account.setUser(user);
        accountRepository.save(account);
    }

    @Override
    public void updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User"));
        Account account = user.getAccount();
        account.setIsActive(request.isActive());
        user.setRole(roleRepository.findByName(request.getRole()));
        user.setFullName(request.getFullName());
        user.setGender(request.getGender());
        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            if (request.getAvatar().getSize() > Constant.AVATAR_MAX_SIZE) {
                throw new AppException(ErrorCode.IMAGE_SIZE_EXCEEDED);
            }
            cloudinaryUtil.validateImageFile(request.getAvatar());
            user.setAvatar(user.getAvatar() == null
                    ? cloudinaryUtil.uploadFile(request.getAvatar())
                    : cloudinaryUtil.updateFile(request.getAvatar(), user.getAvatar()));
        }
        accountRepository.save(account);
    }

    @Override
    public void resetPassword(Long userId, UserResetPasswordRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User"));
        Account account = user.getAccount();
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        accountRepository.save(account);
    }

    @Override
    public void changeAccountStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User"));
        user.getAccount().setIsActive(!user.getAccount().getIsActive());
        userRepository.save(user);
    }

    private boolean isDuplicateEmail(String email) {
        return accountRepository.findByEmail(email).isPresent();
    }
}
