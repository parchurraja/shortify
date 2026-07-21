package com.shortify.util;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
@Builder
public class UserAgentParser {

    private final String browser;
    private final String os;
    private final String device;

    public static UserAgentParser parse(String userAgent) {
        if (!StringUtils.hasText(userAgent)) {
            return UserAgentParser.builder()
                    .browser("Unknown")
                    .os("Unknown")
                    .device("Unknown")
                    .build();
        }

        String ua = userAgent.toLowerCase();

        // 1. Parse OS
        String os = "Unknown";
        if (ua.contains("windows")) {
            os = "Windows";
        } else if (ua.contains("iphone") || ua.contains("ipad") || ua.contains("ipod")) {
            os = "iOS";
        } else if (ua.contains("android")) {
            os = "Android";
        } else if (ua.contains("macintosh") || ua.contains("mac os x") || ua.contains("mac_powerpc")) {
            os = "macOS";
        } else if (ua.contains("linux")) {
            os = "Linux";
        }

        // 2. Parse Device
        String device = "Desktop";
        if (ua.contains("ipad")) {
            device = "Tablet";
        } else if (ua.contains("mobile") || ua.contains("iphone") || ua.contains("ipod") || ua.contains("android")) {
            // Android without "mobile" keyword is usually a tablet
            if (ua.contains("android") && !ua.contains("mobile")) {
                device = "Tablet";
            } else {
                device = "Mobile";
            }
        }

        // 3. Parse Browser
        String browser = "Other";
        if (ua.contains("edg/") || ua.contains("edge")) {
            browser = "Edge";
        } else if (ua.contains("opr/") || ua.contains("opera")) {
            browser = "Opera";
        } else if (ua.contains("chrome") || ua.contains("crios")) {
            // Chrome on iOS contains crios
            browser = "Chrome";
        } else if (ua.contains("firefox") || ua.contains("fxios")) {
            // Firefox on iOS contains fxios
            browser = "Firefox";
        } else if (ua.contains("safari") && !ua.contains("chrome") && !ua.contains("crios") && !ua.contains("android")) {
            browser = "Safari";
        }

        return UserAgentParser.builder()
                .browser(browser)
                .os(os)
                .device(device)
                .build();
    }
}
