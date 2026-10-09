# Project Architecture

The application follows the MVC (Model-View-Controller) design pattern without using heavy frameworks.

### Request Flow

1. **Browser**: The user interacts with the UI (HTML/CSS/JS) and sends an HTTP request.
2. **JSP (View)**: Generates the HTML to display data, or provides forms for user input. Forwards actions to Servlets.
3. **Servlet (Controller)**: Intercepts HTTP requests, reads parameters, and routes them to the appropriate Service layer.
4. **Service (Business Logic)**: Contains the core business rules. It uses DAOs to fetch or persist data.
5. **DAO (Data Access Object)**: Abstracts and encapsulates all access to the data source.
6. **JDBC (Java Database Connectivity)**: The Java API used by the DAO to execute SQL queries.
7. **MySQL (Database)**: The relational database where data is persisted.
