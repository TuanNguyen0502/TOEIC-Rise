package com.hcmute.fit.toeicrise.validators;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ValidationFields {
    private final Validator validator;

    public void validateAndThrow(Object target,
                                 String objectName,
                                 Class<?> controllerClass,
                                 String methodName,
                                 Class<?>... parameterTypes) throws Exception {
        Set<ConstraintViolation<Object>> violationSet = validator.validate(target);
        if (!violationSet.isEmpty()) {
            BindingResult bindingResult = new BeanPropertyBindingResult(target, objectName);
            for (ConstraintViolation<Object> constraintViolation : violationSet) {
                String fieldName = constraintViolation.getPropertyPath().toString();
                bindingResult.addError(new FieldError(objectName, fieldName, constraintViolation.getMessage()));
            }
            Method method = controllerClass.getDeclaredMethod(methodName, parameterTypes);
            throw new MethodArgumentNotValidException(new MethodParameter(method,0), bindingResult);
        }
    }
}