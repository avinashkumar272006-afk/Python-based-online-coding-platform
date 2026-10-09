package com.codingplatform.util;

import com.codingplatform.exception.DatabaseException;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DBConnectionTest {

    @Test
    public void testConnection() {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        if (url == null || user == null || password == null) {
            // Environment variables not set, just verify that the correct exception is thrown
            DatabaseException exception = assertThrows(DatabaseException.class, DBConnection::getConnection);
            assertEquals("Database credentials are not configured in environment variables.", exception.getMessage());
            System.out.println("Skipping actual connection test: environment variables DB_URL, DB_USERNAME, or DB_PASSWORD are not set.");
        } else {
            // Environment variables are set, try to connect
            try (Connection conn = DBConnection.getConnection()) {
                assertNotNull(conn, "Connection should not be null");
                assertTrue(conn.isValid(5), "Connection should be valid");
                System.out.println("Successfully connected to the Supabase PostgreSQL database.");
            } catch (DatabaseException | SQLException e) {
                fail("Database connection failed: " + e.getMessage());
            }
        }
    }
}
