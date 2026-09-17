package com.bookstore.repository;

import com.bookstore.entity.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    Optional<AuthToken> findByTokenAndExpiresAtAfter(
            String token,
            LocalDateTime time
    );

    void deleteByToken(String token);
}
