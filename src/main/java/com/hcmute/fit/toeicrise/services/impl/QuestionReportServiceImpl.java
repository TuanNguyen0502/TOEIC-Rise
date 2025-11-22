package com.hcmute.fit.toeicrise.services.impl;

import com.hcmute.fit.toeicrise.dtos.requests.report.QuestionReportRequest;
import com.hcmute.fit.toeicrise.dtos.requests.report.QuestionReportResolveRequest;
import com.hcmute.fit.toeicrise.dtos.responses.PageResponse;
import com.hcmute.fit.toeicrise.dtos.responses.report.QuestionReportDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.report.QuestionReportResponse;
import com.hcmute.fit.toeicrise.exceptions.AppException;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.models.entities.QuestionReport;
import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.EQuestionReportStatus;
import com.hcmute.fit.toeicrise.models.enums.ERole;
import com.hcmute.fit.toeicrise.models.enums.ErrorCode;
import com.hcmute.fit.toeicrise.models.mappers.PageResponseMapper;
import com.hcmute.fit.toeicrise.models.mappers.QuestionReportMapper;
import com.hcmute.fit.toeicrise.repositories.QuestionReportRepository;
import com.hcmute.fit.toeicrise.repositories.UserRepository;
import com.hcmute.fit.toeicrise.repositories.specifications.QuestionReportSpecification;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionGroupService;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionReportService;
import com.hcmute.fit.toeicrise.services.interfaces.IQuestionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionReportServiceImpl implements IQuestionReportService {
    private final UserRepository userRepository;
    private final QuestionReportRepository questionReportRepository;
    private final IQuestionService questionService;
    private final IQuestionGroupService questionGroupService;
    private final QuestionReportMapper questionReportMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    public void createReport(String email, QuestionReportRequest questionReportRequest) {
        User reporter = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
        Question question = questionService.findById(questionReportRequest.getQuestionId())
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
        User currentUser = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
        QuestionReport questionReport = questionReportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question Report"));
        checkStaffPermission(currentUser, questionReport);
        return questionReportMapper.toQuestionReportDetailResponse(questionReport);
    }

    @Override
    public PageResponse getAllReports(EQuestionReportStatus status, int page, int size) {
        Specification<QuestionReport> specification = (_, _, cb) -> cb.conjunction();
        if (status != null) {
            specification = specification.and(QuestionReportSpecification.hasStatus(status));
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<QuestionReportResponse> questionReports = questionReportRepository.findAll(specification, pageable).map(questionReportMapper::toQuestionReportResponse);
        return pageResponseMapper.toPageResponse(questionReports);
    }

    @Override
    public PageResponse getAllReports(int page, int size) {
        Specification<QuestionReport> specification = (_, _, cb) -> cb.conjunction();
        specification = specification.and(QuestionReportSpecification.hasStatus(EQuestionReportStatus.PENDING));
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<QuestionReportResponse> questionReports = questionReportRepository.findAll(specification, pageable).map(questionReportMapper::toQuestionReportResponse);
        return pageResponseMapper.toPageResponse(questionReports);
    }

    @Transactional
    @Override
    public void resolveReport(String email, Long reportId, QuestionReportResolveRequest request) {
        User resolver = userRepository.findByAccount_Email(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
        QuestionReport questionReport = questionReportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Question Report"));
        Question question = questionReport.getQuestion();
        QuestionGroup questionGroup = question.getQuestionGroup();

        checkStaffPermission(resolver, questionReport);

        if (request.getQuestionUpdate() != null) {
            questionService.updateQuestion(question, request.getQuestionUpdate());
        }
        if (request.getQuestionGroupUpdate() != null) {
            questionGroupService.updateQuestionGroupWithEntity(questionGroup, request.getQuestionGroupUpdate());
        }
        questionReport.setResolver(resolver);
        questionReport.setResolvedNote(request.getResolvedNote());
        questionReport.setStatus(request.getStatus());
        questionReportRepository.save(questionReport);
    }

    private void checkStaffPermission(User currentUser, QuestionReport questionReport) {
        // If the staff, they can only access the report assigned to them or the pending reports
        if (currentUser.getRole().getName().equals(ERole.STAFF)) {
            if (questionReport.getResolver() != null) {
                if (!questionReport.getResolver().getId().equals(currentUser.getId())) {
                    throw new AppException(ErrorCode.UNAUTHORIZED);
                }
            } else if (questionReport.getStatus() != EQuestionReportStatus.PENDING) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }
    }
}
