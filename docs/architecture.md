# Project Architecture

The application follows a layered MVC (Model-View-Controller) design pattern built with Java Servlet and JSP technologies without heavy enterprise frameworks.

## Layered Architecture

1. **Web Presentation Layer (View - JSP & Static Assets)**:
   - JSP pages render role-based user interfaces for Students, Instructors, and Administrators.
   - Reusable layout fragments (`header.jsp`, `sidebar.jsp`, `topbar.jsp`, `alerts.jsp`, `footer.jsp`).
   - Vanilla CSS styling and lightweight client-side UI controllers (`app.js`) without external frontend framework dependencies.

2. **Security & Perimeter Filter Layer**:
   - **`AuthenticationFilter`**: Intercepts requests to enforce server-side role-based access control (RBAC) across `/student/*`, `/instructor/*`, `/admin/*`, and `/messages/*` routes. Unauthenticated requests redirect to `/login.jsp`.
   - **`CsrfFilter`**: Validates session-bound CSRF tokens on all state-changing requests (`POST`). Issues anonymous tokens for public authentication forms (`/login`, `/register`) and enforces authenticated tokens on protected endpoints. Rejects tokens supplied in URL query parameters.

3. **Controller Layer (Jakarta Servlets)**:
   - Servlets intercept HTTP requests, validate input parameters, verify caller identity/roles from the session, and invoke the appropriate Service layer methods.
   - Dispatches responses by forwarding to JSP views or issuing redirects to avoid duplicate submissions (Post/Redirect/Get pattern).

4. **Service Layer (Business Logic)**:
   - Encapsulates business rules, ownership validation (e.g., verifying an instructor owns an exercise before review), input boundary checks (e.g., score ranges 0–100), and state transition rules (e.g., terminal challenge participation states).

5. **Data Access Layer (DAO & JDBC)**:
   - Data Access Object (DAO) interfaces and implementations encapsulate all SQL interactions.
   - Uses standard JDBC `PreparedStatement` to ensure protection against SQL injection.
   - Borrows and releases connections using `try-with-resources` blocks to prevent connection leaks.

6. **Connection Pool Management (HikariCP)**:
   - `DBConnection` provides a high-performance HikariCP connection pool (`HikariDataSource`).
   - Pool configuration is resolved from environment variables (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_POOL_MAX_SIZE`, etc.) or JVM system properties.
   - `DatabaseContextListener` ensures the connection pool is gracefully shut down on servlet context termination or container redeployment.

7. **Persistence Layer (PostgreSQL / Supabase)**:
   - Relational database hosted via Supabase PostgreSQL, structured and versioned across incremental migrations (`database/schema.sql` and `database/migrations/V2` through `V8`).
   - In-memory H2 databases are used during automated unit and integration tests to ensure deterministic test isolation without requiring external network connectivity.

## Request Flow

1. **Browser**: User submits an action via HTTP GET or POST form.
2. **Filter Chain**: `AuthenticationFilter` verifies role authorization; `CsrfFilter` verifies anti-CSRF token validity for POST requests.
3. **Servlet**: Handles the request, parses inputs, and delegates to the Service layer.
4. **Service**: Enforces domain constraints and calls DAO methods.
5. **DAO / HikariCP**: Borrows a JDBC connection from the HikariCP pool and executes parameterized SQL against PostgreSQL (or H2 in test).
6. **Servlet / JSP**: Prepares response attributes and forwards to the JSP view (or redirects to dashboard/list routes).

