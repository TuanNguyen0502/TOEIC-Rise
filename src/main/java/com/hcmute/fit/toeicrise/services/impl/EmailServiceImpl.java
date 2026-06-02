package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.services.interfaces.IEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import sibApi.TransactionalEmailsApi;
import sibModel.CreateSmtpEmail;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements IEmailService {
    private final TemplateEngine templateEngine;
    private final TransactionalEmailsApi apiInstance;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    @Async
    @Override
    public void sendVerificationEmail(Account account) {
        try {
            // 1. Cấu hình người gửi (Sender)
            SendSmtpEmailSender sender = new SendSmtpEmailSender();
            sender.setEmail(senderEmail);
            sender.setName(senderName);

            // 2. Cấu hình người nhận (To)
            SendSmtpEmailTo toReceiver = new SendSmtpEmailTo();
            toReceiver.setEmail(account.getEmail()); // Email lấy từ object Account của bạn
            List<SendSmtpEmailTo> toList = new ArrayList<>();
            toList.add(toReceiver);

            // 3. Nội dung Email (Có thể dùng HTML)
            Context context = new Context();
            // Set variables for the template from the POST request data
            String subject = "Account Verification";
            context.setVariable("subject", subject);
            context.setVariable("verificationCode", account.getVerificationCode());
            String htmlContent = templateEngine.process("emailTemplate", context);

            // 4. Tạo Object Mail tổng hợp
            SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
            sendSmtpEmail.setSender(sender);
            sendSmtpEmail.setTo(toList);
            sendSmtpEmail.setHtmlContent(htmlContent);
            sendSmtpEmail.setSubject("[TOEIC Rise] Xác Thực Tài Khoản Của Bạn");

            // 5. Thực hiện gọi HTTP API gửi mail đi
            CreateSmtpEmail response = apiInstance.sendTransacEmail(sendSmtpEmail);
            log.info("Email verification sent successfully to {}. Response: {}", account.getEmail(), response);
        } catch (Exception e) {
            log.error("Lỗi gửi mail qua Brevo API: ", e);
        }
    }
}
