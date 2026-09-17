package com.bookstore.dto;

public record AuthResponse(
        String token,
        Long userId,
        String name,
        String email
) {
}
