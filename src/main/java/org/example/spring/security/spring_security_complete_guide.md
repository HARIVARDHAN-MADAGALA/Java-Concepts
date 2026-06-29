# Spring Security Complete Guide — End to End Flow

## Let's Start With an Analogy

Think of Spring Security as **airport security**.

```
Your Spring Boot App = The Airport
Spring Security = Security + Immigration + Boarding Gate staff
Filters = Each checkpoint (baggage scan, ID check, boarding pass scan)
You don't build security from scratch, you configure existing checkpoints.
```

## The Big Picture — What Happens to Every Request

```
Browser/Postman sends request
        |
        ▼
DelegatingFilterProxy (entry point from servlet container)
        |
        ▼
FilterChainProxy (holds the list of security filters)
        |
        ▼
Security Filter Chain (10-15 filters run in fixed order)
        |
        ▼
DispatcherServlet → Your Controller
        |
        ▼
Response goes back through filters → Browser
```

## 1. Where Spring Security Plugs Into Spring MVC

### Analogy:
Like a **security gate built INTO the building's main entrance**, before you even reach the reception desk (DispatcherServlet).

```
Tomcat/Servlet Container
        |
        ▼
┌────────────────────────┐
│ DelegatingFilterProxy    │  ← registered as a normal Servlet Filter
└────────────────────────┘
        |
        ▼
┌────────────────────────┐
│ FilterChainProxy (Bean)  │  ← the real Spring-managed filter, named "springSecurityFilterChain"
└────────────────────────┘
        |
        ▼
   SecurityFilterChain (your configured filters list)
        |
        ▼
DispatcherServlet → Controller
```

| Class | Role |
|---|---|
| `DelegatingFilterProxy` | Bridges Servlet container's filter world to Spring's bean world |
| `FilterChainProxy` | The actual filter Spring registers; delegates to one matching `SecurityFilterChain` |
| `SecurityFilterChain` | An ordered list of filters for a given URL pattern (you usually define ONE bean of this) |

## 2. The Security Filter Chain — Order Matters

### Analogy:
Like **multiple checkpoints in a queue** — you can't skip ahead, each checkpoint does ONE job.

```
1.  SecurityContextHolderFilter        → loads SecurityContext (from session/JWT) for this request
2.  CsrfFilter                          → checks/generates CSRF token
3.  CorsFilter                          → handles cross-origin headers
4.  LogoutFilter                        → handles /logout
5.  UsernamePasswordAuthenticationFilter → handles login form POST (/login)
6.  JwtAuthenticationFilter (custom)    → your own filter for JWT validation
7.  BasicAuthenticationFilter           → handles HTTP Basic auth header
8.  RequestCacheAwareFilter             → replays original request after login redirect
9.  SecurityContextHolderAwareRequestFilter
10. AnonymousAuthenticationFilter      → assigns "anonymous" if nobody is authenticated
11. ExceptionTranslationFilter         → catches AuthenticationException/AccessDeniedException
12. AuthorizationFilter (FilterSecurityInterceptor in older versions) → final gatekeeper, checks permissions
        |
        ▼
   Your Controller
```

**Key rule:** Authentication filters run BEFORE the Authorization filter. You must prove WHO you are before Spring checks WHAT you can do.

## 3. SecurityContextHolder & SecurityContext — Who Am I, Right Now?

### Analogy:
Like a **visitor badge** that's checked at every door inside the building for the duration of your visit.

```
┌──────────────────────────────────┐
│        SecurityContextHolder       │  ← static holder, ThreadLocal based
│                                     │
│   SecurityContext                  │
│      └── Authentication            │
│             ├── principal (who)     │
│             ├── credentials (pwd)   │
│             └── authorities (roles) │
└──────────────────────────────────┘
```

```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();
Collection<? extends GrantedAuthority> roles = auth.getAuthorities();
```

