package org.example.spring.security;

/// Stage 2 — Authentication: UserDetails, UserDetailsService, SecurityContext
///
/// Authentication flow (username/password):
///
///   Request → UsernamePasswordAuthenticationFilter
///       │  creates UsernamePasswordAuthenticationToken (unauthenticated)
///       ↓
///   AuthenticationManager (ProviderManager)
///       │  delegates to one of its AuthenticationProviders
///       ↓
///   DaoAuthenticationProvider
///       │  calls UserDetailsService.loadUserByUsername(username)
///       │  verifies password with PasswordEncoder
///       ↓
///   Returns authenticated UsernamePasswordAuthenticationToken
///       │  (principal=UserDetails, credentials=null, authorities=roles)
///       ↓
///   SecurityContextHolder.getContext().setAuthentication(token)
///       │  stored in SecurityContext — available for the rest of the request
///       ↓
///   Controller can call SecurityContextHolder.getContext().getAuthentication()
///
/// Key interfaces:
///   UserDetails          — represents the loaded user (username, password, authorities)
///   UserDetailsService   — loads UserDetails by username (you implement this)
///   PasswordEncoder      — BCrypt hashes passwords (NEVER store plain text)
///   AuthenticationManager — orchestrates the authentication process

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class Stage2_AuthenticationAndUserDetails {

    // ── 1. PasswordEncoder — always BCrypt in production ──
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // strength 12 = ~300ms per hash (intentionally slow)
    }

    // ── 2. UserDetailsService — your implementation loads users from DB ──
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        return new CustomUserDetailsService(encoder);
    }

    // ── 3. AuthenticationManager — wires UserDetailsService + PasswordEncoder ──
    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    // ════════════════════════════════════════════════════════
    // Custom UserDetailsService implementation
    // ════════════════════════════════════════════════════════
    static class CustomUserDetailsService implements UserDetailsService {

        private final PasswordEncoder encoder;

        // ── in production, inject a UserRepository (JPA) here ──
        private final Map<String, String[]> users = new ConcurrentHashMap<>(Map.of(
            "alice", new String[]{"$2a$12$hashedPasswordHere", "ROLE_USER"},
            "admin", new String[]{"$2a$12$hashedPasswordHere", "ROLE_ADMIN"}
        ));

        CustomUserDetailsService(PasswordEncoder encoder) { this.encoder = encoder; }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            String[] data = users.get(username);
            if (data == null) throw new UsernameNotFoundException("User not found: " + username);

            // ── build UserDetails — Spring Security uses this to verify password ──
            // Note: accountNonExpired/accountNonLocked/credentialsNonExpired removed from
            // User.builder() in Spring Security 6.2+ — all default to true internally
            return User.builder()
                .username(username)
                .password(data[0])                              // must be encoded
                .authorities(new SimpleGrantedAuthority(data[1]))
                .disabled(false)
                .build();
        }
    }

    // ════════════════════════════════════════════════════════
    // Custom UserDetails — richer than Spring's built-in User
    // ════════════════════════════════════════════════════════
    static class AppUserDetails implements UserDetails {

        private final String username;
        private final String password;
        private final Long   userId;          // extra field — not in standard UserDetails
        private final String email;           // extra field
        private final List<GrantedAuthority> authorities;

        AppUserDetails(Long userId, String username, String password,
                       String email, List<String> roles) {
            this.userId      = userId;
            this.username    = username;
            this.password    = password;
            this.email       = email;
            this.authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .map(a -> (GrantedAuthority) a)
                .toList();
        }

        public Long   getUserId() { return userId; }
        public String getEmail()  { return email; }

        @Override public String getUsername()               { return username; }
        @Override public String getPassword()               { return password; }
        @Override public List<GrantedAuthority> getAuthorities() { return authorities; }
        @Override public boolean isAccountNonExpired()      { return true; }
        @Override public boolean isAccountNonLocked()       { return true; }
        @Override public boolean isCredentialsNonExpired()  { return true; }
        @Override public boolean isEnabled()                { return true; }
    }

    // ════════════════════════════════════════════════════════
    // Reading the current user from anywhere in the app
    // ════════════════════════════════════════════════════════
    static void howToGetCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            var authorities = auth.getAuthorities();
            System.out.println("Current user: " + username + " roles: " + authorities);

            // if you used AppUserDetails:
            if (auth.getPrincipal() instanceof AppUserDetails user) {
                System.out.println("User ID: " + user.getUserId());
            }
        }

        // ── in a @RestController you can also inject it directly ──
        // @GetMapping("/me")
        // public String me(@AuthenticationPrincipal AppUserDetails user) {
        //     return user.getEmail();
        // }
    }
}
