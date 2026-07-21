package com.shortify.controller;

import com.shortify.dto.response.AnalyticsDashboardResponse;
import com.shortify.dto.response.ApiResponse;
import com.shortify.security.CustomUserDetails;
import com.shortify.service.AnalyticsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AnalyticsDashboardResponse>> getDashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest httpServletRequest) {
        AnalyticsDashboardResponse response = analyticsService.getDashboardData(userDetails.getUser());
        return ResponseEntity
                .ok(ApiResponse.success("Dashboard data retrieved successfully", response, httpServletRequest.getRequestURI()));
    }
}
