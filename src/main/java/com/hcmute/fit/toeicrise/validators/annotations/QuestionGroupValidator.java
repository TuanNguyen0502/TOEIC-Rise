package com.hcmute.fit.toeicrise.validators.annotations;

import com.hcmute.fit.toeicrise.validators.constraints.QuestionGroupValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = QuestionGroupValidation.class)
@Documented
public @interface QuestionGroupValidator {
    String message() default "Invalid question group";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}