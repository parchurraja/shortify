package com.shortify.service;

import com.shortify.config.AppProperties;
import com.shortify.dto.request.UrlCreateRequest;
import com.shortify.dto.request.UrlUpdateRequest;
import com.shortify.dto.response.UrlResponse;
import com.shortify.entity.Url;
import com.shortify.entity.User;
import com.shortify.exception.BadRequestException;
import com.shortify.exception.DuplicateResourceException;
import com.shortify.exception.ResourceNotFoundException;
import com.shortify.exception.UnauthorizedException;
import com.shortify.monitoring.MetricsService;
import com.shortify.repository.UrlRepository;
import com.shortify.util.Base62Utils;
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
    private final AppProperties appProperties;

    public UrlService(UrlRepository urlRepository,
                      PasswordEncoder passwordEncoder,
                      MetricsService metricsService,
                      AppProperties appProperties) {
        this.urlRepository = urlRepository;
        this.passwordEncoder = passwordEncoder;
        this.metricsService = metricsService;
        this.appProperties = appProperties;
    }

    @Transactional
    public UrlResponse createShortUrl(UrlCreateRequest request, User user) {

        long startTime = System.currentTimeMillis();

        try {

            String shortCode;

            if (StringUtils.hasText(request.getCustomAlias())) {

                String alias = request.getCustomAlias().trim();

                if (urlRepository.existsByShortCode(alias)) {
                    throw new DuplicateResourceException(
                            "Custom alias '" + alias + "' is already taken");
                }

                shortCode = alias;

            } else {

                int retries = 0;

                do {
                    shortCode = Base62Utils.generateShortCode();
                    retries++;
                } while (urlRepository.existsByShortCode(shortCode) && retries < 5);

                if (retries >= 5) {
                    throw new BadRequestException(
                            "Could not generate a unique short code.");
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

            metricsService.incrementUrlsCreated();

            return mapToUrlResponse(savedUrl);

        } finally {

            metricsService.recordUrlCreateDuration(
                    System.currentTimeMillis() - startTime);
        }
    }

    @Transactional(readOnly = true)
    public Page<UrlResponse> getUrls(User user,
                                     String search,
                                     Pageable pageable) {

        Page<Url> urls;

        if (StringUtils.hasText(search)) {
            urls = urlRepository.searchUrls(user, search.trim(), pageable);
        } else {
            urls = urlRepository.findByUserAndDeletedAtIsNull(user, pageable);
        }

        return urls.map(this::mapToUrlResponse);
    }

    @Transactional(readOnly = true)
    public UrlResponse getUrlById(Long id, User user) {

        Url url = urlRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("URL not found"));

        if (!url.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException(
                    "You are not authorized to access this URL");
        }

        return mapToUrlResponse(url);
    }

    @Transactional
    public UrlResponse updateUrl(Long id,
                                 UrlUpdateRequest request,
                                 User user) {

        Url url = urlRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("URL not found"));

        if (!url.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException(
                    "You are not authorized to update this URL");
        }

        url.setOriginalUrl(request.getOriginalUrl());

        Url updatedUrl = urlRepository.save(url);

        return mapToUrlResponse(updatedUrl);
    }

    @Transactional
    public void deleteUrl(Long id, User user) {

        Url url = urlRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("URL not found"));

        if (!url.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException(
                    "You are not authorized to delete this URL");
        }

        urlRepository.delete(url);
    }

    public UrlResponse mapToUrlResponse(Url url) {

        return UrlResponse.builder()
                .id(url.getId())
                .originalUrl(url.getOriginalUrl())
                .shortCode(url.getShortCode())
                .shortUrl(appProperties.getBaseUrl() + "/" + url.getShortCode())
                .customAlias(url.getCustomAlias())
                .expiresAt(url.getExpiresAt())
                .maxClicks(url.getMaxClicks())
                .clickCount(url.getClickCount())
                .isActive(url.getIsActive())
                .createdAt(url.getCreatedAt())
                .build();
    }
}