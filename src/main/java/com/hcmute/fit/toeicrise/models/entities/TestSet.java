package com.hcmute.fit.toeicrise.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

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
}
