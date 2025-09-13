package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.services.interfaces.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    @Async
    public void sendVerificationEmail(Account account) {
        Context context = new Context();
        // Set variables for the template from the POST request data
        String subject = "Account Verification";
        context.setVariable("subject", subject);
        context.setVariable("verificationCode", account.getVerificationCode());

        try {
            sendEmail(account.getEmail(), subject, "emailTemplate", context);
        } catch (MessagingException e) {
            // Handle email sending exception
            e.printStackTrace();
        }
    }

    private void sendEmail(String to, String subject, String template, Context context) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        // Process the template with the given context
        String htmlContent = templateEngine.process(template, context);

        // Set email properties
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // Set true for HTML content

        // Send the email
        mailSender.send(mimeMessage);
    }
}
