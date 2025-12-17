package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.dtos.responses.statistic.RegSourceInsightResponse;
import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.models.enums.EAuthProvider;
import com.hcmute.fit.toeicrise.models.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);

    Optional<Account> findByRefreshToken(String refreshToken);

    @Query("SELECT COUNT (a) FROM Account a WHERE a.user.role.name =:role AND a.updatedAt >= :from AND a.updatedAt < :to")
    Long countByRole_NameBetweenDays(@Param("role") ERole role, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT new com.hcmute.fit.toeicrise.dtos.responses.statistic.RegSourceInsightResponse(" +
            "COALESCE(SUM(CASE WHEN a.authProvider = :local THEN 1 ELSE 0 END), 0)," +
            "COALESCE(SUM(CASE WHEN a.authProvider = :google THEN 1 ELSE 0 END), 0)) " +
            "FROM Account a " +
            "WHERE a.user.role.name = :role AND a.createdAt >= :start AND a.createdAt < :end")
    RegSourceInsightResponse countSourceInsight(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("role") ERole role,
                                                @Param("local")EAuthProvider local, @Param("google") EAuthProvider google);
}
