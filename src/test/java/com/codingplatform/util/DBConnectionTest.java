package com.codingplatform.util;

import com.codingplatform.exception.DatabaseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class DBConnectionTest {

    private static final String H2_URL = "jdbc:h2:mem:dbconnectiontest;DB_CLOSE_DELAY=-1";
    private static final String H2_USER = "sa";
    private static final String H2_PASSWORD = "sa";

    @AfterEach
    public void tearDown() {
        DBConnection.shutdownPool();
        DBConnection.resetEnvLookup();
        System.clearProperty("DB_URL");
        System.clearProperty("DB_USERNAME");
        System.clearProperty("DB_PASSWORD");
    }

    @Test
    public void testIsolatedConnectionWithH2() throws Exception {
        System.setProperty("DB_URL", H2_URL);
        System.setProperty("DB_USERNAME", H2_USER);
        System.setProperty("DB_PASSWORD", H2_PASSWORD);

        try (Connection conn = DBConnection.getConnection()) {
            assertNotNull(conn, "Connection should not be null");
            assertTrue(conn.isValid(2), "Connection should be valid");
            assertTrue(conn.getMetaData().getURL().startsWith("jdbc:h2:"), "Must use H2 in-memory database");
        }
    }

    @Test
    public void testLiveConnection() {
        assumeTrue(Boolean.getBoolean("live.db.test"),
                "Skipping live database test: opt-in flag -Dlive.db.test=true or -Plive-db not enabled");

        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        if (url == null || user == null || password == null) {
            fail("Live database test opted-in, but DB_URL, DB_USERNAME, or DB_PASSWORD is missing");
        }

        try (Connection conn = DBConnection.getConnection()) {
            assertNotNull(conn, "Connection should not be null");
            assertTrue(conn.isValid(5), "Connection should be valid");
        } catch (DatabaseException | SQLException e) {
            fail("Live database connection failed: " + e.getMessage());
        }
    }
}
