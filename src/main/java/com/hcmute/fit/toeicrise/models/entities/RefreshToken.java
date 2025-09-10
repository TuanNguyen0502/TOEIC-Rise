package com.hcmute.fit.toeicrise.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Table(name = "refresh_tokens")
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;
}
