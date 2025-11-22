package com.hcmute.fit.toeicrise.commons.utils;

import jakarta.validation.ConstraintValidatorContext;

public class ValidationUtils {
    public static void addViolation(ConstraintValidatorContext context, String message, String property) {
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(property).addConstraintViolation();
    }
}
