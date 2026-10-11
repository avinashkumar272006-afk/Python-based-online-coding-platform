# Python-Based Online Coding Platform

A role-based educational web application built with Java Servlets, JSP, JDBC, and PostgreSQL. The platform enables students to practice programming exercises, submit Python solutions for instructor review and grading, track learning progress, and participate in coding challenges.

## Project Overview

Educational coding workflows often require managing exercise distribution, collecting student submissions, performing instructor reviews, and tracking individual progress across cohorts. This project consolidates these activities into a centralized, role-based web application tailored for students, instructors, and administrators.

### Code Submission and Evaluation Model

* **Instructor Review and Scoring:** Students compose and submit Python solution code through the web portal. Submissions are persisted in PostgreSQL with initial `PENDING` status. Instructors and administrators inspect student submissions, assign numerical grades (0–100), and provide targeted qualitative feedback.
* **No Automated Execution:** Automated sandboxed Python code execution (such as containerized execution or WebAssembly evaluation) is intentionally not implemented. The application focuses on educational workflow management, submission tracking, and instructor-led grading. Coding challenges track student participation and self-completion milestones rather than automated test runner outputs.

## Features

### Student

* Register and authenticate with secure role-based sessions.
* Browse approved programming exercises filtered by difficulty and topic.
* Submit Python code solutions for instructor review.
* View submission review status, grades, and instructor feedback.
* Monitor dynamic progress metrics (completed exercises, pending reviews, average scores).
* Join active coding challenges and mark challenge tasks as completed.
* Communicate directly with instructors via the internal messaging system.
* Manage user profile details.

### Instructor

* Author programming exercises with starter code and expected outputs.
* Submit exercises for administrative approval.
* Review pending student submissions, assign scores (0–100), and supply qualitative feedback.
* Create and manage coding challenges with start and end dates.
* View aggregate student progress and performance reports.
* Communicate with students via internal messaging.

### Administrator

* Oversee user accounts and manage role assignments.
* Review, approve, or reject exercises submitted by instructors.
* Review, approve, or reject proposed coding challenges.
* Audit submissions and overall platform activity.
* Configure system-wide settings (registration toggles, maintenance mode, default difficulty).

## Technology Stack

| Component                       | Technology / Version                                        | Details / Specification                                      |
| ------------------------------- | ----------------------------------------------------------- | ------------------------------------------------------------ |
| **Language & Runtime**          | **Java 26**                                                 | Configured via `<maven.compiler.release>26</maven.compiler.release>` in `pom.xml` |
| **Web Layer**                   | **Jakarta Servlet 6.0** & **Jakarta JSP 3.1**               | Modern Jakarta EE 10 web specification                      |
| **View Template Engine**        | **Jakarta JSTL 3.0**                                        | GlassFish JSTL 3.0.1 implementation                          |
| **Application Server**          | **Apache Tomcat 10.1.x**                                    | Supports Jakarta EE 10 / Servlet 6.0 namespace (`jakarta.*`) |
| **Database**                    | **PostgreSQL** (Supabase-hosted or standalone)               | PostgreSQL 14+ compatible relational store                   |
| **Connection Pooling**          | **HikariCP 5.1.0**                                          | Managed via `DBConnection` and `DatabaseContextListener`      |
| **JDBC Driver**                 | **PostgreSQL JDBC Driver 42.7.3**                           | Production driver for PostgreSQL / Supabase connections       |
| **Security & Hashing**          | **jBCrypt 0.4** & **Custom Anti-CSRF Token Filter**         | BCrypt password hashing, session-bound CSRF defense           |
| **Build Tool**                  | **Apache Maven 3.9+**                                       | Standard Maven lifecycle (`war` packaging)                   |
| **Testing Framework**           | **JUnit 5 (5.11.3)** & **Mockito (5.14.2)**                 | Unit testing and servlet/service layer mocking               |
| **Test In-Memory Database**     | **H2 Database 2.3.232**                                     | Isolated, zero-dependency in-memory database for test suites |

## Architecture

The application adopts a layered MVC (Model-View-Controller) architecture without heavy enterprise frameworks:

