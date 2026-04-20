package com.hcmute.fit.toeicrise.models.entities;

import com.hcmute.fit.toeicrise.models.enums.ETestSetStatus;
import com.hcmute.fit.toeicrise.models.enums.ETestSetType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "test_sets")
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestSet extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ETestSetStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ETestSetType type;
  
    @OneToMany(mappedBy = "testSet")
    @Builder.Default
    private List<Test> tests = new ArrayList<>();
}
