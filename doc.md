# ecommerce — Developer documentation

This repository is a small Spring Boot backend for an e-commerce-like application. The project provides user authentication (JWT + refresh token cookie), user management (upgrade to seller), and basic category management endpoints.

Quick status
------------
- Java + Spring Boot application (Maven wrapper included).
- Endpoints documented below: /auth, /users, /categories.
- JWT-based access tokens and refresh tokens (refresh token is returned as an HttpOnly cookie).

Prerequisites
-------------
- JDK 17 (or matching the project's target Java version).
- Maven (the repository includes `mvnw` so you can use the wrapper without installing Maven).
- A running database compatible with Spring Data JPA (e.g., Postgres, MySQL). The app reads DB configuration from environment variables (see below).

Environment variables
---------------------
The application uses the following environment variables (used in `src/main/resources/application.yaml`):

- DATABASE_URL — JDBC URL for the database, e.g. `jdbc:postgresql://localhost:5432/ecommerce`
- DATABASE_USERNAME — database user
- DATABASE_PASSWORD — database password
- JWT_SECRET — secret key used to sign JWTs (must be long enough for HMAC-SHA; keep secret)

Note: The YAML file uses properties under `spring.jwt.*` for token expirations and the secret; the code reads `spring.jwt.secret-key` (hyphenated) — ensure your property names match what's configured in your environment or set the environment variable `JWT_SECRET` used in the YAML.

Build & run
-----------
From the project root you can run with the included Maven wrapper:

```bash
./mvnw clean package
./mvnw spring-boot:run
```

Or build a JAR and run it:

```bash
./mvnw clean package
java -jar target/*.jar
```

By default the application will use the Spring Boot default port (8080) unless overridden via `server.port`.

API overview
------------
All successful responses conform to ApiResponse<T>:

{
  "success": true|false,
  "errors": ["..."], // or null
  "data": T
}

For failing operations, `success` is false and `errors` contains messages.

Authentication & tokens
-----------------------
- /auth/login returns an access token in the JSON body and sets a refresh token in an HttpOnly cookie named `refreshToken` with Path=/auth/refresh.
- Access tokens are JWTs and should be sent in the Authorization header as `Authorization: Bearer <token>` for protected endpoints.
- To obtain a new access token, POST to /auth/refresh with the `refreshToken` cookie present.

Security notes
- The refresh cookie is set with `HttpOnly` and `Secure` flags in the current code. `Secure` requires HTTPS to be present in most browsers; during local HTTP development you may need to change cookie settings in the code to allow non-HTTPS testing (not recommended for production).
- Token expiration values are configured via `spring.jwt.accessTokenExpiration` and `spring.jwt.refreshTokenExpiration` (duration units used in code are seconds multiplied by 1000 when building the JWT expiration), and the secret key is taken from `spring.jwt.secret-key` (see note above about YAML vs code property names).

Endpoints
---------
The controllers in this project and their endpoints are summarized below. Examples use curl and assume the server is running on http://localhost:8080.

1) AuthController (base path: /auth)

- POST /auth/signup
  - Description: Register a new user.
  - Request body (JSON): RegisterUserRequest
    {
      "username": "string",
      "email": "string", // must be a valid email
      "password": "string"
    }
  - Response: ApiResponse<UserDto>

  Example:
  ```bash
  curl -X POST http://localhost:8080/auth/signup \
    -H "Content-Type: application/json" \
    -d '{"username":"alice","email":"alice@example.com","password":"P@ssw0rd"}'
  ```

- POST /auth/login
  - Description: Authenticate a user and return an access token. Also sets an HttpOnly cookie `refreshToken`.
  - Request body (JSON): LoginDto
    {
      "username": "string",
      "password": "string"
    }
  - Response: ApiResponse<LoginResponse>
    {
      "token": "<access-token>"
    }

  Notes: The controller sets a cookie named `refreshToken` with Path=/auth/refresh and HttpOnly.

  Example:
  ```bash
  curl -i -X POST http://localhost:8080/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"alice","password":"P@ssw0rd"}'
  ```
  The `-i` flag shows headers; the `Set-Cookie` header will contain the refresh token.