| Class/Interface | Role |
|---|---|
| `SecurityContextHolder` | Static accessor; uses `ThreadLocal` by default (so it's per-request, per-thread) |
| `SecurityContext` | Holds the current `Authentication` object |
| `Authentication` | Represents "who is making this request" — principal + authorities + authenticated flag |

## 4. The Authentication Object

```java
public interface Authentication extends Principal {
    Collection<? extends GrantedAuthority> getAuthorities();
    Object getCredentials();
    Object getDetails();
    Object getPrincipal();
    boolean isAuthenticated();
}
```

Common implementations:

| Implementation | Used For |
|---|---|
| `UsernamePasswordAuthenticationToken` | Form login / username+password flows |
| `JwtAuthenticationToken` | When using Spring's OAuth2 Resource Server with JWT |
| `AnonymousAuthenticationToken` | Unauthenticated requests (still gets a token, just with "ROLE_ANONYMOUS") |
| `PreAuthenticatedAuthenticationToken` | Headers/certs already validated by something upstream (e.g. API Gateway) |

## 5. Form Login Flow — Username & Password (Stateful, Session-based)

### Analogy:
Like **checking in at a hotel reception** — show ID once, get a room key card (session cookie) that works for every door after that.

```
1. POST /login (username, password)
        |
        ▼
2. UsernamePasswordAuthenticationFilter intercepts
        |
        ▼
3. Creates UsernamePasswordAuthenticationToken(username, password) [NOT authenticated yet]
        |
        ▼
4. Passes to AuthenticationManager.authenticate(token)
        |
        ▼
5. ProviderManager (the default AuthenticationManager) loops through AuthenticationProviders
        |
        ▼
6. DaoAuthenticationProvider picked (because it supports UsernamePasswordAuthenticationToken)
        |
        ▼
7. Calls UserDetailsService.loadUserByUsername(username) → returns UserDetails
        |
        ▼
8. PasswordEncoder.matches(rawPassword, encodedPasswordFromDB)
        |
        ▼
9. Match? → builds a NEW fully-authenticated UsernamePasswordAuthenticationToken
        |
        ▼
10. SecurityContextHolder.getContext().setAuthentication(token)
        |
        ▼
11. SecurityContextHolderFilter / HttpSessionSecurityContextRepository saves SecurityContext into the HTTP Session
        |
        ▼
12. JSESSIONID cookie sent back to browser
        |
        ▼
13. Every future request: cookie → session → SecurityContext restored automatically
```

### Key classes in this flow:

| Class/Interface | Role |
|---|---|
| `UsernamePasswordAuthenticationFilter` | Intercepts the login POST request |
| `AuthenticationManager` | Interface with one method: `authenticate(Authentication)` |
| `ProviderManager` | Default impl of `AuthenticationManager`; delegates to a list of `AuthenticationProvider`s |
| `AuthenticationProvider` | Interface that actually performs ONE type of authentication check |
| `DaoAuthenticationProvider` | Default provider for username/password; uses `UserDetailsService` + `PasswordEncoder` |
| `UserDetailsService` | Interface with `loadUserByUsername(String)` — YOU implement this to fetch from DB |
| `UserDetails` | Represents the user record Spring Security understands (username, password, authorities, enabled flags) |
| `PasswordEncoder` | Encodes/verifies passwords (e.g. `BCryptPasswordEncoder`) |
| `SecurityContextRepository` | Persists/restores `SecurityContext` (session-based by default) |

### Code — Custom UserDetailsService:

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),               // already BCrypt encoded
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList())
        );
    }
}
```

### Code — Security Config (Spring Security 6, lambda DSL):

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.loginPage("/login").permitAll())
            .logout(logout -> logout.logoutUrl("/logout"));

        return http.build();
    }
}
```

## 6. JWT Authentication Flow — Stateless (What FoodieFleet/Microservices Use)

### Analogy:
Like a **concert wristband** — checked once at entry (login), then the wristband itself (token) is shown at every stage door. No one needs to look you up again.

```
LOGIN (one time):
1. POST /auth/login {username, password}
        |
        ▼
2. AuthenticationManager.authenticate() — same DaoAuthenticationProvider flow as above
        |
        ▼
3. On success: JwtUtil.generateToken(userDetails)
        |
        ▼
4. Server returns: { "token": "eyJhbGciOiJI..." }
        |
        ▼
5. Client stores token (localStorage / memory) and sends it on every future request:
   Authorization: Bearer eyJhbGciOiJI...
```

