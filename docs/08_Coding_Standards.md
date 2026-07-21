# 08 - Coding Standards

## 1. Naming Conventions

### Backend (Java)
- **Packages**: lowercase, descriptive (e.g., `com.shortify.controller`, `com.shortify.security.jwt`).
- **Classes**: PascalCase (e.g., `UrlService`, `JwtUtils`).
- **Interfaces**: PascalCase, often reflecting ability or service (e.g., `UrlRepository`).
- **Methods**: camelCase, verb-noun format (e.g., `findByShortCode`, `generateToken`).
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_ATTEMPTS`).

### Architecture Naming
- **Controllers**: End with `Controller` (e.g., `UrlController`).
- **DTOs**: End with `Request` or `Response` (e.g., `UrlCreateRequest`, `AuthResponse`).
- **Entities**: Singular, exact table representation (e.g., `User`, `Url`).
- **Repositories**: End with `Repository` (e.g., `UserRepository`).
- **Exceptions**: End with `Exception` (e.g., `ResourceNotFoundException`).

### Frontend (React)
- **Components/Pages**: PascalCase, file name matches component name (e.g., `Dashboard.jsx`, `CreateUrlModal.jsx`).
- **Hooks**: camelCase, prefix with `use` (e.g., `useAuth`, `useFetch`).
- **CSS/Styles**: kebab-case for classes (though Tailwind utilities are preferred).

## 2. Git Conventions

### Branch Naming
- `main` / `master`: Production-ready code.
- `develop`: Integration branch for active development.
- `feature/<ticket>-<description>`: New features (e.g., `feature/jwt-auth`).
- `fix/<ticket>-<description>`: Bug fixes (e.g., `fix/url-collision`).

### Commit Messages (Conventional Commits)
- `feat:` A new feature (e.g., `feat: implement Base62 short code generation`).
- `fix:` A bug fix (e.g., `fix: resolve CORS issue on login endpoint`).
- `docs:` Documentation only changes.
- `refactor:` Code change that neither fixes a bug nor adds a feature.
- `test:` Adding missing tests or correcting existing tests.

## 3. General Rules
- **No Magic Numbers**: Extract hardcoded values to constants or environment variables.
- **Fail Fast**: Validate DTOs at the controller layer using `@Valid` before reaching the service layer.
- **Lombok**: Use `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor` to reduce boilerplate, but avoid `@Data` on JPA Entities (use `@Getter`/`@Setter` to prevent `toString()` circular references).