* **View Layer (JSP & Static Assets):** Role-tailored user interfaces with reusable fragments in `WEB-INF/includes/` (`header.jsp`, `topbar.jsp`, `sidebar.jsp`, `alerts.jsp`, `footer.jsp`) and vanilla CSS/JS assets in `css/style.css` and `js/app.js`.
* **Security & Filter Perimeter:**
  * `AuthenticationFilter`: Enforces server-side Role-Based Access Control (RBAC) across `/student/*`, `/instructor/*`, `/admin/*`, and `/messages/*` routes.
  * `CsrfFilter` / `CsrfUtil`: Validates cryptographic, session-bound anti-CSRF tokens on all state-altering `POST` requests and rejects URL-embedded tokens.
* **Controller Layer (Jakarta Servlets):** Intercepts requests, validates inputs, queries sessions, delegates to services, and renders JSP views or redirects (Post/Redirect/Get pattern).
* **Service Layer:** Encapsulates business logic, ownership checks, boundary validation, and domain invariants.
* **DAO Layer (JDBC):** Executes parameterized queries via `PreparedStatement` to prevent SQL injection.
* **Connection Lifecycle:** Managed through HikariCP in `DBConnection`, initialized on demand and gracefully shut down by `DatabaseContextListener`.

See [`docs/architecture.md`](docs/architecture.md) and [`docs/database-design.md`](docs/database-design.md) for architectural and schema details.

## Project Structure

```text
.
├── database/
│   ├── migrations/
│   │   ├── V2__create_exercises.sql
│   │   ├── V3__create_submissions.sql
│   │   ├── V4__add_exercise_approval_status.sql
│   │   ├── V5__create_messages.sql
│   │   ├── V6__create_system_settings.sql
│   │   ├── V7__add_score_to_submissions.sql
│   │   └── V8__create_challenges.sql
│   └── schema.sql
├── docs/
│   ├── architecture.md
│   └── database-design.md
├── screenshots/
│   └── Screenshot 2026-10-09 123133.png
├── src/
│   ├── main/
│   │   ├── java/com/codingplatform/
│   │   │   ├── dao/
│   │   │   ├── daoimpl/
│   │   │   ├── exception/
│   │   │   ├── listener/
│   │   │   ├── model/
│   │   │   ├── security/
│   │   │   ├── service/
│   │   │   ├── servlet/
│   │   │   └── util/
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── includes/
│   │       │   └── web.xml
│   │       ├── admin/
│   │       ├── css/
│   │       ├── instructor/
│   │       ├── js/
│   │       ├── messages/
│   │       ├── student/
│   │       └── index.jsp
│   └── test/
├── pom.xml
├── README.md
└── .gitignore
```

## Prerequisites

Before running or building the project, ensure the following are installed:

* **Java Development Kit (JDK) 26** (compatible with `<maven.compiler.release>26</maven.compiler.release>`)
* **Apache Maven 3.9+**
* **Apache Tomcat 10.1.x** (Jakarta EE 10 / Servlet 6.0 compatible container)
* **PostgreSQL 14+** (a local PostgreSQL instance or a remote Supabase PostgreSQL project)

## Fresh Database Setup

The database schema must be initialized in sequence. Review the files under `database/` and execute them in the following required order against your PostgreSQL or Supabase instance:

### Migration Sequence and Dependency Order

1. **`database/schema.sql` (Base Schema):**
   * Creates the `users` table with role check constraint (`ADMIN`, `INSTRUCTOR`, `STUDENT`).
   * Creates the initial `exercises` table referencing `users(id)`.
   * *Prerequisites:* None.
2. **`database/migrations/V2__create_exercises.sql` (Idempotent Exercise Definition):**
   * Ensures the `exercises` table exists with creator foreign key and difficulty constraint (`EASY`, `MEDIUM`, `HARD`).
   * *Prerequisites:* `users` table (from `schema.sql`).
3. **`database/migrations/V3__create_submissions.sql` (Submissions & RLS):**
   * Creates the `submissions` table referencing `exercises(id)` and `users(id)`.
   * Creates indexes on `exercise_id` and `student_id`, and enables row-level security.
   * *Prerequisites:* `exercises` and `users` tables.
4. **`database/migrations/V4__add_exercise_approval_status.sql` (Exercise Approval Workflow):**
   * Adds the `status` column (`PENDING`, `APPROVED`, `REJECTED`) with default `'PENDING'` to `exercises`.
   * *Prerequisites:* `exercises` table.
