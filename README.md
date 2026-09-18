# Online Bookstore REST API

The Online Bookstore REST API is an application developed using Java 17 and Spring Boot that provides RESTful APIs for managing users, books, shopping carts,
and orders. The application follows a layered architecture with separate Controller, Service, Repository, Entity, DTO, Security, and Exception Handling components. It uses Spring
Data JPA for database operations and H2 as the database for development and testing.

# Technology

- Java 17
- Spring Boot 3.5.16
- Spring Web
- Spring Data JPA
- Spring Security
- H2 Database
- Maven
- JUnit 5
- Mockito

# Features

- User registration
- User login
- Password hashing
- Book CRUD
- Shopping cart
- Add/remove books
- Update quantities
- Checkout
- Order history
- Input validation
- Global exception handling
- H2 database
- REST APIs

# Requirements

- Java 17+
- Maven 3.6+

# Run

bash
mvn clean test
mvn spring-boot:run

# Application:

http://localhost:8080

# H2 console:

http://localhost:8080/h2-console

# Authentication
Register/login first.

Then use:

Authorization: Bearer <token> for protected endpoints.

## API Authentication
POST /api/auth/register

POST /api/auth/login

#API Books
GET /api/books

GET /api/books/{id}

POST /api/books

PUT /api/books/{id}

DELETE /api/books/{id}

# API Cart
GET /api/cart

POST /api/cart/items

PATCH /api/cart/items/{bookId}

DELETE /api/cart/items/{bookId}

# API Orders
POST /api/orders/checkout

GET /api/orders

GET /api/orders/{id}

## H2 Databse Details
JDBC URL: jdbc:h2:mem:bookstoredb

Username: sa

Password:

#Application Run :
Bash
Run: mvn spring-boot:run

Bash
Run: mvn test


------------------------------------------
############################################## API DETAILS ##########################################

#Register
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
"name": "Rana Singh",
"email": "ranasigh@gmail.com",
"password": "*********"
}

Expected:

{
"token": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
"userId": 1,
"name": "Rana Singh",
"email": "ranasigh@gmail.com"
}

Save the token.

# Get books
GET http://localhost:8080/api/books

Example:

[
{
"id": 1,
"title": "Five Point Someone",
"author": "Chetan Bhagat",
"price": 45.00
},
{
"id": 2,
"title": "The White Tiger",
"author": "Aravind Adiga",
"price": 55.00
}
]

# Add book to cart
Use the token:

POST http://localhost:8080/api/cart/items
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
"bookId": 1,
"quantity": 2
}

#View cart
GET http://localhost:8080/api/cart
Authorization: Bearer YOUR_TOKEN

Example:

{
"items": [
{
"id": 1,
"book": {
"id": 1,
"title": "XXXXX",
"author": "XXXXXXX",
"price": 45.00
},
"quantity": 2
}
],
"total": 90.00
}

# Change quantity
PATCH http://localhost:8080/api/cart/items/1
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
"quantity": 5
}

# Remove from cart
DELETE http://localhost:8080/api/cart/items/1
Authorization: Bearer YOUR_TOKEN
    

# Checkout
    First add something to the cart again.

Then:

POST http://localhost:8080/api/orders/checkout
Authorization: Bearer YOUR_TOKEN

Example response:

{
"id": 1,
"totalAmount": 90.00,
"status": "PLACED",
"createdAt": "2026-09-17T11:15:20",
"items": [
{
"title": "XXXXX",
"price": 45.00,
"quantity": 2,
"subtotal": 90.00
}
]
}

# Get order history
GET http://localhost:8080/api/orders
Authorization: Bearer YOUR_TOKEN


#Specific order:
GET http://localhost:8080/api/orders/1
Authorization: Bearer YOUR_TOKEN


# Architecture

The application follows a layered REST API architecture using Spring Boot.

                    ┌─────────────────────┐
                    │     REST Client     │
                    │      Postman  │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │     Controllers     │
                    │ Auth / Book / Cart  │
                    │       / Order       │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │      Services       │
                    │    Business Logic   │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │   Spring Data JPA   │
                    │    Repositories     │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │     H2 Database     │
                    │                     │
                    │ Users               │
                    │ Books               │
                    │ Cart Items          │
                    │ Orders              │
                    │ Order Items         │
                    │ Auth Tokens         │
                    └─────────────────────┘
Architecture Components