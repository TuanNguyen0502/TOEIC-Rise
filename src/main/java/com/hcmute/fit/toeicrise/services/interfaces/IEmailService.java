package com.hcmute.fit.toeicrise.services.interfaces;

import jakarta.mail.MessagingException;
import org.thymeleaf.context.Context;

public interface IEmailService {
    void sendEmail(String to, String subject, String template, Context context) throws MessagingException;
}
