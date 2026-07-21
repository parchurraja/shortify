package com.shortify.controller;

import com.shortify.monitoring.MetricsService;
import com.shortify.entity.Url;
import com.shortify.exception.BadRequestException;
import com.shortify.exception.ResourceNotFoundException;
import com.shortify.repository.UrlRepository;
import com.shortify.service.AnalyticsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public class RedirectController {

    private final UrlRepository urlRepository;
    private final PasswordEncoder passwordEncoder;
    private final AnalyticsService analyticsService;
    private final MetricsService metricsService;

    public RedirectController(UrlRepository urlRepository,
                              PasswordEncoder passwordEncoder,
                              AnalyticsService analyticsService,
                              MetricsService metricsService) {
        this.urlRepository = urlRepository;
        this.passwordEncoder = passwordEncoder;
        this.analyticsService = analyticsService;
        this.metricsService = metricsService;
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirect(
            @PathVariable String shortCode,
            @RequestParam(required = false) String password,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            HttpServletRequest request) {

        long startTime = System.currentTimeMillis();
        try {
            Url url = urlRepository.findByShortCode(shortCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Short URL not found"));

            if (!url.getIsActive()) {
                throw new BadRequestException("This link is currently inactive");
            }

            if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new BadRequestException("This link has expired");
            }

            if (url.getMaxClicks() != null && url.getClickCount() >= url.getMaxClicks()) {
                throw new BadRequestException("This link click limit has been reached");
            }

            // Check if password protected
            if (StringUtils.hasText(url.getPasswordHash())) {
                boolean authenticated = StringUtils.hasText(password) &&
                        passwordEncoder.matches(password, url.getPasswordHash());

                if (!authenticated) {
                    metricsService.incrementRedirectsFailure("Unauthorized");
                    // Return premium HTML password prompt if requested by a browser
                    String acceptHeader = request.getHeader("Accept");
                    if (acceptHeader != null && acceptHeader.contains("text/html")) {
                        String errorHtml = StringUtils.hasText(password)
                                ? "<div class=\"error-message\">Incorrect password. Please try again.</div>"
                                : "";
                        String html = getPasswordPromptPageHtml(errorHtml);
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .contentType(MediaType.TEXT_HTML)
                                .body(html);
                    } else {
                        // API request: throw Bad Request or Unauthorized
                        throw new BadRequestException("Password required or incorrect password provided");
                    }
                }
            }

            // Log the click asynchronously
            String ipAddress = getClientIp(request);
            analyticsService.logClickAsync(url.getId(), userAgent, ipAddress);

            metricsService.incrementRedirectsSuccess();
            // Perform 302 redirect
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, url.getOriginalUrl())
                    .build();
        } catch (Exception ex) {
            metricsService.incrementRedirectsFailure(ex.getClass().getSimpleName());
            throw ex;
        } finally {
            metricsService.recordRedirectDuration(System.currentTimeMillis() - startTime);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }

    private String getPasswordPromptPageHtml(String errorHtml) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Shortify | Password Protected Link</title>\n" +
                "    <link href=\"https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;600;800&display=swap\" rel=\"stylesheet\">\n" +
                "    <style>\n" +
                "        * {\n" +
                "            box-sizing: border-box;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "        body {\n" +
                "            font-family: 'Outfit', sans-serif;\n" +
                "            background: linear-gradient(135deg, #0f172a 0%, #1e1b4b 100%);\n" +
                "            color: #f8fafc;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "            min-height: 100vh;\n" +
                "            overflow: hidden;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "        .container {\n" +
                "            background: rgba(30, 41, 59, 0.7);\n" +
                "            backdrop-filter: blur(16px);\n" +
                "            -webkit-backdrop-filter: blur(16px);\n" +
                "            border: 1px solid rgba(255, 255, 255, 0.1);\n" +
                "            border-radius: 24px;\n" +
                "            padding: 40px;\n" +
                "            width: 100%;\n" +
                "            max-width: 440px;\n" +
                "            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4);\n" +
                "            text-align: center;\n" +
                "            animation: fadeIn 0.6s ease-out;\n" +
                "        }\n" +
                "        @keyframes fadeIn {\n" +
                "            from { opacity: 0; transform: translateY(20px); }\n" +
                "            to { opacity: 1; transform: translateY(0); }\n" +
                "        }\n" +
                "        .logo {\n" +
                "            font-size: 32px;\n" +
                "            font-weight: 800;\n" +
                "            background: linear-gradient(to right, #6366f1, #a855f7);\n" +
                "            -webkit-background-clip: text;\n" +
                "            -webkit-text-fill-color: transparent;\n" +
                "            margin-bottom: 24px;\n" +
                "        }\n" +
                "        h1 {\n" +
                "            font-size: 20px;\n" +
                "            font-weight: 600;\n" +
                "            margin-bottom: 12px;\n" +
                "            color: #e2e8f0;\n" +
                "        }\n" +
                "        p {\n" +
                "            font-size: 14px;\n" +
                "            color: #94a3b8;\n" +
                "            margin-bottom: 30px;\n" +
                "            line-height: 1.5;\n" +
                "        }\n" +
                "        .error-message {\n" +
                "            background: rgba(239, 68, 68, 0.15);\n" +
                "            border: 1px solid rgba(239, 68, 68, 0.3);\n" +
                "            color: #fca5a5;\n" +
                "            padding: 12px;\n" +
                "            border-radius: 12px;\n" +
                "            font-size: 13px;\n" +
                "            margin-bottom: 20px;\n" +
                "            text-align: left;\n" +
                "        }\n" +
                "        .input-group {\n" +
                "            margin-bottom: 24px;\n" +
                "            text-align: left;\n" +
                "        }\n" +
                "        label {\n" +
                "            display: block;\n" +
                "            font-size: 12px;\n" +
                "            font-weight: 600;\n" +
                "            text-transform: uppercase;\n" +
                "            letter-spacing: 0.05em;\n" +
                "            color: #818cf8;\n" +
                "            margin-bottom: 8px;\n" +
                "        }\n" +
                "        input[type=\"password\"] {\n" +
                "            width: 100%;\n" +
                "            padding: 14px 16px;\n" +
                "            background: rgba(15, 23, 42, 0.6);\n" +
                "            border: 1px solid rgba(255, 255, 255, 0.1);\n" +
                "            border-radius: 12px;\n" +
                "            color: #ffffff;\n" +
                "            font-size: 16px;\n" +
                "            transition: all 0.3s ease;\n" +
                "            outline: none;\n" +
                "        }\n" +
                "        input[type=\"password\"]:focus {\n" +
                "            border-color: #6366f1;\n" +
                "            box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.25);\n" +
                "        }\n" +
                "        button {\n" +
                "            width: 100%;\n" +
                "            padding: 14px;\n" +
                "            background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);\n" +
                "            border: none;\n" +
                "            border-radius: 12px;\n" +
                "            color: white;\n" +
                "            font-size: 16px;\n" +
                "            font-weight: 600;\n" +
                "            cursor: pointer;\n" +
                "            transition: all 0.3s ease;\n" +
                "            box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);\n" +
                "        }\n" +
                "        button:hover {\n" +
                "            transform: translateY(-1px);\n" +
                "            box-shadow: 0 6px 20px rgba(99, 102, 241, 0.4);\n" +
                "        }\n" +
                "        button:active {\n" +
                "            transform: translateY(1px);\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"logo\">Shortify</div>\n" +
                "        <h1>Password Protected Link</h1>\n" +
                "        <p>This link is encrypted. Please enter the password below to gain access.</p>\n" +
                "        \n" +
                errorHtml +
                "        \n" +
                "        <form method=\"GET\" action=\"\">\n" +
                "            <div class=\"input-group\">\n" +
                "                <label for=\"password\">Password</label>\n" +
                "                <input type=\"password\" id=\"password\" name=\"password\" placeholder=\"Enter password\" required autofocus>\n" +
                "            </div>\n" +
                "            <button type=\"submit\">Unlock & Redirect</button>\n" +
                "        </form>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}
