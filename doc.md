API Endpoints
=============

This document lists the HTTP endpoints implemented in the project and describes their HTTP method, request body shapes, and response envelope.

Response envelope
-----------------
All successful endpoints return an ApiResponse<T> JSON object with the following shape:

{
  "success": boolean,
  "errors": ["..."], // or null
  "data": T // the actual payload
}

Controllers and endpoints
-------------------------

1) AuthController (base path: /auth)

- POST /auth/signup
  - Description: Register a new user.
  - Request body (JSON): RegisterUserRequest
    {
      "username": "string", // required
      "email": "string", // required, must be a valid email
      "password": "string" // required
    }
  - Response: ApiResponse<UserDto>
    UserDto:
    {
      "id": number,
      "username": "string",
      "email": "string",
      "role": { /* Role entity fields */ }
    }

- POST /auth/login
  - Description: Authenticate a user and return an access token. Also sets an HttpOnly cookie named "refreshToken" for refresh operations.
  - Request body (JSON): LoginDto
    {
      "username": "string",
      "password": "string"
    }
  - Response: ApiResponse<LoginResponse>
    LoginResponse:
    {
      "token": "<access-token-string>"
    }
  - Notes: The server sets a cookie `refreshToken` (HttpOnly, Path=/auth/refresh) with a refresh token. Access tokens are returned in the JSON response.

- POST /auth/refresh
  - Description: Exchange refresh token (sent as cookie) for a new access token.
  - Request body: none. Must include cookie named `refreshToken`.
  - Response: ApiResponse<LoginResponse>
    {
      "token": "<new-access-token>"
    }

- GET /auth/me
  - Description: Returns the currently authenticated user's details.
  - Request body: none (requires authentication, typically by presenting the access token in Authorization header).
  - Response: ApiResponse<UserDto>

2) UserController (base path: /users)

- POST /users/upgrade-to-seller
  - Description: Upgrade a customer account to a seller profile.
  - Authorization: @PreAuthorize("hasRole('CUSTOMER')") — user must have CUSTOMER role.
  - Request body (JSON): UpgradeToSellerRequest
    {
      "userId": number, // the id of the user to upgrade (the controller also accepts authenticated principal userId)
      "storeName": "string",
      "description": "string"
    }
  - Notes: The endpoint gets the authenticated user id via @AuthenticationPrincipal Long userId in the controller method signature.
  - Response: ApiResponse<SellerProfileDto>
    SellerProfileDto:
    {
      "id": number,
      "storeName": "string",
      "description": "string",
      "userId": number,
      "createdAt": "YYYY-MM-DDTHH:MM:SS" // ISO datetime
    }

Notes and assumptions
---------------------
- Authentication: Access tokens appear to be JWTs returned in the response of /auth/login and /auth/refresh. To call protected endpoints (e.g., /users/upgrade-to-seller, /auth/me), include the access token in the Authorization header as `Authorization: Bearer <token>` (common pattern used with JwtFilter/SecurityConfig in the project).
- Error responses: When operation fails, controllers use the ApiResponse with success=false and errors listing messages.
- Some request fields are validated (e.g., RegisterUserRequest fields are annotated with @NotNull and @Email).

If you want, I can also:
- Add example curl commands for each endpoint.
- Expand the Role and UserDto shapes by including role enum/fields from the entities.
- Generate OpenAPI/Swagger annotations or a small Postman collection.
