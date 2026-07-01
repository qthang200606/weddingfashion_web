<div align="center">

# 👰 Wedding Fashion Web

### Wedding Dress E-Commerce & Appointment Booking System

A full-stack web application developed using **Spring Boot MVC** for managing wedding fashion products, online shopping, appointment booking, AI consultation, and secure online payment.

![Java](https://img.shields.io/badge/Java-17-red)
![Spring Boot](https://img.shields.io/badge/SpringBoot-3.x-6DB33F)
![Spring Security](https://img.shields.io/badge/SpringSecurity-6.x-green)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1-success)
![MySQL](https://img.shields.io/badge/MySQL-8-blue)
![Gradle](https://img.shields.io/badge/Gradle-8-02303A)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED)

</div>

---

# 📖 Project Overview

Wedding Fashion Web is a web-based e-commerce application that helps customers search, purchase, and book appointments for wedding dresses and accessories.

The system also provides an administration dashboard for managing products, categories, orders, appointments, customers, and inventory.

Besides shopping features, the application integrates an AI assistant to provide personalized wedding fashion consultation and supports online payment through SePay.

---

# ✨ Features

## 👤 Customer

- Register new account
- Secure login/logout
- Browse wedding products
- View product details
- Search products by keyword
- Filter products by category
- Shopping cart
- Checkout
- Online payment via SePay
- Book fitting appointments
- View order history
- AI-powered consultation

---

## 👨‍💼 Administrator

- Dashboard overview
- Product management
- Category management
- Order management
- Appointment management
- Revenue statistics

---

# 🤖 AI Assistant

The project integrates **Groq AI** to improve customer experience.

Functions include:

- Wedding dress consultation
- Product recommendations
- Customer support
- Fashion suggestions
- Frequently asked questions

---

# 💳 Online Payment

The application integrates **SePay Webhook** for payment processing.

Supported features:

- Online payment
- Automatic payment confirmation
- Payment verification
- Automatic order status update

---

# 🛠 Technology Stack

## Backend

- Java 17
- Spring Boot
- Spring MVC
- Spring Security
- Spring Data JPA
- Hibernate

---

## Frontend

- HTML5
- CSS3
- JavaScript
- Bootstrap
- Thymeleaf

---

## Database

- MySQL

---

## AI

- Groq API

---

## Payment

- SePay Webhook

---

## Build Tool

- Gradle

---

## Deployment

- Docker

---

# 📂 Project Structure

```text
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
│   │   ├── config
│   │   ├── security
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

# 🚀 Getting Started

## Clone the repository

```bash
git clone https://github.com/qthang200606/weddingfashion_web.git
```

---

## Open the project

```bash
cd weddingfashion_web
```

---

## Configure MySQL

Edit

```properties
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/weddingfashion
spring.datasource.username=root
spring.datasource.password=yourpassword

spring.jpa.hibernate.ddl-auto=update
```

---

## Build the project

Linux / macOS

```bash
./gradlew build
```

Windows

```bash
gradlew.bat build
```

---

## Run the application

Linux / macOS

```bash
./gradlew bootRun
```

Windows

```bash
gradlew.bat bootRun
```

Open your browser:

```
http://localhost:8080
```

---

# 📊 Main Modules

- User Authentication
- Product Management
- Category Management
- Shopping Cart
- Order Processing
- Appointment Booking
- AI Consultation
- Inventory Management
- Revenue Statistics
- Online Payment

---


# 🎯 Future Improvements

- Email notification
- Product review system
- Wishlist
- Responsive mobile interface
- Recommendation engine
- Multi-language support

---

# 📚 Learning Outcomes

Through this project, I gained practical experience in:

- Spring Boot MVC Architecture
- Spring Security
- Hibernate & Spring Data JPA
- MySQL Database Design
- Thymeleaf Template Engine
- Docker Deployment
- AI Integration with Groq API
- SePay Payment Integration
- Git & GitHub Version Control

---

# 👨‍💻 Author

**Ngô Quang Thắng**

Computer Science Student

📧 Email: thang93dhp@gmail.com

🌐 GitHub: https://github.com/qthang200606

---

# ⭐ Support

If you find this project useful, please consider giving it a ⭐ on GitHub.

Thank you for visiting this repository!
