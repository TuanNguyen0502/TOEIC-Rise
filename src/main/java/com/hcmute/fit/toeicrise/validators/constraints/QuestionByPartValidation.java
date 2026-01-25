package com.hcmute.fit.toeicrise.validators.constraints;

import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionRequest;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionGroupService;
import com.hcmute.fit.toeicrise.validators.annotations.ValidQuestionByPart;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QuestionByPartValidation implements ConstraintValidator<ValidQuestionByPart, QuestionRequest> {
    private final IQuestionGroupService questionGroupService;

    @Override
    public boolean isValid(QuestionRequest value, ConstraintValidatorContext context) {
        QuestionGroup questionGroup = questionGroupService.getQuestionGroupEntity(value.getQuestionGroupId());
        context.disableDefaultConstraintViolation();
        boolean valid = true;
        switch ((int) questionGroup.getPart().getId().longValue()) {
            case 3, 4, 5, 7 -> {
                if (value.getContent() == null || value.getContent().isEmpty()) {
                    context.buildConstraintViolationWithTemplate("Content is required for Part " +
                                    questionGroup.getPart().getId())
                            .addPropertyNode("question").addConstraintViolation();
                    valid = false;
                }
                if (value.getOptions() == null || value.getOptions().isEmpty()) {
                    context.buildConstraintViolationWithTemplate("Options are required for Part " +
                                    questionGroup.getPart().getId())
                            .addPropertyNode("option").addConstraintViolation();
                    valid = false;
                }
            }
            case 6 -> {
                if (value.getOptions() == null) {
                    context.buildConstraintViolationWithTemplate("Options are required for Part " +
                                    questionGroup.getPart().getId())
                            .addPropertyNode("option").addConstraintViolation();
                    valid = false;
                }
            }
        }
        return valid;
    }
}