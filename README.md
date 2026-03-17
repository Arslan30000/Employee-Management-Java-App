# CoreSync Employee Payroll & Leave Management System - Client

A secure and user-friendly Java desktop application designed to streamline HR operations by automating employee onboarding, leave tracking, and payroll calculations. This repository contains the frontend client built with JavaFX and managed via Maven.

##  Team CoreSync
Developed by undergraduate CS students at FAST NUCES, Islamabad:
* **Muhammad Arslan**  - Scrum Master / PO / Developer
* **Masab Tahir** - Developer / Tester 
* **Abdul Mateen** - UI Designer / Analyst/ Developer

##  Features 
* **Secure Authentication:** Role-based login system for HR Admins and Employees.
* **Admin Dashboard:** Restricts unauthorized access and allows HR to register new employees.
* **Employee Dashboard:** Allows standard users to view their current profile and leave balances securely.

##  Tech Stack
* **Language:** Java 11+
* **UI Framework:** JavaFX (FXML)
* **Architecture:** MVC (Model-View-Controller)
* **Build Tool:** Maven
* **Networking:** `java.net.http.HttpClient` for REST API consumption

##  Project Structure
This project follows a standard Maven directory layout for clean separation of concerns:
* `src/main/java/`: Contains all business logic, API services, and JavaFX controllers.
* `src/main/resources/`: Contains all FXML layout files and UI assets.
* `pom.xml`: Maven configuration and dependency management.

##  Setup & Run Instructions
1. Clone this repository to your local machine:
   ```bash
   git clone [https://github.com/Arslan30000/CoreSync-Payroll-Client.git](https://github.com/Arslan30000/CoreSync-Payroll-Client.git)
