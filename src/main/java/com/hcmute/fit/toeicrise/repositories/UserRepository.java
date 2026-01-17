package com.hcmute.fit.toeicrise.repositories;

import com.hcmute.fit.toeicrise.models.entities.User;
import com.hcmute.fit.toeicrise.models.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @Query("SELECT u FROM User u JOIN FETCH u.account JOIN FETCH u.role WHERE u.account.id = :accountId")
    Optional<User> findByAccount_Id(Long accountId);

    @Query("SELECT u FROM User u JOIN FETCH u.account JOIN FETCH u.role WHERE u.account.email = :email")
    Optional<User> findByAccount_Email(@Param("email") String email);

    Long countByRole_Name(ERole role);

    @Query("SELECT COUNT (u) FROM User u WHERE u.role.name =:role AND u.createdAt >= :from AND u.createdAt < :to")
    Long countByRole_NameBetweenDays(@Param("role") ERole role, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
