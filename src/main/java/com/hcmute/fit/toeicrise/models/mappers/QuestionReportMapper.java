package com.hcmute.fit.toeicrise.models.mappers;

import com.hcmute.fit.toeicrise.dtos.responses.report.QuestionReportDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.report.QuestionReportResponse;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.entities.QuestionReport;
import com.hcmute.fit.toeicrise.models.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionReportMapper {
    default QuestionReportDetailResponse toQuestionReportDetailResponse(QuestionReport questionReport) {
        Question question = questionReport.getQuestion();
        QuestionGroup questionGroup = question.getQuestionGroup();
        User reporter = questionReport.getReporter();
        User resolver = questionReport.getResolver();
        return QuestionReportDetailResponse.builder()
                .questionReportId(questionReport.getId())
                .questionId(question.getId())
                .questionContent(question.getContent())
                .questionOptions(question.getOptions())
                .questionExplanation(question.getExplanation())
                .questionGroupId(questionGroup.getId())
                .questionGroupAudioUrl(questionGroup.getAudioUrl())
                .questionGroupImageUrl(questionGroup.getImageUrl())
                .questionGroupPassage(questionGroup.getPassage())
                .questionGroupTranscript(questionGroup.getTranscript())
                .partName(questionGroup.getPart().getName())
                .reporterId(reporter.getId())
                .reporterFullName(reporter.getFullName())
                .reporterEmail(reporter.getAccount().getEmail())
                .resolverId(resolver != null ? resolver.getId() : null)
                .resolverFullName(resolver != null ? resolver.getFullName() : null)
                .resolverEmail(resolver != null ? resolver.getAccount().getEmail() : null)
                .reasons(questionReport.getReasons())
                .description(questionReport.getDescription())
                .status(questionReport.getStatus())
                .resolvedNote(questionReport.getResolvedNote())
                .build();
    }
    @Mapping(source = "question.questionGroup.test.name", target = "testName")
    @Mapping(source = "reporter.fullName", target = "reporterName")
    @Mapping(source = "resolver.fullName", target = "resolverName")
    QuestionReportResponse toQuestionReportResponse(QuestionReport questionReport);
}
