package com.shortify.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shortify.service.RateLimitingService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RateLimitingFilterTest {

    private RateLimitingFilter rateLimitingFilter;

    @Mock
    private RateLimitingService rateLimitingService;

    private MeterRegistry meterRegistry;

    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        meterRegistry = new SimpleMeterRegistry();
        objectMapper = new ObjectMapper();
        rateLimitingFilter = new RateLimitingFilter(rateLimitingService, meterRegistry, objectMapper);

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    @DisplayName("Should pass requests that do not exceed rate limit")
    void testRequestAllowed() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/auth/login");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        when(rateLimitingService.tryConsume(eq("login:127.0.0.1"), eq(5L), any(Duration.class)))
                .thenReturn(true);

        rateLimitingFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    @DisplayName("Should block requests and record metric when rate limit is exceeded")
    void testRequestBlocked() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/auth/login");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        when(rateLimitingService.tryConsume(eq("login:127.0.0.1"), eq(5L), any(Duration.class)))
                .thenReturn(false);

        rateLimitingFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        verify(response).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());

        // Assert response content contains the status and message
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("Too Many Requests"));
        assertTrue(responseContent.contains("Rate limit exceeded"));

        // Assert that the metric counter was incremented
        double hitCount = meterRegistry.counter("shortify_rate_limit_hits_total", 
                "endpoint", "login", 
                "identifier", "127.0.0.1")
                .count();
        assertEquals(1.0, hitCount);
    }

    @Test
    @DisplayName("Should detect short code redirect request path")
    void testShortCodePathCheck() throws ServletException, IOException {
        // GET /abc1234 should be matches as redirect path
        when(request.getRequestURI()).thenReturn("/abc1234");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");

        when(rateLimitingService.tryConsume(eq("redirect:192.168.1.1"), eq(200L), any(Duration.class)))
                .thenReturn(true);

        rateLimitingFilter.doFilterInternal(request, response, filterChain);

        verify(rateLimitingService).tryConsume(eq("redirect:192.168.1.1"), eq(200L), any(Duration.class));
        verify(filterChain).doFilter(request, response);
    }
}
