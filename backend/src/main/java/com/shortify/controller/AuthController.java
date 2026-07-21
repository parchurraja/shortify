package com.shortify.controller;

import com.shortify.dto.request.LoginRequest;
import com.shortify.dto.request.RegisterRequest;
import com.shortify.dto.request.TokenRefreshRequest;
import com.shortify.dto.response.ApiResponse;
import com.shortify.dto.response.AuthResponse;
import com.shortify.dto.response.TokenRefreshResponse;
import com.shortify.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpServletRequest) {
        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", response, httpServletRequest.getRequestURI()));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpServletRequest) {
        AuthResponse response = authService.login(request);
        return ResponseEntity
                .ok(ApiResponse.success("Login successful", response, httpServletRequest.getRequestURI()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refresh(
            @Valid @RequestBody TokenRefreshRequest request,
            HttpServletRequest httpServletRequest) {
        TokenRefreshResponse response = authService.refresh(request);
        return ResponseEntity
                .ok(ApiResponse.success("Token refreshed successfully", response, httpServletRequest.getRequestURI()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            Principal principal,
            HttpServletRequest httpServletRequest) {
        if (principal != null) {
            authService.logout(principal.getName());
        }
        return ResponseEntity
                .ok(ApiResponse.success("Logout successful", null, httpServletRequest.getRequestURI()));
    }
}
