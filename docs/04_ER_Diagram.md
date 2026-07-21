# 04 - Entity Relationship Diagram

## Schema Diagram

```mermaid
erDiagram
    users {
        bigint id PK
        varchar(100) name
        varchar(150) email "UNIQUE"
        varchar(255) password
        varchar(20) role "DEFAULT 'ROLE_USER'"
        datetime created_at
        datetime updated_at
        datetime deleted_at "NULLABLE"
    }

    urls {
        bigint id PK
        bigint user_id FK
        text original_url
        varchar(20) short_code "UNIQUE"
        varchar(50) custom_alias "NULLABLE"
        varchar(255) password_hash "NULLABLE"
        datetime expires_at "NULLABLE"
        bigint max_clicks "NULLABLE"
        bigint click_count "DEFAULT 0"
        boolean is_active "DEFAULT true"
        datetime created_at
        datetime updated_at
        datetime deleted_at "NULLABLE"
    }

    url_clicks {
        bigint id PK
        bigint url_id FK
        varchar(50) browser "NULLABLE"
        varchar(50) device "NULLABLE"
        varchar(50) os "NULLABLE"
        varchar(50) country "NULLABLE"
        datetime clicked_at
    }

    refresh_tokens {
        bigint id PK
        bigint user_id FK
        varchar(255) token "UNIQUE"
        datetime expiry_date
        boolean revoked "DEFAULT false"
        datetime created_at
    }

    users ||--o{ urls : "creates"
    users ||--o{ refresh_tokens : "owns"
    urls ||--o{ url_clicks : "tracks"
```

## Database Rules & Constraints

### Primary Keys
- All tables use `id` (`BIGINT`, `AUTO_INCREMENT`) as the primary key.

### Foreign Keys
- `urls.user_id` references `users.id`.
- `url_clicks.url_id` references `urls.id` (ON DELETE CASCADE).
- `refresh_tokens.user_id` references `users.id` (ON DELETE CASCADE).

### Indexes
- `idx_urls_user_id` on `urls(user_id)`
- `idx_urls_short_code` on `urls(short_code)`
- `idx_urls_created_at` on `urls(created_at)`
- `idx_url_clicks_url_id` on `url_clicks(url_id)`

### Unique Constraints
- `users(email)`
- `urls(short_code)`
- `refresh_tokens(token)`

### Cascade Rules
- **Soft Delete**: Deleting a User or URL is handled via an `@SQLDelete` annotation updating the `deleted_at` timestamp. 
- **Hard Deletes**: `url_clicks` and `refresh_tokens` are deleted automatically via SQL `ON DELETE CASCADE` if the parent entity is hard-deleted from the database.
