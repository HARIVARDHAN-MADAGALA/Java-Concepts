package org.example.spring.security;

/// Stage 4 — Authorization: Roles, Authorities, @PreAuthorize, @Secured
///
/// Authentication  = WHO are you?   (identity)
/// Authorization   = WHAT can you do? (permissions)
///
/// Two concepts in Spring Security:
///   Role      = "ROLE_ADMIN", "ROLE_USER"  — coarse-grained, prefixed with ROLE_
///   Authority = any string                 — fine-grained, e.g. "products:write", "orders:read"
///
/// In the filter chain:
///   hasRole("ADMIN")           → checks for GrantedAuthority "ROLE_ADMIN"  (adds prefix)
///   hasAuthority("ROLE_ADMIN") → checks for exact string     "ROLE_ADMIN"  (no prefix added)
///   hasAuthority("products:write") → fine-grained permission check
///
/// Method-level security — enabled by @EnableMethodSecurity on a @Configuration class:
///   @PreAuthorize  — evaluated BEFORE the method runs (most flexible, SpEL)
///   @PostAuthorize — evaluated AFTER method runs (can check return value)
///   @Secured       — simpler, no SpEL, just role names
///   @RolesAllowed  — JSR-250 standard, same as @Secured

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

// ── enable method-level security — add this to your main @Configuration ──
@Configuration
@EnableMethodSecurity(
    prePostEnabled  = true,   // @PreAuthorize, @PostAuthorize (default true)
    securedEnabled  = true,   // @Secured
    jsr250Enabled   = true    // @RolesAllowed
)
class MethodSecurityConfig {}

// ════════════════════════════════════════════════════════
// Authorization in SecurityFilterChain (URL-level)
// ════════════════════════════════════════════════════════
//
// .authorizeHttpRequests(auth -> auth
//     .requestMatchers("/api/public/**").permitAll()
//     .requestMatchers(HttpMethod.GET, "/api/products/**").hasAnyRole("USER","ADMIN")
//     .requestMatchers(HttpMethod.POST, "/api/products/**").hasAuthority("products:write")
//     .requestMatchers("/api/admin/**").hasRole("ADMIN")
//     .anyRequest().authenticated()
// )
//
// Rules are evaluated TOP-DOWN — first match wins.
// Put specific rules before general ones.

// ════════════════════════════════════════════════════════
// Method-level security examples
// ════════════════════════════════════════════════════════
@RestController
@RequestMapping("/api")
class Stage4_AuthorizationAndRoles {

    record Order(Long id, String owner, double amount) {}

    // ── @PreAuthorize — most powerful, full SpEL support ──
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String listAllUsers() {
        return "all users — admin only";
    }

    @PostMapping("/products")
    @PreAuthorize("hasAuthority('products:write')")   // fine-grained authority
    public String createProduct(@RequestBody String product) {
        return "created: " + product;
    }

    // ── access method args in SpEL ──
    @DeleteMapping("/products/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('products:delete')")
    public String deleteProduct(@PathVariable Long id) {
        return "deleted product " + id;
    }

    // ── access the current principal in SpEL ──
    @GetMapping("/orders/{id}")
    @PreAuthorize("#id > 0 and hasRole('USER')")       // #id = method param
    public Order getOrder(@PathVariable Long id,
                          @AuthenticationPrincipal UserDetails user) {
        return new Order(id, user.getUsername(), 99.99);
    }

    // ── @PostAuthorize — check return value ──
    // returnObject is the method's return value
    @GetMapping("/orders/{id}/secure")
    @PostAuthorize("returnObject.owner() == authentication.name or hasRole('ADMIN')")
    public Order getOrderSecure(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails user) {
        // runs first, then Spring checks if current user owns this order
        return new Order(id, user.getUsername(), 99.99);
    }

    // ── @Secured — simpler, no SpEL ──
    @GetMapping("/reports")
    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    public String getReports() {
        return "reports — admin or manager";
    }

    // ── programmatic check — when annotation isn't enough ──
    @GetMapping("/me")
    public String getCurrentUser(@AuthenticationPrincipal UserDetails user) {
        // @AuthenticationPrincipal injects the principal directly — no SecurityContextHolder needed
        return "Hello, " + user.getUsername() + " roles: " + user.getAuthorities();
    }

    // ── roles vs authorities summary ──
    //
    // hasRole("ADMIN")           → looks for "ROLE_ADMIN" in authorities  (adds prefix)
    // hasAuthority("ROLE_ADMIN") → looks for "ROLE_ADMIN" exact match     (no prefix)
    // hasAuthority("read:orders")→ looks for "read:orders" exact match    (fine-grained)
    //
    // Best practice: use authorities for fine-grained permissions,
    // roles for coarse-grained grouping (ADMIN can do everything USER can + more)
}
