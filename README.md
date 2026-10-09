# Python-Based Online Coding Platform

## 1. Project Title
Python-Based Online Coding Platform

## 2. Problem Statement
Many educational institutions lack an integrated, dedicated environment where students can write, compile, and learn Python directly from their browsers without setting up complex local development environments.

## 3. Project Objective
To develop a Java Web Application that provides a robust, scalable online platform for instructors to assign Python coding exercises and for students to practice and submit their code.

## 4. Main User Roles
* **Admin:** Manages users, system settings, and platform health.
* **Instructor:** Creates coding challenges, views student progress, and grades submissions.
* **Student:** Solves Python exercises, views progress, and interacts with the platform.

## 5. Technology Stack
* **Backend:** Java 26, Servlets, JSP, JDBC
* **Frontend:** HTML, CSS, JavaScript
* **Database:** Supabase PostgreSQL
* **Server:** Apache Tomcat
* **Build Tool:** Apache Maven

## 6. Project Architecture
The platform is built on an MVC (Model-View-Controller) pattern using core Java EE technologies.
See `docs/architecture.md` for full details.

## 7. Project Structure
Standard Maven Web Application structure (WAR).
* `src/main/java` - Java source code
* `src/main/webapp` - JSP, HTML, CSS, JS
* `database/` - SQL schemas

## 8. Current Implementation Status
**Phase 1:** Initial Project Foundation
- Project structure created
- Core OOP Model classes
- DAO and Service interfaces
- Maven build configured

## 9. Maven Build Instructions
Compile and test:
```bash
mvn clean test
```
Package as WAR:
```bash
mvn package
```

## 10. Future Modules
* Code Execution Engine
* Interactive Dashboards
* Role-based Authentication
* Progress Tracking
* Messaging System
