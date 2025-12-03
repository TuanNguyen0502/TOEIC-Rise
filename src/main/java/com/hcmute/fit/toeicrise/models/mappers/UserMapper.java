package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.dtos.requests.authentication.RegisterRequest;
import com.hcmute.fit.toeicrise.dtos.responses.authentication.CurrentUserResponse;
import com.hcmute.fit.toeicrise.dtos.responses.user.ProfileResponse;
import com.hcmute.fit.toeicrise.dtos.responses.user.UserDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.user.UserResponse;
import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.models.entities.Role;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.EGender;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "account.email", target = "email")
    @Mapping(target = "role", expression = "java(user.getRole().getName().name())")
    @Mapping(target = "hasPassword", expression = "java(user.getAccount() != null && !\"{oauth2}\".equals(user.getAccount().getPassword()))")
    CurrentUserResponse toCurrentUserResponse(User user);

    User toUserEntity(String fullName, Role role, Account account, EGender gender);

    default ProfileResponse toProfileResponse(String email, User user) {
        return ProfileResponse.builder()
                .email(email)
                .fullName(user.getFullName())
                .gender(user.getGender())
                .avatar(user.getAvatar())
                .build();
    }

    default UserResponse toUserResponse(User user) {
        Account account = user.getAccount();
        return UserResponse.builder()
                .userId(user.getId())
                .email(account.getEmail())
                .isActive(account.getIsActive())
                .fullName(user.getFullName())
                .avatar(user.getAvatar())
                .role(user.getRole().getName())
                .updatedAt(user.getUpdatedAt().format(DateTimeFormatter.ofPattern(Constant.DATE_TIME_PATTERN)))
                .build();
    }

    default UserDetailResponse toUserDetailResponse(User user) {
        Account account = user.getAccount();
        return UserDetailResponse.builder()
                .userId(user.getId())
                .email(account.getEmail())
                .authProvider(account.getAuthProvider())
                .isActive(account.getIsActive())
                .fullName(user.getFullName())
                .gender(user.getGender())
                .avatar(user.getAvatar())
                .role(user.getRole().getName())
                .createdAt(user.getCreatedAt().format(DateTimeFormatter.ofPattern(Constant.DATE_TIME_PATTERN)))
                .updatedAt(user.getUpdatedAt().format(DateTimeFormatter.ofPattern(Constant.DATE_TIME_PATTERN)))
                .build();
    }
}
