package com.hcmute.fit.toeicrise.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import sendinblue.ApiClient;
import sendinblue.auth.ApiKeyAuth;
import sendinblue.Configuration;
import sibApi.TransactionalEmailsApi;

@org.springframework.context.annotation.Configuration
public class BrevoConfig {
    @Value("${brevo.api.key}")
    private String apiKey;

    @Bean
    public TransactionalEmailsApi transactionalEmailsApi() {
        ApiClient defaultClient = Configuration.getDefaultApiClient();

        // Cấu hình API Key xác thực
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKeyAuth.setApiKey(apiKey);

        return new TransactionalEmailsApi();
    }
}
