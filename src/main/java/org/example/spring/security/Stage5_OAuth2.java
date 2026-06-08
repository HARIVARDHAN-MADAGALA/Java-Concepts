package org.example.spring.security;

/// Stage 5 — OAuth2 (Login + Resource Server)
///
/// Two distinct OAuth2 use cases in Spring Security:
///
///   A. OAuth2 Login (Social Login)
///      User clicks "Login with Google/GitHub" → redirected to provider → redirected back
///      Spring handles the OAuth2 code flow automatically.
///      You get an OidcUser / OAuth2User principal — NO password involved.
///
///   B. OAuth2 Resource Server (JWT Bearer tokens)
///      Your API accepts JWTs issued by an Authorization Server (Keycloak, Auth0, Okta, AWS Cognito)
///      Spring validates the token's signature using the provider's public key (JWKS endpoint).
///      You do NOT write JwtAuthFilter yourself — Spring does it.
///
/// OAuth2 Authorization Code Flow:
///   Client → GET /oauth2/authorization/google
///     → redirect to accounts.google.com/o/oauth2/auth?...
///       → user logs in + consents
///         → Google → redirect to /login/oauth2/code/google?code=xxx
///           → Spring exchanges code for access_token + id_token
///             → creates OAuth2User or OidcUser → sets SecurityContext

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@EnableWebSecurity
public class Stage5_OAuth2 {

    // ════════════════════════════════════════════════════════
    // A. OAuth2 Login (Social Login — Google, GitHub, etc.)
    // application.properties:
    //   spring.security.oauth2.client.registration.google.client-id=<your-client-id>
    //   spring.security.oauth2.client.registration.google.client-secret=<your-secret>
    //   spring.security.oauth2.client.registration.github.client-id=<your-client-id>
    //   spring.security.oauth2.client.registration.github.client-secret=<your-secret>
    // ════════════════════════════════════════════════════════
    @Bean
    public SecurityFilterChain oauth2LoginChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")                            // custom login page
                .defaultSuccessUrl("/dashboard", true)         // redirect after login
                .failureUrl("/login?error=true")
                // optional: customize user info extraction
                // .userInfoEndpoint(info -> info.oidcUserService(customOidcUserService))
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
            );

        return http.build();
    }

    // ════════════════════════════════════════════════════════
    // B. OAuth2 Resource Server (validates incoming JWT Bearer tokens)
    // application.properties:
    //   spring.security.oauth2.resourceserver.jwt.issuer-uri=https://your-auth-server.com
    //   (Spring auto-fetches JWKS from issuer-uri + /.well-known/openid-configuration)
    // ════════════════════════════════════════════════════════
    @Bean
    public SecurityFilterChain resourceServerChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter()) // extract roles from JWT
                )
            );

        return http.build();
    }

    // ── maps JWT claims → Spring GrantedAuthorities ──
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthoritiesClaimName("roles");        // claim name in your JWT
        converter.setAuthorityPrefix("ROLE_");             // adds ROLE_ prefix to each role

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);
        return jwtConverter;
    }

    // ── custom JwtDecoder — use when NOT using issuer-uri auto-config ──
    // (e.g. your own JWT from Stage3, or a specific JWKS endpoint)
    @Bean
    public JwtDecoder jwtDecoder() {
        // for JWKS-based validation (Auth0, Keycloak, Cognito)
        return NimbusJwtDecoder
            .withJwkSetUri("https://your-auth-server.com/.well-known/jwks.json")
            .build();
    }
}

// ════════════════════════════════════════════════════════
// Controller — accessing OAuth2 principal
// ════════════════════════════════════════════════════════
@RestController
class OAuth2UserController {

    // ── OAuth2 Login: principal is OAuth2User ──
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OAuth2User principal) {
        String name  = principal.getAttribute("name");
        String email = principal.getAttribute("email");
        return "Welcome " + name + " (" + email + ")";
    }

    // ── OIDC Login (Google uses OIDC): principal is OidcUser (extends OAuth2User) ──
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal OidcUser oidcUser) {
        return "Subject: "      + oidcUser.getSubject()
             + " Name: "        + oidcUser.getFullName()
             + " Email: "       + oidcUser.getEmail()
             + " Picture: "     + oidcUser.getPicture();
    }

    // ── Resource Server: principal is Jwt ──
    @GetMapping("/api/me")
    public String apiMe(@AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt) {
        return "Subject: "  + jwt.getSubject()
             + " Issuer: "  + jwt.getIssuer()
             + " Claims: "  + jwt.getClaims();
    }

    // ── OAuth2 vs JWT Resource Server ──
    // OAuth2 Login     → browser-based, session-based, social login
    // Resource Server  → API-based, stateless, token passed in Authorization header
    // Both can coexist in one app with different SecurityFilterChains + @Order
}
