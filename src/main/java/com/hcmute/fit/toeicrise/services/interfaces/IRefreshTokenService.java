package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.models.entities.RefreshToken;

public interface IRefreshTokenService {
    RefreshToken createRefreshToken(String email);

    RefreshToken findByToken(String token);

    RefreshToken verifyExpiration(RefreshToken token);
}
