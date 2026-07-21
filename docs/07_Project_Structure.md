# 07 - Project Structure

The repository follows a monorepo-style structure separating backend, frontend, and configuration.

```text
Shortify/
│
├── backend/                  # Spring Boot Application
│   ├── src/main/java/com/shortify/
│   │   ├── config/           # Security, CORS, etc.
│   │   ├── controller/       # REST API endpoints
│   │   ├── dto/              # Request/Response payloads
│   │   ├── entity/           # JPA entities
│   │   ├── exception/        # GlobalExceptionHandler
│   │   ├── repository/       # Spring Data interfaces
│   │   ├── security/         # JWT Utils & Filters
│   │   ├── service/          # Business logic
│   │   └── ShortifyApplication.java
│   └── pom.xml
│
├── frontend/                 # React + Vite Application
│   ├── src/
│   │   ├── api/              # Axios instance & endpoints
│   │   ├── assets/           # Images, SVGs
│   │   ├── components/       # Reusable UI elements
│   │   ├── context/          # React Context (Auth, Theme)
│   │   ├── hooks/            # Custom React hooks
│   │   ├── layouts/          # Dashboard & Auth layouts
│   │   ├── pages/            # Routable views
│   │   └── App.jsx
│   ├── package.json
│   └── vite.config.js
│
├── database/                 
│   └── migrations/           # Flyway SQL scripts (V1__init.sql)
│
├── docs/                     # Documentation (SRS, ER Diagram, etc.)
│
├── postman/                  # Postman Collections & Environments
│
├── screenshots/              # UI screenshots for README
│
├── .github/
│   └── workflows/            # CI/CD pipelines (build.yml, deploy.yml)
│
├── README.md                 # Main project overview
├── LICENSE                   # Open source license (e.g., MIT)
└── .gitignore                # Global ignore rules
```
