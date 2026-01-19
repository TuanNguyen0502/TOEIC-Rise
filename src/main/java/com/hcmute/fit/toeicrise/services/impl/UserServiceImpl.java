package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.commons.utils.CloudinaryUtil;
import com.hcmute.fit.toeicrise.commons.utils.SecurityUtils;
import com.hcmute.fit.toeicrise.dtos.requests.user.ProfileUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.user.UserCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.user.UserResetPasswordRequest;
import com.hcmute.fit.toeicrise.dtos.requests.user.UserUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.user.ProfileResponse;
import com.hcmute.fit.toeicrise.dtos.responses.user.UserDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.user.UserResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.EGender;
import com.hcmute.fit.toeicrise.models.enums.ERole;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.models.mappers.UserMapper;
import com.hcmute.fit.toeicrise.repositories.UserRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.UserSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.IAccountService;
import com.hcmute.fit.toeicrise.services.interfaces.IRoleService;
import com.hcmute.fit.toeicrise.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService {
    private final IAccountService accountService;
    private final IRoleService roleService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryUtil cloudinaryUtil;
    private final UserMapper userMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse getAllUsers(String email, Boolean isActive, ERole role, int page, int size, String sortBy, String direction) {
        Specification<User> specification = (_, _, cb) -> cb.conjunction();
        if (email != null && !email.trim().isEmpty())
            specification = specification.and(UserSpecification.emailContains(email));
        if (isActive != null)
            specification = specification.and(UserSpecification.isActiveEquals(isActive));
        if (role != null)
            specification = specification.and(UserSpecification.hasRole(role));

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserResponse> userResponses = userRepository.findAll(specification, pageable).map(userMapper::toUserResponse);
        return pageResponseMapper.toPageResponse(userResponses);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponse getUserDetailById(Long userId) {
        User user = findUserById(userId);
        return userMapper.toUserDetailResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getUserProfileByEmail(String email) {
        return userRepository.findByAccount_Email(email)
                .map(user -> userMapper.toProfileResponse(email, user))
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User"));
    }

    @Override
    @Transactional
    public void updateUserProfile(String email, ProfileUpdateRequest request) {
        log.info("Updating user with email: {}", email);
        User user = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User"));

        uploadImage(request.getAvatar(), user, Constant.PROFILE_AVATAR_MAX_SIZE);
        user.setFullName(request.getFullName());
        user.setGender(request.getGender());
        userRepository.save(user);
        log.info("User updated successfully with email: {}", email);
    }

    private void uploadImage(MultipartFile file, User user, int maxSize) {
        if (file != null && !file.isEmpty()) {
            cloudinaryUtil.handleUploadFile(file, maxSize);
            user.setAvatar(user.getAvatar() == null
                    ? cloudinaryUtil.uploadFile(file)
                    : cloudinaryUtil.updateFile(file, user.getAvatar()));
        }
    }

    @Override
    @Transactional
    public void createUser(UserCreateRequest request) {
        log.info("Creating user with email: {}", request.getEmail());
        if (accountService.findByEmail(request.getEmail()).isPresent())
            throw new AppException(ErrorCode.DUPLICATE_EMAIL);
        accountService.validatePasswordMatch(request.getPassword(), request.getConfirmPassword());

        Account account = accountService.createAccountForRegistration(request.getEmail(), request.getPassword());
        User user = createUser(account, request.getFullName(), request.getRole(), request.getGender());
        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            cloudinaryUtil.handleUploadFile(request.getAvatar(), Constant.AVATAR_MAX_SIZE);
            user.setAvatar(cloudinaryUtil.uploadFile(request.getAvatar()));
        }

        account.setUser(user);
        accountService.save(account);
        log.info("User created successfully with email: {}", request.getEmail());
    }

    @Override
    @Transactional
    public void updateUser(Long userId, UserUpdateRequest request) {
        log.info("Updating user with id: {}", userId);
        User user = findUserById(userId);
        Account account = user.getAccount();

        account.setIsActive(request.isActive());
        user.setRole(roleService.findByName(request.getRole()));
        user.setFullName(request.getFullName());
        user.setGender(request.getGender());

        uploadImage(request.getAvatar(), user, Constant.AVATAR_MAX_SIZE);
        accountService.save(account);
        log.info("User updated successfully with id: {}", userId);
    }

    @Override
    @Transactional
    public void resetPassword(Long userId, UserResetPasswordRequest request) {
        log.info("Resetting password for user ID: {}", userId);
        accountService.validatePasswordMatch(request.getPassword(), request.getConfirmPassword());

        User user = findUserById(userId);
        Account account = user.getAccount();
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        accountService.save(account);
        log.info("Password reset successfully for user ID: {}", userId);
    }

    @Override
    @Transactional
    public void changeAccountStatus(Long userId) {
        User user = findUserById(userId);
        if (user.getRole().getName().equals(ERole.ADMIN))
            throw new AppException(ErrorCode.INVALID_REQUEST, "Cannot change status of ADMIN");
        if (user.getAccount().getEmail().equals(SecurityUtils.getCurrentUser()))
            throw new AppException(ErrorCode.INVALID_REQUEST, "Cannot change your own account status");

        user.getAccount().setIsActive(!user.getAccount().getIsActive());
        userRepository.save(user);
        log.info("Account status changed to {} for user ID: {}", user.getAccount().getIsActive(), userId);
    }

    @Override
    public User createUserWithGoogle(String avatar, String fullName, Account account) {
        return User.builder()
                .avatar(avatar)
                .fullName(fullName)
                .role(roleService.findByName(ERole.LEARNER))
                .account(account)
                .build();
    }

    @Override
    public User createUser(Account account, String fullName, ERole roleName, EGender gender) {
        return User.builder()
                .role(roleService.findByName(roleName))
                .account(account)
                .fullName(fullName)
                .gender(gender)
                .build();
    }

    @Override
    public Optional<User> findAccountById(Long id) {
        return userRepository.findAccountById(id);
    }

    @Override
    public Long countAllUsersWithRole(ERole role) {
        return userRepository.countByRole_Name(role);
    }

    @Override
    public Long countUsersBetweenDays(LocalDateTime from, LocalDateTime to) {
        return userRepository.countByRole_NameBetweenDays(ERole.LEARNER, from, to);
    }

    private User findUserById(Long id) {
        return userRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User"));
    }
}
