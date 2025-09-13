package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.LoginRequest;
import com.hcmute.fit.toeicrise.dtos.requests.RegisterRequest;
import com.hcmute.fit.toeicrise.dtos.requests.VerifyUserRequest;
import com.hcmute.fit.toeicrise.models.entities.Account;

public interface IAuthenticationService {
    boolean register(RegisterRequest input);

    Account authenticate(LoginRequest input);

    void verifyUser(VerifyUserRequest input);

    void resendVerificationCode(String email);
}
