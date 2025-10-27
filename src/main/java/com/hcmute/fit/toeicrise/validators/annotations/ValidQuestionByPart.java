package com.hcmute.fit.toeicrise.validators.annotations;

import com.hcmute.fit.toeicrise.validators.constraints.QuestionByPartValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = QuestionByPartValidation.class)
@Documented
public @interface ValidQuestionByPart {
    String message() default "Invalid question for this question group";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}