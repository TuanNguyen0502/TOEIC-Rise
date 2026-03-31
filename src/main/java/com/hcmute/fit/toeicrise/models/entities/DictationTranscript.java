package com.hcmute.fit.toeicrise.models.entities;

import com.hcmute.fit.toeicrise.commons.utils.StringListJsonConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "dictation_transcripts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictationTranscript extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String questionText;

    @Column(columnDefinition = "json")
    @Convert(converter = StringListJsonConverter.class)
    private List<String> options;

    @Column(columnDefinition = "TEXT")
    private String passageText;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_group_id", nullable = false, unique = true)
    private QuestionGroup questionGroup;

}