```
EVERY SUBSEQUENT REQUEST:
1. Request hits JwtAuthenticationFilter (custom OncePerRequestFilter)
        |
        ▼
2. Extracts token from "Authorization: Bearer <token>" header
        |
        ▼
3. JwtUtil.validateToken(token) → checks signature + expiry
        |
        ▼
4. JwtUtil.extractUsername(token)
        |
        ▼
5. userDetailsService.loadUserByUsername(username)
        |
        ▼
6. new UsernamePasswordAuthenticationToken(userDetails, null, authorities)
        |
        ▼
7. SecurityContextHolder.getContext().setAuthentication(token)
        |
        ▼
8. Request continues to AuthorizationFilter → Controller
        |
        ▼
9. NOTHING is stored server-side — next request repeats steps 1-7 fresh
```

### Key classes in JWT flow:

| Class | Role |
|---|---|
| `OncePerRequestFilter` | Base class you extend to write a custom filter that runs exactly once per request |
| `JwtAuthenticationFilter` (custom) | Your own filter — extracts token, validates, sets `SecurityContext` |
| `JwtUtil` / `JwtService` (custom) | Generates token, signs it, validates signature & expiry, extracts claims |
| `Jwts` (from `io.jsonwebtoken`) | Builder class from the `jjwt` library to create/parse tokens |
| `SecretKey` / `Keys` | Used to sign the JWT with HMAC (HS256) or load an RSA key pair |

### Code — Custom JWT Filter:

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtUtil.extractUsername(token);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

### Code — JwtUtil:

```java
@Component
public class JwtUtil {

    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long expirationMs = 3600000; // 1 hour

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isExpired(token);
    }

    private boolean isExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody();
    }
}
```

### Code — Security Config for JWT (stateless):

```java
@Configuration
@EnableWebSecurity
public class JwtSecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

**Why `SessionCreationPolicy.STATELESS`?** Tells Spring Security: "Never create or use an HTTP session." This is what makes JWT auth horizontally scalable across microservices — no shared session store needed.

## 7. Authorization — Who Can Access What

### Analogy:
Authentication = showing your ID at the door.
Authorization = the bouncer checking the guest list to see if YOUR ID is allowed into THIS specific room.

```
Request (already authenticated, SecurityContext populated)
        |
        ▼
AuthorizationFilter (was FilterSecurityInterceptor pre Spring Security 6)
        |
        ▼
AuthorizationManager<HttpServletRequest>
        |
        ▼
Checks against rules defined in authorizeHttpRequests():
  - hasRole("ADMIN")?
  - hasAuthority("ORDER_WRITE")?
  - permitAll()?
        |
        ▼
ALLOWED → Controller          DENIED → AccessDeniedException thrown
```

| Class/Interface | Role |
|---|---|
| `AuthorizationFilter` | Last filter in the chain; the final gatekeeper before your controller |
| `AuthorizationManager<T>` | Functional interface — decides `ALLOW`/`DENY` for a given request |
| `RequestMatcherDelegatingAuthorizationManager` | Default impl; matches URL patterns to specific rules in order |
| `FilterSecurityInterceptor` | Older (pre Spring Security 6) name for the same concept, used with `AccessDecisionManager` |

## 8. Method-Level Security — @PreAuthorize, @Secured

### Analogy:
Like a **locked drawer inside an already-unlocked office** — even if you're in the building (authenticated), this specific drawer (method) checks your specific clearance.

```
Controller calls service.placeOrder()
        |
        ▼
Spring AOP Proxy intercepts the call (because method has @PreAuthorize)
        |
        ▼
MethodSecurityInterceptor evaluates the SpEL expression
        |
        ▼
Expression true? → method executes
Expression false? → AccessDeniedException thrown, method never runs
```

```java
@Configuration
@EnableMethodSecurity   // enables @PreAuthorize, @PostAuthorize, @Secured
public class MethodSecurityConfig { }

@Service
public class OrderService {

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOrder(Long orderId) { ... }

    @PreAuthorize("#userId == authentication.principal.id")
    public Order getOrder(Long userId) { ... }

    @Secured("ROLE_USER")
    public void placeOrder(Order order) { ... }
}
```

| Annotation | Notes |
|---|---|
| `@PreAuthorize` | Evaluated BEFORE method runs; supports full SpEL (can reference method args) |
| `@PostAuthorize` | Evaluated AFTER method runs; can check the returned object |
| `@Secured` | Older, simpler — only supports role names, no SpEL |
| `@RolesAllowed` | JSR-250 standard annotation, also supported |

## 9. Exception Handling — When Auth/Access Fails

### Analogy:
Like having two different signs: "Please show ID" (you weren't authenticated at all) vs "Sorry, staff only" (you're identified, but not allowed here).

```
Request fails security check
        |
        ▼
