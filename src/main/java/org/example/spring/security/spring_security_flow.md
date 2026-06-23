# Spring Security — Complete Flow & Implementation Guide

---

## Full Request Flow (Big Picture)

```
HTTP Request
     ↓
SecurityFilterChain          → CSRF disabled, Stateless session, URL rules
     ↓
OncePerRequestFilter (JWT)   → extract token → extractUsername → validateToken
     ↓
UserDetailsService           → loadUserByUsername → CustomUserDetails (from DB)
     ↓
PasswordEncoder              → BCrypt match (during login only)
     ↓
AuthenticationManager        → delegates to DaoAuthenticationProvider
     ↓
SecurityContext              → stores authenticated user
     ↓
Controller                   → @PreAuthorize / @AuthenticationPrincipal
```

---

## Step 1 — SecurityFilterChain (Entry Point)

Defines URL access rules, disables CSRF, makes session stateless, registers JWT filter.

```java
@Bean
SecurityFilterChain chain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())                                          // stateless JWT — no CSRF needed
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/login", "/auth/register").permitAll()      // public endpoints
            .requestMatchers("/admin/**").hasRole("ADMIN")                     // role-based
            .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
            .anyRequest().authenticated())                                     // everything else needs auth
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)// register JWT filter
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))  // 401
            .accessDeniedHandler((req, res, e) -> res.sendError(403)))                    // 403
        .build();
}
```

---

## Step 2 — PasswordEncoder

Always define as a @Bean to avoid circular dependency issues.

```java
@Bean
PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();   // strength default = 10
}
```

---

## Step 3 — UserDetailsService

Loads user from DB by username. Returns UserDetails (username, encoded password, roles).

```java
@Service
class CustomUserDetailsService implements UserDetailsService {

    @Autowired UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())                          // must be BCrypt encoded
            .roles(user.getRole())                                 // e.g. "ADMIN" → "ROLE_ADMIN"
            .build();
    }
}
```

### Custom UserDetails (optional — to carry extra fields like userId, email)

```java
class CustomUserDetails implements UserDetails {
    private final User user;

    public CustomUserDetails(User user) { this.user = user; }

    public Long getUserId() { return user.getId(); }           // extra field
    public String getEmail() { return user.getEmail(); }       // extra field

    @Override public String getUsername() { return user.getUsername(); }
    @Override public String getPassword() { return user.getPassword(); }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
```

---

## Step 4 — AuthenticationManager

Wires UserDetailsService + PasswordEncoder → delegates to DaoAuthenticationProvider internally.

```java
@Bean
AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
    // Spring Boot auto-wires your UserDetailsService + PasswordEncoder into DaoAuthenticationProvider
}
```

### What DaoAuthenticationProvider does internally

```
authManager.authenticate(UsernamePasswordAuthenticationToken)
    ↓
DaoAuthenticationProvider
    ├── UserDetailsService.loadUserByUsername(username)   → fetch user
    └── PasswordEncoder.matches(rawPassword, encodedPassword)  → verify
    ↓
returns authenticated token → set in SecurityContextHolder
```

---

## Step 5 — JWT Filter (OncePerRequestFilter)

Runs once per request. Extracts, validates token, sets SecurityContext.

```java
@Component
class JwtFilter extends OncePerRequestFilter {

    @Autowired JwtUtil jwtUtil;
    @Autowired CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {

        String header = req.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String username = jwtUtil.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(authToken);  // mark as authenticated
                }
            }
        }
        chain.doFilter(req, res);   // always continue chain
    }
}
```

---

## Step 6 — JwtUtil (Token operations)

```java
@Component
class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Generate token
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
            .subject(userDetails.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 86_400_000))  // 24 hours
            .signWith(getKey())
            .compact();
    }

    // Extract username from token
    public String extractUsername(String token) {
        return Jwts.parser().verifyWith(getKey()).build()
            .parseSignedClaims(token).getPayload().getSubject();
    }

    // Check expiry
    private boolean isTokenExpired(String token) {
        Date expiry = Jwts.parser().verifyWith(getKey()).build()
            .parseSignedClaims(token).getPayload().getExpiration();
        return expiry.before(new Date());
    }

    // Validate: username match + not expired
    public boolean validateToken(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}
```

---

## Step 7 — Login Endpoint (Authenticate + Return Token)

