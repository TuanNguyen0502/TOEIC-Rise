package com.hcmute.fit.toeicrise.models.entities;

import com.hcmute.fit.toeicrise.commons.utils.QuestionReportReasonListJsonConverter;
import com.hcmute.fit.toeicrise.models.enums.EQuestionReportReason;
import com.hcmute.fit.toeicrise.models.enums.EQuestionReportStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "question_reports")
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionReport extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "resolver_id")
    private User resolver;

    @Convert(converter = QuestionReportReasonListJsonConverter.class)
    @Column(columnDefinition = "json")
    private List<EQuestionReportReason> reasons;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private EQuestionReportStatus status;

    @Column(name = "resolved_note", columnDefinition = "TEXT")
    private String resolvedNote;
}
