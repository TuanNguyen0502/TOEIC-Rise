package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.models.entities.Account;
import org.springframework.scheduling.annotation.Async;

public interface IEmailService {
    @Async
    void sendVerificationEmail(Account account);
}
