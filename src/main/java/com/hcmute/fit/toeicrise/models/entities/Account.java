package com.hcmute.fit.toeicrise.models.entities;

import com.hcmute.fit.toeicrise.models.enums.EAuthProvider;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "accounts")
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account extends BaseEntity implements UserDetails {
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false)
    private EAuthProvider authProvider;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isActive;

    @Column(name = "verification_code", columnDefinition = "VARCHAR(255)")
    private String verificationCode;

    @Column(name = "verification_code_expires_at")
    private LocalDateTime verificationCodeExpiresAt;

    @Column(name = "failed_login_attempts", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer failedLoginAttempts = 0;

    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;

    @Column(name = "resend_verification_attempts", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer resendVerificationAttempts = 0;

    @Column(name = "resend_verification_locked_until")
    private LocalDateTime resendVerificationLockedUntil;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "refresh_token_expiry_date")
    private Instant refreshTokenExpiryDate;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, optional = false)
    private User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }

    //TODO: add proper boolean checks
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountLockedUntil == null || LocalDateTime.now().isAfter(accountLockedUntil);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
