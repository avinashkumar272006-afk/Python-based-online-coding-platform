package com.codingplatform.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.codingplatform.exception.DatabaseException;

public class DBConnection {

    public static Connection getConnection() throws DatabaseException {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        if (url == null || user == null || password == null) {
            throw new DatabaseException("Database credentials are not configured in environment variables.", null);
        }

        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("PostgreSQL JDBC Driver not found", e);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to the database", e);
        }
    }
}
