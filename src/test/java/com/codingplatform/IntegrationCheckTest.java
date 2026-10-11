package com.codingplatform;

import com.codingplatform.util.DBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class IntegrationCheckTest {

    @AfterEach
    public void tearDown() {
        DBConnection.shutdownPool();
    }

    @Test
    public void testDb() throws Exception {
        assumeTrue(Boolean.getBoolean("live.db.test"),
                "Skipping IntegrationCheckTest: opt-in flag -Dlive.db.test=true or -Plive-db not enabled");

        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT count(*) FROM system_settings")) {
            assertNotNull(rs, "ResultSet should not be null");
            assertTrue(rs.next(), "Count query should return a row");
            assertTrue(rs.getInt(1) >= 0, "System settings count must be non-negative");
        }
    }
}
