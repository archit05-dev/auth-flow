# AuthFlow – JWT Authentication System with OTP Verification

> A secure authentication system built with **Spring Boot**, **Spring Security**, **JWT (Access & Refresh Tokens)**, **PostgreSQL**, and the **Brevo Email API**. It implements email verification using OTP, secure password hashing with BCrypt, refresh token management, protected authentication endpoints, and production deployment using Railway.

## Live API

**Base URL:**

https://auth-flow-production-fb66.up.railway.app

Health check:

GET /

Response:

AuthFlow API is running!

---

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
- Email delivery using the **Brevo REST API**
- Deployed on **Railway**
- Production PostgreSQL database using **Neon**

---

## Tech Stack

| Category | Technology |
|----------|------------|
| Backend | Spring Boot 4 |
| Security | Spring Security + JWT |
| Database | PostgreSQL |
| ORM | Spring Data JPA (Hibernate) |
| Email | Brevo REST API |
| API Client | Spring RestClient |
| Deployment | Railway |
| Database Hosting | Neon PostgreSQL |
| Build Tool | Maven |
| Java | Java 21 |

---

## Project Structure

```text
src/main/java/com/archit/authflow
├── config
│   ├── SecurityConfig
│   └── RestClientConfig
├── controller
│   ├── AuthController
│   └── HealthController
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
HTTPS Request to Brevo API
    │
    ▼
OTP Email Sent
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
| GET | `/` | Health check |
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

---

### Verify OTP

```http
POST /api/auth/verify-otp
```

```json
{
  "email": "archit@example.com",
  "otp": "123456"
}
```

---

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

---

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

### Logout

```http
POST /api/auth/logout
Authorization: Bearer <access-token>
```

---

## JWT Implementation

### Access Token

- Validity: **15 minutes**
- Used for authenticated requests.
- Verified through a custom `JwtAuthenticationFilter`.
- Not stored in the database.
- Signed using **HS256**.

### Refresh Token

- Validity: **7 days**
- Stored in PostgreSQL.
- Only one active refresh token exists per user.
- Old refresh tokens are removed before saving a new one.
- Deleted when the user logs out.

---

## OTP Implementation

- 6-digit OTP
- Hashed before storing
- 5-minute expiry
- Automatically deleted after successful verification or expiry
- Maximum 3 resend attempts
- Expired OTP records are cleaned up automatically

---

## Email Delivery

AuthFlow uses the **Brevo REST API** for transactional email delivery.

Instead of connecting directly to an SMTP server, the application sends an HTTPS POST request to Brevo's email API.

```text
AuthFlow
    │
    ▼
EmailService
    │
    ▼
Spring RestClient
    │
    ▼
HTTPS POST
    │
    ▼
Brevo Email API
    │
    ▼
OTP Email
```

The Brevo API key is stored securely as an environment variable and is never hardcoded into the application.

---

## Security Features

- BCrypt password hashing
- JWT signature verification (HS256)
- Stateless authentication
- Refresh token storage and revocation
- Refresh token invalidation on logout
- Request validation using `@Valid`
- Global exception handling with meaningful HTTP status codes
- Secrets managed through environment variables
- Protected authentication endpoints

---

## Environment Variables

Create the following environment variables before running the project:

| Variable | Purpose |
|----------|---------|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC connection URL |
| `DB_USERNAME` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |
| `BREVO_API_KEY` | Brevo API authentication key |
| `MAIL_FROM` | Verified sender email address |
| `JWT_SECRET` | Secret used for signing JWTs |

**Do not commit actual secret values to GitHub.**

---

## Running Locally

Clone the repository:

```bash
  git clone https://github.com/archit05-dev/auth-flow.git
```

Move into the project:

```bash
  cd auth-flow
```

Configure PostgreSQL and the required environment variables.

Run the application:

```bash
  mvn spring-boot:run
```

The API will be available at:

http://localhost:8080

---

## Production Deployment

AuthFlow is deployed using **Railway**.

The production application uses:

- **Railway** for application hosting
- **Neon PostgreSQL** for the production database
- **Brevo REST API** for transactional email delivery

Production Base URL:

https://auth-flow-production-fb66.up.railway.app

---

## Postman Collection

The project includes a Postman collection covering:

- Register
- Verify OTP
- Resend OTP
- Login
- Refresh Token
- Logout

The endpoints have been tested against the deployed production API.

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