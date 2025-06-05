# Task Management API 🗂️

A RESTful task-management API built with Spring Boot, H2 Database, and JWT security. Allows users to register/login, manage their tasks (CRUD), apply filters/searches, and view reports. Includes Swagger documentation and a Postman collection.

---

## 📦 Features

* 🔐 **Authentication**: Register/login with JWT token generation
* 📝 **Task Management**: Full CRUD on tasks (title, description, status, etc.)
* 🔍 **Search & Filter**: Filter tasks by status, category, priority, and due date range
* 📊 **Reports**: Summary (total, completed, pending) and category-wise report
* 👮 **Admin**: View and delete users (admin only)
* 📄 **Swagger UI**: Built-in documentation and API tester

---

## 🧰 Technologies Used

* Java 17
* Spring Boot
* Spring Security with JWT
* Spring Data JPA
* H2 In-Memory Database
* Swagger (OpenAPI)
* Maven

---

## 🚀 Getting Started

### Prerequisites

* Java 17
* Maven

### Run the Project

```bash
git clone https://github.com/SalahShehade/Task_Managment.git
cd Task_Managment
mvn clean install
mvn spring-boot:run
```

The app will be available at: `http://localhost:8080`

Access H2 console at: `http://localhost:8080/h2-console`

* JDBC URL: `jdbc:h2:mem:testdb`
* User: `sa`
* Password: *(leave empty)*

Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## 📬 Postman Collection

Import the collection file from:
`postman/Tasks_Managment_Collection.json`

Includes requests for:

* Register/Login
* Task operations (create, get, update, delete)
* Reports (summary, category)
* Admin (user list, delete)

---

## 📄 Project Report

A full project report (in PDF format) is available in the repository. It includes:

* Project goals and planning
* Database design and ERD
* API design rationale
* Screenshots of endpoints and usage
* Challenges faced and solutions

📥 **[Download the Report](./TaskManagmentReport.pdf)**

Make sure the `TaskManagmentReport.pdf` file is located at the root of your repository.

---

## 📂 Project Structure

```
Task_Managment/
├── controller/        # REST controllers
├── dto/               # Request/Response models
├── model/             # JPA entities
├── repository/        # Database interfaces
├── security/          # JWT config & filters
├── service/           # Business logic
├── exception/         # Error handling
├── resources/
│   ├── application.properties
│   └── data.sql       # Optional sample data
├── postman/
│   └── Tasks_Managment_Collection.json
├── TaskManagmentReport.pdf  # Full project report
└── README.md
```

---

## 👤 Author

**Salahaldin Shehada**
GitHub: [SalahShehade](https://github.com/SalahShehade)
