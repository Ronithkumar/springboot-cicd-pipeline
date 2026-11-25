# Spring Boot CI/CD Pipeline

A Spring Boot Employee Management REST API built with Java and Maven. This application serves as the application component of an end-to-end DevOps portfolio project using GitHub, Jenkins, and AWS EC2.

The goal is to demonstrate automated builds, testing, artifact generation, and deployment through a Jenkins CI/CD pipeline.

> **Project status:** Application development and Maven verification completed. Jenkins CI/CD and AWS deployment will be configured separately.

## Technology Stack 

- **Language:** Java 21
- **Framework:** Spring Boot 3.5.16
- **Build Tool:** Maven
- **Backend:** Spring Web, Spring Data JPA
- **Database:** PostgreSQL
- **Testing Database:** H2
- **Validation:** Jakarta Bean Validation
- **Monitoring:** Spring Boot Actuator
- **Testing:** JUnit 5, Mockito, Spring Boot Test
- **Version Control:** Git and GitHub

## Architecture

The application follows a layered architecture:

```text
Client
  |
  v
EmployeeController
  |
  v
EmployeeService
  |
  v
EmployeeRepository
  |
  v
PostgreSQL Database
```

Responsibilities:

- **Controller:** Handles HTTP requests and responses.
- **Service:** Contains employee management business logic.
- **Repository:** Handles database operations through Spring Data JPA.
- **Entity:** Represents employee database records.
- **DTO:** Separates API request and response objects from persistence entities.
- **GlobalExceptionHandler:** Provides consistent error responses.

## Project Structure

```text
springboot-cicd-pipeline/
│
├── src/
│   ├── main/
│   │   ├── java/com/example/employee/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── entity/
│   │   │   ├── dto/
│   │   │   ├── exception/
│   │   │   └── config/
│   │   │
│   │   └── resources/
│   │       └── application.yml
│   │
│   └── test/
│       └── java/com/example/employee/
│
├── .mvn/
├── mvnw
├── mvnw.cmd
├── pom.xml
├── .gitignore
├── .env.example
└── README.md
```

## Prerequisites

- JDK 21
- PostgreSQL 14 or later
- Git
- A terminal or IDE such as VS Code

A local PostgreSQL installation is not required to execute the automated tests.

## Local Setup

### 1. Clone the repository

```bash
git clone https://github.com/YOUR_GITHUB_USERNAME/springboot-cicd-pipeline.git

cd springboot-cicd-pipeline
```

Replace `YOUR_GITHUB_USERNAME` with your GitHub username.

### 2. Configure PostgreSQL

Create a PostgreSQL database:

```sql
CREATE DATABASE employee_db;
```

### 3. Configure environment variables

Copy `.env.example` to `.env` and replace the placeholder values with your local database credentials.

Spring Boot does not automatically load `.env` files. Export the variables in your terminal before starting the application.

**PowerShell:**

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/employee_db"
$env:DB_USERNAME = "your_database_username"
$env:DB_PASSWORD = "your_database_password"
```

**Linux / macOS / Bash:**

```bash
export DB_URL="jdbc:postgresql://localhost:5432/employee_db"
export DB_USERNAME="your_database_username"
export DB_PASSWORD="your_database_password"
```

Never commit actual database credentials to GitHub.

## Run the Application

Start the application using Maven:

**Linux / macOS:**

```bash
./mvnw spring-boot:run
```

**Windows:**

```powershell
.\mvnw.cmd spring-boot:run
```

The application runs on:

```text
http://localhost:8081
```

### Build the Executable JAR

```bash
./mvnw clean package
```

Run the generated JAR:

```bash
java -jar target/employee-management-api.jar
```

The application logs to standard output, making it suitable for process management and deployment on Linux EC2 instances.

## API Endpoints

Base URL:

```text
http://localhost:8081
```

| Method | Endpoint              | Success Status | Description              |
| ------ | --------------------- | -------------- | ------------------------ |
| GET    | `/api/employees`      | 200            | Retrieve all employees   |
| GET    | `/api/employees/{id}` | 200            | Retrieve employee by ID  |
| POST   | `/api/employees`      | 201            | Create an employee       |
| PUT    | `/api/employees/{id}` | 200            | Update an employee       |
| DELETE | `/api/employees/{id}` | 204            | Delete an employee       |
| GET    | `/actuator/health`    | 200            | Application health check |

### Employee Request Fields

| Field      | Description                       |
| ---------- | --------------------------------- |
| firstName  | Employee first name               |
| lastName   | Employee last name                |
| email      | Unique employee email             |
| department | Employee department               |
| salary     | Employee salary (zero or greater) |

### Example: Create an Employee

**Request:**

```bash
curl -X POST http://localhost:8081/api/employees \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Doe",
    "email": "jane.doe@example.com",
    "department": "Engineering",
    "salary": 90000.00
  }'
```

**Example response:**

```json
{
  "id": 1,
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane.doe@example.com",
  "department": "Engineering",
  "salary": 90000.0
}
```

### Error Handling

The application handles common API errors:

| HTTP Status     | Scenario                              |
| --------------- | ------------------------------------- |
| 400 Bad Request | Invalid request or validation failure |
| 404 Not Found   | Employee does not exist               |
| 409 Conflict    | Duplicate employee email              |

Error responses include timestamp, status, error, message, request path, and validation details.

## Health Check

Spring Boot Actuator exposes the health endpoint:

```text
GET http://localhost:8081/actuator/health
```

Example response:

```json
{
  "status": "UP"
}
```

Only the health endpoint is exposed over HTTP. Detailed health information is disabled.

## Environment Variables

The application uses environment variables for database configuration.

| Variable    | Required | Default | Description         |
| ----------- | -------- | ------- | ------------------- |
| DB_URL      | Yes      | None    | PostgreSQL JDBC URL |
| DB_USERNAME | Yes      | None    | PostgreSQL username |
| DB_PASSWORD | Yes      | None    | PostgreSQL password |

No database credentials are hardcoded in the application.

Hibernate is configured to validate the existing database schema at startup. Schema creation and changes must be managed separately.

## Testing

The project includes:

- Service-layer unit tests using JUnit 5 and Mockito.
- Controller integration tests using Spring Boot Test.
- Request validation tests.
- CRUD operation tests.
- Exception handling tests.
- Actuator health endpoint test.

Tests use an in-memory H2 database and do not require a running PostgreSQL instance.

### Run Tests

```bash
./mvnw clean test
```

### Run Complete Maven Verification

```bash
./mvnw clean verify
```

On Windows:

```powershell
.\mvnw.cmd clean verify
```

Maven fails the build if any test fails.

## Build Artifact

The Maven build generates an executable Spring Boot JAR:

```text
target/employee-management-api.jar
```

This artifact will be used in the Jenkins CI/CD pipeline for deployment to AWS EC2.

## Planned DevOps Integration

The following capabilities will be implemented as separate stages of this portfolio project:

1. GitHub repository integration.
2. Jenkins installation and configuration on AWS EC2.
3. Automated pipeline triggering using GitHub webhooks.
4. Maven build and automated test execution.
5. Executable JAR artifact generation.
6. Automated deployment to an Ubuntu EC2 instance.
7. Linux systemd service management.
8. Application health verification after deployment.

Jenkins, AWS infrastructure, Docker, and deployment configuration are intentionally excluded from the current application implementation.

---

## Project Objective

This project demonstrates how a Java application can be built, tested, packaged, and prepared for automated deployment using modern DevOps practices.

## CI/CD Pipeline

This project uses Jenkins to automatically build, test, and deploy the application whenever changes are pushed to the main branch.
