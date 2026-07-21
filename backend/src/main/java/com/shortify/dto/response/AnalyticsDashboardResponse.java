package com.shortify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDashboardResponse {
    
    private long totalUrls;
    private long totalClicks;
    private long clicksToday;
    private UrlResponse topUrl;
    private List<ClickDateCountDto> clicksPastWeek;
    private List<GroupCountDto> deviceStats;
    private List<GroupCountDto> osStats;
    private List<GroupCountDto> browserStats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClickDateCountDto {
        private String date;
        private Long clicks;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupCountDto {
        private String name;
        private Long value;
    }
}
