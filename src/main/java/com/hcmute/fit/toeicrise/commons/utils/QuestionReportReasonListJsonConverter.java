package com.hcmute.fit.toeicrise.commons.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.fit.toeicrise.models.enums.EQuestionReportReason;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;

@Converter
public class QuestionReportReasonListJsonConverter implements AttributeConverter<List<EQuestionReportReason>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<EQuestionReportReason> reasons) {
        try {
            return objectMapper.writeValueAsString(reasons);
        } catch (Exception e) {
            return "[]";
        }
    }

    @Override
    public List<EQuestionReportReason> convertToEntityAttribute(String dbData) {
        try {
            // First try to read as enum list
            return objectMapper.readValue(dbData, new TypeReference<>() {
            });
        } catch (Exception e1) {
            try {
                // If that fails, try to read as string list and convert to enums
                List<String> stringReasons = objectMapper.readValue(dbData, new TypeReference<>() {
                });
                List<EQuestionReportReason> enumReasons = new ArrayList<>();
                for (String reason : stringReasons) {
                    try {
                        enumReasons.add(EQuestionReportReason.valueOf(reason));
                    } catch (IllegalArgumentException e2) {
                        // Skip invalid enum values
                    }
                }
                return enumReasons;
            } catch (Exception e3) {
                return new ArrayList<>();
            }
        }
    }
}
