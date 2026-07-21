# 05 - API Specification

All endpoints return a standard `ApiResponse<T>` wrapper:
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

## 1. Authentication
### POST /api/auth/login
- **Method**: `POST`
- **Auth**: None
- **Request**:
  ```json
  {
    "email": "user@example.com",
    "password": "Password123!"
  }
  ```
- **Validation**: `email` must be valid, `password` not empty.
- **Response**: `200 OK`
  ```json
  {
    "accessToken": "eyJhbG...",
    "refreshToken": "d7a8...",
    "user": { "id": 1, "name": "User" }
  }
  ```
- **Errors**: `401 Unauthorized` (Invalid credentials).

## 2. URL Management
### GET /api/urls
- **Method**: `GET`
- **Auth**: Bearer Token
- **Query Params**: `page` (default 0), `size` (default 10), `sort` (default `createdAt,desc`), `search` (optional string).
- **Response**: `200 OK` (Paginated list of URLs)
  ```json
  {
    "content": [
      {
        "id": 1,
        "originalUrl": "https://verylong...",
        "shortCode": "aB3x9",
        "clickCount": 42
      }
    ],
    "totalPages": 5,
    "totalElements": 50
  }
  ```

### POST /api/urls
- **Method**: `POST`
- **Auth**: Bearer Token
- **Request**:
  ```json
  {
    "originalUrl": "https://example.com/very/long/path",
    "customAlias": "my-promo" // optional
  }
  ```
- **Validation**: `originalUrl` must be a valid URL string.
- **Response**: `201 Created`
  ```json
  {
    "id": 2,
    "shortUrl": "https://shorti.fy/my-promo",
    "shortCode": "my-promo"
  }
  ```
- **Errors**: `400 Bad Request` (Alias taken).

## 3. Analytics
### GET /api/analytics/dashboard
- **Method**: `GET`
- **Auth**: Bearer Token
- **Response**: `200 OK`
  ```json
  {
    "totalUrls": 15,
    "totalClicks": 1240,
    "clicksPastWeek": [
      { "date": "2023-10-01", "clicks": 45 }
    ]
  }
  ```
