package com.bookstore.online_bookstore.service;

import com.bookstore.dto.AuthResponse;
import com.bookstore.dto.LoginRequest;
import com.bookstore.dto.RegisterRequest;
import com.bookstore.entity.AppUser;
import com.bookstore.repository.AppUserRepository;
import com.bookstore.security.TokenService;
import com.bookstore.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUser() {

        RegisterRequest request =
                new RegisterRequest(
                        "John",
                        "john@gmail.com",
                        "password123"
                );

        when(userRepository.existsByEmail("john@gmail.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("hashed-password");

        AppUser savedUser = new AppUser();
        savedUser.setId(1L);
        savedUser.setName("John");
        savedUser.setEmail("john@gmail.com");
        savedUser.setPassword("hashed-password");

        when(userRepository.save(any(AppUser.class)))
                .thenReturn(savedUser);

        when(tokenService.createToken(savedUser))
                .thenReturn("test-token");

        AuthResponse response =
                authService.register(request);

        assertEquals("test-token", response.token());
        assertEquals(1L, response.userId());
        assertEquals("John", response.name());
        assertEquals("john@gmail.com", response.email());

        verify(userRepository).save(any(AppUser.class));
        verify(passwordEncoder).encode("password123");
        verify(tokenService).createToken(savedUser);
    }

    @Test
    void shouldRejectDuplicateEmail() {

        RegisterRequest request =
                new RegisterRequest(
                        "John",
                        "john@gmail.com",
                        "password123"
                );

        when(userRepository.existsByEmail("john@gmail.com"))
                .thenReturn(true);

        assertThrows(
                RuntimeException.class,
                () -> authService.register(request)
        );

        verify(userRepository, never())
                .save(any(AppUser.class));
    }

    @Test
    void shouldLoginSuccessfully() {

        AppUser user = new AppUser();

        user.setId(1L);
        user.setName("John");
        user.setEmail("john@gmail.com");
        user.setPassword("hashed-password");

        LoginRequest request =
                new LoginRequest(
                        "john@gmail.com",
                        "password123"
                );

        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password123",
                "hashed-password"
        )).thenReturn(true);

        when(tokenService.createToken(user))
                .thenReturn("test-token");

        AuthResponse response =
                authService.login(request);

        assertEquals("test-token", response.token());
        assertEquals("john@gmail.com", response.email());
    }

    @Test
    void shouldRejectInvalidPassword() {

        AppUser user = new AppUser();

        user.setEmail("john@gmail.com");
        user.setPassword("hashed-password");

        LoginRequest request =
                new LoginRequest(
                        "john@gmail.com",
                        "wrong-password"
                );

        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrong-password",
                "hashed-password"
        )).thenReturn(false);

        assertThrows(
                RuntimeException.class,
                () -> authService.login(request)
        );

        verify(tokenService, never())
                .createToken(any());
    }
}
