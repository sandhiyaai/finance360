package com.myfinance.finance360.controller;

import com.myfinance.finance360.dto.AuthResponse;
import com.myfinance.finance360.dto.LoginRequest;
import com.myfinance.finance360.dto.RegisterRequest;
import com.myfinance.finance360.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /auth/register
     * Body: { "name": "John", "email": "john@example.com", "password": "secret123" }
     * Returns: { "token": "eyJ...", "email": "john@example.com", "name": "John" }
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * POST /auth/login
     * Body: { "email": "john@example.com", "password": "secret123" }
     * Returns: { "token": "eyJ...", "email": "john@example.com", "name": "John" }
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}