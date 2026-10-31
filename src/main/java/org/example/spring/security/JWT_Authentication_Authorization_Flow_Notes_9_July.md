# JWT Authentication & Authorization Flow (Spring Boot + Spring Security)

## Company Office Analogy

Imagine a company office:

-   **Employee** → Client/User
-   **Security Guard** → JWT Authentication Filter
-   **HR Department** → AuthenticationManager
-   **Employee Records** → UserDetailsService
-   **Password Verification Machine** → PasswordEncoder
-   **ID Card Printer** → JwtUtil
-   **Office Departments** → Controllers

------------------------------------------------------------------------

# Authentication (Login Flow)

## Step 1: User Sends Login Request

**Request**

``` http
POST /login
```

``` json
{
  "username": "hari",
  "password": "1234"
}
```

### Class

``` java
AuthController
```

### Method

``` java
login(@RequestBody LoginRequest request)
```

**Input**

-   LoginRequest
    -   username
    -   password

The controller does **not** verify the password.

It calls:

``` java
authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(
        request.getUsername(),
        request.getPassword()
    )
);
```

------------------------------------------------------------------------

## Step 2: AuthenticationManager

**Role:** HR Department

### Method

``` java
authenticate(Authentication authentication)
```

**Input**

``` java
UsernamePasswordAuthenticationToken(username, password)
```

**Output**

``` java
Authentication
```

AuthenticationManager delegates authentication to
**DaoAuthenticationProvider**.

------------------------------------------------------------------------

## Step 3: DaoAuthenticationProvider

### Method

``` java
authenticate()
```

It calls:

``` java
UserDetailsService.loadUserByUsername(username)
```

------------------------------------------------------------------------

## Step 4: UserDetailsService

This is your custom implementation.

``` java
@Service
public class MyUserDetailsService implements UserDetailsService
```

### Method

``` java
loadUserByUsername(String username)
```

It fetches the user from the database.

Returns:

``` java
UserDetails
```

Contains:

-   Username
-   Encrypted Password
-   Roles
-   Enabled status
-   Account status

------------------------------------------------------------------------

## Step 5: PasswordEncoder

### Method

``` java
matches(rawPassword, encodedPassword)
```

Example:

``` text
Raw Password     : 1234
Database Password: $2a$10....
```

Returns:

``` text
true
```

or

``` text
false
```

------------------------------------------------------------------------

## Step 6: Authentication Success

AuthenticationManager returns an authenticated object.

Contains:

-   Username
-   Roles
-   authenticated=true

------------------------------------------------------------------------

## Step 7: Generate JWT

Controller calls:

``` java
jwtUtil.generateToken(username)
```

### JwtUtil

Method:

``` java
generateToken(username)
```

Creates:

-   Header
-   Payload
-   Signature

Payload usually contains:

-   Username
-   Issued Time
-   Expiry Time

Returns JWT:

``` text
eyJhbGciOiJIUzI1Ni...
```

Controller returns:

``` json
{
  "token": "eyJhbGc..."
}
```

------------------------------------------------------------------------

# Authorization Flow (Every Request)

Client sends:

``` http
GET /employees
```

Header:

``` text
Authorization: Bearer eyJhbGc...
```

------------------------------------------------------------------------

## Step 8: JwtAuthenticationFilter

Usually extends:

``` java
OncePerRequestFilter
```

### Method

``` java
doFilterInternal(HttpServletRequest request,
                 HttpServletResponse response,
                 FilterChain filterChain)
```

Reads:

``` java
request.getHeader("Authorization")
```

Gets:

``` text
Bearer eyJhbGc...
```

------------------------------------------------------------------------

## Step 9: Extract JWT

Remove:

``` text
Bearer
```

Now only JWT remains.

------------------------------------------------------------------------

## Step 10: Validate JWT

Calls:

``` java
jwtUtil.extractUsername(token)
```

Returns:

``` text
hari
```

Then:

``` java
jwtUtil.validateToken(token)
```

Checks:

-   Signature
-   Expiry
-   Secret Key
-   Token Integrity

Returns:

``` text
true
```

or

``` text
false
```

------------------------------------------------------------------------

## Step 11: Load User Again

Filter calls:

``` java
userDetailsService.loadUserByUsername(username)
```

Reason:

-   Fetch latest roles
-   Verify account status

------------------------------------------------------------------------

## Step 12: Create Authentication Object

``` java
new UsernamePasswordAuthenticationToken(
    userDetails,
    null,
    userDetails.getAuthorities()
)
```

Password is **null** because login already verified it.

------------------------------------------------------------------------

## Step 13: Store in SecurityContext

``` java
SecurityContextHolder
    .getContext()
    .setAuthentication(authentication);
```

Think of this as giving the employee a visitor badge valid for the
current request.

Now Spring Security knows the current authenticated user.

------------------------------------------------------------------------

## Step 14: Continue Filter Chain

``` java
filterChain.doFilter(request, response);
```

The request now reaches the controller.

------------------------------------------------------------------------

## Step 15: Authorization

Example:

``` java
@PreAuthorize("hasRole('ADMIN')")
```

or

``` java
.requestMatchers("/admin/**")
.hasRole("ADMIN")
```

If the role matches:

-   Controller executes

Otherwise:

``` text
403 Forbidden
```

------------------------------------------------------------------------

# Why UserDetailsService is Called Twice

### During Login

-   Fetch user
-   Verify password

### During Every Request

-   Load latest roles
-   Check account status
-   Build Authentication object

------------------------------------------------------------------------

# Complete Flow

  ----------------------------------------------------------------------------------------------------------------------------
  Step        Class                       Method                 Input                                 Output
  ----------- --------------------------- ---------------------- ------------------------------------- -----------------------
  1           AuthController              login()                LoginRequest                          Calls
                                                                                                       AuthenticationManager

  2           AuthenticationManager       authenticate()         UsernamePasswordAuthenticationToken   Authentication

  3           DaoAuthenticationProvider   authenticate()         Authentication                        Calls
                                                                                                       UserDetailsService

  4           UserDetailsService          loadUserByUsername()   Username                              UserDetails

  5           PasswordEncoder             matches()              Raw Password, Encoded Password        true/false

  6           JwtUtil                     generateToken()        Username                              JWT

  7           JwtAuthenticationFilter     doFilterInternal()     HTTP Request                          Reads JWT

  8           JwtUtil                     extractUsername()      JWT                                   Username

  9           JwtUtil                     validateToken()        JWT                                   true/false

  10          UserDetailsService          loadUserByUsername()   Username                              UserDetails

  11          SecurityContextHolder       setAuthentication()    Authentication                        Stores Current User

  12          FilterChain                 doFilter()             Request                               Controller
  ----------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------

# One-Line Summary

**Authentication**

Username + Password → AuthenticationManager → UserDetailsService →
PasswordEncoder → JWT Generated

**Authorization**

JWT → JwtAuthenticationFilter → JwtUtil → UserDetailsService →
SecurityContextHolder → Controller → Role Check