```java
@RestController
@RequestMapping("/auth")
class AuthController {

    @Autowired AuthenticationManager authManager;
    @Autowired JwtUtil jwtUtil;
    @Autowired CustomUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        try {
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        // encode password before saving
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepo.save(user);
        return ResponseEntity.ok("Registered successfully");
    }
}
```

---

## Step 8 — Controller Authorization

```java
@RestController
@RequestMapping("/api")
class ResourceController {

    // Role-based access
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/data")
    public String adminOnly() { return "admin data"; }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/user/data")
    public String userAndAdmin() { return "user data"; }

    // Method-arg based — user can only access their own data
    @PreAuthorize("#id == authentication.principal.userId")
    @GetMapping("/user/{id}")
    public String getUser(@PathVariable Long id) { return "user " + id; }

    // PostAuthorize — check after method returns
    @PostAuthorize("returnObject.owner == authentication.name")
    @GetMapping("/document/{id}")
    public Document getDocument(@PathVariable Long id) { return docRepo.findById(id); }

    // JWT — get logged in user from SecurityContext
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails) {
        return userDetails.getUsername();
    }

    // Get custom fields from CustomUserDetails
    @GetMapping("/profile/full")
    public String fullProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userDetails.getEmail() + " | id: " + userDetails.getUserId();
    }

    // OAuth2 — get logged in user
    @GetMapping("/oauth/profile")
    public String oauthProfile(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAttribute("name");      // from Google/GitHub
    }
}
```

---

## Step 9 — Enable @PreAuthorize

```java
@SpringBootApplication
@EnableMethodSecurity   // required for @PreAuthorize, @PostAuthorize, @Secured to work
public class Application {
    public static void main(String[] args) { SpringApplication.run(Application.class, args); }
}
```

---

## What's Missing (Important Additions)

### Token Refresh
```java
// Short-lived access token + long-lived refresh token pattern
@PostMapping("/refresh")
public ResponseEntity<String> refresh(@RequestBody RefreshRequest request) {
    String username = jwtUtil.extractUsername(request.getRefreshToken());
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    if (jwtUtil.validateToken(request.getRefreshToken(), userDetails)) {
        return ResponseEntity.ok(jwtUtil.generateToken(userDetails));  // new access token
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
}
```

### Logout — Blacklisting Token
```java
// JWT is stateless — blacklist invalidated tokens in Redis
@PostMapping("/logout")
public ResponseEntity<String> logout(@RequestHeader("Authorization") String header) {
    String token = header.substring(7);
    redisTemplate.opsForValue().set("blacklist:" + token, "true",
        Duration.ofMillis(jwtUtil.getExpirationMs()));   // expire when token expires
    return ResponseEntity.ok("Logged out");
}

// In JwtFilter — check blacklist before validating
if (Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token))) {
    chain.doFilter(req, res);
    return;
}
```

### application.properties
```properties
jwt.secret=your-256-bit-secret-key-here-must-be-long-enough
jwt.expiration=86400000

spring.security.oauth2.client.registration.google.client-id=<client-id>
spring.security.oauth2.client.registration.google.client-secret=<client-secret>
```

---

## Roles vs Authorities

| | hasRole("ADMIN") | hasAuthority("ROLE_ADMIN") | hasAuthority("products:write") |
|---|---|---|---|
| Prefix | adds "ROLE_" auto | exact match | exact match |
| Use case | coarse-grained | coarse-grained | fine-grained permissions |

---

## Exception Flow

```
Request fails authentication  → AuthenticationException  → AuthenticationEntryPoint  → 401
Request fails authorization   → AccessDeniedException    → AccessDeniedHandler       → 403
```

---

## Quick Summary Table

| Component | Responsibility |
|---|---|
| SecurityFilterChain | URL rules, CSRF, session, filter order |
| OncePerRequestFilter (JwtFilter) | Extract + validate JWT per request |
| UserDetailsService | Load user from DB by username |
| PasswordEncoder | BCrypt encode + match passwords |
| AuthenticationManager | Coordinate login via DaoAuthenticationProvider |
| JwtUtil | Generate, extract, validate JWT tokens |
| @PreAuthorize | Method-level role/permission check |
| @AuthenticationPrincipal | Inject logged-in user into controller |
| @AuthenticationPrincipal OAuth2User | Inject OAuth2 logged-in user |
