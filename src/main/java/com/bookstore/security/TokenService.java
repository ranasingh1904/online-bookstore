package com.bookstore.security;

import com.bookstore.entity.AppUser;
import com.bookstore.entity.AuthToken;
import com.bookstore.repository.AuthTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TokenService {

    private final AuthTokenRepository tokenRepository;

    public TokenService(AuthTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String createToken(AppUser user) {

        String token = UUID.randomUUID().toString();

        AuthToken authToken = new AuthToken(
                token,
                user,
                LocalDateTime.now().plusHours(24)
        );

        tokenRepository.save(authToken);

        return token;
    }

    public AppUser findUserByToken(String token) {

        return tokenRepository
                .findByTokenAndExpiresAtAfter(
                        token,
                        LocalDateTime.now()
                )
                .map(AuthToken::getUser)
                .orElse(null);
    }

    public void deleteToken(String token) {
        tokenRepository.deleteByToken(token);
    }
}
