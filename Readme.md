# StudyRooms - Booking System

This project is a distributed web application for booking library study rooms. It is built with **Spring Boot** and implements a hybrid architecture serving both a **Web UI** (Thymeleaf) and a RESTful **API**.

**Course:** Distributed Systems
**Semester:** Winter 2025-2026

---

## Key Features
* **User Roles:** Student & Library Staff.
* **Hybrid Security:**
    * **Cookie-based (Session)** for Web UI access.
    * **JWT (Stateless)** for REST API access.
* **Booking Rules:**
    * Max 2 bookings per day per student.
    * Bookings allowed only on weekdays (Mon-Fri).
    * Bookings must be within operating hours.
* **Advanced Logic:**
    * **No-Show Penalty:** Students with a "No-Show" record cannot book for 7 days.
    * **Public Holiday Check:** Integration with external API to prevent bookings on holidays.
    * **Notifications:** Simulation of email notifications via external webhook.

---

## Technology Stack
* **Backend:** Java 17, Spring Boot 3.x
* **Database:** H2 In-Memory Database
* **Frontend:** Thymeleaf, HTML5, CSS3
* **Security:** Spring Security 6, JWT (io.jsonwebtoken)
* **Documentation:** OpenAPI / Swagger UI

---

## How to Run

### Prerequisites
* JDK 17 or higher installed.
* Maven (or use the provided `mvnw` wrapper).

### Option 1: Using IntelliJ IDEA (Recommended)
1.  Open the project in IntelliJ IDEA.
2.  Locate `StudyroomsApplication.java` in `src/main/java/gr/teipir/studyrooms`.
3.  Right-click and select **Run 'StudyroomsApplication'**.

### Option 2: Using Command Line
Open a terminal in the project root folder and run:
```bash
./mvnw spring-boot:run
```

The application will start at: http://localhost:8080

## Default Users (Data Initializer)
The application automatically creates the following users on startup:

| Role | Username | Password | Access Rights |
| :--- | :--- | :--- | :--- |
| **Student** | `student1` | `1234` | Can browse rooms, book, view own history. |
| **Staff** | `staff1` | `1234` | Can manage rooms, view all bookings, mark No-Shows, view stats. |


## API Documentation (Swagger UI)
The REST API is fully documented using OpenAPI 3.
Once the application is running, access the documentation here:

👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

> **Note:** To test secured endpoints in Swagger:
> 1. Use the `/api/auth/login` endpoint to get a token.
> 2. Click the **Authorize** button (top right).
> 3. Enter the token value (without "Bearer ").

## External Services

### 1. Public Holidays API (GET)
* **Service:** [Nager.Date API](https://date.nager.at/)
* **Usage:** Checks if the selected booking date is a public holiday in Greece. If yes, the booking is rejected.

### 2. Notification Service (POST - Secured)
* **Service:** Webhook.site (Mock Notification Service)
* **Usage:** Sends a secure POST request (with Bearer Token) to an external webhook whenever a booking is successfully created, simulating an email confirmation system.

## Project Structure
```text
src/main/java/gr/teipir/studyrooms
├── config/          # Security, CORS, OpenAPI config
├── controller/      # Web Controllers (Thymeleaf)
│   └── api/         # REST Controllers (JSON)
├── dto/             # Data Transfer Objects
├── model/           # JPA Entities
├── repository/      # Database Repositories
├── security/        # JWT Filter, UserDetails Service
└── service/         # Business Logic (Booking, Room, Holiday Services)
```