# 11 - Testing Strategy

## 1. Unit Testing
- **Framework**: JUnit 5 & Mockito.
- **Focus Areas**: 
  - `UrlService`: Ensure Base62 generation properly handles collisions and uniqueness.
  - `JwtUtils`: Ensure token generation, validation, and expiration work correctly.
  - `AnalyticsService`: Ensure User-Agent parsing accurately extracts OS and Browser data.

## 2. Integration Testing
- **Framework**: Spring Boot Test (`@SpringBootTest`, `MockMvc`).
- **Focus Areas**:
  - `AuthController`: E2E test for Login and Registration flows using an H2 in-memory database.
  - `RedirectController`: E2E test to verify HTTP 302 statuses and Location header injection.

## 3. Manual / API Testing (Postman)
- A complete Postman collection is maintained in the `postman/` directory.
- Includes environments for `Local` and `Production`.
- Covers all endpoints with pre-request scripts to automatically attach `Bearer` tokens upon login.

## 4. Performance Testing (Load Testing)
- **Tool**: Apache JMeter (or k6).
- **Target**: Ensure the `GET /{shortCode}` redirect endpoint can handle 500+ requests per second on minimal hardware without dropping requests.

## 5. Frontend Testing
- **Framework**: Vitest & React Testing Library.
- **Focus Areas**: Component rendering, conditional UI states (e.g., logged in vs. logged out), and form validation triggers.
