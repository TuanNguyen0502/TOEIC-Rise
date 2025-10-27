package com.hcmute.fit.toeicrise.validators.constraints;

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
        boolean valid = true;
        try {
            EPart part = EPart.getEPart(value.getPart().getName());
            if (part.isRequiredAudio() && isBlank(value.getAudioUrl())){
                context.buildConstraintViolationWithTemplate("Audio is required for " + part.getName())
                        .addPropertyNode("audioUrl")
                        .addConstraintViolation();
                valid = false;
            }
            if (part.isRequiredImage() && isBlank(value.getImageUrl())){
                context.buildConstraintViolationWithTemplate("Image is required for " + part.getName())
                        .addPropertyNode("imageUrl")
                        .addConstraintViolation();
                valid = false;
            }
            if (part.isRequiredPassage() && isBlank(value.getPassage())){
                context.buildConstraintViolationWithTemplate("Passage is required for " + part.getName())
                        .addPropertyNode("passage")
                        .addConstraintViolation();
                valid = false;
            }
        } catch (Exception e) {
            context.buildConstraintViolationWithTemplate("Invalid part name: "+value.getPart().getName())
                    .addPropertyNode("part")
                    .addConstraintViolation();
            valid = false;
        }
        return valid;
    }

    private boolean isBlank(String str){
        return str == null || str.trim().isEmpty();
    }
}