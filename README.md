<div align="center">

# 👰 Wedding Fashion Web

### AI-Powered Wedding Dress E-commerce Platform

A full-stack web application built with **Spring Boot**, providing an online wedding fashion shopping experience with **AI consultation**, **appointment booking**, **online payment**, and **admin management**.

<p align="center">

![Java](https://img.shields.io/badge/Java-17-red)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F)
![Spring Security](https://img.shields.io/badge/Spring_Security-6.x-green)
![MySQL](https://img.shields.io/badge/MySQL-8-blue)
![Gradle](https://img.shields.io/badge/Gradle-8-02303A)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED)
![REST API](https://img.shields.io/badge/REST-API-orange)
![License](https://img.shields.io/badge/License-MIT-yellow)

</p>

</div>

---

# 📖 Introduction

Wedding Fashion Web is a full-stack e-commerce application designed for wedding fashion businesses.

The system enables customers to browse wedding dresses, place orders, book fitting appointments, communicate with an AI assistant, and complete secure online payments.

The project also provides a comprehensive administration dashboard for managing products, customers, appointments, inventory, and orders.

---

# ✨ Main Features

## 👤 Customer

- User Registration
- User Login
- JWT Authentication
- Browse Products
- Product Categories
- Search Products
- Shopping Cart
- Checkout
- Online Payment (SePay)
- Order History
- Appointment Booking
- AI Fashion Consultant
- User Profile Management

---

## 👨‍💼 Administrator

- Dashboard
- Product Management
- Category Management
- Customer Management
- Order Management
- Appointment Management
- Inventory Management
- Revenue Statistics
- Low Stock Monitoring

---

# 🤖 AI Features

The application integrates **Groq API** to provide an intelligent shopping assistant.

Capabilities include:

- Wedding dress consultation
- Product recommendation
- Natural language conversation
- Customer support
- Fashion suggestions
- FAQ assistance

---

# 💳 Online Payment

Integrated with **SePay Webhook API**

Supported features:

- Secure online payment
- Automatic payment confirmation
- Order status synchronization
- Transaction verification
- Payment notification

---

# 🏗 System Architecture

```
                    +----------------------+
                    |      Customer        |
                    +----------+-----------+
                               |
                               |
                        HTML / CSS / JS
                               |
                               |
                    Spring Boot REST API
                               |
       +-----------+-----------+-------------+
       |           |                         |
       |           |                         |
    MySQL       Groq API                 SePay API
(Database)      (AI Chat)              (Payment)
```

---

# 🛠 Technology Stack

## Backend

- Java 17
- Spring Boot
- Spring MVC
- Spring Security
- Spring Data JPA
- Hibernate
- RESTful API

---

## Frontend

- HTML5
- CSS3
- JavaScript
- Bootstrap

---

## Database

- MySQL

---

## Authentication

- JWT
- Spring Security

---

## AI

- Groq API

---

## Payment

- SePay Webhook API

---

## Build Tool

- Gradle

---

## Deployment

- Docker

---

# 📂 Project Structure

```
WeddingFashionWeb
│
├── src
│   ├── main
│   │
│   ├── java
│   │   ├── controller
│   │   ├── service
│   │   ├── repository
│   │   ├── entity
│   │   ├── dto
│   │   ├── security
│   │   ├── config
│   │   ├── exception
│   │   └── util
│   │
│   └── resources
│       ├── static
│       ├── templates
│       └── application.properties
│
├── uploads
├── gradle
├── Dockerfile
├── build.gradle
└── README.md
```

---

# 📸 Screenshots

## Home Page

> Add screenshot here

---

## Product List

> Add screenshot here

---

## Product Details

> Add screenshot here

---

## Shopping Cart

> Add screenshot here

---

## Checkout

> Add screenshot here

---

## AI Chat

> Add screenshot here

---

## Appointment Booking

> Add screenshot here

---

## Admin Dashboard

> Add screenshot here

---

# 🔒 Security

Implemented security mechanisms include:

- JWT Authentication
- Password Encryption
- Spring Security
- Role-Based Authorization
- Form Validation
- API Protection

---

# 🚀 Installation

## Clone repository

```bash
git clone https://github.com/qthang200606/weddingfashion_web.git
```

---

## Enter project

```bash
cd weddingfashion_web
```

---

## Configure Database

Open

```
src/main/resources/application.properties
```

Configure

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/weddingfashion
spring.datasource.username=root
spring.datasource.password=yourpassword

spring.jpa.hibernate.ddl-auto=update
```

---

## Build project

```bash
./gradlew build
```

Windows

```bash
gradlew.bat build
```

---

## Run application

```bash
./gradlew bootRun
```

or

```bash
gradlew.bat bootRun
```

Application URL

```
http://localhost:8080
```

---

# 🐳 Docker

Build Docker Image

```bash
docker build -t weddingfashion .
```

Run Container

```bash
docker run -p 8080:8080 weddingfashion
```

---

# 📊 Database

Example tables

- users
- roles
- products
- categories
- carts
- orders
- order_details
- appointments
- payments
- inventory

---

# 🔄 Workflow

```
Customer

↓

Browse Products

↓

Add to Cart

↓

Checkout

↓

SePay Payment

↓

Webhook Verification

↓

Order Confirmation

↓

Order Management

↓

Delivery
```

---

# 🎯 Future Improvements

- Email Notification
- Recommendation System
- Product Reviews
- Wishlist
- Responsive Mobile UI
- Multi-language Support
- Product Rating
- Analytics Dashboard

---

# 📚 Learning Outcomes

This project demonstrates practical experience in:

- Full Stack Java Web Development
- Spring Boot
- REST API Development
- JWT Authentication
- Spring Security
- MySQL Database Design
- AI Integration
- Payment Gateway Integration
- Docker Deployment
- MVC Architecture
- Git & GitHub

---

# 👨‍💻 Author

**Ngô Quang Thắng**

Computer Science Student

GitHub

https://github.com/qthang200606

---

# ⭐ If you find this project useful, please consider giving it a Star!
