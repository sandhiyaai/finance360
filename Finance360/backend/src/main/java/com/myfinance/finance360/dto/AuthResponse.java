package com.myfinance.finance360.dto;

public record AuthResponse(
        String token,
        String email,
        String name
) {}