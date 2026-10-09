# Python-Based Online Coding Platform

A role-based educational web application built using Java Servlets, JSP, JDBC, and PostgreSQL. The platform helps students practice programming exercises, submit solutions for review, track progress, and participate in coding challenges.

## Project Overview

Educational coding workflows often involve separate tools for assigning exercises, collecting submissions, reviewing student work, and tracking progress. This project brings these activities together in one web application for students, instructors, and administrators.

## Features

### Student

* Register and log in to the platform.
* Browse available programming exercises.
* Submit exercise solutions for instructor review.
* View submission status, scores, and progress.
* Join approved coding challenges and mark participation as completed.
* Exchange messages through the platform.
* Manage profile information.

### Instructor

* Create and manage programming exercises.
* Submit exercises for administrative approval.
* Review student submissions and assign grades.
* Create coding challenges for approval.
* View student progress and performance reports.
* Communicate with users through messaging.

### Administrator

* Manage users and role-based access.
* Approve or reject exercises and coding challenges.
* Review submissions and platform activity.
* Manage system settings.

## Technology Stack

| Component                       | Technology                   |
| ------------------------------- | ---------------------------- |
| Backend                         | Java                         |
| Web layer                       | Java Servlets and JSP        |
| Database access                 | JDBC                         |
| Database                        | PostgreSQL (Supabase-hosted) |
| Web server                      | Apache Tomcat                |
| Build and dependency management | Apache Maven                 |
| Testing                         | JUnit and Mockito            |

## Architecture

The application follows a layered MVC-style architecture:

* **JSP:** User interface and rendered pages.
* **Servlets:** HTTP request handling and navigation.
* **Services:** Application business logic.
* **DAO layer:** Database operations.
* **Models:** Application data structures.
* **PostgreSQL:** Persistent application data.

See [`docs/architecture.md`](docs/architecture.md) and [`docs/database-design.md`](docs/database-design.md) for additional details.

## Project Structure

```text
.
├── database/
│   ├── migrations/
│   └── schema.sql
├── docs/
│   ├── architecture.md
│   └── database-design.md
├── src/
│   ├── main/
│   │   ├── java/com/codingplatform/
│   │   └── webapp/
│   └── test/
├── pom.xml
├── README.md
└── .gitignore
```

## Prerequisites

Install the following before running the application:

* JDK compatible with the version configured in `pom.xml`
* Apache Maven
* Apache Tomcat compatible with the configured Servlet API
* A PostgreSQL database (a Supabase PostgreSQL project can be used)

## Database Configuration

The application reads database connection details from environment variables:

* `DB_URL`
* `DB_USERNAME`
* `DB_PASSWORD`

Configure these variables in your local environment before starting the application. Use your own database credentials.

**Security:** Never commit database passwords, API keys, or `.env` files containing secrets to GitHub.

Apply the SQL schema and migrations in the correct order for a fresh database. Review the files under `database/` and confirm the schema matches the application version you are deploying.

## Build and Test

Run these commands from the project root:

```bash
mvn clean test
mvn package
```

The packaged web application WAR is generated in Maven's `target/` directory.

## Deployment

Deploy the generated WAR to a compatible Apache Tomcat server. Configure the required database environment variables in the Tomcat runtime environment, then start Tomcat and open the application's context URL in a browser.

Deployment details may vary depending on your operating system and Tomcat configuration.

## Current Limitations

* Python code execution in the browser is not implemented.
* Coding challenges currently track participation and completion; they do not execute or automatically evaluate Python programs.
* A live public demo is not currently provided.

## Testing

The project has previously passed **109 automated tests** with Maven, and the application was manually tested on a local Tomcat server. Test results may change as the code evolves.

## Future Improvements

* Add a secure, sandboxed code execution service.
* Expand automated evaluation and test-case support.
* Add screenshots and a public demo deployment.
* Improve reporting and learning analytics.
* Add CI-based build and test checks.

## Author

**Avinash Kumar**

GitHub: [@avinashkumar272006-afk](https://github.com/avinashkumar272006-afk)

---

This project is developed for learning, demonstrating Java web development, and exploring real-world educational workflow management.
