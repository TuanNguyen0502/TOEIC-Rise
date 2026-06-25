package com.hcmute.fit.toeicrise.validators.constraints;

import com.hcmute.fit.toeicrise.dtos.requests.question.SpeakingQuestionExcelRequest;
import com.hcmute.fit.toeicrise.models.enums.EPart;
import com.hcmute.fit.toeicrise.validators.annotations.ValidQuestionSpeakingByPart;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SpeakingQuestionExcelValidation implements ConstraintValidator<ValidQuestionSpeakingByPart, SpeakingQuestionExcelRequest> {

    @Override
    public boolean isValid(SpeakingQuestionExcelRequest value, ConstraintValidatorContext context) {
        if (value == null)
            return true;

        context.disableDefaultConstraintViolation();
        boolean valid = true;

        try {
            EPart part = EPart.valueOf("SPEAKING_PART_" + value.getPartNumber());

            if (part.isRequiredImage() && isBlank(value.getImageUrl())) {
                context.buildConstraintViolationWithTemplate("Image is required for Speaking Part " + value.getPartNumber() + " at row " + value.getIndexRow())
                        .addPropertyNode("imageUrl").addConstraintViolation();
                valid = false;
            }
        } catch (IllegalArgumentException e) {
            context.buildConstraintViolationWithTemplate("Invalid part number: " + value.getPartNumber() + " at row " + value.getIndexRow())
                    .addPropertyNode("partNumber").addConstraintViolation();
            valid = false;
        }
        return valid;
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
