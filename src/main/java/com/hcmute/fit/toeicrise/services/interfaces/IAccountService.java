package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.responses.statistic.RegSourceInsightResponse;
import com.hcmute.fit.toeicrise.models.entities.Account;

import java.time.LocalDateTime;
import java.util.Optional;

public interface IAccountService {
    Optional<Account> findByEmail(String email);
    Account save(Account account);
    Account findByRefreshToken(String refreshToken);
    Long countByRole_NameBetweenDays(LocalDateTime startDate, LocalDateTime endDate);
    RegSourceInsightResponse countSourceInsight(LocalDateTime from, LocalDateTime to);
    Account createAccountForRegistration(String email, String password);
    void handleFailedLoginAttempt(Account account);
    Account resetFailedLoginAttempts(Account account);
    Account createGoogleAccount(String email);
    void validateRefreshToken(Account account);
    void validatePasswordMatch(String password, String confirmPassword);
    Long countAllUsers();
}
