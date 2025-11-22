package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.report.QuestionReportRequest;
import com.hcmute.fit.toeicrise.dtos.responses.report.QuestionReportDetailResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.QuestionReport;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.EQuestionReportStatus;
import com.hcmute.fit.toeicrise.models.enums.ERole;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.QuestionReportMapper;
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
    private final QuestionReportMapper questionReportMapper;

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

    @Override
    public QuestionReportDetailResponse getReportDetail(String email, Long reportId) {
        QuestionReport questionReport = questionReportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question Report"));
        checkStaffPermission(email, questionReport);
        return questionReportMapper.toQuestionReportDetailResponse(questionReport);
    }

    private void checkStaffPermission(String email, QuestionReport questionReport) {
        User currentUser = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
        // If the staff, they can only access reports assigned to them and still pending
        if (currentUser.getRole().getName().equals(ERole.STAFF)) {
            if (questionReport.getResolver() != null &&
                    !questionReport.getResolver().getId().equals(currentUser.getId())) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            } else if (!questionReport.getStatus().equals(EQuestionReportStatus.PENDING)) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }
    }
}
