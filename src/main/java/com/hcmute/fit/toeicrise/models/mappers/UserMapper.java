package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.CurrentUserResponse;
import com.hcmute.fit.toeicrise.dtos.responses.ProfileResponse;
import com.hcmute.fit.toeicrise.models.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "account.email", target = "email")
    @Mapping(target = "role", expression = "java(user.getRole().getName().name())")
    @Mapping(target = "hasPassword", expression = "java(user.getAccount().getPassword() != null && !user.getAccount().getPassword().isEmpty())")
    CurrentUserResponse toCurrentUserResponse(User user);

    default ProfileResponse toProfileResponse(String email, User user) {
        return ProfileResponse.builder()
                .userId(user.getId())
                .email(email)
                .fullName(user.getFullName())
                .gender(user.getGender().name())
                .avatar(user.getAvatar())
                .build();
    }
}
