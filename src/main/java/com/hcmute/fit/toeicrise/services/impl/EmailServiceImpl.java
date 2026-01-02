package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.services.interfaces.IEmailService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {
    private final SendGrid sendGrid;
    private final TemplateEngine templateEngine;

    @Value("${sendgrid.from.email:thihoangduyendo@gmail.com}")
    private String fromEmail;

    @Value("${sendgrid.from.name:TOEIC Rise}")
    private String fromName;

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
        } catch (IOException e) {
            // Handle email sending exception
            e.printStackTrace();
        }
    }

    private void sendEmail(String to, String subject, String template, Context context) throws IOException {
        // Process the template with the given context
        String htmlContent = templateEngine.process(template, context);

        Email from = new Email(fromEmail, fromName);
        Email toEmail = new Email(to);
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, toEmail, content);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sendGrid.api(request);
        if (response.getStatusCode() <= 200 || response.getStatusCode() > 300)
            throw new AppException(ErrorCode.MAIL_SEND_FAILED);
    }
}
