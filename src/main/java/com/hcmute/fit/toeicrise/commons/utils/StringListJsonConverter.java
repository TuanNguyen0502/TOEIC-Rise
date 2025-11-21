package com.hcmute.fit.toeicrise.commons.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Converter
@Slf4j
public class StringListJsonConverter implements AttributeConverter<List<String>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> strings) {
        try {
            return objectMapper.writeValueAsString(strings);
        } catch (Exception e) {
            log.error("Failed to serialize list to JSON for DB column: {}", e.getMessage());
            throw new AppException(ErrorCode.CONVERT_LIST_TO_JSON);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("Failed to deserialize list to JSON for DB column: {}", e.getMessage());
            throw new AppException(ErrorCode.CONVERT_JSON_TO_LIST);
        }
    }
}
