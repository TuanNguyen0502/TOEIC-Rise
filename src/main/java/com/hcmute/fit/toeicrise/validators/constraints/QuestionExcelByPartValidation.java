package com.hcmute.fit.toeicrise.validators.constraints;

import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionExcelRequest;
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

        if (value.getTags() == null || value.getTags().isEmpty()) {
            context.buildConstraintViolationWithTemplate("Tags are required for Part " +
                            value.getPartNumber() + "at row " + value.getIndexRow())
                    .addPropertyNode("tag").addConstraintViolation();
            valid = false;
        }

        switch (value.getPartNumber()) {
            case 3, 4, 5, 7 -> {
                if (value.getQuestion() == null || value.getQuestion().isEmpty()) {
                    context.buildConstraintViolationWithTemplate("Question is required for Part " +
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
        }
        return valid;
    }
}
