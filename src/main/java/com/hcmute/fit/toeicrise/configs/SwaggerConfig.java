package com.hcmute.fit.toeicrise.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI getOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                    .title("TOEIC Rise API")
                        .version("1.0")
                        .description("API documentation for TOEIC Rise application. "
                                + "This API provides endpoints for authentication, "
                                + "user management, tests, questions, and chat features.")
                        .contact(new Contact()
                                .name("TOEIC Rise Team")))
                .components(new Components()
                        .addSecuritySchemes("JWT", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                                .name("Authorization")
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("JWT"));
    }
}