ExceptionTranslationFilter catches it
        |
        ▼
   AuthenticationException?                AccessDeniedException?
        |                                          |
        ▼                                          ▼
AuthenticationEntryPoint.commence()      AccessDeniedHandler.handle()
   (e.g. redirect to /login,                (e.g. return 403 Forbidden
    or return 401 Unauthorized for APIs)      with a JSON error body)
```

```java
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"Unauthorized - invalid or missing token\"}");
    }
}
```

## 10. CORS & CSRF

### Analogy:
- **CORS** = the bouncer checking which OTHER websites are allowed to talk to your API from a browser.
- **CSRF** = making sure a request claiming to be from a logged-in user actually came from YOUR form, not a malicious site tricking their browser.

```
CORS: Browser sends OPTIONS preflight → CorsFilter checks allowed origins/methods/headers
CSRF: Browser sends state-changing request (POST/PUT/DELETE) → CsrfFilter checks token matches
```

```java
http
    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
    .csrf(csrf -> csrf.disable()); // typically disabled for stateless JWT REST APIs,
                                     // kept enabled for session-based form login apps
```

**Rule of thumb:** Stateless JWT APIs (no cookies) → CSRF disabled is safe. Session/cookie-based apps → keep CSRF enabled.

## 11. OAuth2 Login Flow (Brief) — "Login with Google/GitHub"

### Analogy:
Like using your **passport** (Google account) to enter a partner country (your app) instead of applying for a new visa (creating a new password).

```
1. User clicks "Login with Google"
        |
        ▼
2. Redirect to Google's consent screen (Authorization Server)
        |
        ▼
3. User approves → Google redirects back with an authorization code
        |
        ▼
4. OAuth2LoginAuthenticationFilter exchanges code for access token
        |
        ▼
5. OAuth2UserService fetches user profile from Google
        |
        ▼
6. OAuth2AuthenticationToken created → stored in SecurityContext
```

| Class | Role |
|---|---|
| `OAuth2LoginAuthenticationFilter` | Handles the redirect callback (`/login/oauth2/code/google`) |
| `ClientRegistration` | Holds the OAuth2 provider's config (client ID, secret, URLs) |
| `OAuth2UserService` | Fetches the user's profile from the provider after token exchange |
| `OAuth2AuthenticationToken` | The `Authentication` implementation used for OAuth2 logins |

## 12. Password Encoding

### Analogy:
Never store the actual key to the house — store a **one-way scrambled fingerprint** of it that can only be checked, never reversed.

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(); // includes salt automatically, cost factor 10 by default
}
```

| Encoder | Notes |
|---|---|
| `BCryptPasswordEncoder` | Industry standard, adaptive cost, auto-salted — use this by default |
| `Pbkdf2PasswordEncoder` | FIPS-compliant alternative |
| `Argon2PasswordEncoder` | Newer, memory-hard, winner of the Password Hashing Competition |
| `NoOpPasswordEncoder` | Plaintext — **never use in production** |

## 13. Key Libraries / Maven Dependencies Involved

