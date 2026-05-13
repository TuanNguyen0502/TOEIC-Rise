package com.hcmute.fit.toeicrise.models.entities;

import com.hcmute.fit.toeicrise.commons.utils.EPartListJsonConverter;
import com.hcmute.fit.toeicrise.models.enums.EPart;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import com.hcmute.fit.toeicrise.models.enums.ETestType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tests")
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Test extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ETestStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ETestType type;

    @Column(name = "number_of_learner_tests")
    private Long numberOfLearnerTests;

    @ManyToOne
    @JoinColumn(name = "test_set_id")
    @ToString.Exclude
    private TestSet testSet;

    @Column(name = "dictation_status", columnDefinition = "json NOT NULL DEFAULT (JSON_ARRAY())")
    @Convert(converter = EPartListJsonConverter.class)
    @Builder.Default
    @ToString.Exclude
    private List<EPart> dictationStatus = new ArrayList<>();

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<QuestionGroup> questionGroups;
}
