# Workflow Design (Application Flow)

This document maps out the runtime workflows and execution paths within Shortify.

---

## 1. User Authentication Workflow

Describes how a user registers or logs in, receives a JWT, and sends authenticated requests.

```mermaid
sequenceDiagram
    actor User
    participant React as React Frontend (Vite)
    participant Filter as Spring Security Filter Chain
    participant Controller as AuthController
    participant Service as AuthService
    participant DB as MySQL Database

    User->>React: Fill register/login form & submit
    React->>Filter: POST /api/auth/login or /register
    Filter->>Controller: Dispatch request
    Controller->>Service: Authenticate credentials
    Service->>DB: Query user records (or save new user)
    DB-->>Service: Return User details
    Service->>Service: Verify Password (BCrypt) & Generate JWT Token
    Service-->>Controller: Return AuthResponse (JWT + User Profile)
    Controller-->>React: Send HTTP 200/201 (AccessToken + RefreshToken)
    React->>React: Store JWT in LocalStorage / Auth Context
    React-->>User: Redirect to Dashboard
```

---

## 2. URL Creation Workflow

Describes the process of generating a shortened link, optionally using a custom alias.

```mermaid
sequenceDiagram
    actor User
    participant React as React Frontend
    participant Filter as Spring Security Filter Chain
    participant Controller as UrlController
    participant Service as UrlService
    participant Util as Base62Util
    participant DB as MySQL Database

    User->>React: Enter original URL & optional custom alias
    React->>Filter: POST /api/urls (Header: Authorization: Bearer <token>)
    Filter->>Filter: Validate JWT Token
    Filter->>Controller: Dispatch RequestBody (UrlCreateRequest)
    Controller->>Service: createShortUrl(request, user)
    alt Custom Alias provided
        Service->>DB: Check if alias exists
        DB-->>Service: Return existence check
    else No Alias provided
        Service->>DB: Save template and generate auto-increment ID
        Service->>Util: Encode ID into Base62 Short Code
    end
    Service->>DB: Save URL entity (shortCode, originalUrl, clickCount: 0)
    DB-->>Service: Confirm save
    Service-->>Controller: Return URL DTO (shortCode, full short URL)
    Controller-->>React: Send HTTP 201 Created (ApiResponse)
    React-->>User: Display short link + QR Code Option
```

---

## 3. Redirection Workflow

How short links are resolved and visitors redirected, utilizing Redis optimization.

```mermaid
graph TD
    A[User clicks short URL: Shortify/xyz789] --> B{RateLimitingFilter}
    B -- Exceeded --> C[Return HTTP 429 Too Many Requests]
    B -- Allowed --> D{CorrelationIdFilter}
    D --> E[Assign X-Correlation-ID tracing header]
    E --> F{Redis Cache lookup}
    F -- Cache Hit --> G[Retrieve target original URL]
    F -- Cache Miss --> H[Database query select original_url]
    H --> I[Update Redis Cache with key/value]
    I --> J[Asynchronously dispatch Analytics Log Click]
    G --> K[HTTP 302 Location: originalUrl]
    J --> K
    K --> L[User browser arrives at destination]
```

---

## 4. Analytics Workflow

How click statistics (browser, OS, device, timestamps) are asynchronously recorded and processed.

```mermaid
sequenceDiagram
    participant Redirect as Redirect Handler
    participant Service as AnalyticsService (Async Thread)
    participant Parser as UserAgentParser
    participant DB as MySQL Database
    participant Actuator as Micrometer / MetricsRegistry

    Redirect->>Service: logClickAsync(urlId, userAgentHeader, ipAddress)
    Note over Service: @Async("taskExecutor") spawns thread pool task
    Service->>Parser: Parse user agent header
    Parser-->>Service: Return browser, OS, and device categories
    Service->>DB: INSERT INTO url_clicks (url_id, browser, os, device, country, clicked_at)
    Service->>DB: UPDATE urls SET click_count = click_count + 1
    Service->>Actuator: Increment shortify_rate_limit_hits_total or metrics count
    DB-->>Service: Transaction commit
```

---

## 5. Monitoring Workflow

Tracking application health, request throughput, and performance details.

```mermaid
graph LR
    App[Spring Boot App] -->|Exposes| Actuator[Spring Boot Actuator]
    App -->|Exports| Micrometer[Micrometer Registry]
    Actuator -->|HTTP Endpoint /actuator/prometheus| Prometheus[Prometheus Server]
    Prometheus -->|Data Source| Grafana[Grafana Dashboard]
```

---

## 6. CI/CD Workflow

The automated build, quality assurance, and deployment pipeline.

```mermaid
graph TD
    Dev[Developer git push / pull request] --> GitHub[GitHub Remote Repository]
    GitHub --> GHA[GitHub Actions Runner]
    
    subgraph Backend Pipeline
        GHA --> Mvn[Checkout Code & Setup JDK 21]
        Mvn --> Tests[Execute Maven Test Suite]
        Tests --> Jacoco[Generate JaCoCo Coverage Report]
    end
    
    subgraph Frontend Pipeline
        GHA --> Node[Setup Node 20 & npm ci]
        Node --> Build[Execute npm run build]
    end

    Tests -- Failure --> Notify[Fail Status Check]
    Build -- Failure --> Notify
    Tests -- Success --> BuildOK[Pass Status Check]
    Build -- Success --> BuildOK
```
