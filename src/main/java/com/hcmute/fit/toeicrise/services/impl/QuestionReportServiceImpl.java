package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.report.QuestionReportRequest;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.QuestionReport;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.EQuestionReportStatus;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.repositories.QuestionReportRepository;
import com.hcmute.fit.toeicrise.repositories.QuestionRepository;
import com.hcmute.fit.toeicrise.repositories.UserRepository;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionReportServiceImpl implements IQuestionReportService {
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final QuestionReportRepository questionReportRepository;

    @Override
    public void createReport(String email, QuestionReportRequest questionReportRequest) {
        User reporter = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
        Question question = questionRepository.findById(questionReportRequest.getQuestionId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question"));

        QuestionReport questionReport = new QuestionReport();
        questionReport.setQuestion(question);
        questionReport.setReporter(reporter);
        questionReport.setReasons(questionReportRequest.getReasons());
        questionReport.setDescription(questionReportRequest.getDescription());
        questionReport.setStatus(EQuestionReportStatus.PENDING);
        questionReportRepository.save(questionReport);
    }
}
