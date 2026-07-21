package com.shortify.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlCreateRequest {

    @NotBlank(message = "Original URL is required")
    @Pattern(regexp = "^https?://.+", message = "URL must start with http:// or https://")
    private String originalUrl;

    @Pattern(regexp = "^[a-zA-Z0-9-]*$", message = "Custom alias must be alphanumeric and dashes only")
    @Size(max = 50, message = "Custom alias must be under 50 characters")
    private String customAlias;

    private String password;

    private LocalDateTime expiresAt;

    private Long maxClicks;
}
