package com.hcmute.fit.toeicrise.validators.constraints;

import com.hcmute.fit.toeicrise.commons.constants.MessageConstant;
import com.hcmute.fit.toeicrise.commons.utils.ValidationUtils;
import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionRequest;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.enums.EPart;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionGroupService;
import com.hcmute.fit.toeicrise.validators.annotations.ValidQuestionByPart;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuestionByPartValidation implements ConstraintValidator<ValidQuestionByPart, QuestionRequest> {
    private final IQuestionGroupService questionGroupService;

    @Override
    public boolean isValid(QuestionRequest value, ConstraintValidatorContext context) {
        QuestionGroup questionGroup = questionGroupService.getQuestionGroup(value.getQuestionGroupId());
        context.disableDefaultConstraintViolation();
        if (questionGroup == null) {
            ValidationUtils.addViolation(context, "Question group " + ErrorCode.RESOURCE_NOT_FOUND.getMessage(), " questionGroupId");
            return false;
        }
        if (questionGroup.getPart() == null || questionGroup.getPart().getId() == null) {
            ValidationUtils.addViolation(context, MessageConstant.PART_NOT_BLANK, "part");
            return false;
        }
        EPart part;
        try {
            String partName = questionGroup.getPart().getName();
            part = EPart.getEPart(partName);
        } catch (AppException _){
            ValidationUtils.addViolation(context, "Invalid part name: "+ questionGroup.getPart().getName(), " part");
            return false;
        }
        return switch (part){
            case PART_3, PART_4, PART_5, PART_7 -> validateContentAndOptions(value, context, part);
            case PART_6 -> validateOptionsOnly(value, context, part);
            default -> true;
        };
    }

    private boolean validateContentAndOptions(QuestionRequest value, ConstraintValidatorContext context, EPart part) {
        boolean valid = true;
         if (value.getContent() == null || value.getContent().isEmpty()) {
             ValidationUtils.addViolation(context, "Content is required for " + part.getName(), "content");
             valid = false;
         }
         if (value.getOptions() == null || value.getOptions().isEmpty()) {
             ValidationUtils.addViolation(context, "Options are required for " + part.getName(), "option");
             valid = false;
         }
         return valid;
    }

    private boolean validateOptionsOnly(QuestionRequest value, ConstraintValidatorContext context, EPart part) {
        boolean valid = true;
        if (value.getOptions() == null) {
            ValidationUtils.addViolation(context, "Options are required for " + part.getName(), "option");
            valid = false;
        }
        return valid;
    }
}