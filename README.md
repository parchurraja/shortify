# Shortify — Enterprise-Grade URL Shortening Platform

Shortify is a production-ready, high-performance URL shortening application engineered with **Spring Boot 3**, **React 19**, **MySQL**, and **Redis**. It is designed with robust security configurations, asynchronous traffic analytics parsing, Prometheus metrics collectors, rate limiting, and fully integrated CI/CD pipelines.

---

## Key Features

- **Base62 URL Encoding**: Resolves auto-increment identifiers into short, reliable routing tokens.
- **Custom Aliases**: Allows users to specify descriptive routing keys for branded links.
- **JWT Authentication & Security**: Stateless auth checks using Spring Security and JSON Web Tokens.
- **Asynchronous Click Analytics**: High-throughput User-Agent and IP tracking parsed out-of-band via `@Async` task pools.
- **Interactive Metrics Monitoring**: Custom Prometheus export registry with Correlation-ID request tracking.
- **Client-Side QR Code Engine**: Local canvas rendering for instant downloadable QR codes.
- **Bucket4j API Rate Limiting**: Token-bucket protection against abuse on registration, login, and redirection paths.
- **Auto-Rollback Database Testing**: H2 test configuration leveraging `@Transactional` profiles to keep the database state clean.

---

## Technology Stack

### Backend
- **Core**: Java 21, Spring Boot 3.4.0 (Spring MVC, JPA, Security)
- **Security**: JJWT (Java JWT) 0.12.5, BCrypt Hashing
- **Migrations**: Flyway Schema Management
- **Monitoring**: Spring Actuator, Micrometer Prometheus Registry
- **Testing**: JUnit 5, Mockito, H2 Database, JaCoCo Coverage Plugin
- **API Protection**: Bucket4j Rate Limiter

### Frontend
- **Framework**: React 19 (Vite, Axios, Tailwind CSS)
- **Icons**: Lucide React
- **QR Codes**: qrcode.react (Canvas)

---

## Architectural Diagram

```
User Browser 
   │
   ├── (Auth API Requests / UI Navigation) ──> React Frontend (Vite)
   │
   └── (Short Code Redirect: GET /xyz789)
         │
         ▼
     [Spring Security Filter Chain]
         │
         ├── RateLimitingFilter (Bucket4j API checks)
         └── CorrelationIdFilter (Correlation ID tagging)
                 │
                 ▼
         [RedirectController]
                 │
                 ├── Cache Hit? ──> [Redis Cache] ──> Returns Original URL
                 └── Cache Miss?
                         │
                         ▼
                    [MySQL Database]
                         │
                   (Async Log)
                         │
                         ▼
                [AnalyticsService] ──> User Agent Parser ──> Saves Click Metrics
                         │
                         ▼
             [Micrometer Prometheus] ──> Actuator Metrics Scrapers
```

---

## Database Schema (JPA Entities)

### 1. `users`
- `id` (BIGINT, Primary Key, Auto-Increment)
- `name` (VARCHAR(100), NOT NULL)
- `email` (VARCHAR(150), Unique Index, NOT NULL)
- `password` (VARCHAR(255), Hashed, NOT NULL)
- `role` (VARCHAR(20), Default: `ROLE_USER`, NOT NULL)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)
- `deleted_at` (TIMESTAMP, Nullable)

### 2. `urls`
- `id` (BIGINT, Primary Key, Auto-Increment)
- `original_url` (VARCHAR(2048), NOT NULL)
- `short_code` (VARCHAR(50), Unique Index, NOT NULL)
- `click_count` (BIGINT, Default: 0)
- `user_id` (BIGINT, Foreign Key -> `users.id`)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)
- `deleted_at` (TIMESTAMP, Nullable)

### 3. `url_clicks`
- `id` (BIGINT, Primary Key, Auto-Increment)
- `url_id` (BIGINT, Foreign Key -> `urls.id`, NOT NULL)
- `browser` (VARCHAR(50))
- `os` (VARCHAR(50))
- `device` (VARCHAR(50))
- `country` (VARCHAR(100))
- `clicked_at` (TIMESTAMP, Default: `CURRENT_TIMESTAMP`)

---

## Environment Variables

### Backend Configuration (`backend/src/main/resources/application-dev.yml`)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/shortify?useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: yourpassword
  jpa:
    hibernate:
      ddl-auto: validate

jwt:
  secret: 404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
  expiration-ms: 3600000
```

### Frontend Configuration (`frontend/.env`)
```env
VITE_API_BASE_URL=http://localhost:8080
```

---

## Installation & Setup

### Backend Setup
1. Navigate to the backend directory:
   ```bash
   cd backend
   ```
2. Compile and package the project:
   ```bash
   mvn clean package
   ```
3. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

### Frontend Setup
1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```
2. Install npm dependencies:
   ```bash
   npm install
   ```
3. Run the development server:
   ```bash
   npm run dev
   ```

---

## Core API Endpoints

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/auth/register` | Register a new user profile | No |
| **POST** | `/api/auth/login` | Authenticate credentials and return JWT | No |
| **POST** | `/api/urls` | Generate a shortened URL (auto or custom alias) | Yes |
| **GET** | `/api/urls` | Retrieve paginated listing of user's URLs | Yes |
| **GET** | `/{shortCode}` | Redirection handler to target destination | No |
| **GET** | `/api/analytics/dashboard` | Dashboard analytics (Clicks history, OS/Browser) | Yes |

---

## Test & QA Suites
- **Backend Tests**: Run unit and integration tests with coverage:
  ```bash
  mvn test
  ```
- **Jacoco Reports**: Reports are generated in `backend/target/site/jacoco/index.html` after testing.
- **Frontend Verify**: Lint and verify React distribution bundle compiler:
  ```bash
  npm run build
  ```

---

## Project Documentation Index
1. [Software Requirements Specification (SRS)](docs/02_SRS.md)
2. [Workflow Design Document](docs/workflow-design.md)
3. [Architecture Blueprint](docs/03_Architecture.md)
4. [Development Implementation Roadmap](docs/implementation-plan.md)
5. [API Specification Specifications](docs/05_API_Specification.md)
