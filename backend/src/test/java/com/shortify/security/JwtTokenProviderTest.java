package com.shortify.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtUtils jwtUtils;
    private final String secret = "abcdefghijklmnopqrstuvwxyz1234567890SecretKeyWith32Bytes";
    private final long expirationMs = 3600000;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(secret, expirationMs);
    }

    @Test
    @DisplayName("Should generate valid JWT token containing correct email subject")
    void testGenerateAndValidateToken() {
        String email = "test@shortify.com";
        String token = jwtUtils.generateToken(email);

        assertNotNull(token);
        assertTrue(jwtUtils.validateToken(token));
        assertEquals(email, jwtUtils.getEmailFromToken(token));
    }

    @Test
    @DisplayName("Should return false for invalid or tampered JWT token")
    void testInvalidTokenValidation() {
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.invalid.token";
        assertFalse(jwtUtils.validateToken(invalidToken));
    }
}
