# E-commerce API

A RESTful backend for a small e-commerce platform built with Spring Boot 3. It covers user registration and login, a customer-to-seller upgrade flow, category and product management, a shopping cart, and order placement with seller-side status tracking.

## Table of contents

- [Tech stack](#tech-stack)
- [Features](#features)
- [Project layout](#project-layout)
- [Quick start](#quick-start)
- [Environment variables](#environment-variables)
- [Running tests](#running-tests)
- [Authentication](#authentication)
- [Roles and permissions](#roles-and-permissions)
- [API overview](#api-overview)
- [Data models](#data-models)
- [Documentation](#documentation)

---

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.11 |
| Build | Maven Wrapper (`./mvnw`) |
| Security | Spring Security + JWT (JJWT 0.12.x) |
| Persistence | Spring Data JPA / Hibernate |
| Database | MySQL-compatible (MySQL 8 / MariaDB) |
| Validation | Jakarta Bean Validation |
| Utilities | Lombok, dotenv-java |
| Testing | JUnit 5, Mockito, Spring Boot Test |

---

## Features

- **Auth** — sign up, login, access token + HttpOnly refresh-token cookie
- **Roles** — `CUSTOMER`, `SELLER`, `SYSADMIN` with method-level authorization
- **Seller upgrade** — a `CUSTOMER` can request a seller profile and get a `SELLER` role
- **Categories** — full CRUD managed by `SYSADMIN`
- **Products** — sellers create, update and delete their own listings; public read access
- **Cart** — customers add, update quantity, remove items, and clear the cart
- **Orders** — customers place orders from their cart; sellers update per-item status

---

## Project layout

```text
src/
└── main/
    ├── java/com/springboot/ecommerce/
    │   ├── config/          # Security config, JWT filter, JWT utility
    │   ├── controllers/     # REST controllers (one per resource)
    │   ├── dtos/            # Request and response DTOs
    │   ├── entities/        # JPA entities and enums
    │   ├── exceptions/      # Custom exceptions and global handler
    │   ├── mappers/         # Entity ↔ DTO mapping
    │   ├── repositories/    # Spring Data JPA repositories
    │   └── services/        # Business logic interfaces + implementations
    └── resources/
        └── application.yaml
```

---

## Quick start

### Prerequisites

- Java 21
- A running MySQL 8 (or MariaDB) instance

### 1. Clone and configure

```bash
git clone https://github.com/yora-dev/ecommerce-backend.git
cd ecommerce-backend
cp .env.example .env
```

Open `.env` and fill in the four required values (see [Environment variables](#environment-variables)).

### 2. Run tests

```bash
./mvnw clean test
```

### 3. Start the application

```bash
./mvnw spring-boot:run
```

The API is available at `http://localhost:8080`.

### 4. Build a deployable jar

```bash
./mvnw clean package
java -jar target/*.jar
```

---

## Environment variables

The application reads these variables from a `.env` file in the project root (loaded automatically by dotenv-java) and from `src/main/resources/application.yaml`.

| Variable | Description | Example |
|---|---|---|
| `DATABASE_URL` | JDBC connection string | `jdbc:mysql://localhost:3306/ecommerce` |
| `DATABASE_USERNAME` | Database user | `root` |
| `DATABASE_PASSWORD` | Database password | `secret` |
| `JWT_SECRET` | HMAC-SHA secret for signing JWTs (use a long random string) | `changeme-use-a-long-random-value` |

Token lifetimes (configured in `application.yaml`):

| Token | Duration |
|---|---|
| Access token | 720 seconds (12 minutes) |
| Refresh token | 604 800 seconds (7 days) |

---

## Running tests

```bash
./mvnw clean test
```

Tests live under `src/test/java/com/springboot/ecommerce/` and cover each controller using JUnit 5 and Mockito.

---

## Authentication

### Sign up and log in

```http
POST /auth/signup
Content-Type: application/json

{
  "username": "alice",
  "email": "alice@example.com",
  "password": "s3cr3t"
}
```

```http
POST /auth/login
Content-Type: application/json

{
  "username": "alice",
  "password": "s3cr3t"
}
```

The login response body contains an `accessToken`. The response also sets an `HttpOnly` cookie named `refreshToken` scoped to `/auth/refresh`.

### Using the access token

Include the token in the `Authorization` header on every protected request:

```http
Authorization: Bearer <access-token>
```

### Refreshing the token

```http
POST /auth/refresh
```

The browser (or HTTP client) must send the `refreshToken` cookie. Because the cookie is set with `Secure=true`, browsers will only include it over HTTPS. CLI clients can pass the cookie explicitly.

### JWT claims

| Claim | Value |
|---|---|
| `sub` | User ID |
| `username` | Username |
| `email` | Email |
| `role` | `CUSTOMER` / `SELLER` / `SYSADMIN` |

---

## Roles and permissions

| Role | What it can do |
|---|---|
| `CUSTOMER` | Browse products and categories, manage own cart, place and cancel orders, upgrade to seller |
| `SELLER` | Everything a `CUSTOMER` can do, plus create/update/delete own products and manage the status of their order items |
| `SYSADMIN` | Create, update and delete categories |

New accounts always start as `CUSTOMER`. A customer can call `POST /users/upgrade-to-seller` to become a `SELLER`.

---

## API overview

All responses are wrapped in an `ApiResponse` envelope:

```json
{
  "message": "human-readable status",
  "data": { ... }
}
```

Errors follow the same envelope with an `error` field in place of `data` and an appropriate HTTP status code.

### Auth — `/auth`

| Method | Path | Access | Description |
|---|---|---|---|
| `POST` | `/auth/signup` | Public | Register a new `CUSTOMER` account |
| `POST` | `/auth/login` | Public | Log in and receive an access token |
| `POST` | `/auth/refresh` | Cookie required | Exchange the refresh cookie for a new access token |

### Users — `/users`

| Method | Path | Access | Description |
|---|---|---|---|
| `GET` | `/users/me` | Authenticated | Get the current user's profile |
| `POST` | `/users/upgrade-to-seller` | `CUSTOMER` | Create a seller profile and receive a `SELLER` role |

### Categories — `/categories`

| Method | Path | Access | Description |
|---|---|---|---|
| `GET` | `/categories` | Public | List all categories |
| `GET` | `/categories/{id}` | Public | Get a single category |
| `POST` | `/categories` | `SYSADMIN` | Create a category |
| `PUT` | `/categories/{id}` | `SYSADMIN` | Update a category |
| `DELETE` | `/categories/{id}` | `SYSADMIN` | Delete a category |

### Products — `/products`

| Method | Path | Access | Description |
|---|---|---|---|
| `GET` | `/products` | Public | List all products (optional `?category={id}` or `?seller={id}` filter) |
| `GET` | `/products/{id}` | Public | Get a single product |
| `POST` | `/products` | `SELLER` | Create a product |
| `PUT` | `/products/{id}` | `SELLER` | Update own product |
| `DELETE` | `/products/{id}` | `SELLER` | Delete own product |

### Cart — `/carts`

| Method | Path | Access | Description |
|---|---|---|---|
| `GET` | `/carts` | Authenticated | Get the current user's cart (created on first access) |
| `POST` | `/carts` | `CUSTOMER` | Add a product to the cart |
| `GET` | `/carts/items/{itemId}` | Authenticated | Get a specific cart item |
| `PUT` | `/carts/items/{productId}?quantity={n}` | Authenticated | Update quantity for a product in the cart |
| `DELETE` | `/carts/items/{productId}` | Authenticated | Remove a product from the cart |
| `DELETE` | `/carts` | Authenticated | Clear the entire cart |

### Orders — `/orders`

| Method | Path | Access | Description |
|---|---|---|---|
| `POST` | `/orders` | `CUSTOMER` | Place an order from the current cart |
| `GET` | `/orders` | `CUSTOMER` | List the current user's orders |
| `GET` | `/orders/{id}` | `CUSTOMER` | Get a specific order |
| `PUT` | `/orders?orderId={id}` | `CUSTOMER` | Cancel an order (only `PENDING` items) |
| `GET` | `/orders/seller` | `SELLER` | List all order items for the seller's products |
| `GET` | `/orders/seller/{productId}` | `SELLER` | List order items for a specific product |
| `PUT` | `/orders?id={id}&status={status}` | `SELLER` | Update an order item's status |

**`OrderStatus` values:** `PENDING` → `PROCESSING` → `SHIPPED` → `DELIVERED` (or `CANCELLED`)

---

## Data models

### User

| Field | Type | Notes |
|---|---|---|
| `id` | `Long` | Primary key |
| `username` | `String` | Login identifier |
| `email` | `String` | Email address |
| `password` | `String` | BCrypt-hashed |
| `role` | `Role` | `CUSTOMER`, `SELLER`, or `SYSADMIN` |
| `createdAt` | `LocalDateTime` | |
| `updatedAt` | `LocalDateTime` | |

### Product

| Field | Type | Notes |
|---|---|---|
| `id` | `Long` | Primary key |
| `name` | `String` | |
| `description` | `String` | |
| `price` | `BigDecimal` | |
| `stockQuantity` | `Integer` | |
| `seller` | `User` | FK to users |
| `category` | `Category` | FK to categories |
| `createdAt` | `LocalDateTime` | |
| `updatedAt` | `LocalDateTime` | |

### Category

| Field | Type |
|---|---|
| `id` | `Long` |
| `name` | `String` |
| `description` | `String` |
| `createdAt` | `LocalDateTime` |
| `updatedAt` | `LocalDateTime` |

### Cart / CartItem

A `Cart` belongs to one `User` and holds a list of `CartItem` records. Each `CartItem` references a `Product` and stores a `quantity`.

### Order / OrderItem

An `Order` is created from the customer's cart. Each `OrderItem` captures the `product`, `quantity`, `priceAtOrder` (snapshot of price at purchase time), and an independent `OrderStatus` that the seller can advance.

### SellerProfile

Created when a `CUSTOMER` upgrades to `SELLER`. Stores `storeName` and `description` alongside a one-to-one reference to the `User`.

---

## Documentation

For a complete developer guide including full payload schemas, curl examples, error codes, and known caveats, see [`DOCUMENTATION.md`](DOCUMENTATION.md).

