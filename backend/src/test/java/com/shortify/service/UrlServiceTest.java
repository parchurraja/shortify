package com.shortify.service;

import com.shortify.dto.request.UrlCreateRequest;
import com.shortify.dto.response.UrlResponse;
import com.shortify.entity.Role;
import com.shortify.entity.Url;
import com.shortify.entity.User;
import com.shortify.exception.DuplicateResourceException;
import com.shortify.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.shortify.config.AppProperties;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private com.shortify.monitoring.MetricsService metricsService;

    @Mock
    private AppProperties appProperties;

    @InjectMocks
    private UrlService urlService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(appProperties.getBaseUrl()).thenReturn("https://shortify-backend-eubz.onrender.com");
        user = User.builder()
                .id(1L)
                .name("Alice")
                .email("alice@example.com")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    @DisplayName("Should create short URL successfully with generated code")
    void testCreateShortUrlSuccess() {
        UrlCreateRequest request = UrlCreateRequest.builder()
                .originalUrl("https://example.com/very/long/path")
                .build();

        Url savedUrl = Url.builder()
                .id(100L)
                .user(user)
                .originalUrl(request.getOriginalUrl())
                .shortCode("aB12cD")
                .clickCount(0L)
                .isActive(true)
                .build();

        when(urlRepository.existsByShortCode(any())).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenReturn(savedUrl);

        UrlResponse response = urlService.createShortUrl(request, user);

        assertNotNull(response);
        assertEquals("aB12cD", response.getShortCode());
        assertEquals("https://example.com/very/long/path", response.getOriginalUrl());
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when custom alias is taken")
    void testCreateShortUrlTakenAlias() {
        UrlCreateRequest request = UrlCreateRequest.builder()
                .originalUrl("https://example.com")
                .customAlias("takenAlias")
                .build();

        when(urlRepository.existsByShortCode("takenAlias")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> urlService.createShortUrl(request, user));
        verify(urlRepository, never()).save(any());
    }
}
