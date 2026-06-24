package com.hcmute.fit.toeicrise.validators.constraints;

import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.models.enums.EPart;
import com.hcmute.fit.toeicrise.validators.annotations.ValidQuestionByPart;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QuestionExcelByPartValidation implements ConstraintValidator<ValidQuestionByPart, QuestionExcelRequest> {

    @Override
    public boolean isValid(QuestionExcelRequest value, ConstraintValidatorContext context) {
        if (value == null || value.getQuestionGroupId() == null)
            return true;

        context.disableDefaultConstraintViolation();
        boolean valid = true;

        try {
            EPart part = EPart.valueOf("PART_" + value.getPartNumber());

            if (part.isRequiredAudio() && isBlank(value.getAudioUrl())){
                context.buildConstraintViolationWithTemplate("Audio is required for Part " + value.getPartNumber() + " at row " + value.getIndexRow())
                        .addPropertyNode("audioUrl")
                        .addConstraintViolation();
                valid = false;
            }

            if (part.isRequiredImage() && isBlank(value.getImageUrl())){
                context.buildConstraintViolationWithTemplate("Image is required for Part " + value.getPartNumber() + " at row " + value.getIndexRow())
                        .addPropertyNode("imageUrl")
                        .addConstraintViolation();
                valid = false;
            }

            if (part.isRequiredPassage() && isBlank(value.getPassageText())){
                context.buildConstraintViolationWithTemplate("Passage is required for Part " + value.getPartNumber() + " at row " + value.getIndexRow())
                        .addPropertyNode("passage")
                        .addConstraintViolation();
                valid = false;
            }

        } catch (IllegalArgumentException e) {
            context.buildConstraintViolationWithTemplate("Invalid part number: " + value.getPartNumber() + " at row " + value.getIndexRow())
                    .addPropertyNode("partNumber")
                    .addConstraintViolation();
            valid = false;
        }

        switch (value.getPartNumber()) {
            case 3, 4, 5, 7 -> {
                if (value.getQuestion() == null || value.getQuestion().isEmpty()) {
                    context.buildConstraintViolationWithTemplate("Content is required for Part " +
                                    value.getPartNumber() + " at row " + value.getIndexRow())
                            .addPropertyNode("question").addConstraintViolation();
                    valid = false;
                }
                if (value.getOptionA() == null || value.getOptionA().isEmpty() || value.getOptionB() == null ||
                        value.getOptionB().isEmpty()|| value.getOptionC() == null || value.getOptionC().isEmpty() ||
                        value.getOptionD() == null || value.getOptionD().isEmpty()) {
                    context.buildConstraintViolationWithTemplate("Options are required for Part " +
                                    value.getPartNumber() + "at row " + value.getIndexRow())
                            .addPropertyNode("option").addConstraintViolation();
                    valid = false;
                }
            }
            case 6 -> {
                if (value.getQuestion() == null) {
                    context.buildConstraintViolationWithTemplate("Options are required for Part " +
                                    value.getPartNumber() + "at row " + value.getIndexRow())
                            .addPropertyNode("option").addConstraintViolation();
                    valid = false;
                }
            }
        }
        return valid;
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
