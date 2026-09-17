package com.bookstore.service;

import com.bookstore.dto.AuthResponse;
import com.bookstore.dto.LoginRequest;
import com.bookstore.dto.RegisterRequest;
import com.bookstore.entity.AppUser;
import com.bookstore.exception.BadRequestException;
import com.bookstore.repository.AppUserRepository;
import com.bookstore.security.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException(
                    "Email is already registered"
            );
        }

        AppUser user = new AppUser();

        user.setName(request.name());
        user.setEmail(request.email().toLowerCase());
        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        user = userRepository.save(user);

        String token = tokenService.createToken(user);

        return new AuthResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public AuthResponse login(LoginRequest request) {

        AppUser user = userRepository
                .findByEmail(request.email().toLowerCase())
                .orElseThrow(() ->
                        new BadRequestException(
                                "Invalid email or password"
                        )
                );

        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )) {
            throw new BadRequestException(
                    "Invalid email or password"
            );
        }

        String token = tokenService.createToken(user);

        return new AuthResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}

