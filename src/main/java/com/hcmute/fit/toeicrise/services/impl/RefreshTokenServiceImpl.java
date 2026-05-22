package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.responses.authentication.RefreshTokenResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.services.interfaces.IAccountService;
import com.hcmute.fit.toeicrise.services.interfaces.IJwtService;
import com.hcmute.fit.toeicrise.services.interfaces.IRefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements IRefreshTokenService {
    private final IAccountService accountService;
    private final IJwtService jwtService;

    @Value("${security.jwt.refresh-token.expiration}")
    private Long refreshTokenDurationMs;

    @Override
    public RefreshTokenResponse getRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty())
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        Account account = accountService.findByRefreshToken(refreshToken);
        if (account == null)
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        accountService.validateRefreshToken(account);

        String newAccessToken = jwtService.generateToken(account);
        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .accessTokenExpirationTime(jwtService.getExpirationTime())
                .build();
    }

    @Override
    public String createRefreshTokenWithRefreshToken(String refreshToken) {
        Account account = accountService.findByRefreshToken(refreshToken);
        if (account == null)
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        String newRefreshToken = UUID.randomUUID().toString();
        account.setRefreshToken(newRefreshToken);
        account.setRefreshTokenExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        accountService.save(account);
        return newRefreshToken;
    }

    @Override
    public String createRefreshTokenWithEmail(String email) {
        Account account = accountService.findByEmail(email).orElseThrow(
                () -> new AppException(ErrorCode.INVALID_CREDENTIALS)
        );
        String refreshToken = UUID.randomUUID().toString();
        account.setRefreshToken(refreshToken);
        account.setRefreshTokenExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        accountService.save(account);
        return refreshToken;
    }

    @Override
    public long getRefreshTokenDurationMs() {
        return refreshTokenDurationMs;
    }
}