5. **`database/migrations/V5__create_messages.sql` (Peer Messaging):**
   * Creates the `messages` table with sender and receiver references to `users(id)`.
   * Creates performance indexes on `sender_id`, `receiver_id`, and `sent_at`.
   * *Prerequisites:* `users` table.
6. **`database/migrations/V6__create_system_settings.sql` (Platform Configuration):**
   * Creates the `system_settings` key-value table referencing `users(id)`.
   * Seeds default configuration keys: `platform_name`, `allow_registration`, `maintenance_mode`, and `default_exercise_difficulty`.
   * *Prerequisites:* `users` table.
7. **`database/migrations/V7__add_score_to_submissions.sql` (Submission Grading):**
   * Adds the `score` column to `submissions` with constraint `CHECK (score >= 0 AND score <= 100)`.
   * *Prerequisites:* `submissions` table (created in `V3`).
8. **`database/migrations/V8__create_challenges.sql` (Challenges & Participation):**
   * Creates the `challenges` table (with creator FK, difficulty, status, and date checks).
   * Creates the `challenge_participants` table (composite unique constraint on challenge and student, status constraint for `JOINED`, `COMPLETED`, `DROPPED`).
   * *Prerequisites:* `users` table.

### Applying Migrations via Command Line

Run the migrations in order using `psql` (substitute your actual host, port, username, and database name):

```bash
psql -h <DB_HOST> -p <DB_PORT> -U <DB_USER> -d <DB_NAME> -f database/schema.sql
psql -h <DB_HOST> -p <DB_PORT> -U <DB_USER> -d <DB_NAME> -f database/migrations/V2__create_exercises.sql
psql -h <DB_HOST> -p <DB_PORT> -U <DB_USER> -d <DB_NAME> -f database/migrations/V3__create_submissions.sql
psql -h <DB_HOST> -p <DB_PORT> -U <DB_USER> -d <DB_NAME> -f database/migrations/V4__add_exercise_approval_status.sql
psql -h <DB_HOST> -p <DB_PORT> -U <DB_USER> -d <DB_NAME> -f database/migrations/V5__create_messages.sql
psql -h <DB_HOST> -p <DB_PORT> -U <DB_USER> -d <DB_NAME> -f database/migrations/V6__create_system_settings.sql
psql -h <DB_HOST> -p <DB_PORT> -U <DB_USER> -d <DB_NAME> -f database/migrations/V7__add_score_to_submissions.sql
psql -h <DB_HOST> -p <DB_PORT> -U <DB_USER> -d <DB_NAME> -f database/migrations/V8__create_challenges.sql
```

## Environment and Database Configuration

### Application Environment (`APP_ENV`)

The application supports explicit runtime environment classification through `APP_ENV`. Configuration is read from the JVM system property first (`-DAPP_ENV=...`), then the operating-system environment variable.

| Environment Type | Canonical Name | Accepted Aliases | Purpose / Notes |
| ---------------- | -------------- | ---------------- | --------------- |
| **Test** | `TEST` | `TESTING` | Automated test suite execution (injected by default in Maven Surefire) |
| **Local Development** | `LOCAL_DEV` | `LOCAL`, `DEV`, `DEVELOPMENT`, `LOCAL-DEV` | Local developer machine execution |
| **Staging** | `STAGING` | `STAGE` | Pre-production testing server |
| **Production** | `PRODUCTION` | `PROD` | Production deployment |
| **Unknown** | `UNKNOWN` | *(fallback)* | Missing, blank, or unrecognized values resolve strictly to `UNKNOWN` |

#### Normalization & Behavior
* Case is normalized case-insensitively using `Locale.ROOT`, and leading/trailing whitespace is trimmed.
* If the JVM system property is explicitly set to blank, it resolves to `UNKNOWN` and does not fall back to the operating-system environment variable.
* When `APP_ENV` resolves to `UNKNOWN`, the application logs a warning on context startup but permits execution for backward compatibility.
* **Important:** `APP_ENV` provides environment classification and diagnostic awareness only. Setting `APP_ENV` does not itself validate database credentials, enforce connection encryption, or isolate database access.

