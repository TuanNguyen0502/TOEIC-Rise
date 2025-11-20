package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.commons.constants.Constant;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.learner.LearnerTestPartsResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.LearnerTestResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.PartResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.TestDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.test.TestResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Test;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import org.mapstruct.*;
import org.apache.poi.ss.usermodel.*;

import java.util.*;

@Mapper(componentModel = "spring", uses = {PartMapper.class})
public interface TestMapper {
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double value = cell.getNumericCellValue();
                    if (value == Math.floor(value)) {
                        return String.valueOf((long) value);
                    }
                    return String.valueOf(value);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BLANK:
            default:
                return null;
        }
    }

    private static Integer getCellValueAsInteger(Cell cell) {
        String value = getCellValueAsString(cell);
        if (value == null) return null;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null; // hoặc throw exception tùy bạn muốn validate
        }
    }

    @Mapping(source = "createdAt", target = "createdAt", dateFormat = Constant.DATE_TIME_PATTERN)
    @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = Constant.DATE_TIME_PATTERN)
    TestResponse toResponse(Test test);

    @Mapping(source = "createdAt", target = "createdAt", dateFormat = Constant.DATE_TIME_PATTERN)
    @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = Constant.DATE_TIME_PATTERN)
    TestDetailResponse toDetailResponse(Test test, @Context List<PartResponse> partResponses);

    @Mapping(source = "name", target = "testName")
    @Mapping(target = "partResponses", ignore = true)
    LearnerTestPartsResponse toLearnerTestPartsResponse(Test test);

    default LearnerTestDetailResponse toLearnerTestDetailResponse(List<Object[]> objects, @Context PartMapper partMapper) {
        if (objects == null || objects.isEmpty()) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Test");
        }
        Object[] object = objects.getFirst();
        return LearnerTestDetailResponse.builder()
                .testId(((Number) object[0]).longValue())
                .testName((String) object[1])
                .numberOfLearnedTests(((Number) object[2]).longValue())
                .learnerPartResponses(objects.stream().map(partMapper::mapToLearnerPartResponse).toList())
                .build();
    }

    @AfterMapping
    default void setPartResponses(@MappingTarget TestDetailResponse dto,
                                  @Context List<PartResponse> partResponses) {
        dto.setPartResponses(partResponses);
    }

    @Mapping(source = "name", target = "testName")
    @Mapping(source = "testSet.name", target = "testSetName")
    LearnerTestResponse toLearnerTestResponse(Test test);

    default QuestionExcelRequest mapRowToDTO(Row row) {
        QuestionExcelRequest request = new QuestionExcelRequest();

        // Các field số
        request.setPartNumber(getCellValueAsInteger(row.getCell(0)));
        request.setQuestionGroupId(getCellValueAsString(row.getCell(1)));
        request.setNumberOfQuestions(getCellValueAsInteger(row.getCell(2)));

        // Các field text
        request.setPassageText(getCellValueAsString(row.getCell(3)));
        request.setQuestion(getCellValueAsString(row.getCell(4)));
        request.setOptionA(getCellValueAsString(row.getCell(5)));
        request.setOptionB(getCellValueAsString(row.getCell(6)));
        request.setOptionC(getCellValueAsString(row.getCell(7)));
        request.setOptionD(getCellValueAsString(row.getCell(8)));
        request.setCorrectAnswer(getCellValueAsString(row.getCell(9)));
        request.setAudioUrl(getCellValueAsString(row.getCell(10)));
        request.setImageUrl(getCellValueAsString(row.getCell(11)));
        request.setTags(getCellValueAsString(row.getCell(12)));
        request.setExplanation(getCellValueAsString(row.getCell(13)));
        request.setTranscript(getCellValueAsString(row.getCell(14)));

        return request;
    }
}