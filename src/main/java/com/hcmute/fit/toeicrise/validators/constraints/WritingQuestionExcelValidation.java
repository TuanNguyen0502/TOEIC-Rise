package com.hcmute.fit.toeicrise.validators.constraints;

import com.hcmute.fit.toeicrise.dtos.requests.question.WritingQuestionExcelRequest;
import com.hcmute.fit.toeicrise.models.enums.EPart;
import com.hcmute.fit.toeicrise.validators.annotations.ValidQuestionWritingByPart;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class WritingQuestionExcelValidation implements ConstraintValidator<ValidQuestionWritingByPart, WritingQuestionExcelRequest> {

    @Override
    public boolean isValid(WritingQuestionExcelRequest value, ConstraintValidatorContext context) {
        if (value == null)
            return true;

        context.disableDefaultConstraintViolation();
        boolean valid = true;

        try {
            EPart part = EPart.valueOf("WRITING_PART_" + value.getPartNumber());

            if (part.isRequiredImage() && isBlank(value.getImageUrl())) {
                context.buildConstraintViolationWithTemplate("Image is required for Writing Part " + value.getPartNumber() + " at row " + value.getIndexRow())
                        .addPropertyNode("imageUrl").addConstraintViolation();
                valid = false;
            }
            if (part.isRequiredPassage() && isBlank(value.getPassageText())) {
                context.buildConstraintViolationWithTemplate("Passage/Essay prompt is required for Writing Part " + value.getPartNumber() + " at row " + value.getIndexRow())
                        .addPropertyNode("passage").addConstraintViolation();
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
