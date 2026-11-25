# Spring Boot CI/CD Pipeline

An Employee Management REST API built with Java 21, Spring Boot, Maven, and PostgreSQL. This project demonstrates an end-to-end CI/CD workflow using GitHub, Jenkins, and AWS EC2.

The pipeline automatically builds, tests, packages, and deploys the application whenever changes are pushed to the `main` branch.

## Project Status

**Completed:** Application development, automated testing, Jenkins CI/CD pipeline, GitHub webhook integration, and automated deployment to AWS EC2.

**Future enhancement:** Reimplement the CI/CD workflow using AWS CodeBuild and AWS CodeDeploy.

## Technology Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.16 |
| Build Tool | Maven |
| Backend | Spring Web, Spring Data JPA |
| Database | PostgreSQL |
| Testing Database | H2 |
| Validation | Jakarta Bean Validation |
| Monitoring | Spring Boot Actuator |
| Testing | JUnit 5, Mockito, Spring Boot Test |
| Version Control | Git, GitHub |
| CI/CD | Jenkins |
| Cloud Platform | AWS |
| Compute | Amazon EC2 |
| Deployment | SSH, SCP, Linux systemd |

## Application Architecture

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

## CI/CD Architecture

The project uses GitHub and Jenkins to automate application delivery to AWS EC2.

```text
Developer
   |
   | Git Push
   v
GitHub Repository
   |
   | GitHub Webhook
   v
Jenkins Controller (AWS EC2)
   |
   | 1. Checkout Source Code
   | 2. Maven Build and Test
   | 3. Archive JAR Artifact
   | 4. SSH Connectivity Test
   | 5. Transfer Artifact using SCP
   v
Application Server (AWS EC2)
   |
   | 6. Restart systemd Service
   | 7. Verify Application Health
   v
Spring Boot Application
   |
   v
PostgreSQL Database
```

### Pipeline Stages

| Stage | Description |
|---|---|
| Build and Test | Executes `./mvnw clean verify` to compile the application and run automated tests. |
| Archive Artifact | Archives the generated executable JAR in Jenkins with fingerprinting enabled. |
| SSH Connectivity Test | Verifies that Jenkins can connect to the application EC2 instance using SSH credentials. |
| Deploy Application | Transfers the JAR to the application server using SCP. |
| Restart Application | Restarts the application using its Linux systemd service. |
| Health Check | Checks the Actuator health endpoint and fails deployment if the application does not become healthy. |

The pipeline is triggered automatically by GitHub push events through a configured GitHub webhook.

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
├── .env.example
├── .gitignore
├── Jenkinsfile
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## Prerequisites

For local application development:

- JDK 21
- PostgreSQL 14 or later
- Git
- A terminal or IDE such as VS Code

For the CI/CD infrastructure:

- A GitHub repository
- A Jenkins controller running on AWS EC2
- An Ubuntu application EC2 instance
- Network connectivity between Jenkins and the application server
- Jenkins SSH credentials for deployment

A local PostgreSQL installation is not required to execute the automated tests.

## Local Setup

### 1. Clone the repository

```bash
git clone https://github.com/Ronithkumar/springboot-cicd-pipeline.git

cd springboot-cicd-pipeline
```

### 2. Configure PostgreSQL

Create a PostgreSQL database:

```sql
CREATE DATABASE employee_db;
```

Configure a PostgreSQL user with the necessary database and schema permissions.

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

## API Endpoints

Base URL:

```text
http://localhost:8081
```

| Method | Endpoint | Success Status | Description |
|---|---|---|---|
| GET | `/api/employees` | 200 | Retrieve all employees |
| GET | `/api/employees/{id}` | 200 | Retrieve employee by ID |
| POST | `/api/employees` | 201 | Create an employee |
| PUT | `/api/employees/{id}` | 200 | Update an employee |
| DELETE | `/api/employees/{id}` | 204 | Delete an employee |
| GET | `/actuator/health` | 200 | Application health check |

### Employee Request Fields

| Field | Description |
|---|---|
| firstName | Employee first name |
| lastName | Employee last name |
| email | Unique employee email |
| department | Employee department |
| salary | Employee salary (zero or greater) |

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

| HTTP Status | Scenario |
|---|---|
| 400 Bad Request | Invalid request or validation failure |
| 404 Not Found | Employee does not exist |
| 409 Conflict | Duplicate employee email |

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

| Variable | Required | Default | Description |
|---|---|---|---|
| DB_URL | Yes | None | PostgreSQL JDBC URL |
| DB_USERNAME | Yes | None | PostgreSQL username |
| DB_PASSWORD | Yes | None | PostgreSQL password |

No database credentials are hardcoded in the application.

**Database schema management:** Hibernate is currently configured with `ddl-auto: update`. Hibernate can update the database schema during application startup. A future improvement is to introduce Flyway migrations and use Hibernate `ddl-auto: validate` for more controlled schema management.

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

Jenkins archives this artifact and transfers it to the application EC2 instance during deployment.

## Deployment on AWS EC2

The application is deployed on an Ubuntu EC2 instance.

The deployment process includes:

1. Establishing SSH connectivity from Jenkins to the application server.
2. Transferring the generated JAR using SCP.
3. Restarting the `employee-management-api` systemd service.
4. Checking the Actuator health endpoint.
5. Marking the pipeline as failed if the health check does not succeed.

The application is managed through a Linux systemd service, allowing it to run independently of the SSH session.

## Security Considerations

- Database credentials are supplied through environment variables.
- SSH private keys are stored in Jenkins Credentials.
- The application server restricts inbound access to required sources.
- Jenkins webhook access is restricted using GitHub webhook source IP ranges.
- Actual secrets, private keys, and environment files must never be committed to the repository.

## Future Enhancements

The next iteration of this project will implement an AWS-native CI/CD pipeline using:

1. AWS CodeBuild for Maven builds and automated tests.
2. Amazon S3 for build artifact storage.
3. AWS CodeDeploy for automated EC2 deployments.
4. IAM roles and least-privilege permissions for service access.
5. Deployment lifecycle hooks and application health verification.

This will provide an opportunity to compare a Jenkins-based CI/CD implementation with AWS-managed CI/CD services.

## Project Objective

This project demonstrates practical DevOps concepts, including:

- CI/CD pipeline design and implementation.
- GitHub webhook integration.
- Automated build and test execution.
- Artifact generation and management.
- Jenkins credential management.
- AWS EC2 infrastructure.
- Linux systemd service management.
- SSH-based application deployment.
- Post-deployment health verification.

The project is intended as a practical portfolio demonstrating the application delivery lifecycle from source-code commit to deployment on cloud infrastructure.
