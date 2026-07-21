package com.shortify.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shortify.service.RateLimitingService;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimitingService rateLimitingService;
    private final MeterRegistry meterRegistry;
    private final ObjectMapper objectMapper;

    @org.springframework.beans.factory.annotation.Value("${rate-limiting.enabled:true}")
    private boolean enabled = true;

    public RateLimitingFilter(RateLimitingService rateLimitingService,
                              MeterRegistry meterRegistry,
                              ObjectMapper objectMapper) {
        this.rateLimitingService = rateLimitingService;
        this.meterRegistry = meterRegistry;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (!enabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        String method = request.getMethod();

        String ip = getClientIp(request);
        String userIdentifier = getUserIdentifier();
        String key = null;
        long limit = 0;
        String endpointName = null;

        if (method.equalsIgnoreCase("POST") && path.equals("/api/auth/login")) {
            limit = 5;
            key = "login:" + ip;
            endpointName = "login";
        } else if (method.equalsIgnoreCase("POST") && path.equals("/api/auth/register")) {
            limit = 3;
            key = "register:" + ip;
            endpointName = "register";
        } else if (method.equalsIgnoreCase("POST") && path.equals("/api/urls")) {
            limit = 30;
            key = "createUrl:" + (userIdentifier != null ? userIdentifier : ip);
            endpointName = "create_url";
        } else if (method.equalsIgnoreCase("GET") && isRedirectPath(path)) {
            limit = 200;
            key = "redirect:" + ip;
            endpointName = "redirect";
        }

        if (key != null) {
            boolean allowed = rateLimitingService.tryConsume(key, limit, Duration.ofMinutes(1));
            if (!allowed) {
                log.warn("Rate limit exceeded for key: {} on endpoint: {} [Limit: {}/min]", key, path, limit);
                
                // Increment rate limit hits counter
                meterRegistry.counter("shortify_rate_limit_hits_total", 
                        "endpoint", endpointName, 
                        "identifier", userIdentifier != null ? userIdentifier : ip)
                        .increment();

                sendErrorResponse(response, HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded. Please try again later.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.trim().isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }

    private String getUserIdentifier() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return auth.getName();
        }
        return null;
    }

    private boolean isRedirectPath(String path) {
        if (path == null || path.equals("/") || path.contains("//")) {
            return false;
        }
        String cleanPath = path.startsWith("/") ? path.substring(1) : path;
        if (cleanPath.contains("/")) {
            return false;
        }
        return !cleanPath.equals("favicon.ico") &&
               !cleanPath.equals("error") &&
               !cleanPath.equals("index.html") &&
               !cleanPath.startsWith("api") &&
               !cleanPath.startsWith("actuator");
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", status.value());
        errorDetails.put("error", status.getReasonPhrase());
        errorDetails.put("message", message);

        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }
}
