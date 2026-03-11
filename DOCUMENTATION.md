# E-commerce API guide for developers

This project is a Spring Boot 3 backend for a small e-commerce platform.

Use this document if you want to:

- run the API locally
- authenticate against it
- understand roles and permissions
- call each endpoint with the right payload shape
- know the current response and error contracts

This guide was written from the code currently present under `src/main/java`.

## 1. Quick facts

- Runtime: Java 21
- Framework: Spring Boot 3.5.11
- Build tool: Maven Wrapper (`./mvnw`)
- Auth: JWT bearer tokens + refresh token cookie
- Database: MySQL-compatible datasource
- Default local base URL: `http://localhost:8080`
- API prefix: none

## 2. Running the API locally

### Required environment variables

The application reads these values from `src/main/resources/application.yaml`:

- `DATABASE_URL`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`
- `JWT_SECRET`

Current JWT-related settings:

- access token expiration: `720` seconds (~12 minutes)
- refresh token expiration: `604800` seconds (7 days)

### Start commands

```bash
./mvnw clean test
./mvnw spring-boot:run
```

### Build a jar

```bash
./mvnw clean package
java -jar target/*.jar
```

## 3. How authentication works

### Access token

After login, the API returns a JWT access token in JSON.

Send it on protected routes like this:

```http
Authorization: Bearer <access-token>
```

### Refresh token

`POST /auth/login` also sets a cookie named `refreshToken`.

Cookie characteristics from the controller:

- name: `refreshToken`
- path: `/auth/refresh`
- `HttpOnly=true`
- `Secure=true`
- max age: 7 days

Important implication:

- browsers typically won't send this cookie over plain HTTP because `Secure=true`
- local browser testing of `POST /auth/refresh` may fail unless you're using HTTPS
- CLI clients can still send the cookie explicitly

### JWT claims

Tokens currently include:

- `sub` = user id
- `username`
- `email`
- `role`

### Roles used by the API

- `CUSTOMER`
- `SELLER`
- `SYSADMIN`

Newly registered users start as `CUSTOMER`.

## 4. Authorization rules at a glance

| Route pattern | Access |
|---|---|
| `/auth/**` | Public |
| `GET /categories/**` | Public |
| `POST/PUT/DELETE /categories/**` | `SYSADMIN` |
| `GET /products/**` | Public |
| `POST/PUT/DELETE /products/**` | `SELLER` |
| everything else | Authenticated |

Method-level authorization is also enabled, so some controllers apply `@PreAuthorize` on top of the global route rules.

## 5. Common response format

Most successful endpoints return:

```json
{
  "success": true,
  "errors": null,
  "data": {}
}
```

The wrapper type is `ApiResponse<T>`:

- `success`: `boolean`
- `errors`: `List<String> | null`
- `data`: endpoint-specific payload or `null`

### Endpoints that return `204 No Content`

These do not return an `ApiResponse` body:

- `DELETE /categories/{categoryId}`
- `DELETE /products/{productId}`
- `DELETE /carts`
- `PUT /orders?orderId={orderId}`

## 6. Error format and status codes

Most handled errors return this shape:

```json
{
  "success": false,
  "errors": ["message"],
  "data": null
}
```

Current exception/status mapping:

| Condition | Status |
|---|---|
| validation failure (`MethodArgumentNotValidException`) | `400 Bad Request` |
| generic `RuntimeException` | `400 Bad Request` |
| `UserNotFoundException` | `404 Not Found` |
| `DuplicateResourceException` | `409 Conflict` |
| `IllegalStateException` | `403 Forbidden` |
| unauthenticated protected request | `401 Unauthorized` |
| authenticated but forbidden | `403 Forbidden` |
| unexpected unhandled exception | `500 Internal Server Error` |

## 7. Recommended client flow

For a typical customer flow:

1. `POST /auth/signup`
2. `POST /auth/login`
3. keep the returned access token
4. send `Authorization: Bearer <token>` on protected routes
5. use cart endpoints
6. place an order with `POST /orders`
7. if the access token expires, call `POST /auth/refresh` with the refresh cookie

For a seller flow:

1. register/login as a customer
2. call `POST /users/upgrade-to-seller`
3. login again if your client needs a fresh token carrying the `SELLER` role
4. create and manage products
5. inspect seller order items under `/orders/seller...`

## 8. Request and response payloads

### Authentication DTOs

#### Register request

```json
{
  "username": "alice",
  "email": "alice@example.com",
  "password": "P@ssw0rd"
}
```

Validation rules:

- `username` required
- `email` required and must be a valid email
- `password` required

#### Login request

```json
{
  "username": "alice",
  "password": "P@ssw0rd"
}
```

Note: the login DTO does not currently use bean-validation annotations, but both fields are expected by the auth flow.

#### Login response payload

```json
{
  "token": "<access-token>"
}
```

### User payloads

#### `UserDto`

```json
{
  "id": 1,
  "username": "alice",
  "email": "alice@example.com",
  "role": "CUSTOMER",
  "createdAt": "2026-03-09T10:00:00",
  "updatedAt": "2026-03-09T10:00:00"
}
```

#### Upgrade-to-seller request

```json
{
  "userId": 1,
  "storeName": "Alice Shop",
  "description": "My store"
}
```

Important rule: `userId` must match the authenticated user id.

#### `SellerProfileDto`

```json
{
  "id": 1,
  "storeName": "Alice Shop",
  "description": "My store",
  "userId": 1,
  "createdAt": "2026-03-09T10:00:00"
}
```

### Category payloads

#### Create category

```json
{
  "name": "Electronics",
  "description": "Devices and gadgets"
}
```

Validation rules:

- `name` required
- `description` required

#### Update category

```json
{
  "name": "Updated name",
  "description": "Updated description"
}
```

Both fields are optional.

#### `CategoryDto`

```json
{
  "categoryId": 10,
  "name": "Electronics",
  "description": "Devices and gadgets",
  "createdAt": "2026-03-09T10:00:00",
  "updatedAt": "2026-03-09T10:00:00"
}
```

### Product payloads

#### Create product

```json
{
  "name": "Keyboard",
  "description": "Mechanical keyboard",
  "price": 89.99,
  "stockQuantity": 10,
  "categoryId": 10
}
```

Validation rules:

- all fields required
- `price >= 0`
- `stockQuantity >= 0`

#### Update product

```json
{
  "name": "Keyboard Pro",
  "description": "Updated description",
  "price": 99.99,
  "stockQuantity": 12,
  "categoryId": 11
}
```

All fields are optional, but numeric fields still require `>= 0`.

#### `ProductDto`

```json
{
  "id": 1,
  "name": "Keyboard",
  "description": "Mechanical keyboard",
  "price": 89.99,
  "stockQuantity": 10,
  "categoryId": 10,
  "sellerId": 5,
  "createdAt": "2026-03-09T10:00:00",
  "updatedAt": "2026-03-09T10:00:00"
}
```

### Cart payloads

#### Add to cart

```json
{
  "productId": 1,
  "quantity": 2
}
```

Validation rules:

- `productId` required
- `quantity` optional; the service defaults it to `1` when omitted

#### `CartItemDto`

```json
{
  "id": 3,
  "productId": 1,
  "quantity": 2
}
```

#### `CartDto`

```json
{
  "id": 1,
  "userId": 2,
  "items": [
    {
      "id": 3,
      "productId": 1,
      "quantity": 2
    }
  ],
  "createdAt": "2026-03-09T10:00:00",
  "updatedAt": "2026-03-09T10:05:00"
}
```

### Order payloads

#### `OrderItemDto`

```json
{
  "id": 7,
  "orderId": 4,
  "productId": 1,
  "quantity": 2,
  "price": 89.99,
  "status": "PENDING",
  "createdAt": "2026-03-09T10:10:00",
  "updatedAt": "2026-03-09T10:10:00"
}
```

#### `OrderDto`

```json
{
  "id": 4,
  "createdAt": "2026-03-09T10:10:00",
  "updatedAt": "2026-03-09T10:10:00",
  "totalPrice": 179.98,
  "items": []
}
```

## 9. Endpoint reference

Base URL examples below assume `http://localhost:8080`.

---

## 9.1 Authentication (`/auth`)

### `POST /auth/signup`

Create a new user account.

- Access: public
- Response: `201 Created`
- Body: `ApiResponse<UserDto>`

Example request:

```json
{
  "username": "alice",
  "email": "alice@example.com",
  "password": "P@ssw0rd"
}
```

Notes:

- new users are created with role `CUSTOMER`
- duplicate email returns `409 Conflict`
- duplicate username currently bubbles through the generic runtime handler and returns `400 Bad Request`

### `POST /auth/login`

Authenticate a user.

- Access: public
- Response: `200 OK`
- Body: `ApiResponse<LoginResponse>`
- Side effect: sets the `refreshToken` cookie

Example request:

```json
{
  "username": "alice",
  "password": "P@ssw0rd"
}
```

Example success payload:

```json
{
  "success": true,
  "errors": null,
  "data": {
    "token": "<access-token>"
  }
}
```

### `POST /auth/refresh`

Exchange the refresh token cookie for a new access token.

- Access: public route, but requires a valid `refreshToken` cookie
- Response: `200 OK` with `ApiResponse<LoginResponse>`
- Failure: `401 Unauthorized` when the token is missing or invalid

### `GET /auth/me`

Not implemented as an active endpoint.

---

## 9.2 Users (`/users`)

All `/users/**` routes require authentication.

### `GET /users/me`

Return the authenticated user.

- Access: authenticated
- Response: `200 OK`
- Body: `ApiResponse<UserDto>`

### `POST /users/upgrade-to-seller`

Upgrade the authenticated customer to a seller profile.

- Access: `CUSTOMER`
- Response: `200 OK`
- Body: `ApiResponse<SellerProfileDto>`

Example request:

```json
{
  "userId": 1,
  "storeName": "Alice Shop",
  "description": "My store"
}
```

Rules enforced in service logic:

- the authenticated user must exist
- request `userId` must equal the authenticated user id
- only `CUSTOMER` users can upgrade

Failure examples:

- mismatched `userId` -> `403 Forbidden`
- non-customer trying to upgrade -> `403 Forbidden`

---

## 9.3 Categories (`/categories`)

### `GET /categories`

Return all categories.

- Access: public
- Response: `200 OK`
- Body: `ApiResponse<List<CategoryDto>>`

### `GET /categories/{categoryId}`

Return one category by id.

- Access: public
- Response: `200 OK`
- Body: `ApiResponse<CategoryDto>`

### `POST /categories`

Create a category.

- Access: `SYSADMIN`
- Response: `201 Created`
- Body: `ApiResponse<CategoryDto>`

Example request:

```json
{
  "name": "Electronics",
  "description": "Devices and gadgets"
}
```

### `PUT /categories/{categoryId}`

Update category fields.

- Access: `SYSADMIN`
- Response: `200 OK`
- Body: `ApiResponse<CategoryDto>`

### `DELETE /categories/{categoryId}`

Delete a category.

- Access: `SYSADMIN`
- Response: `204 No Content`

Note: there is no public bootstrap/admin endpoint in this repository, so creating categories requires a user that already has the `SYSADMIN` role in the database.

---

## 9.4 Products (`/products`)

### `GET /products`

Return all products.

- Access: public
- Response: `200 OK`
- Body: `ApiResponse<List<ProductDto>>`

### `GET /products/{productId}`

Return one product.

- Access: public
- Response: `200 OK`
- Body: `ApiResponse<ProductDto>`

### `GET /products?category={categoryId}`

Return products for a category.

- Access: public
- Response: `200 OK`
- Body: `ApiResponse<List<ProductDto>>`

### `GET /products?seller={sellerId}`

Return products for a seller.

- Access: public
- Response: `200 OK`
- Body: `ApiResponse<List<ProductDto>>`

Use one query filter at a time. The controller exposes separate handlers for `category` and `seller`.

### `POST /products`

Create a product.

- Access: `SELLER`
- Response: `201 Created`
- Body: `ApiResponse<ProductDto>`

Example request:

```json
{
  "name": "Keyboard",
  "description": "Mechanical keyboard",
  "price": 89.99,
  "stockQuantity": 10,
  "categoryId": 10
}
```

Rules enforced in service logic:

- authenticated user must be a seller
- target category must exist

### `PUT /products/{productId}`

Update a seller-owned product.

- Access: `SELLER`
- Response: `200 OK`
- Body: `ApiResponse<ProductDto>`

Rules enforced in service logic:

- user must be a seller
- seller must own the product
- new category must exist if `categoryId` is supplied

### `DELETE /products/{productId}`

Delete a seller-owned product.

- Access: `SELLER`
- Response: `204 No Content`

Rules enforced in service logic:

- user must be a seller
- seller must own the product

---

## 9.5 Cart (`/carts`)

All cart routes require authentication. The controller and service behavior effectively make cart usage customer-only.

### `GET /carts`

Return the authenticated user's cart.

- Access: authenticated
- Response: `200 OK`
- Body: `ApiResponse<CartDto>`

Behavior:

- if the user has no cart yet, one is created automatically

### `POST /carts`

Add a product to cart.

- Access: `CUSTOMER`
- Response: `201 Created`
- Body: `ApiResponse<CartDto>`

Rules enforced in service logic:

- only customers can add to cart
- quantity defaults to `1` when omitted
- quantity must be greater than `0`
- product must exist
- total cart quantity for that product cannot exceed stock
- a cart is created automatically if missing

### `GET /carts/items/{cartItemId}`

Return one cart item belonging to the authenticated user.

- Access: authenticated
- Response: `200 OK`
- Body: `ApiResponse<CartItemDto>`

### `PUT /carts/items/{productId}?quantity={quantity}`

Update the quantity for a product already in the cart.

- Access: authenticated
- Response: `200 OK`
- Body: `ApiResponse<CartDto>`

Rules enforced in service logic:

- quantity must be greater than `0`
- quantity cannot exceed available stock
- the product must already be in the cart

### `DELETE /carts/items/{productId}`

Remove a product from cart by product id.

- Access: authenticated
- Response: `200 OK`
- Body: `ApiResponse<CartDto>`

### `DELETE /carts`

Clear the authenticated user's cart.

- Access: authenticated
- Response: `204 No Content`

---

## 9.6 Orders (`/orders`)

### Customer endpoints

#### `POST /orders`

Place an order from the authenticated customer's cart.

- Access: `CUSTOMER`
- Response: `201 Created`
- Body: `ApiResponse<OrderDto>`

Rules enforced in service logic:

- only customers can place orders
- customer must already have a cart
- cart must not be empty
- order items are created from cart items
- each item starts with status `PENDING`
- `totalPrice` is computed server-side
- cart is cleared after order placement

#### `GET /orders`

Return all orders for the authenticated customer.

- Access: `CUSTOMER`
- Response: `200 OK`
- Body: `ApiResponse<List<OrderDto>>`

#### `GET /orders/{orderId}`

Return one order owned by the authenticated customer.

- Access: `CUSTOMER`
- Response: `200 OK`
- Body: `ApiResponse<OrderDto>`

#### `PUT /orders?orderId={orderId}`

Cancel an order.

- Access: `CUSTOMER`
- Response: `204 No Content`

Rules enforced in service logic:

- only customers can cancel orders
- customer must own the order
- every order item must still be `PENDING`
- cancelled items are updated to `CANCELLED`

### Seller endpoints

#### `GET /orders/seller`

Return all order items associated with the authenticated seller's products.

- Access: `SELLER`
- Response: `200 OK`
- Body: `ApiResponse<List<OrderItemDto>>`

#### `GET /orders/seller/{productId}`

Return order items for a specific seller-owned product.

- Access: `SELLER`
- Response: `200 OK`
- Body: `ApiResponse<List<OrderItemDto>>`

#### `PUT /orders?id={id}&status={status}`

Update a seller-owned order item's status.

- Access: `SELLER`
- Response: `200 OK`
- Body: `ApiResponse<OrderItemDto>`

Allowed `status` values:

- `PENDING`
- `PROCESSING`
- `SHIPPED`
- `DELIVERED`
- `CANCELLED`

Important caveat:

- the route mapping requires query params named `id` and `status`
- the method reads `@RequestParam(name = "orderItemId")`
- because of that mismatch, this endpoint is currently inconsistent in code and may not behave as intended until fixed

## 10. Copy-paste examples

### Sign up

```bash
curl -X POST http://localhost:8080/auth/signup \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "alice",
    "email": "alice@example.com",
    "password": "P@ssw0rd"
  }'
```

### Login

```bash
curl -i -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "alice",
    "password": "P@ssw0rd"
  }'
```

### Call an authenticated endpoint

```bash
curl http://localhost:8080/users/me \
  -H 'Authorization: Bearer <access-token>'
```

### Refresh an access token with a cookie

```bash
curl -X POST http://localhost:8080/auth/refresh \
  --cookie 'refreshToken=<refresh-token>'
```

### Create a product as a seller

```bash
curl -X POST http://localhost:8080/products \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <seller-access-token>' \
  -d '{
    "name": "Keyboard",
    "description": "Mechanical keyboard",
    "price": 89.99,
    "stockQuantity": 10,
    "categoryId": 10
  }'
```

### Add to cart as a customer

```bash
curl -X POST http://localhost:8080/carts \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <customer-access-token>' \
  -d '{
    "productId": 1,
    "quantity": 2
  }'
```

## 11. Known integration caveats

These are current code-level behaviors worth knowing before you integrate:

1. `POST /auth/login` calls the login service twice internally.
2. The refresh token cookie is marked `Secure`, which complicates plain-HTTP browser testing.
3. Many business-rule failures are thrown as generic `RuntimeException`, so they surface as `400 Bad Request`.
4. The seller order-item status update endpoint has a query parameter mismatch and should be treated cautiously.
5. There is no built-in endpoint for creating a `SYSADMIN`; category management assumes one already exists in the database.
6. Product listing exposes separate handlers for `?category=` and `?seller=` filters.

## 12. What to read next in the codebase

If you want to extend or debug the API, these are the main entry points:

- controllers: `src/main/java/com/springboot/ecommerce/controllers`
- security config: `src/main/java/com/springboot/ecommerce/config/SecurityConfig.java`
- JWT filter/config: `src/main/java/com/springboot/ecommerce/config/JwtFilter.java`, `src/main/java/com/springboot/ecommerce/config/JwtConfig.java`
- services: `src/main/java/com/springboot/ecommerce/services`
- DTOs: `src/main/java/com/springboot/ecommerce/dtos`
- error handling: `src/main/java/com/springboot/ecommerce/exceptions/GlobalExceptionHandler.java`
