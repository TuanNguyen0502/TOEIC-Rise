package com.hcmute.fit.toeicrise.services.interfaces;

import jakarta.mail.MessagingException;

public interface IEmailService {
    void sendVerificationEmail(String to, String subject, String text) throws MessagingException;
}
