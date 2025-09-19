package com.hcmute.fit.toeicrise.models.entities;

import com.hcmute.fit.toeicrise.commons.utils.StringListJsonConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_tests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "test_id")
    private Test test;

    @Convert(converter = StringListJsonConverter.class)
    @Column(columnDefinition = "json")
    private List<String> parts; // store scores per part in JSON

    @Column(name = "score")
    private Integer score;

    @Column(name = "time_spent")
    private Integer timeSpent; // in seconds

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
