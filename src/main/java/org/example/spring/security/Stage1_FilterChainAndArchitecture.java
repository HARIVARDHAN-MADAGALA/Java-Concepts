package org.example.spring.security;

/// Stage 1 — Filter Chain and Architecture
///
/// Spring Security is a chain of Servlet Filters sitting in front of your app.
/// Every HTTP request passes through this chain BEFORE reaching your controller.
///
/// Full request flow:
///
///   HTTP Request
///       │
///   DelegatingFilterProxy          ← registered in Servlet container, bridges to Spring context
///       │
///   FilterChainProxy               ← Spring Security's master filter
///       │
///   SecurityFilterChain            ← ordered list of security filters
///       │
///   ┌───┴─────────────────────────────────────────────────────┐
///   │  DisableEncodeUrlFilter                                  │
///   │  WebAsyncManagerIntegrationFilter                        │
///   │  SecurityContextHolderFilter  ← loads SecurityContext    │
///   │  HeaderWriterFilter           ← X-Frame-Options etc.     │
///   │  CorsFilter                   ← CORS preflight           │
///   │  CsrfFilter                   ← CSRF token check         │
///   │  LogoutFilter                                            │
///   │  UsernamePasswordAuthenticationFilter  ← form login      │
///   │  BearerTokenAuthenticationFilter       ← JWT/OAuth2      │
///   │  BasicAuthenticationFilter                               │
///   │  RequestCacheAwareFilter                                 │
///   │  SecurityContextHolderAwareRequestFilter                 │
///   │  AnonymousAuthenticationFilter  ← sets anonymous if none │
///   │  ExceptionTranslationFilter     ← catches 401/403        │
///   │  AuthorizationFilter            ← the access decision    │
///   └──────────────────────────────────────────────────────────┘
///       │
///   DispatcherServlet → Controller
///
/// Key principle: each filter either:
///   a) processes the request and passes to next filter (chain.doFilter)
///   b) short-circuits — writes a response directly (401, 403, redirect)
///
/// SecurityFilterChain is the bean you configure in @Configuration classes.
/// You can have MULTIPLE SecurityFilterChains with different request matchers
/// (e.g. one for /api/** with JWT, another for /admin/** with basic auth).

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity                          // activates Spring Security's web support
public class Stage1_FilterChainAndArchitecture {

    // ── minimal SecurityFilterChain — shows the skeleton of every security config ──
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ── CSRF: disable for stateless REST APIs (JWT handles its own state) ──
            .csrf(csrf -> csrf.disable())

            // ── Session: STATELESS — no HttpSession created or used ──
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // ── Authorization rules — evaluated top-to-bottom, first match wins ──
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()     // public: login, register
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()                    // everything else: must be logged in
            );

        return http.build();
    }

    // ── multiple SecurityFilterChains — different configs for different URL spaces ──
    // @Bean
    // @Order(1)  ← lower number = higher priority
    // public SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
    //     http.securityMatcher("/api/**")   ← only applies to /api/**
    //         .csrf(csrf -> csrf.disable())
    //         .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
    //         ...
    //     return http.build();
    // }
    //
    // @Bean
    // @Order(2)
    // public SecurityFilterChain adminChain(HttpSecurity http) throws Exception {
    //     http.securityMatcher("/admin/**") ← only applies to /admin/**
    //         .formLogin(Customizer.withDefaults())
    //         ...
    //     return http.build();
    // }

    // ── how Spring Security integrates with Servlet container ──
    // DelegatingFilterProxy is registered by Spring Boot auto-configuration.
    // It delegates to FilterChainProxy bean from the Spring application context.
    // FilterChainProxy iterates SecurityFilterChains and finds the first matching one.
    // That chain's filters run in order — no chain matches → request passes through unsecured.
}
