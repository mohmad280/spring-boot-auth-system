![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/SpringBoot-3.x-brightgreen)
![License](https://img.shields.io/badge/License-MIT-blue)
# 🔐 Spring Boot Authentication & Security API

A secure authentication system built with **Spring Boot** that provides JWT-based authentication, email verification using OTP, password reset functionality, and secure profile management.

This project demonstrates how to build a modern authentication backend using **Spring Security**, **JWT**, and **email-based verification**.

---

## 🚀 Features

- User Registration
- User Login with JWT Authentication
- JWT stored in HttpOnly Cookies
- Email Verification using OTP
- Password Reset with OTP
- Secure Profile Retrieval
- Spring Security Integration
- Global Exception Handling
- Clean Architecture (Controller → Service → Repository)

---

## 🛠️ Technologies Used

- Java
- Spring Boot
- Spring Security
- JWT (JSON Web Token)
- Maven
- MySQL
- Lombok
- Jakarta Validation
- SMTP Email Service

---

## 📂 API Endpoints

### Authentication APIs

| Method | Endpoint | Description |
|------|------|------|
| POST | `/login` | Login user and generate JWT token |
| GET | `/is-Authenticated` | Check if the current user is authenticated |

---

### Registration

| Method | Endpoint | Description |
|------|------|------|
| POST | `/register` | Register a new user |

---

### Profile

| Method | Endpoint | Description |
|------|------|------|
| GET | `/profile` | Get authenticated user profile |

---

### Email Verification (OTP)

| Method | Endpoint | Description |
|------|------|------|
| POST | `/send-otp` | Send verification OTP to email |
| POST | `/verify-otp` | Verify email using OTP |

---

### Password Reset

| Method | Endpoint | Description |
|------|------|------|
| POST | `/send-rest-otp` | Send OTP for password reset |
| POST | `/reset-password` | Reset password using OTP |

---

## 🔐 Security

The system uses **JWT Authentication** with the following security features:

- HttpOnly Cookie for storing JWT
- Spring Security authentication filters
- Secure password hashing
- Email OTP verification
- Protected endpoints

---

## 📧 Email Service

The system integrates with an SMTP email service to send:

- Welcome email after registration
- OTP verification codes
- Password reset OTP

---