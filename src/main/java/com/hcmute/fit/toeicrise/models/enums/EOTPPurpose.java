package com.hcmute.fit.toeicrise.models.enums;

import lombok.Getter;

@Getter
public enum EOTPPurpose {
    REGISTRATION("Registration"),
    FORGOT_PASSWORD("Forgot password"),
    VERIFY_ACCOUNT("Verify Account");

    private final String name;

    EOTPPurpose(String name) {
        this.name = name;
    }
}