```xml
<!-- Core Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT support (jjwt library) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>

<!-- OAuth2 client (Login with Google/GitHub etc) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>

<!-- OAuth2 resource server (validating JWTs issued by an external IdP) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>

<!-- For method security testing -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

| Library | What it actually contains |
|---|---|
| `spring-security-core` | `Authentication`, `AuthenticationManager`, `UserDetails`, core interfaces |
| `spring-security-web` | All the servlet `Filter` classes (`FilterChainProxy`, `UsernamePasswordAuthenticationFilter`, etc.) |
| `spring-security-config` | The `HttpSecurity` DSL, `@EnableWebSecurity`, `@EnableMethodSecurity` |
| `spring-security-crypto` | `PasswordEncoder` implementations (`BCryptPasswordEncoder`, etc.) |
| `spring-security-oauth2-client` | OAuth2 login flow support |
| `spring-security-oauth2-jose` | JWT decoding/encoding for OAuth2 (JOSE = JSON Object Signing & Encryption) |
| `jjwt` (third-party, not Spring) | Used to manually build/parse/sign JWTs in custom filters like above |
| `nimbus-jose-jwt` (third-party) | Used internally by `spring-security-oauth2-jose` for JWT processing |

## Complete End-to-End Flow — JWT Login + Protected Request

```
┌─────────┐  POST /auth/login          ┌──────────────────┐
│ Client   │ ──────────────────────────▶│  AuthController   │
└─────────┘                             └──────────────────┘
                                                  |
                                                  ▼
                                   AuthenticationManager.authenticate()
                                                  |
                                                  ▼
                                   DaoAuthenticationProvider
                                       ├── UserDetailsService.loadUserByUsername()
                                       └── PasswordEncoder.matches()
                                                  |
                                                  ▼
                                       JwtUtil.generateToken()
                                                  |
                                                  ▼
┌─────────┐  { "token": "eyJ..." }     ┌──────────────────┐
│ Client   │ ◀──────────────────────────│  AuthController   │
└─────────┘                             └──────────────────┘

────────────────────────────────────────────────────────────

┌─────────┐  GET /orders                ┌──────────────────────┐
│ Client   │  Authorization: Bearer ...  │ JwtAuthenticationFilter│
└─────────┘ ───────────────────────────▶ └──────────────────────┘
                                                  |
                                  validate token, set SecurityContext
                                                  |
                                                  ▼
                                          AuthorizationFilter
                                       (checks hasRole/hasAuthority)
                                                  |
                                          ALLOWED |  DENIED
                                                  ▼      ▼
                                        OrderController  403 Forbidden
```

## Summary Table — Every Important Class/Interface

| Class/Interface | Layer | One-line Purpose |
|---|---|---|
| `DelegatingFilterProxy` | Bootstrap | Bridges Servlet filters to Spring beans |
| `FilterChainProxy` | Bootstrap | Holds and delegates to the real `SecurityFilterChain` |
| `SecurityFilterChain` | Config | Your ordered list of security filters for a URL pattern |
| `SecurityContextHolderFilter` | Filter | Loads `SecurityContext` for the current request |
| `UsernamePasswordAuthenticationFilter` | Filter | Handles form-login POST requests |
| `OncePerRequestFilter` | Filter (base) | Base class for custom filters like JWT auth filter |
| `BasicAuthenticationFilter` | Filter | Handles HTTP Basic auth header |
| `ExceptionTranslationFilter` | Filter | Routes auth/access exceptions to entry point/handler |
| `AuthorizationFilter` | Filter | Final gatekeeper — allow or deny based on rules |
| `AuthenticationManager` | Core | Interface: `authenticate(Authentication)` |
| `ProviderManager` | Core | Default `AuthenticationManager`; delegates to providers |
| `AuthenticationProvider` | Core | Performs one specific kind of authentication check |
| `DaoAuthenticationProvider` | Core | Default provider for username/password via DB |
| `UserDetailsService` | Core | YOU implement: fetch user from DB by username |
| `UserDetails` | Core | Spring's view of a user record |
| `PasswordEncoder` | Core | Encode/verify passwords |
| `Authentication` | Core | "Who is making this request" — principal + authorities |
| `SecurityContext` | Core | Wraps the current `Authentication` |
| `SecurityContextHolder` | Core | Static, `ThreadLocal`-based access to `SecurityContext` |
| `GrantedAuthority` | Core | A single permission/role string (e.g. `ROLE_ADMIN`) |
| `AuthorizationManager` | Authorization | Decides ALLOW/DENY for a request or method |
| `MethodSecurityInterceptor` | Authorization | AOP interceptor behind `@PreAuthorize` |
| `AuthenticationEntryPoint` | Exception | Handles "not authenticated at all" (401) |
| `AccessDeniedHandler` | Exception | Handles "authenticated but not allowed" (403) |
| `JwtUtil` / `JwtService` (custom) | JWT | Generate/validate/parse tokens |
| `OAuth2LoginAuthenticationFilter` | OAuth2 | Handles OAuth2 provider redirect callback |
| `OAuth2UserService` | OAuth2 | Fetches user profile from external provider |
