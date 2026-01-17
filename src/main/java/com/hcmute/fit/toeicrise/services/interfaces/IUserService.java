package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.user.ProfileUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.user.UserCreateRequest;
import com.hcmute.fit.toeicrise.dtos.requests.user.UserResetPasswordRequest;
import com.hcmute.fit.toeicrise.dtos.requests.user.UserUpdateRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.user.ProfileResponse;
import com.hcmute.fit.toeicrise.dtos.responses.user.UserDetailResponse;
import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.ERole;

import java.time.LocalDateTime;
import java.util.Optional;

public interface IUserService {
    PageResponse getAllUsers(String email, Boolean isActive, ERole role, int page, int size, String sortBy, String direction);
    UserDetailResponse getUserDetailById(Long userId);
    ProfileResponse getUserProfileByEmail(String email);
    void updateUserProfile(String email, ProfileUpdateRequest request);
    void createUser(UserCreateRequest request);
    void updateUser(Long id, UserUpdateRequest request);
    void resetPassword(Long userId, UserResetPasswordRequest request);
    void changeAccountStatus(Long userId);
    Long countAllUsers();
    User createUserWithGoogle(String avatar, String fullName, Account account);
    User createUser(Account account, String fullName);
    Optional<User> findAccountById(Long id);
    Long countAllUsersWithRole(ERole role);
    Long countUsersBetweenDays(LocalDateTime from, LocalDateTime to);
}
