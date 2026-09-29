# AuthFlow – JWT Authentication System with OTP Verification

> A secure authentication system built with **Spring Boot**, **Spring Security**, **JWT (Access & Refresh Tokens)**, **PostgreSQL**, and **Brevo SMTP**. It implements email verification using OTP, secure password hashing with BCrypt, refresh token management, and protected authentication endpoints.

## Features

- User registration with **BCrypt hashed passwords**
- Email verification using **6-digit OTP**
- OTP expiry (5 minutes)
- OTP resend with attempt limit (3)
- JWT **Access Token** (15 minutes)
- JWT **Refresh Token** (7 days)
- One active refresh token per user
- Secure logout with refresh token invalidation
- Global exception handling
- Request validation using Jakarta Validation
- PostgreSQL database integration
- Email delivery using Brevo SMTP

---

## Tech Stack

| Category | Technology |
|----------|------------|
| Backend | Spring Boot 3 |
| Security | Spring Security + JWT |
| Database | PostgreSQL |
| ORM | Spring Data JPA (Hibernate) |
| Email | Brevo SMTP |
| Build Tool | Maven |
| Java | Java 17 |

---

## Project Structure

```text
src/main/java/com/archit/authflow
├── config
│   └── SecurityConfig
├── controller
│   └── AuthController
├── dto
│   ├── request
│   └── response
├── entity
│   ├── User
│   ├── Otp
│   └── RefreshToken
├── repository
├── security
│   └── JwtAuthenticationFilter
├── service
│   ├── AuthService
│   ├── JwtService
│   ├── OtpService
│   └── EmailService
└── exception
```

---

## Authentication Flow

```text
Register
    │
    ▼
Password Hashed (BCrypt)
    │
    ▼
OTP Generated & Hashed
    │
    ▼
OTP Sent via Brevo
    │
    ▼
Verify OTP
    │
    ▼
User Verified
    │
    ▼
Login
    │
    ├── Access Token (15 min)
    └── Refresh Token (7 days)
            │
            ▼
Stored in PostgreSQL
            │
            ▼
Refresh Token Endpoint
            │
            ▼
New Access Token
            │
            ▼
Logout
            │
            ▼
Refresh Token Deleted
```

---

## API Endpoints

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/auth/register` | Register user |
| POST | `/api/auth/verify-otp` | Verify email OTP |
| POST | `/api/auth/resend-otp` | Resend OTP |
| POST | `/api/auth/login` | Login and receive JWTs |
| POST | `/api/auth/refresh-token` | Generate new Access Token |
| POST | `/api/auth/logout` | Logout (Protected) |

---

## Example Requests

### Register

```http
POST /api/auth/register
```

```json
{
  "name": "Archit",
  "email": "archit@example.com",
  "password": "Password123"
}
```

### Login

```http
POST /api/auth/login
```

```json
{
  "email": "archit@example.com",
  "password": "Password123"
}
```

Response:

```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc..."
}
```

### Refresh Token

```http
POST /api/auth/refresh-token
```

```json
{
  "refreshToken": "eyJhbGc..."
}
```

---

## JWT Implementation

### Access Token

- Validity: **15 minutes**
- Used for authenticated requests.
- Verified through a custom `JwtAuthenticationFilter`.

### Refresh Token

- Validity: **7 days**
- Stored in PostgreSQL.
- Only one active refresh token exists per user.
- Old refresh tokens are removed before saving a new one.

---

## OTP Implementation

- 6-digit OTP
- Hashed before storing
- 5-minute expiry
- Automatically deleted after successful verification or expiry
- Maximum 3 resend attempts

---

## Security Features

- BCrypt password hashing
- JWT signature verification (HS256)
- Stateless authentication
- Refresh token revocation on logout
- Request validation using `@Valid`
- Global exception handling with meaningful HTTP status codes

---

## Environment Variables

Create the following environment variables before running the project:

| Variable | Purpose |
|----------|---------|
| `DB_USERNAME` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |
| `MAIL_USERNAME` | Brevo SMTP login |
| `MAIL_PASSWORD` | Brevo SMTP key |
| `JWT_SECRET` | Secret used for signing JWTs |

---

## Running Locally

Clone the repository.

```bash
  git clone https://github.com/archit05-dev/auth-flow.git
```

Move into the project.

```bash
  cd auth-flow
```

Configure PostgreSQL and environment variables.

Run the application.

```bash
  mvn spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

---

## Postman Collection

The project includes a Postman collection covering:

- Register
- Verify OTP
- Resend OTP
- Login
- Refresh Token
- Logout

---

## Future Improvements

- HttpOnly Cookie authentication
- Rate limiting for login attempts
- Refresh token rotation
- HTML email templates
- Password reset using OTP

---

## Author

**Archit Singhal**

- GitHub: https://github.com/archit05-dev
- LinkedIn: https://www.linkedin.com/in/archit-singhal-4a5049389/