#### Setting `APP_ENV` in PowerShell

```powershell
# Set for the current PowerShell session
$env:APP_ENV = "LOCAL_DEV"

# Or pass directly to Maven test runs
mvn test -DAPP_ENV=TEST
```

### Database Configuration

The application reads database configuration from environment variables or JVM system properties via `DBConnection`:

### Required Environment Variables

| Variable | Description | Example Format |
| -------- | ----------- | -------------- |
| `DB_URL` | JDBC connection URL for PostgreSQL / Supabase | `jdbc:postgresql://<DB_HOST>:<DB_PORT>/<DB_NAME>` |
| `DB_USERNAME` | Database username | `<DB_USER>` |
| `DB_PASSWORD` | Database user password | `<DB_PASSWORD>` |

### Optional Pool Tuning Variables

| Variable | Default Value | Description |
| -------- | ------------- | ----------- |
| `DB_POOL_NAME` | `PythonCodingPlatformPool` | Name identifier for the HikariCP pool |
| `DB_POOL_MAX_SIZE` | `10` | Maximum connections in the pool |
| `DB_POOL_MIN_IDLE` | `2` | Minimum idle connections retained |
| `DB_POOL_CONNECTION_TIMEOUT_MS` | `30000` | Connection checkout timeout in milliseconds |
| `DB_POOL_IDLE_TIMEOUT_MS` | `600000` | Idle connection retirement timeout in milliseconds |
| `DB_POOL_MAX_LIFETIME_MS` | `1800000` | Maximum connection lifetime in milliseconds |

> [!IMPORTANT]
> Never commit real database passwords, API tokens, or `.env` files to source control. Set credentials in your local environment or Tomcat runtime configuration.

## Build and Packaging

From the project root directory, run:

```bash
# Clean and run automated tests
mvn clean test

# Build the deployable WAR artifact
mvn package
```

The compiled web application archive is generated at:
`target/python-coding-platform-1.0-SNAPSHOT.war`

## Deployment

1. Install and start **Apache Tomcat 10.1.x**.
2. Configure the required environment variables (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`) in Tomcat's environment (e.g., via `setenv.sh` or `setenv.bat` in Tomcat's `bin/` directory).
3. Copy the packaged WAR file (`target/python-coding-platform-1.0-SNAPSHOT.war`) to Tomcat's `webapps/` folder (or rename it to `ROOT.war` for root context deployment).
4. Access the platform in your browser at `http://localhost:8080/python-coding-platform-1.0-SNAPSHOT/` (or `http://localhost:8080/` if deployed as `ROOT`).

## Testing and Verification

The automated test suite runs via Maven Surefire:

* **Tests run:** 352
* **Failures:** 0
* **Errors:** 0
* **Skipped:** 3
* **Build status:** BUILD SUCCESS

The standard test suite executes in complete isolation using in-memory H2 databases and Mockito mocks. No external database connection or live network access is required for standard builds.

The 3 skipped tests (`IntegrationCheckTest`, `ProgressDAOImplTest.testLiveDatabaseVerification`, and `DBConnectionTest.testLiveConnection`) are opt-in live database verification tests. They are skipped by default and only run when explicitly targeting a configured database with `-Dlive.db.test=true` or `-Plive-db`.

## Current Limitations

* **No Automated Code Execution:** Python submissions are stored for instructor inspection and manual evaluation. Browser-side or server-side sandboxed execution (e.g., Docker containerization) is not implemented.
* **Challenge Evaluation:** Coding challenges track student participation (`JOINED`, `COMPLETED`, `DROPPED`) rather than running automated unit test suites against submitted solutions.
* **Public Demo:** A hosted public demonstration instance is not currently configured.

## Future Roadmap

* Implement a secure, sandboxed Python code runner (e.g., isolated container worker).
* Support automated test cases and assertion grading for exercises.
* Add gamification elements (badges, leaderboards, streaks).
* Integrate CI/CD automated build pipelines with code coverage reporting.

## Author

**Avinash Kumar**  
GitHub: [@avinashkumar272006-afk](https://github.com/avinashkumar272006-afk)

---

Developed for learning, demonstrating robust Java Jakarta EE web development, and exploring structured educational workflow management.

