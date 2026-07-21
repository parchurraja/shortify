# 03 - Architecture

## High-Level Architecture
Shortify operates on a decoupled client-server architecture:
- **Client (Frontend)**: React SPA hosted on Vercel. Communicates strictly via RESTful JSON APIs.
- **Server (Backend)**: Spring Boot REST API hosted on Render. Completely stateless.
- **Database**: Managed MySQL instance containing persistent state and Flyway for schema migrations.

## Layered Architecture (Backend)
1. **Controller Layer**: Handles HTTP requests, input validation (DTOs), and returns `ApiResponse<T>`.
2. **Service Layer**: Contains core business logic, transaction boundaries (`@Transactional`), and algorithmic implementations (Base62 encoding).
3. **Repository Layer**: Spring Data JPA interfaces for database interaction.
4. **Security Layer**: Intercepts requests, validates JWTs, enforces Bucket4j rate limits, and extracts UserDetails.

## Request Flows

### JWT Authentication Flow
1. Client sends `POST /api/auth/login` with credentials.
2. `AuthController` passes credentials to `AuthService`.
3. `AuthenticationManager` verifies the BCrypt hashed password against the DB.
4. `JwtUtils` generates an Access Token (15m) and Refresh Token (7d).
5. Tokens returned to Client. Subsequent requests include `Authorization: Bearer <token>`.
6. `JwtAuthenticationFilter` validates token per request.

### Redirect Flow
1. Visitor navigates to `https://shorti.fy/aB3x9`.
2. `RedirectController` handles `GET /{shortCode}`.
3. `UrlService` queries the database for the short code.
4. If valid (not expired, `deleted_at` is null), `UrlService` triggers an async event to log the click.
5. Controller returns an HTTP 302 Redirect with the `Location` header set to `original_url`.

### Analytics Flow
1. Redirect flow triggers an asynchronous `ClickEvent`.
2. `AnalyticsService` parses the `User-Agent` string to extract OS, Browser, and Device type.
3. The parsed data is saved to the `click_analytics` table.
4. Frontend requests `GET /api/analytics/dashboard` -> Service aggregates counts using JPA projections/custom queries -> Returns aggregated DTO.
