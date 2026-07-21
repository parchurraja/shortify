package com.shortify.service;

import com.shortify.dto.response.AnalyticsDashboardResponse;
import com.shortify.dto.response.UrlResponse;
import com.shortify.entity.Url;
import com.shortify.entity.UrlClick;
import com.shortify.entity.User;
import com.shortify.repository.UrlClickRepository;
import com.shortify.repository.UrlRepository;
import com.shortify.util.UserAgentParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AnalyticsService {

    private final UrlClickRepository urlClickRepository;
    private final UrlRepository urlRepository;
    private final UrlService urlService;

    public AnalyticsService(UrlClickRepository urlClickRepository,
                            UrlRepository urlRepository,
                            @Lazy UrlService urlService) {
        this.urlClickRepository = urlClickRepository;
        this.urlRepository = urlRepository;
        this.urlService = urlService;
    }

    @Async("taskExecutor")
    @Transactional
    public void logClickAsync(Long urlId, String userAgentHeader, String ipAddress) {
        try {
            Url url = urlRepository.findById(urlId)
                    .orElseThrow(() -> new IllegalArgumentException("URL not found with id: " + urlId));

            // Parse User Agent
            UserAgentParser parser = UserAgentParser.parse(userAgentHeader);

            // Log click
            UrlClick click = UrlClick.builder()
                    .url(url)
                    .browser(parser.getBrowser())
                    .os(parser.getOs())
                    .device(parser.getDevice())
                    .country("Unknown") // Default to unknown as CDNs/proxies headers aren't simulated locally
                    .build();

            urlClickRepository.save(click);

            // Update URL count
            url.setClickCount(url.getClickCount() + 1);
            urlRepository.save(url);

            log.info("Logged click for URL: {} from IP: {}, Device: {}, OS: {}, Browser: {}", 
                    url.getShortCode(), ipAddress, parser.getDevice(), parser.getOs(), parser.getBrowser());
        } catch (Exception e) {
            log.error("Failed to log click asynchronously for URL ID: {}", urlId, e);
        }
    }

    @Transactional(readOnly = true)
    public AnalyticsDashboardResponse getDashboardData(User user) {
        long totalUrls = urlRepository.countByUserAndDeletedAtIsNull(user);
        long totalClicks = urlClickRepository.countAllClicksByUser(user);
        
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        long clicksToday = urlClickRepository.countClicksSinceByUser(user, startOfDay);

        // Fetch Top Performing Url
        Page<Url> topUrlsPage = urlRepository.findByUserAndDeletedAtIsNull(
                user, PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "clickCount", "createdAt"))
        );
        UrlResponse topUrl = null;
        if (!topUrlsPage.isEmpty()) {
            topUrl = urlService.mapToUrlResponse(topUrlsPage.getContent().get(0));
        }

        // Click history over the past 7 days
        LocalDateTime sevenDaysAgo = LocalDate.now().minusDays(7).atStartOfDay();
        List<UrlClickRepository.ClickDateCount> clicksPastWeekProj = urlClickRepository.getClicksPastWeekByUser(user, sevenDaysAgo);
        List<AnalyticsDashboardResponse.ClickDateCountDto> clicksPastWeek = clicksPastWeekProj.stream()
                .map(proj -> AnalyticsDashboardResponse.ClickDateCountDto.builder()
                        .date(String.valueOf(proj.getDate()))
                        .clicks(proj.getClicks())
                        .build())
                .collect(Collectors.toList());

        // Device, OS and Browser stats
        List<UrlClickRepository.GroupCount> deviceProj = urlClickRepository.getDeviceStatsByUser(user);
        List<AnalyticsDashboardResponse.GroupCountDto> deviceStats = deviceProj.stream()
                .map(proj -> AnalyticsDashboardResponse.GroupCountDto.builder()
                        .name(proj.getName())
                        .value(proj.getValue())
                        .build())
                .collect(Collectors.toList());

        List<UrlClickRepository.GroupCount> osProj = urlClickRepository.getOsStatsByUser(user);
        List<AnalyticsDashboardResponse.GroupCountDto> osStats = osProj.stream()
                .map(proj -> AnalyticsDashboardResponse.GroupCountDto.builder()
                        .name(proj.getName())
                        .value(proj.getValue())
                        .build())
                .collect(Collectors.toList());

        List<UrlClickRepository.GroupCount> browserProj = urlClickRepository.getBrowserStatsByUser(user);
        List<AnalyticsDashboardResponse.GroupCountDto> browserStats = browserProj.stream()
                .map(proj -> AnalyticsDashboardResponse.GroupCountDto.builder()
                        .name(proj.getName())
                        .value(proj.getValue())
                        .build())
                .collect(Collectors.toList());

        return AnalyticsDashboardResponse.builder()
                .totalUrls(totalUrls)
                .totalClicks(totalClicks)
                .clicksToday(clicksToday)
                .topUrl(topUrl)
                .clicksPastWeek(clicksPastWeek)
                .deviceStats(deviceStats)
                .osStats(osStats)
                .browserStats(browserStats)
                .build();
    }
}
