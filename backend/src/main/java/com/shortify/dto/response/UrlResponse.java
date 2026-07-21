package com.shortify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlResponse {
    
    private Long id;
    private String originalUrl;
    private String shortCode;
    private String customAlias;
    private LocalDateTime expiresAt;
    private Long maxClicks;
    private Long clickCount;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private String shortUrl;
}
