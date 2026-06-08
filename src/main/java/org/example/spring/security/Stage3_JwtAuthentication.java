package org.example.spring.security;

/// Stage 3 — JWT Authentication
///
/// JWT (JSON Web Token) flow:
///
///   1. Client sends POST /auth/login  { username, password }
///   2. Server authenticates → generates JWT → returns it
///   3. Client stores JWT (memory or cookie) and sends it as:
///        Authorization: Bearer <token>
///   4. On every request: JwtAuthFilter extracts token → validates → sets SecurityContext
///   5. No server-side session needed → stateless
///
/// JWT structure:  header.payload.signature
///   header  : { "alg": "HS256", "typ": "JWT" }
///   payload : { "sub": "alice", "roles": ["ROLE_USER"], "iat": ..., "exp": ... }
///   signature: HMAC-SHA256(base64(header) + "." + base64(payload), secretKey)
///
/// The server NEVER stores the token — it just validates the signature on each request.
///
/// Dependencies needed:
///   io.jsonwebtoken:jjwt-api:0.12.x
///   io.jsonwebtoken:jjwt-impl:0.12.x
///   io.jsonwebtoken:jjwt-jackson:0.12.x

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

// ════════════════════════════════════════════════════════
// 1. JWT Utility — generate and validate tokens
// ════════════════════════════════════════════════════════
@Component
class JwtUtil {

    // ── store this in application.properties / AWS Secrets Manager — NEVER hardcode ──
    @Value("${jwt.secret:my-very-long-secret-key-at-least-32-chars}")
    private String secret;

    @Value("${jwt.expiration-ms:86400000}") // 24 hours default
    private long expirationMs;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ── generate token ──
    public String generateToken(UserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities().stream()
            .map(a -> a.getAuthority())
            .collect(Collectors.toList());

        return Jwts.builder()
            .subject(userDetails.getUsername())
            .claim("roles", roles)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(key())
            .compact();
    }

    // ── extract username from token ──
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    // ── validate: checks signature + expiry ──
    public boolean isValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isExpired(token);
        } catch (JwtException e) {
            return false; // tampered, expired, wrong signature
        }
    }

    private boolean isExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(key())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}

// ════════════════════════════════════════════════════════
// 2. JWT Filter — runs once per request, before auth checks
// ════════════════════════════════════════════════════════
@Component
class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    JwtAuthFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil            = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // ── 1. extract token from Authorization header ──
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response); // no token → pass through (AnonymousFilter sets anonymous)
            return;
        }

        String token = header.substring(7); // strip "Bearer "

        // ── 2. only process if not already authenticated ──
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String username = jwtUtil.extractUsername(token);
            UserDetails user = userDetailsService.loadUserByUsername(username);

            // ── 3. validate and set SecurityContext ──
            if (jwtUtil.isValid(token, user)) {
                var authToken = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                // ↑ from this point on, the request is authenticated
            }
        }

        chain.doFilter(request, response); // always continue to next filter
    }
}

// ════════════════════════════════════════════════════════
// 3. Auth Controller — login endpoint
// ════════════════════════════════════════════════════════
@RestController
@RequestMapping("/api/auth")
class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    AuthController(AuthenticationManager authManager,
                   JwtUtil jwtUtil,
                   UserDetailsService userDetailsService) {
        this.authManager        = authManager;
        this.jwtUtil            = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    record LoginRequest(String username, String password) {}
    record LoginResponse(String token, String username) {}

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        // ── authenticate — throws AuthenticationException if credentials are wrong ──
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        UserDetails user  = (UserDetails) auth.getPrincipal();
        String token      = jwtUtil.generateToken(user);
        return ResponseEntity.ok(new LoginResponse(token, user.getUsername()));
    }
}

// ════════════════════════════════════════════════════════
// 4. Security config wiring JWT filter into the chain
// ════════════════════════════════════════════════════════
// In your Stage1 SecurityFilterChain, add:
//
//  .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//
// Full config snippet:
//
//  @Bean
//  public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtFilter) throws Exception {
//      http
//          .csrf(csrf -> csrf.disable())
//          .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
//          .authorizeHttpRequests(auth -> auth
//              .requestMatchers("/api/auth/**").permitAll()
//              .anyRequest().authenticated()
//          )
//          .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//      return http.build();
//  }

class Stage3_JwtAuthentication {}
