package com.hcmute.fit.toeicrise.validators.annotations;

import com.hcmute.fit.toeicrise.validators.constraints.NotBlankOrEmptyOptionalValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = NotBlankOrEmptyOptionalValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlankOrEmptyOptional {
    String message() default "{fieldName} is not blank or empty";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String regexp();
    int max() default Integer.MAX_VALUE;
}
