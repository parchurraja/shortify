package com.shortify.controller;

import com.shortify.dto.request.UrlCreateRequest;
import com.shortify.dto.request.UrlUpdateRequest;
import com.shortify.dto.response.ApiResponse;
import com.shortify.dto.response.UrlResponse;
import com.shortify.security.CustomUserDetails;
import com.shortify.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/urls")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UrlResponse>> createShortUrl(
            @Valid @RequestBody UrlCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest httpServletRequest) {
        UrlResponse response = urlService.createShortUrl(request, userDetails.getUser());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Short URL created successfully", response, httpServletRequest.getRequestURI()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UrlResponse>>> getUrls(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest httpServletRequest) {
        Page<UrlResponse> urls = urlService.getUrls(userDetails.getUser(), search, pageable);
        return ResponseEntity
                .ok(ApiResponse.success("URLs retrieved successfully", urls, httpServletRequest.getRequestURI()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UrlResponse>> updateUrl(
            @PathVariable Long id,
            @Valid @RequestBody UrlUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest httpServletRequest) {
        UrlResponse response = urlService.updateUrl(id, request, userDetails.getUser());
        return ResponseEntity
                .ok(ApiResponse.success("URL updated successfully", response, httpServletRequest.getRequestURI()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUrl(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest httpServletRequest) {
        urlService.deleteUrl(id, userDetails.getUser());
        return ResponseEntity
                .ok(ApiResponse.success("URL deleted successfully", null, httpServletRequest.getRequestURI()));
    }
}
