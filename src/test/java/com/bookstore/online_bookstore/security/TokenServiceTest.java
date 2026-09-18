package com.bookstore.online_bookstore.security;

import com.bookstore.entity.AppUser;
import com.bookstore.entity.AuthToken;
import com.bookstore.repository.AuthTokenRepository;
import com.bookstore.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private AuthTokenRepository tokenRepository;

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService(tokenRepository);
    }

    @Test
    void createToken_shouldCreateAndSaveToken() {
        AppUser user = mock(AppUser.class);

        AuthToken savedToken = mock(AuthToken.class);
        when(tokenRepository.save(any(AuthToken.class))).thenReturn(savedToken);

        String result = tokenService.createToken(user);

        assertNotNull(result);
        assertFalse(result.isBlank());

        ArgumentCaptor<AuthToken> captor =
                ArgumentCaptor.forClass(AuthToken.class);

        verify(tokenRepository, times(1)).save(captor.capture());

        AuthToken capturedToken = captor.getValue();

        assertNotNull(capturedToken);
    }

    @Test
    void findUserByToken_shouldReturnUser_whenTokenIsValid() {
        String token = "test-token";
        AppUser user = mock(AppUser.class);
        AuthToken authToken = mock(AuthToken.class);

        when(authToken.getUser()).thenReturn(user);

        when(tokenRepository.findByTokenAndExpiresAtAfter(
                eq(token),
                any(LocalDateTime.class)
        )).thenReturn(Optional.of(authToken));

        AppUser result = tokenService.findUserByToken(token);

        assertNotNull(result);
        assertSame(user, result);

        verify(tokenRepository, times(1))
                .findByTokenAndExpiresAtAfter(
                        eq(token),
                        any(LocalDateTime.class)
                );

        verify(authToken, times(1)).getUser();
    }

    @Test
    void findUserByToken_shouldReturnNull_whenTokenIsInvalidOrExpired() {
        String token = "invalid-token";

        when(tokenRepository.findByTokenAndExpiresAtAfter(
                eq(token),
                any(LocalDateTime.class)
        )).thenReturn(Optional.empty());

        AppUser result = tokenService.findUserByToken(token);

        assertNull(result);

        verify(tokenRepository, times(1))
                .findByTokenAndExpiresAtAfter(
                        eq(token),
                        any(LocalDateTime.class)
                );
    }

    @Test
    void deleteToken_shouldDeleteToken() {
        String token = "test-token";

        tokenService.deleteToken(token);

        verify(tokenRepository, times(1))
                .deleteByToken(token);
    }
}
