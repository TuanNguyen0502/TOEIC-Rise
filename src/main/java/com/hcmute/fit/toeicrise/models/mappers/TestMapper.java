package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.requests.QuestionExcelRequest;
import org.apache.poi.ss.usermodel.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TestMapper {

    default QuestionExcelRequest mapRowToDTO(Row row) {
        QuestionExcelRequest request = new QuestionExcelRequest();

        // Các field số
        request.setPartNumber(getCellValueAsInteger(row.getCell(0)));
        request.setQuestionGroupId(getCellValueAsInteger(row.getCell(1)));
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
}
