package org.example.spring.security;

/// Stage 7 — Custom Filters, AuthenticationEntryPoint, AccessDeniedHandler
///
/// ── Custom Filters ──
/// Add your own filter into the SecurityFilterChain at a specific position:
///   addFilterBefore(filter, ExistingFilter.class)  — runs BEFORE the given filter
///   addFilterAfter(filter, ExistingFilter.class)   — runs AFTER the given filter
///   addFilterAt(filter, ExistingFilter.class)      — replaces the given filter's slot
///
/// Common uses: request logging, rate limiting, API key validation, IP whitelisting
///
/// ── Exception Handling ──
/// ExceptionTranslationFilter catches two types of security exceptions:
///
///   AuthenticationException (401) → delegates to AuthenticationEntryPoint
///     Triggered when: no credentials, expired token, invalid token
///     Default behavior: redirect to /login
///     For REST APIs: return 401 JSON response
///
///   AccessDeniedException (403) → delegates to AccessDeniedHandler
///     Triggered when: authenticated user lacks required role/authority
///     Default behavior: redirect to /403 error page
///     For REST APIs: return 403 JSON response

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

// ════════════════════════════════════════════════════════
// 1. Custom Filter — request logging + rate limiting example
// ════════════════════════════════════════════════════════
@Component
class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        long start = System.currentTimeMillis();

        try {
            chain.doFilter(request, response); // proceed down the chain
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            System.out.printf("[%s] %s %s → %d (%dms)%n",
                Instant.now(), request.getMethod(), request.getRequestURI(),
                response.getStatus(), elapsed);
        }
    }

    // ── skip logging for actuator health checks ──
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/actuator/health");
    }
}

// ════════════════════════════════════════════════════════
// 2. API Key Filter — validates X-API-KEY header
// ════════════════════════════════════════════════════════
@Component
class ApiKeyFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final String VALID_KEY      = "my-secret-api-key"; // store in config/secrets

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // only enforce on /api/internal/**
        if (!request.getRequestURI().startsWith("/api/internal/")) {
            chain.doFilter(request, response);
            return;
        }

        String key = request.getHeader(API_KEY_HEADER);
        if (key == null || !key.equals(VALID_KEY)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\": \"Invalid or missing API key\"}");
            return; // short-circuit — don't call chain.doFilter
        }

        chain.doFilter(request, response);
    }
}

// ════════════════════════════════════════════════════════
// 3. AuthenticationEntryPoint — handles 401 (unauthenticated)
// ════════════════════════════════════════════════════════
@Component
class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> body = Map.of(
            "status",    401,
            "error",     "Unauthorized",
            "message",   authException.getMessage(),
            "path",      request.getRequestURI(),
            "timestamp", Instant.now().toString()
        );

        mapper.writeValue(response.getOutputStream(), body);
    }
}

// ════════════════════════════════════════════════════════
// 4. AccessDeniedHandler — handles 403 (authenticated but not authorized)
// ════════════════════════════════════════════════════════
@Component
class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> body = Map.of(
            "status",    403,
            "error",     "Forbidden",
            "message",   "You don't have permission to access this resource",
            "path",      request.getRequestURI(),
            "timestamp", Instant.now().toString()
        );

        mapper.writeValue(response.getOutputStream(), body);
    }
}

// ════════════════════════════════════════════════════════
// 5. Wiring everything into SecurityFilterChain
// ════════════════════════════════════════════════════════
@Configuration
class Stage7_CustomFiltersAndExceptionHandling {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthFilter jwtAuthFilter,
                                           RequestLoggingFilter loggingFilter,
                                           ApiKeyFilter apiKeyFilter,
                                           RestAuthenticationEntryPoint entryPoint,
                                           RestAccessDeniedHandler deniedHandler) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(
                org.springframework.security.config.http.SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/internal/**").permitAll() // guarded by ApiKeyFilter
                .anyRequest().authenticated()
            )

            // ── custom filters — order matters ──
            .addFilterBefore(loggingFilter, UsernamePasswordAuthenticationFilter.class)  // first
            .addFilterBefore(apiKeyFilter,  UsernamePasswordAuthenticationFilter.class)  // second
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)  // third

            // ── exception handling ──
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(entryPoint)   // 401 → JSON response
                .accessDeniedHandler(deniedHandler)      // 403 → JSON response
            );

        return http.build();
    }
}
