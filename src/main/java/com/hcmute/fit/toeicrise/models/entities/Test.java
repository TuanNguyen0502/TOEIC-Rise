package com.hcmute.fit.toeicrise.models.entities;

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
    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "test_set_id")
    private TestSet testSet;
}
