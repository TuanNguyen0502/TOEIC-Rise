package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.models.entities.Account;

public interface IEmailService {
    void sendVerificationEmail(Account account);
}
