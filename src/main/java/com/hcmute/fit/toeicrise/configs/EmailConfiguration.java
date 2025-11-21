package com.hcmute.fit.toeicrise.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfiguration {
    @Value("${spring.mail.username}")
    private String emailUsername;

    @Value("${spring.mail.password}")
    private String emailPassword;

    @Value("${spring.mail.host}")
    private String emailHost;

    @Value("${spring.mail.port}")
    private int emailPort;

    @Value("${spring.mail.properties.mail.transport.protocol}")
    private String emailProtocol;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private boolean emailStarttlsEnable;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean emailSmtpAuth;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailHost);
        mailSender.setPort(emailPort);
        mailSender.setUsername(emailUsername);
        mailSender.setPassword(emailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", emailProtocol);
        props.put("mail.smtp.auth", String.valueOf(emailSmtpAuth));
        props.put("mail.smtp.starttls.enable", String.valueOf(emailStarttlsEnable));

        return mailSender;
    }
}
