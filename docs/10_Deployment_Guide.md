# 10 - Deployment Guide

## 1. Backend Deployment (Render)

### Prerequisites
- GitHub repository linked to Render.
- Managed MySQL instance running (e.g., on Railway or Aiven).

### Setup Steps
1. Create a New "Web Service" on Render.
2. Connect the `Shortify` repository.
3. Configure the build and start commands:
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/shortify-backend-0.0.1-SNAPSHOT.jar`
4. Define Environment Variables:
   - `SPRING_PROFILES_ACTIVE=prod`
   - `DB_URL=jdbc:mysql://<railway-host>:<port>/shortify`
   - `DB_USERNAME=<user>`
   - `DB_PASSWORD=<pass>`
   - `JWT_SECRET=<long-secure-random-string>`
   - `CORS_ALLOWED_ORIGINS=https://shortify-frontend.vercel.app`

## 2. Frontend Deployment (Vercel)

### Setup Steps
1. Create a New Project on Vercel.
2. Import the `Shortify` repository.
3. Set the Root Directory to `frontend/`.
4. Framework Preset: **Vite**.
5. Define Environment Variables:
   - `VITE_API_BASE_URL=https://shortify-backend.onrender.com/api`
6. Deploy.

## 3. Production Profiles
- The backend utilizes `application-prod.properties` when `SPRING_PROFILES_ACTIVE=prod` is set.
- This ensures `ddl-auto` is set to `validate` (relying exclusively on Flyway) and disables debug logging to improve performance and security.
