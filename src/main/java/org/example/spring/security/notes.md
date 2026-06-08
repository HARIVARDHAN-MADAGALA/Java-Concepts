# Spring Security

## The 7 Stages

| Stage | File | Focus |
|-------|------|-------|
| 1 | Stage1_FilterChainAndArchitecture | Filter chain order, request flow, multiple chains |
| 2 | Stage2_AuthenticationAndUserDetails | UserDetailsService, PasswordEncoder, SecurityContext |
| 3 | Stage3_JwtAuthentication | JwtUtil, JwtAuthFilter, login endpoint, wiring |
| 4 | Stage4_AuthorizationAndRoles | @PreAuthorize, @PostAuthorize, @Secured, roles vs authorities |
| 5 | Stage5_OAuth2 | Social login, resource server, JwtDecoder, JwtAuthenticationConverter |
| 6 | Stage6_CsrfSessionCors | CSRF options, session policies, CORS config |
| 7 | Stage7_CustomFiltersAndExceptionHandling | Custom filters, AuthenticationEntryPoint, AccessDeniedHandler |

---

## Full Request Flow

```
HTTP Request
    ↓
DelegatingFilterProxy         (Servlet container → Spring context bridge)
    ↓
FilterChainProxy              (finds matching SecurityFilterChain)
    ↓
SecurityFilterChain (ordered filters)
    │  SecurityContextHolderFilter    ← load SecurityContext
    │  CorsFilter                     ← CORS preflight
    │  CsrfFilter                     ← CSRF token check
    │  [Your custom filters]
    │  JwtAuthFilter / BearerTokenFilter  ← authenticate from token
    │  AnonymousAuthenticationFilter  ← set anonymous if no auth yet
    │  ExceptionTranslationFilter     ← catch 401/403
    │  AuthorizationFilter            ← check permissions
    ↓
DispatcherServlet → @Controller
```

## Authentication Flow

```
POST /auth/login { username, password }
    ↓
AuthenticationManager.authenticate(UsernamePasswordAuthenticationToken)
    ↓
DaoAuthenticationProvider
    ├── UserDetailsService.loadUserByUsername(username)  ← your impl
    └── PasswordEncoder.matches(raw, encoded)
    ↓
authenticated token → SecurityContextHolder
    ↓
JwtUtil.generateToken(userDetails) → return JWT
```

## JWT Structure

```
header.payload.signature

header  : { "alg": "HS256" }
payload : { "sub": "alice", "roles": ["ROLE_USER"], "exp": 1234567890 }
signature: HMAC-SHA256(base64(header)+"."+base64(payload), secretKey)
```

## Authorization — Roles vs Authorities

```java
hasRole("ADMIN")               // checks for "ROLE_ADMIN" (prefix added)
hasAuthority("ROLE_ADMIN")     // checks for "ROLE_ADMIN" (exact)
hasAuthority("products:write") // fine-grained permission (exact)

@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasAuthority('products:write')")
@PreAuthorize("#id == authentication.principal.userId") // SpEL with method arg
@PostAuthorize("returnObject.owner == authentication.name")
@Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
```

## CSRF Rules

| Scenario | CSRF |
|----------|------|
| REST API + JWT (Authorization header) | ✅ disable |
| Traditional web app with session cookies | ✅ enable |
| SPA (React/Angular) + session cookies | enable with `CookieCsrfTokenRepository` |
| SameSite=Strict cookies | ✅ disable (browser won't send cross-site) |

## Session Policies

```
STATELESS    → no session — use for JWT REST APIs
IF_REQUIRED  → create if needed (default)
ALWAYS       → always create
NEVER        → use existing, never create
```

## CORS — Key Rules
- Configure in Spring Security, NOT only `@CrossOrigin` — Security filters run first
- Never use `allowedOrigins("*")` with `allowCredentials(true)` — browser rejects it
- OPTIONS preflight must be permitted (Spring Security handles this automatically)

## Exception Handling

```
AuthenticationException → AuthenticationEntryPoint → 401 response
AccessDeniedException   → AccessDeniedHandler       → 403 response
```

## Dependencies

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT (jjwt) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>

<!-- OAuth2 Resource Server -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>

<!-- OAuth2 Login (social login) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```
