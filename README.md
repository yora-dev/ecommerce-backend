# E-commerce API

A Spring Boot backend for a small e-commerce platform.

## What it includes

- JWT authentication with refresh-token cookies
- user signup and login
- customer-to-seller upgrade flow
- category and product management
- shopping cart operations
- order placement and seller order-status handling

## Tech stack

- Java 21
- Spring Boot 3.5.11
- Maven Wrapper
- Spring Security
- Spring Data JPA
- MySQL-compatible database

## Quick start

### 1. Configure environment variables

Copy `.env.example` and provide values for:

- `JWT_SECRET`
- `DATABASE_URL`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`

The app reads the same values in `src/main/resources/application.yaml`.

### 2. Run tests

```bash
./mvnw clean test
```

### 3. Start the app

```bash
./mvnw spring-boot:run
```

Default local base URL:

```text
http://localhost:8080
```

## Auth at a glance

- `POST /auth/signup` creates a `CUSTOMER`
- `POST /auth/login` returns an access token
- protected routes expect `Authorization: Bearer <token>`
- `POST /auth/refresh` uses a `refreshToken` cookie

Main roles used by the API:

- `CUSTOMER`
- `SELLER`
- `SYSADMIN`

## Main route groups

- `/auth` — authentication
- `/users` — current user and seller upgrade
- `/categories` — category management
- `/products` — product catalog and seller products
- `/carts` — customer cart operations
- `/orders` — customer orders and seller order items

## Documentation

- Full developer/API guide: [`DOCUMENTATION.md`](DOCUMENTATION.md)
- Spring Boot helper notes: [`HELP.md`](./HELP.md)

## Project layout

```text
src/main/java/com/springboot/ecommerce
├── config/
├── controllers/
├── dtos/
├── entities/
├── exceptions/
├── mappers/
├── repositories/
└── services/
```

