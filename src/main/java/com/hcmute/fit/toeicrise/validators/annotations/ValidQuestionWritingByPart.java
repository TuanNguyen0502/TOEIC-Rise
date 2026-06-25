package com.hcmute.fit.toeicrise.validators.annotations;

import com.hcmute.fit.toeicrise.validators.constraints.WritingQuestionExcelValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = WritingQuestionExcelValidation.class)
@Documented
public @interface ValidQuestionWritingByPart {
    String message() default "Invalid speaking question data";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
