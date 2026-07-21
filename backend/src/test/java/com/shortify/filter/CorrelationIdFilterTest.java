package com.shortify.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CorrelationIdFilterTest {

    private CorrelationIdFilter correlationIdFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        correlationIdFilter = new CorrelationIdFilter();
        MDC.clear();
    }

    @Test
    @DisplayName("Should use existing correlation ID from headers")
    void testExistingCorrelationId() throws ServletException, IOException {
        String existingId = "existing-correlation-id-123";
        when(request.getHeader("X-Correlation-ID")).thenReturn(existingId);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(response.getStatus()).thenReturn(200);

        // Verification of MDC during filter chain execution
        doAnswer(invocation -> {
            assertEquals(existingId, MDC.get("correlationId"));
            return null;
        }).when(filterChain).doFilter(request, response);

        correlationIdFilter.doFilterInternal(request, response, filterChain);

        verify(response).setHeader("X-Correlation-ID", existingId);
        verify(filterChain).doFilter(request, response);
        assertNull(MDC.get("correlationId"), "MDC should be cleared after request execution");
    }

    @Test
    @DisplayName("Should generate a new correlation ID if not present in headers")
    void testGeneratedCorrelationId() throws ServletException, IOException {
        when(request.getHeader("X-Correlation-ID")).thenReturn(null);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/data");
        when(response.getStatus()).thenReturn(201);

        doAnswer(invocation -> {
            String mdcId = MDC.get("correlationId");
            assertNotNull(mdcId);
            assertFalse(mdcId.trim().isEmpty());
            return null;
        }).when(filterChain).doFilter(request, response);

        correlationIdFilter.doFilterInternal(request, response, filterChain);

        verify(response).setHeader(eq("X-Correlation-ID"), anyString());
        verify(filterChain).doFilter(request, response);
        assertNull(MDC.get("correlationId"), "MDC should be cleared after request execution");
    }
}
