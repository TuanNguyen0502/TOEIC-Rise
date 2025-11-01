package com.hcmute.fit.toeicrise.models.entities;

import com.hcmute.fit.toeicrise.models.enums.ETestStatus;
import jakarta.persistence.*;
import lombok.*;

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

    private Long numberOfLearnerTests;

    @ManyToOne
    @JoinColumn(name = "test_set_id")
    private TestSet testSet;
}
