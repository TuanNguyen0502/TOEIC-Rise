package com.hcmute.fit.toeicrise.validators.constraints;

import com.hcmute.fit.toeicrise.validators.annotations.NotBlankOrEmptyOptional;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class NotBlankOrEmptyOptionalValidator implements ConstraintValidator<NotBlankOrEmptyOptional, String> {
    private Pattern pattern;
    private int maxValue;

    @Override
    public void initialize(NotBlankOrEmptyOptional constraintAnnotation) {
        this.pattern = Pattern.compile(constraintAnnotation.regexp());
        this.maxValue = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        if (value.length() > maxValue) {
            return false;
        }
        return pattern.matcher(value).matches();
    }
}
