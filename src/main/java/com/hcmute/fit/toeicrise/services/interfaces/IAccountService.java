package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.authentication.RegisterRequest;
import com.hcmute.fit.toeicrise.models.entities.Account;

import java.time.LocalDateTime;

public interface IAccountService {
    Account createLocalAccount(RegisterRequest registerRequest);
    Account createGoogleAccount(String email, String fullName, String avatar);
    Account findByEmail(String email);
    Account saveAccount(Account account);
    Account findByRefreshToken(String refreshToken);
    boolean existsByEmail(String email);
    void registerFailedLoginAttempt(Account account);
    Account resetLoginAttempts(Account account);
    boolean isBefore(Account account, LocalDateTime time);
    void increaseResendAttempt(Account account);
    void setNewOtp(Account account);
    Account createRefreshToken(Account account);
    Long getRefreshTokenDurationMs();
}
