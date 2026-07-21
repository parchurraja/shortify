package com.shortify.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserAgentParserTest {

    @Test
    @DisplayName("Should parse Chrome on Windows Desktop correctly")
    void testParseChromeWindowsDesktop() {
        String ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
        UserAgentParser parsed = UserAgentParser.parse(ua);

        assertEquals("Windows", parsed.getOs());
        assertEquals("Desktop", parsed.getDevice());
        assertEquals("Chrome", parsed.getBrowser());
    }

    @Test
    @DisplayName("Should parse Safari on iPhone Mobile correctly")
    void testParseSafariIphoneMobile() {
        String ua = "Mozilla/5.0 (iPhone; CPU iPhone OS 16_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.5 Mobile/15E148 Safari/604.1";
        UserAgentParser parsed = UserAgentParser.parse(ua);

        assertEquals("iOS", parsed.getOs());
        assertEquals("Mobile", parsed.getDevice());
        assertEquals("Safari", parsed.getBrowser());
    }

    @Test
    @DisplayName("Should return Unknown for null or blank user-agent")
    void testNullOrEmptyUserAgent() {
        UserAgentParser parsedNull = UserAgentParser.parse(null);
        assertEquals("Unknown", parsedNull.getBrowser());
        assertEquals("Unknown", parsedNull.getOs());
        assertEquals("Unknown", parsedNull.getDevice());

        UserAgentParser parsedBlank = UserAgentParser.parse("   ");
        assertEquals("Unknown", parsedBlank.getBrowser());
    }
}
