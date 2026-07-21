# 01 - Project Vision

## Goal
Build a production-ready, highly scalable, and secure URL Shortener platform (Shortify) that serves as a flagship portfolio project.

## Core Value Proposition
Shortify allows users to convert long, unwieldy URLs into concise, branded short links. It provides robust analytics to track link engagement and includes enterprise-grade features like role-based access control, QR code generation, and a fully responsive dashboard.

## Specifications
- **Location**: `C:\projects\Shortify`
- **Database**: MySQL (root / Raja123)
- **Timeline**: 10-12 weeks (2-3 hours/day)
- **Focus**: Version 1 (Core app -> Deploy -> Iterate). Advanced features (Webhooks, API keys, WebSockets) are deferred to Version 2 to prioritize a fast, high-quality MVP deployment.

## Tech Stack
- **Frontend**: React 19, Vite, Tailwind CSS, React Router, Axios, Recharts
- **Backend**: Java 21, Spring Boot 3, Spring Security, JWT, Spring Data JPA, Hibernate, Bucket4j, ZXing
- **Database & Migrations**: MySQL, Flyway
- **Deployment**: Vercel (Frontend), Render (Backend), Railway (MySQL)
- **CI/CD**: GitHub Actions
