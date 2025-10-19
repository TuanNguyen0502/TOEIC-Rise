package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.UserCreateRequest;

public interface IUserService {
    void createUser(UserCreateRequest request);
}