- POST /auth/refresh
  - Description: Exchange the refresh token (sent as cookie) for a new access token.
  - Request body: none. The request must include the `refreshToken` cookie.
  - Response: ApiResponse<LoginResponse> with a new access token.

  Example (using cookie from previous response):
  ```bash
  # If you saved the cookie in a cookie jar
  curl -b cookies.txt -c cookies.txt -X POST http://localhost:8080/auth/refresh
  ```

- GET /auth/me (currently commented out in controller code)
  - Description: Return the currently authenticated user's details (requires Authorization header)

2) UserController (base path: /users)

- POST /users/upgrade-to-seller
  - Description: Upgrade a customer account to a seller profile.
  - Authorization: requires authenticated user with role CUSTOMER (method annotated with @PreAuthorize("hasRole('CUSTOMER')")).
  - Request body (JSON): UpgradeToSellerRequest
    {
      "storeName": "string",
      "description": "string"
    }
  - Response: ApiResponse<SellerProfileDto>

  Example (replace <token> with an access token):
  ```bash
  curl -X POST http://localhost:8080/users/upgrade-to-seller \
    -H "Authorization: Bearer <token>" \
    -H "Content-Type: application/json" \
    -d '{"storeName":"Alice Shop","description":"My store"}'
  ```

- GET /users/me
  - Description: Returns details for the authenticated user.
  - Authorization: pass access token in Authorization header.

  Example:
  ```bash
  curl http://localhost:8080/users/me -H "Authorization: Bearer <token>"
  ```

3) CategoryController (base path: /categories)

- POST /categories
  - Description: Create a category (requires SYSADMIN role).
  - Request body (JSON): CreateCategoryRequest
    {
      "name": "string",
      "description": "string"
    }
  - Response: ApiResponse<CategoryDto>

- GET /categories
  - Description: List all categories.
  - Response: ApiResponse<List<CategoryDto>>

- GET /categories/{categoryId}
  - Description: Get category by id.
  - Response: ApiResponse<CategoryDto>

- PUT /categories/{categoryId}
  - Description: Update category (requires SYSADMIN role).
  - Request body: UpdateCategoryRequest
  - Response: ApiResponse<CategoryDto>

- DELETE /categories/{categoryId}
  - Description: Delete category (requires SYSADMIN role).
  - Response: HTTP 204 No Content on success

Troubleshooting & common notes
------------------------------
- Cookies & Secure flag: The code sets the refresh cookie with `cookie.setSecure(true);`. Browsers require HTTPS to send secure cookies. For local development over HTTP you may need to relax that or test refresh with a manual cookie header (not recommended for production).
- JWT secret: Ensure `JWT_SECRET` is sufficiently long for the HMAC algorithm used by the project. Keep it secret.
- Property names: The code expects `spring.jwt.secret-key` while `application.yaml` uses `spring.jwt.secretKey` (camelCase). If you encounter missing property errors, align your environment variables or YAML keys with the names the code expects (or update the `JwtConfig`/YAML to match).

Testing
-------
- There is a basic test class at `src/test/java/.../EcommerceApplicationTests.java`. Run tests with:

```bash
./mvnw test
```

Next improvements (suggested)
---------------------------
- Add OpenAPI/Swagger documentation for automatic API docs and testing UI.
- Provide a Postman collection or HTTPie examples.
- Add integration tests for authentication flows (login -> use refresh cookie -> refresh -> access protected endpoint).
- Clarify and unify JWT property names between `application.yaml` and `JwtConfig`.

If you'd like, I can:
- Add example Postman/export collection.
- Add OpenAPI annotations and a Swagger UI endpoint.
- Fix the `spring.jwt` property naming mismatch in source or YAML and run the app locally to verify.
