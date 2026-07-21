package com.shortify.service;

import com.shortify.monitoring.MetricsService;
import com.shortify.dto.request.UrlCreateRequest;
import com.shortify.dto.request.UrlUpdateRequest;
import com.shortify.dto.response.UrlResponse;
import com.shortify.entity.Url;
import com.shortify.entity.User;
import com.shortify.exception.BadRequestException;
import com.shortify.exception.DuplicateResourceException;
import com.shortify.exception.ResourceNotFoundException;
import com.shortify.exception.UnauthorizedException;
import com.shortify.repository.UrlRepository;
import com.shortify.util.Base62Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final PasswordEncoder passwordEncoder;
    private final MetricsService metricsService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public UrlService(UrlRepository urlRepository, PasswordEncoder passwordEncoder, MetricsService metricsService) {
        this.urlRepository = urlRepository;
        this.passwordEncoder = passwordEncoder;
        this.metricsService = metricsService;
    }

    @Transactional
    public UrlResponse createShortUrl(UrlCreateRequest request, User user) {
        long startTime = System.currentTimeMillis();
        try {
            String shortCode;

            if (StringUtils.hasText(request.getCustomAlias())) {
                String alias = request.getCustomAlias().trim();
                if (urlRepository.existsByShortCode(alias)) {
                    throw new DuplicateResourceException("Custom alias '" + alias + "' is already taken");
                }
                shortCode = alias;
            } else {
                // Generate a unique Base62 code
                int retries = 0;
                do {
                    shortCode = Base62Utils.generateShortCode();
                    retries++;
                } while (urlRepository.existsByShortCode(shortCode) && retries < 5);

                if (retries >= 5) {
                    throw new BadRequestException("Could not generate a unique short code, please try again.");
                }
            }

            String passwordHash = null;
            if (StringUtils.hasText(request.getPassword())) {
                passwordHash = passwordEncoder.encode(request.getPassword());
            }

            Url url = Url.builder()
                    .user(user)
                    .originalUrl(request.getOriginalUrl())
                    .shortCode(shortCode)
                    .customAlias(request.getCustomAlias())
                    .passwordHash(passwordHash)
                    .expiresAt(request.getExpiresAt())
                    .maxClicks(request.getMaxClicks())
                    .clickCount(0L)
                    .isActive(true)
                    .build();

            Url savedUrl = urlRepository.save(url);
            UrlResponse response = mapToUrlResponse(savedUrl);
            metricsService.incrementUrlsCreated();
            return response;
        } finally {
            metricsService.recordUrlCreateDuration(System.currentTimeMillis() - startTime);
        }
    }

    @Transactional(readOnly = true)
    public Page<UrlResponse> getUrls(User user, String search, Pageable pageable) {
        Page<Url> urlPage;
        if (StringUtils.hasText(search)) {
            urlPage = urlRepository.searchUrls(user, search.trim(), pageable);
        } else {
            urlPage = urlRepository.findByUserAndDeletedAtIsNull(user, pageable);
        }
        return urlPage.map(this::mapToUrlResponse);
    }

    @Transactional
    public UrlResponse updateUrl(Long id, UrlUpdateRequest request, User user) {
        Url url = urlRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("URL not found"));

        if (!url.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to update this URL");
        }

        url.setOriginalUrl(request.getOriginalUrl());
        Url updatedUrl = urlRepository.save(url);
        return mapToUrlResponse(updatedUrl);
    }

    @Transactional
    public void deleteUrl(Long id, User user) {
        Url url = urlRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("URL not found"));

        if (!url.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to delete this URL");
        }

        urlRepository.delete(url);
    }

    public UrlResponse mapToUrlResponse(Url url) {
        String shortUrlString = baseUrl + "/" + url.getShortCode();
        return UrlResponse.builder()
                .id(url.getId())
                .originalUrl(url.getOriginalUrl())
                .shortCode(url.getShortCode())
                .customAlias(url.getCustomAlias())
                .expiresAt(url.getExpiresAt())
                .maxClicks(url.getMaxClicks())
                .clickCount(url.getClickCount())
                .isActive(url.getIsActive())
                .createdAt(url.getCreatedAt())
                .shortUrl(shortUrlString)
                .build();
    }
}
