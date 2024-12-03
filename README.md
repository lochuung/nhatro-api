# Boarding House Management System - Backend

A Spring Boot backend application for managing boarding house operations including contracts, invoices, rooms, and customers.

## Overview

This system provides a comprehensive API for managing boarding house operations, including:
- Contract lifecycle management
- Invoice generation and tracking  
- Room management and availability
- Customer profile management

## Features

- Contract Management
  - Check-in/check-out management
  - Add/remove members
  - Change contract owner
  - Contract document generation
  - Contract search and filtering

- Invoice Management  
  - Monthly invoice generation
  - Invoice document generation (PDF/Word)
  - Invoice search and filtering
  - Payment tracking

- Room Management
  - Room availability tracking
  - Room search and filtering
  - Room service management

- Customer Management
  - Customer profile management
  - Customer search

## Technology Stack

- Java 17
- Spring Boot 3.x
- Spring Security with JWT authentication
- Spring Data JPA
- Spring OpenAPI (Swagger)
- Apache POI for Word document generation 
- OpenHTML for PDF generation
- Maven for dependency management

## Prerequisites

- JDK 17 or higher
- Maven 3.8+
- MySQL 8.0+
- Git

## API Documentation

API documentation is available via Swagger UI at `/swagger-ui/index.html`

## Environment Configuration

The application uses the following configuration files:

- `application.yml` - Main configuration
- `application-prd.yml` - Production configuration (gitignored)

## Security

- Bearer token authentication
- Role-based access control
- Secure password handling

## Getting Started

1. Clone the repository
2. Configure database settings in `application.yml`
3. Run `mvn clean install`
4. Start the application with `mvn spring-boot:run`
5. Access Swagger UI at `http://localhost:8080/swagger-ui/index.html`

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── vn/huuloc/boardinghouse/
│   │       ├── controller/ # REST controllers
│   │       ├── service/    # Business logic
│   │       ├── model/      # Domain models
│   │       └── config/     # Configuration classes
│   └── resources/
│       ├── templates/      # Document templates
│       └── application.yml # Application configuration
```

## License

This project is licensed under the MIT License.