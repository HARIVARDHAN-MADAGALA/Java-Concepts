Interviewer: "Can you explain the JWT authentication flow?"

You:

"Sure. When the user logs in, they send their username and password to the /login endpoint.

The request reaches the AuthenticationManager, which internally delegates to the DaoAuthenticationProvider.

DaoAuthenticationProvider calls my UserDetailsService to load the user from the database and uses the PasswordEncoder to compare the entered password with the encrypted password stored in the DB.

If the credentials are valid, authentication succeeds, and I generate a JWT token containing claims like the username and expiry time. The token is signed using a secret key and returned to the client.

After that, for every request, the client sends the JWT in the Authorization header as a Bearer token.

Every request first goes through my custom JwtFilter, which extends OncePerRequestFilter. The filter extracts the token, validates its signature and expiry, extracts the username, and loads the user again from the database using UserDetailsService.

If the token is valid, the filter creates an authenticated Authentication object and stores it in the SecurityContextHolder.

Once the SecurityContext is populated, Spring Security considers the user authenticated. Then authorization happens. If the endpoint has something like @PreAuthorize("hasRole('ADMIN')"), Spring checks the user's roles before allowing access to the controller.

If the token is invalid or expired, authentication fails, and Spring returns a 401 Unauthorized response."