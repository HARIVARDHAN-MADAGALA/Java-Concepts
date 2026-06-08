package org.example.spring.security;

/// Stage 6 — CSRF, Session Management, CORS
///
/// ── CSRF (Cross-Site Request Forgery) ──
/// Attack: malicious site tricks logged-in browser into sending a state-changing request.
/// Defense: synchronizer token — server issues a CSRF token, client must echo it back.
/// When to disable CSRF:
///   - Stateless REST APIs with JWT (JWT itself is not sent automatically by browsers)
///   - When you use SameSite=Strict cookies
/// When to keep CSRF enabled:
///   - Traditional web apps using session cookies (forms, Thymeleaf, MVC)
///
/// ── Session Management ──
/// SessionCreationPolicy options:
///   STATELESS    — no session created/used → for REST APIs with JWT
///   IF_REQUIRED  — create session if needed (default)
///   ALWAYS       — always create a session
///   NEVER        — never create, but use if already exists
///
/// Concurrent session control: limit how many sessions a user can have.
/// Session fixation protection: new session ID after login (default ON).
///
/// ── CORS (Cross-Origin Resource Sharing) ──
/// Browser enforces same-origin policy — blocks JS from calling a different origin.
/// CORS headers tell the browser which cross-origin requests are allowed.
/// Spring Security's CorsFilter must run BEFORE authentication filters.
/// Configure CORS in Spring Security, NOT just in @CrossOrigin — Spring Security
/// intercepts preflight OPTIONS requests before they reach your controller.

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class Stage6_CsrfSessionCors {

    // ════════════════════════════════════════════════════════
    // Full config showing CSRF + Session + CORS together
    // ════════════════════════════════════════════════════════
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ── CORS — must be configured here, not just on controllers ──
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // ── CSRF: Option A — disable for pure stateless JWT REST API ──
            .csrf(csrf -> csrf.disable())

            // ── CSRF: Option B — enable with cookie repo for SPA (React, Angular) ──
            // .csrf(csrf -> csrf
            //     .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            //     // JS can read XSRF-TOKEN cookie and send as X-XSRF-TOKEN header
            //     .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            // )

            // ── CSRF: Option C — disable for API routes, enable for web routes ──
            // .csrf(csrf -> csrf
            //     .ignoringRequestMatchers("/api/**")  // REST endpoints: no CSRF
            // )

            // ── Session Management ──
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT: no session

                // ── for stateful apps: concurrent session control ──
                // .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                // .maximumSessions(1)                  // only 1 active session per user
                //     .maxSessionsPreventsLogin(false)  // false = new login kicks old session
                //                                       // true  = new login blocked
                // .and()
                // .sessionFixation().newSession()       // new session ID after login (default)
            )

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            );

        return http.build();
    }

    // ════════════════════════════════════════════════════════
    // CORS Configuration Source
    // ════════════════════════════════════════════════════════
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // ── allowed origins — NEVER use "*" in production with credentials ──
        config.setAllowedOrigins(List.of(
            "https://yourfrontend.com",
            "https://admin.yourfrontend.com"
        ));

        // ── allowed HTTP methods ──
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // ── allowed request headers ──
        config.setAllowedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "X-XSRF-TOKEN"   // needed if using CSRF cookie
        ));

        // ── expose response headers to JS ──
        config.setExposedHeaders(List.of("Authorization"));

        // ── allow cookies / Authorization header to be sent cross-origin ──
        config.setAllowCredentials(true);

        // ── how long browser caches preflight response (seconds) ──
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // apply to all paths
        return source;
    }

    // ── needed for concurrent session control to work ──
    // registers HttpSessionDestroyedEvent so Spring Security tracks session lifecycle
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    // ── CSRF attack scenario ──
    //
    // 1. User logs into bank.com → browser stores session cookie
    // 2. User visits evil.com (malicious site)
    // 3. evil.com page contains: <img src="https://bank.com/transfer?to=hacker&amount=1000">
    // 4. Browser auto-sends cookie → bank.com processes the transfer!
    //
    // CSRF token defense:
    // 1. bank.com sends CSRF token in HTML form or cookie
    // 2. evil.com cannot read the token (same-origin policy)
    // 3. bank.com rejects requests without the valid token
    //
    // JWT defense (why we can disable CSRF with JWT):
    // JWT is in Authorization header — browsers do NOT auto-send headers cross-origin
    // (unlike cookies which are sent automatically)
}
