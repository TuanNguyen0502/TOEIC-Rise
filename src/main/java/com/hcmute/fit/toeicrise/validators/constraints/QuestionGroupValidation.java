package com.hcmute.fit.toeicrise.validators.constraints;

import com.hcmute.fit.toeicrise.commons.utils.ValidationUtils;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.enums.EPart;
import com.hcmute.fit.toeicrise.validators.annotations.QuestionGroupValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.function.Function;
import java.util.function.Predicate;

public class QuestionGroupValidation implements ConstraintValidator<QuestionGroupValidator, QuestionGroup> {

    @Override
    public boolean isValid(QuestionGroup value, ConstraintValidatorContext context) {
        if (value == null || value.getPart() == null)
            return true;
        context.disableDefaultConstraintViolation();
        EPart part;
        try {
            part = EPart.getEPart(value.getPart().getName());
        } catch (AppException _) {
            ValidationUtils.addViolation(context, "Invalid part name: "+ value.getPart().getName(), "part");
            return false;
        }
        return validateRequiredField(context, value, part, EPart::isRequiredAudio,
                QuestionGroup::getAudioUrl, "Audio", "audioUrl")
                && validateRequiredField(context, value, part, EPart::isRequiredImage,
                QuestionGroup::getImageUrl, "Image", "imageUrl")
                && validateRequiredField(context, value, part, EPart::isRequiredPassage,
                QuestionGroup::getPassage, "Passage", "passage");
    }

    private boolean validateRequiredField(ConstraintValidatorContext context, QuestionGroup value, EPart part,
                                          Predicate<EPart> isRequiredField,
                                          Function<QuestionGroup, String> getFieldValue,
                                          String fieldName, String fieldPath) {
        if (!isRequiredField.test(part))
            return true;
        if (isBlank(getFieldValue.apply(value))) {
            ValidationUtils.addViolation(context, "Field " + fieldName + " is required for " + part.getName(), fieldPath);
            return false;
        }
        return true;
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}