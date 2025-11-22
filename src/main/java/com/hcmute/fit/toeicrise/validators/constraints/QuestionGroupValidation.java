package com.hcmute.fit.toeicrise.validators.constraints;

import com.hcmute.fit.toeicrise.commons.utils.ValidationUtils;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.enums.EPart;
import com.hcmute.fit.toeicrise.validators.annotations.QuestionGroupValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class QuestionGroupValidation implements ConstraintValidator<QuestionGroupValidator, QuestionGroup> {

    @Override
    public boolean isValid(QuestionGroup value, ConstraintValidatorContext context) {
        if (value == null || value.getPart() == null)
            return true;
        context.disableDefaultConstraintViolation();
        EPart part;
        try {
            part = EPart.getEPart(value.getPart().getName());
        } catch (AppException e) {
            ValidationUtils.addViolation(context, "Invalid part name: "+ value.getPart().getName(), "part");
            return false;
        }
        boolean valid = true;
        if (!validateRequiredAudio(context, value, part))
            valid = false;
        if (!validateRequiredImage(context, value, part))
            valid = false;
        if (!validateRequiredPassage(context, value, part))
            valid = false;
        return valid;
    }

    private boolean validateRequiredAudio(ConstraintValidatorContext context, QuestionGroup value, EPart part) {
        if (!part.isRequiredAudio())
            return true;
        if (isBlank(value.getAudioUrl())){
            ValidationUtils.addViolation(context, "Audio is required for " + part.getName(), "audioUrl");
            return false;
        }
        return true;
    }

    private boolean validateRequiredImage(ConstraintValidatorContext context, QuestionGroup value, EPart part) {
        if (!part.isRequiredImage())
            return true;
        if (isBlank(value.getImageUrl())){
            ValidationUtils.addViolation(context, "Image is required for " + part.getName(), "imageUrl");
            return false;
        }
        return true;
    }

    private boolean validateRequiredPassage(ConstraintValidatorContext context, QuestionGroup value, EPart part) {
        if (!part.isRequiredPassage())
            return true;
        if (isBlank(value.getPassage())){
            ValidationUtils.addViolation(context, "Passage is required for " + part.getName(), "passage");
            return false;
        }
        return true;
    }

    private boolean isBlank(String str){
        return str == null || str.trim().isEmpty();
    }
}