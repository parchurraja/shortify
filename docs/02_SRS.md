# 02 - Software Requirements Specification (SRS)

## 1. Actors
### User
- An authenticated individual who uses the platform to shorten URLs, manage their links, and view analytics.
### Admin
- An elevated user with access to system-wide monitoring, user management, and global statistics.
### System
- Background processes handling automated tasks (e.g., token expiration checks, rate limiting, and cache invalidation).

## 2. Use Cases
### Create URL
- A User submits a long URL (and optional custom alias).
- The System validates the input, generates a unique Base62 short code (handling collisions), and saves it to the database.

### Update URL
- A User can modify the destination of an existing short URL they own, provided it has not expired.

### Delete URL
- A User can soft-delete a URL they own. The System marks `deleted_at` with the current timestamp. The short code remains permanently retired to prevent reuse.

### Redirect
- Any visitor navigating to `/{shortCode}` is intercepted by the System.
- The System checks validity (exists, not deleted, not expired).
- The System logs analytics (User-Agent, IP data) asynchronously.
- The System issues an HTTP 302 redirect to the original URL.

### Login / Logout
- A User authenticates via email and password.
- The System verifies credentials using BCrypt and issues a JWT Access Token and Refresh Token.
- On logout, the System invalidates the Refresh Token.

## 3. Functional Requirements
- **Auth**: User registration, login, JWT issuance, and refresh.
- **URL Management**: CRUD operations for URLs, Base62 generation, custom aliases, and expiration dates.
- **Analytics**: Track total clicks, browsers, operating systems, and devices.
- **QR Codes**: Generate downloadable QR codes for short URLs.

## 4. Non-Functional Requirements
- **Performance**: Redirects must execute in < 100ms.
- **Security**: Passwords hashed via BCrypt, rate-limiting on Auth endpoints, SQL injection & XSS prevention.
- **Scalability**: Stateless backend (JWT) ready for horizontal scaling; connection pooling configured.
- **Reliability**: Graceful exception handling globally (`GlobalExceptionHandler`).
