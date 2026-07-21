package com.shortify.service;

import com.shortify.dto.response.AnalyticsDashboardResponse;
import com.shortify.entity.Role;
import com.shortify.entity.Url;
import com.shortify.entity.UrlClick;
import com.shortify.entity.User;
import com.shortify.repository.UrlClickRepository;
import com.shortify.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AnalyticsServiceTest {

    @Mock
    private UrlClickRepository urlClickRepository;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlService urlService;

    @InjectMocks
    private AnalyticsService analyticsService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(1L)
                .name("Bob")
                .email("bob@example.com")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    @DisplayName("Should successfully log click asynchronously")
    void testLogClickAsync() {
        Url url = Url.builder()
                .id(10L)
                .shortCode("xYz123")
                .clickCount(5L)
                .build();

        when(urlRepository.findById(10L)).thenReturn(Optional.of(url));

        analyticsService.logClickAsync(10L, "Mozilla/5.0 (Windows NT 10.0)", "127.0.0.1");

        verify(urlClickRepository, times(1)).save(any(UrlClick.class));
        verify(urlRepository, times(1)).save(url);
        assertEquals(6L, url.getClickCount());
    }

    @Test
    @DisplayName("Should aggregate dashboard data for user")
    void testGetDashboardData() {
        when(urlRepository.countByUserAndDeletedAtIsNull(user)).thenReturn(5L);
        when(urlClickRepository.countAllClicksByUser(user)).thenReturn(42L);
        when(urlClickRepository.countClicksSinceByUser(any(), any())).thenReturn(10L);
        when(urlRepository.findByUserAndDeletedAtIsNull(any(User.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        when(urlClickRepository.getClicksPastWeekByUser(any(), any())).thenReturn(Collections.emptyList());
        when(urlClickRepository.getDeviceStatsByUser(any())).thenReturn(Collections.emptyList());
        when(urlClickRepository.getOsStatsByUser(any())).thenReturn(Collections.emptyList());
        when(urlClickRepository.getBrowserStatsByUser(any())).thenReturn(Collections.emptyList());

        AnalyticsDashboardResponse response = analyticsService.getDashboardData(user);

        assertNotNull(response);
        assertEquals(5L, response.getTotalUrls());
        assertEquals(42L, response.getTotalClicks());
        assertEquals(10L, response.getClicksToday());
    }
}
