package com.hcmute.fit.toeicrise.models.entities;

import com.hcmute.fit.toeicrise.commons.utils.EPartListJsonConverter;
import com.hcmute.fit.toeicrise.models.enums.EPart;
import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
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

    @Column(name = "number_of_learner_tests")
    private Long numberOfLearnerTests;

    @ManyToOne
    @JoinColumn(name = "test_set_id")
    private TestSet testSet;

    @Column(name = "dictation_status", columnDefinition = "json")
    @Convert(converter = EPartListJsonConverter.class)
    private List<EPart> dictationStatus = new ArrayList<>();

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionGroup> questionGroups;
}
