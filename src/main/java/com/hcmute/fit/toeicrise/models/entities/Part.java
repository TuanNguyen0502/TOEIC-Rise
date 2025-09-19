package com.hcmute.fit.toeicrise.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "parts")
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Part extends BaseEntity {
    @Column(nullable = false, unique = true, length = 100)
    private String name;
}
