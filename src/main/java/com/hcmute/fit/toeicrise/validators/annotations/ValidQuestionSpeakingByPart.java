package com.hcmute.fit.toeicrise.validators.annotations;

import com.hcmute.fit.toeicrise.validators.constraints.SpeakingQuestionExcelValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SpeakingQuestionExcelValidation.class)
@Documented
public @interface ValidQuestionSpeakingByPart {
    String message() default "Invalid speaking question data";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] extendsPayload() default {};
}
