# Implementation Plan (Development Roadmap)

This document chronicles the step-by-step roadmap of development milestones that built the production-ready Shortify URL Shortener.

---

## Phase 1 - Project Setup & Backend Foundation
*   **Objective**: Scaffold a clean Spring Boot Java web architecture.
*   **Features**: Core directory organization, unified global exception mapping, standard `ApiResponse` mapping.
*   **Technologies**: Java 21, Spring Boot 3, Spring Web, Maven, Lombok.
*   **Files Modified**: `ShortifyApplication.java`, `ApiResponse.java`, `GlobalExceptionHandler.java`, `pom.xml`.
*   **Verification**: Executed successful Maven initialization and project compilation.

---

## Phase 2 - Database Design & Migrations
*   **Objective**: Design the relational database schema and set up migration tracking.
*   **Features**: User, Url, and UrlClick entities, soft-delete capabilities (`@SQLDelete`, `@SQLRestriction`), Flyway schema migrations.
*   **Technologies**: MySQL, JPA/Hibernate, Flyway Migrations.
*   **Files Modified**: `V1__init_schema.sql`, `User.java`, `Url.java`, `UrlClick.java`, `BaseEntity.java`.
*   **Verification**: Verified execution of Flyway migrate routines on startup.

---

## Phase 3 - Authentication & JWT Security
*   **Objective**: Setup access controls and endpoint security.
*   **Features**: Secure login/registration endpoints, BCrypt password hashing, JWT custom filter integration.
*   **Technologies**: Spring Security, JJWT (Java JWT), Spring Security Test.
*   **Files Modified**: `SecurityConfig.java`, `JwtUtils.java`, `AuthController.java`, `AuthService.java`, `AuthControllerIT.java`.
*   **Verification**: Automated integration tests mock authentication requests successfully.

---

## Phase 4 - URL Management & Resolution
*   **Objective**: Short link creation and redirection engine.
*   **Features**: Base62 conversion logic, custom alias collision checking, HTTP 302 short url location redirects.
*   **Technologies**: Spring Data JPA, H2 Database (for testing).
*   **Files Modified**: `Base62Util.java`, `UrlController.java`, `UrlService.java`, `RedirectController.java`, `UrlControllerIT.java`.
*   **Verification**: Tested link resolution, pagination, and redirection response headers.

---

## Phase 5 - Analytics Engine
*   **Objective**: Gather metrics on traffic and short link clicks.
*   **Features**: Asynchronous click processing, device/browser/OS parsing, metrics data aggregations.
*   **Technologies**: Spring `@Async` TaskExecutor, User-Agent Header Parser.
*   **Files Modified**: `UserAgentParser.java`, `AnalyticsService.java`, `AnalyticsController.java`, `UrlClickRepository.java`.
*   **Verification**: Asserted metrics data counts and asynchronous worker execution.

---

## Phase 6 - Frontend Foundation & Core UI
*   **Objective**: Scaffold responsive user interface.
*   **Features**: Tailwind setup, responsive dashboard layouts, navigation modules, routing guards.
*   **Technologies**: React 19, Vite, Tailwind CSS, Lucide icons.
*   **Files Modified**: `package.json`, `index.html`, `App.jsx`, `DashboardLayout.jsx`, `ProtectedRoute.jsx`.
*   **Verification**: Compiled frontend build bundle without errors.

---

## Phase 7 - Interactive URL Modals & Features
*   **Objective**: Build action interfaces for link management.
*   **Features**: Modal windows for URL creation, link updates, confirmations, and QR Code generation.
*   **Technologies**: React Canvas (qrcode.react), Axios, Tailwind CSS.
*   **Files Modified**: `CreateUrlModal.jsx`, `EditUrlModal.jsx`, `QrCodeModal.jsx`, `ConfirmModal.jsx`.
*   **Verification**: Verified layout responses and QR Code download functionalities.

---

## Phase 8 - Resilient Rate Limiting
*   **Objective**: Shield APIs against brute-force attacks and spikes.
*   **Features**: Bucket4j token bucket rate limiting filter on login, register, and URL redirection endpoints.
*   **Technologies**: Bucket4j Core, Spring Web Filters.
*   **Files Modified**: `RateLimitingFilter.java`, `RateLimitingService.java`, `RateLimitingFilterTest.java`.
*   **Verification**: Passed targeted rate-limiting filter tests and disabled execution limits in `test` profile.

---

## Phase 9 - Monitoring & Observability
*   **Objective**: Infrastructure performance logs.
*   **Features**: Prometheus metrics scraper integrations, request-tracking Correlation IDs.
*   **Technologies**: Micrometer, Prometheus Registry, Spring Actuator.
*   **Files Modified**: `CorrelationIdFilter.java`, `MetricsService.java`.
*   **Verification**: Monitored registry data tracking on custom actuator endpoints.

---

## Phase 10 - Continuous Integration (CI/CD)
*   **Objective**: Automate code validation.
*   **Features**: GitHub Actions configurations to run backend Maven checkouts and frontend compilation builds.
*   **Technologies**: GitHub Actions, Maven.
*   **Files Modified**: `backend-ci.yml`, `frontend-ci.yml`.
*   **Verification**: Verified CI/CD trigger setups on branch pushes.
