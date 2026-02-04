# 🛒 Logistics Platform - Multi-Tenant E-Commerce Backend

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Maven](https://img.shields.io/badge/Maven-3.8+-red)

A scalable Spring Boot backend system designed to support multiple organizations managing their e-commerce websites with centralized logistics operations including orders, fulfillments, and tracking.

---

## 📌 Table of Contents

- [Project Overview](#-project-overview)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Entity Relationships](#-entity-relationships)
- [API Endpoints](#-api-endpoints)
- [Getting Started](#-getting-started)
- [Configuration](#-configuration)
- [API Documentation](#-api-documentation)
- [Testing](#-testing)
- [Error Handling](#-error-handling)
- [Future Enhancements](#-future-enhancements)
- [Contributing](#-contributing)
- [License](#-license)

---

## 📖 Project Overview

This project is a **multi-tenant logistics platform** that enables:

- **Multiple Organizations** to manage their e-commerce operations
- Each organization can have **multiple websites/stores**
- Websites push **orders, fulfillments, and tracking data** to the centralized platform
- Complete **data isolation** between organizations (tenant-based)
- RESTful APIs for all CRUD operations with advanced filtering and pagination

### Use Case Scenario

```
Organization A (Nike)
  ├── Website 1 (nike.com)
  │   ├── Order #1001
  │   │   ├── Fulfillment #1
  │   │   │   └── Tracking: USPS-123456
  │   │   └── Fulfillment #2
  │   └── Order #1002
  └── Website 2 (nikestore.eu)
      └── Order #2001

Organization B (Adidas)
  └── Website 3 (adidas.com)
      └── Order #3001
```

---

## ✨ Features

### Core Functionality

- ✅ **Multi-Tenant Architecture** - Complete data isolation per organization
- ✅ **Organization Management** - Create and manage multiple organizations
- ✅ **Website Management** - Associate websites with organizations
- ✅ **Order Management** - Full CRUD operations with advanced search
- ✅ **Fulfillment Tracking** - Manage order fulfillments
- ✅ **Tracking Records** - Real-time shipment tracking
- ✅ **Pagination & Filtering** - Efficient data retrieval with search capabilities
- ✅ **Partial Updates** - PATCH support for flexible updates
- ✅ **Duplicate Prevention** - Business rule validations
- ✅ **Error Handling** - Centralized exception management

### Business Rules

- 🔒 Prevent duplicate orders by external order ID
- 🔒 Ensure website belongs to organization before data operations
- 🔒 Cascade relationships (Order → Fulfillment → Tracking)
- 🔒 Validation of all input data with meaningful error messages

---

## 🧱 Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.x |
| **Web** | Spring Web (REST) |
| **Database** | MySQL 8.0 |
| **ORM** | Spring Data JPA / Hibernate |
| **Build Tool** | Maven |
| **Testing** | JUnit 5, Mockito |
| **API Documentation** | OpenAPI 3.0 (Swagger) |
| **Validation** | Jakarta Bean Validation |

---

## 🏗️ Architecture

The application follows **clean layered architecture**:

```
┌─────────────────────────────────────┐
│         Controller Layer            │  ← HTTP Requests/Responses
├─────────────────────────────────────┤
│          Service Layer              │  ← Business Logic & Validations
├─────────────────────────────────────┤
│        Repository Layer             │  ← Data Access (JPA)
├─────────────────────────────────────┤
│           Database                  │  ← MySQL
└─────────────────────────────────────┘
```

### Design Principles

- **Separation of Concerns** - Each layer has a single responsibility
- **Dependency Injection** - Loose coupling via Spring DI
- **Repository Pattern** - Abstracted data access layer
- **DTO Pattern** - Separate API models from domain entities
- **RESTful Design** - Standard HTTP methods and status codes

---

## 📂 Project Structure

```
e-commerce-logistics-platform/
│
├── src/
│   ├── main/
│   │   ├── java/com/samuditha/logisticsplatform/
│   │   │   ├── controller/
│   │   │   │   ├── FulfillmentController.java
│   │   │   │   ├── OrderController.java
│   │   │   │   ├── StoreController.java
│   │   │   │   ├── TenantController.java
│   │   │   │   └── TrackingController.java
│   │   │   │
│   │   │   ├── dto/
│   │   │   │   └── (Data Transfer Objects)
│   │   │   │
│   │   │   ├── entity/
│   │   │   │   ├── Fulfillment.java
│   │   │   │   ├── Order.java
│   │   │   │   ├── Organization.java
│   │   │   │   ├── Tracking.java
│   │   │   │   └── Website.java
│   │   │   │
│   │   │   ├── exception/
│   │   │   │   └── (Custom Exceptions & Global Handler)
│   │   │   │
│   │   │   ├── repository/
│   │   │   │   ├── FulfillmentRepository.java
│   │   │   │   ├── OrderRepository.java
│   │   │   │   ├── StoreRepository.java
│   │   │   │   ├── TenantRepository.java
│   │   │   │   └── TrackingRepository.java
│   │   │   │
│   │   │   ├── service/
│   │   │   │   ├── serviceImpl/
│   │   │   │   │   ├── FulfillmentService.java
│   │   │   │   │   ├── OrderService.java
│   │   │   │   │   ├── StoreService.java
│   │   │   │   │   ├── TenantService.java
│   │   │   │   │   └── TrackingService.java
│   │   │   │
│   │   │   └── LogisticsPlatformApplication.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application.yml (optional)
│   │
│   └── test/
│       └── java/com/samuditha/logisticsplatform/
│           └── (Unit & Integration Tests)
│
├── .gitignore
├── pom.xml
├── README.md
└── logistics-platform-api.json (Postman Collection)
```

---

## 🗄️ Entity Relationships

```
┌──────────────┐
│ Organization │
└──────┬───────┘
       │ 1:N
       ▼
┌──────────────┐
│   Website    │
└──────┬───────┘
       │ 1:N
       ▼
┌──────────────┐
│    Order     │
└──────┬───────┘
       │ 1:N
       ▼
┌──────────────┐
│ Fulfillment  │
└──────┬───────┘
       │ 1:N
       ▼
┌──────────────┐
│   Tracking   │
└──────────────┘
```

### Key Relationships

- One **Organization** has many **Websites**
- One **Website** has many **Orders**
- One **Order** has many **Fulfillments**
- One **Fulfillment** has many **Tracking** records

---

## 🔌 API Endpoints

### Organization APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/organizations` | Create new organization |
| `GET` | `/api/organizations` | List all organizations (paginated) |
| `GET` | `/api/organizations/{id}` | Get organization by ID |
| `PUT` | `/api/organizations/{id}` | Update organization |
| `DELETE` | `/api/organizations/{id}` | Delete organization |

### Website APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/websites` | Create new website |
| `GET` | `/api/websites` | List all websites (paginated) |
| `GET` | `/api/websites/{id}` | Get website by ID |
| `GET` | `/api/websites/search` | Search by domain/code |
| `PUT` | `/api/websites/{id}` | Update website |
| `DELETE` | `/api/websites/{id}` | Delete website |

### Order APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/orders` | Create new order |
| `GET` | `/api/orders/{orderId}` | Get order by ID |
| `GET` | `/api/orders/search` | Search orders with filters |
| `PUT` | `/api/orders/{orderId}` | Full update of order |
| `PATCH` | `/api/orders/{orderId}` | Partial update of order |
| `DELETE` | `/api/orders/{orderId}` | Delete order (204) |

#### Order Search Filters

- `organizationId` - Filter by organization
- `websiteId` - Filter by website
- `status` - Order status (PENDING, PROCESSING, COMPLETED, CANCELLED)
- `financialStatus` - Payment status (PAID, UNPAID, REFUNDED)
- `fulfillmentStatus` - Fulfillment status
- `externalOrderId` - External system order ID
- `orderNumber` - Order number
- `startDate` / `endDate` - Date range filter
- `page` / `size` / `sort` - Pagination parameters

### Fulfillment APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/orders/{orderId}/fulfillments` | Create fulfillment |
| `GET` | `/api/orders/{orderId}/fulfillments` | List fulfillments for order |
| `GET` | `/api/orders/{orderId}/fulfillments/{id}` | Get fulfillment details |
| `PUT` | `/api/orders/{orderId}/fulfillments/{id}` | Full update |
| `PATCH` | `/api/orders/{orderId}/fulfillments/{id}` | Partial update |
| `DELETE` | `/api/orders/{orderId}/fulfillments/{id}` | Delete fulfillment |

### Tracking APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/fulfillments/{id}/tracking` | Add tracking record |
| `GET` | `/api/fulfillments/{id}/tracking` | List tracking records |
| `GET` | `/api/fulfillments/{id}/tracking/{trackingId}` | Get tracking details |
| `PATCH` | `/api/fulfillments/{id}/tracking/{trackingId}` | Update tracking status |

---

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- MySQL 8.0+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

### Installation

1. **Clone the repository**

```bash
git clone https://github.com/yourusername/logistics-platform.git
cd logistics-platform
```

2. **Configure MySQL Database**

Create a new database:

```sql
CREATE DATABASE logistics_platform;
```

3. **Update application.properties**

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/logistics_platform
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

4. **Build the project**

```bash
mvn clean install
```

5. **Run the application**

```bash
mvn spring-boot:run
```

The application will start on: **http://localhost:8080**

---

## ⚙️ Configuration

### application.properties

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/logistics_platform
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Logging
logging.level.org.springframework.web=INFO
logging.level.com.samuditha.logisticsplatform=DEBUG
```

---

## 📚 API Documentation

### Using Swagger UI

Once the application is running, access the interactive API documentation:

```
http://localhost:8080/swagger-ui.html
```

### Postman Collection

Import the provided `logistics-platform-api.json` file into Postman for ready-to-use API requests.

### Example Request

**Create an Order:**

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "organizationId": 1,
    "websiteId": 1,
    "externalOrderId": "ORDER-12345",
    "orderNumber": "ORD-001",
    "status": "PENDING",
    "financialStatus": "PAID",
    "totalAmount": 150.00,
    "currency": "USD",
    "customerEmail": "customer@example.com"
  }'
```

**Response:**

```json
{
  "id": 1,
  "organizationId": 1,
  "websiteId": 1,
  "externalOrderId": "ORDER-12345",
  "orderNumber": "ORD-001",
  "status": "PENDING",
  "financialStatus": "PAID",
  "totalAmount": 150.00,
  "currency": "USD",
  "customerEmail": "customer@example.com",
  "createdAt": "2026-02-03T10:30:00",
  "updatedAt": "2026-02-03T10:30:00"
}
```

---

## 🧪 Testing

### Run Unit Tests

```bash
mvn test
```

### Run Integration Tests

```bash
mvn verify
```

### Test Coverage

The project includes:

- ✅ **Unit Tests** for service layer business logic
- ✅ **Integration Tests** for controller endpoints
- ✅ **Repository Tests** for database operations
- ✅ **Validation Tests** for input validation

Example test structure:

```java
@SpringBootTest
class OrderServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @InjectMocks
    private OrderService orderService;
    
    @Test
    void testCreateOrder_Success() {
        // Test implementation
    }
    
    @Test
    void testCreateOrder_DuplicateExternalId_ThrowsException() {
        // Test implementation
    }
}
```

---

## ❗ Error Handling

Centralized exception handling using `@RestControllerAdvice`:

| Error Type | HTTP Status | Description |
|------------|-------------|-------------|
| `ResourceNotFoundException` | 404 | Resource not found |
| `ValidationException` | 400 | Invalid input data |
| `DuplicateResourceException` | 409 | Duplicate entry conflict |
| `UnauthorizedException` | 401 | Authentication required |
| `ForbiddenException` | 403 | Access denied |
| `InternalServerError` | 500 | Server error |

### Example Error Response

```json
{
  "timestamp": "2026-02-03T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Order with ID 999 not found",
  "path": "/api/orders/999"
}
```

---

## 🔮 Future Enhancements

- [ ] **Authentication & Authorization** (Spring Security + JWT)
- [ ] **Rate Limiting** for API endpoints
- [ ] **Event-Driven Architecture** (Apache Kafka/RabbitMQ)
- [ ] **Caching Layer** (Redis)
- [ ] **Docker Support** (Dockerfile + docker-compose.yml)
- [ ] **Kubernetes Deployment** configurations
- [ ] **Monitoring & Observability** (Prometheus + Grafana)
- [ ] **Advanced Analytics** dashboard
- [ ] **Webhook Support** for real-time notifications
- [ ] **Multi-Database Support** (PostgreSQL, MongoDB)
- [ ] **GraphQL API** support
- [ ] **Internationalization** (i18n)

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Standards

- Follow Java naming conventions
- Write unit tests for new features
- Update documentation as needed
- Ensure all tests pass before submitting PR

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 📧 Contact

**Your Name** - [@yourtwitter](https://twitter.com/yourtwitter)

Project Link: [https://github.com/yourusername/logistics-platform](https://github.com/yourusername/logistics-platform)

---

## 🙏 Acknowledgments

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA Guide](https://spring.io/guides/gs/accessing-data-jpa/)
- [RESTful API Design Best Practices](https://restfulapi.net/)
- [MySQL Documentation](https://dev.mysql.com/doc/)

---

<div align="center">
  <p>Made with ❤️ by <a href="https://github.com/yourusername">Your Name</a></p>
  <p>⭐ Star this repository if you find it helpful!</p>
</div>
