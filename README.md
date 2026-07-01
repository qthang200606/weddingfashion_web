<div align="center">

# 💍 Wedding Fashion Web

### Modern Wedding Dress Shopping & Rental Management System

<img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk"/>
<img src="https://img.shields.io/badge/Spring-Boot-6DB33F?style=for-the-badge&logo=springboot"/>
<img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql"/>

<img src="https://img.shields.io/badge/SePay-Payment-00C853?style=for-the-badge"/>
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker"/>

A full-featured wedding fashion management system supporting product sales, rentals, appointment booking, AI consultation, and online payment.

</div>

---

# 📖 Overview

Wedding Fashion Web is a comprehensive web-based management system designed for wedding fashion businesses.

The system enables customers to browse, purchase, or rent wedding dresses and accessories while allowing administrators to efficiently manage products, orders, inventory, appointments, and customers.

The project is built using **Spring Boot**, providing RESTful APIs for both the Android mobile application and the web administration system.

---

# ✨ Key Features

## 👰 Customer

- User Registration & Login
- Browse Products
- Product Categories
- Product Details
- Shopping Cart
- Checkout
- Online Payment
- Appointment Booking
- Order Tracking
- Wishlist
- Product Reviews

---

## 👨‍💼 Administrator

- Dashboard
- Product Management
- Category Management
- Inventory Management
- Customer Management
- Appointment Management
- Order Management
- Revenue Statistics
- User Management
- Payment Management

---

# 🤖 AI Integration

Integrated with **Groq AI API**

Features include:

- Intelligent wedding dress consultation
- Personalized product recommendations
- Wedding planning suggestions
- Natural language conversations
- Multi-turn conversations

---

# 💳 Payment System

Integrated with **SePay**

Supported Features

- QR Payment
- Payment Confirmation
- Webhook Verification
- Automatic Order Status Update

---

# 🏗 System Architecture

```
Client
│
├── Android Application
├── Web Browser
│
▼

Spring Boot REST API

│

├── Authentication
├── Product Service
├── Appointment Service
├── Order Service
├── AI Service
├── Payment Service
│
▼

MySQL Database

│

Firebase Storage
```

---

# 🛠 Tech Stack

| Technology | Purpose |
|------------|---------|
| Java 17 | Backend Development |
| Spring Boot | REST API |
| Spring Security | Authentication |
| Spring Data JPA | Database Access |
| MySQL | Relational Database |
| Firebase Storage | Image Storage |
| Groq API | AI Chat |
| SePay API | Payment Gateway |
| Docker | Deployment |
| Gradle | Build Tool |

---

# 📂 Project Structure

```
src
│
├── main
│   ├── java
│   │   ├── controller
│   │   ├── service
│   │   ├── repository
│   │   ├── model
│   │   ├── dto
│   │   ├── config
│   │   ├── security
│   │   └── exception
│   │
│   └── resources
│       ├── static
│       ├── templates
│       ├── application.properties
│       └── uploads
│
└── test
```

---

# 🚀 REST APIs

The backend provides RESTful APIs for:

- Authentication
- Products
- Categories
- Orders
- Cart
- Appointments
- Customers
- Payments
- AI Assistant
- Revenue Statistics

---

# 🔐 Security

- Spring Security
- Password Encryption
- Role-based Authorization
- JWT Authentication
- API Protection

---

# 📊 Database

Main entities

- Users
- Roles
- Products
- Categories
- Orders
- Order Details
- Appointments
- Payments
- Reviews
- Inventory

---

# 📸 Screenshots

## Customer

- Home Page
- Product Detail
- Shopping Cart
- Checkout
- Appointment Booking

## Administrator

- Dashboard
- Product Management
- Order Management
- Revenue Statistics
- Inventory Management

> Replace this section with screenshots after deployment.

---

# ⚙ Installation

## Clone Repository

```bash
git clone https://github.com/qthang200606/weddingfashion_web.git
```

Enter project

```bash
cd weddingfashion_web
```

Run

```bash
./gradlew bootRun
```

or

```bash
gradlew bootRun
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

# ⚙ Configuration

Configure the following before running:

- MySQL Database
- Firebase Credentials
- Groq API Key
- SePay API Key
- JWT Secret

---

# 📈 Future Improvements

- Email Notification
- Push Notification
- Product Recommendation AI
- Chatbot Enhancement
- Analytics Dashboard
- Multi-language Support
- Cloud Deployment
- CI/CD Pipeline

---

# 👨‍💻 My Responsibilities

This project was independently designed and developed, including:

- System Analysis
- Database Design
- Backend Development
- REST API Development
- Authentication & Authorization
- AI Integration
- Payment Integration
- Docker Deployment
- Testing & Debugging

---

# 📊 Project Highlights

✅ Spring Boot REST API

✅ JWT Authentication

✅ Role-based Security

✅ MySQL Database

✅ Firebase Storage

✅ SePay Payment Integration

✅ AI Chat Assistant

✅ Docker Deployment

✅ Appointment Booking

✅ Inventory Management

---

# 📄 License

This project is developed for educational purposes and portfolio demonstration.

---

# 👤 Author

**Ngô Quang Thắng**

📧 Email: your_email@gmail.com

💻 GitHub: https://github.com/qthang200606

---

<div align="center">

⭐ If you find this project useful, please consider giving it a star!

</div>
