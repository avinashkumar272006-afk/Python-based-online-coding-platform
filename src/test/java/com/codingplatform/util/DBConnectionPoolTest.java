package com.codingplatform.util;

import com.codingplatform.exception.DatabaseException;
import com.codingplatform.listener.DatabaseContextListener;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContextEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class DBConnectionPoolTest {

    private static final String H2_URL = "jdbc:h2:mem:pooltest;DB_CLOSE_DELAY=-1";
    private static final String H2_USER = "sa";
    private static final String H2_PASSWORD = "sa";

    @BeforeEach
    public void setUp() {
        cleanEnvironment();
    }

    @AfterEach
    public void tearDown() {
        cleanEnvironment();
    }

    private void cleanEnvironment() {
        DBConnection.shutdownPool();
        DBConnection.resetEnvLookup();
        System.clearProperty("DB_URL");
        System.clearProperty("DB_USERNAME");
        System.clearProperty("DB_PASSWORD");
        System.clearProperty("DB_POOL_MAX_SIZE");
        System.clearProperty("DB_POOL_MIN_IDLE");
        System.clearProperty("DB_POOL_CONNECTION_TIMEOUT_MS");
        System.clearProperty("DB_POOL_IDLE_TIMEOUT_MS");
        System.clearProperty("DB_POOL_MAX_LIFETIME_MS");
        System.clearProperty("DB_POOL_NAME");
        System.clearProperty("DB_CREDENTIALS_FILE");
    }

    @Test
    public void testMissingCredentialsThrowsDatabaseException() {
        // Explicitly simulate absent environment credentials
        DBConnection.setEnvLookup(key -> null);
        DatabaseException exception = assertThrows(DatabaseException.class, DBConnection::getConnection);
        assertEquals("Database credentials are not configured in environment variables.", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void testExplicitlyBlankSystemPropertyTreatedAsMissingCredentials() {
        // When a system property is explicitly set to blank, it must not fall back to environment variables
        DBConnection.setEnvLookup(key -> "configured-in-env");
        System.setProperty("DB_URL", "   ");
        System.setProperty("DB_USERNAME", "dummyUser");
        System.setProperty("DB_PASSWORD", "dummyPass");

        DatabaseException exception = assertThrows(DatabaseException.class, DBConnection::getConnection);
        assertEquals("Database credentials are not configured in environment variables.", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void testPoolConfigurationDefaults() {
        HikariConfig config = DBConnection.createDefaultConfig(H2_URL, H2_USER, H2_PASSWORD);

        assertEquals(H2_URL, config.getJdbcUrl());
        assertEquals(H2_USER, config.getUsername());
        assertEquals(H2_PASSWORD, config.getPassword());
        assertEquals(DBConnection.DEFAULT_MAX_POOL_SIZE, config.getMaximumPoolSize());
        assertEquals(DBConnection.DEFAULT_MIN_IDLE, config.getMinimumIdle());
        assertEquals(DBConnection.DEFAULT_CONNECTION_TIMEOUT_MS, config.getConnectionTimeout());
        assertEquals(DBConnection.DEFAULT_IDLE_TIMEOUT_MS, config.getIdleTimeout());
        assertEquals(DBConnection.DEFAULT_MAX_LIFETIME_MS, config.getMaxLifetime());
        assertEquals(DBConnection.DEFAULT_POOL_NAME, config.getPoolName());
        assertEquals("org.h2.Driver", config.getDriverClassName());
    }

    @Test
    public void testCustomPoolConfigurationFromSystemProperties() {
        System.setProperty("DB_POOL_MAX_SIZE", "5");
        System.setProperty("DB_POOL_MIN_IDLE", "1");
        System.setProperty("DB_POOL_CONNECTION_TIMEOUT_MS", "15000");
        System.setProperty("DB_POOL_IDLE_TIMEOUT_MS", "300000");
        System.setProperty("DB_POOL_MAX_LIFETIME_MS", "900000");
        System.setProperty("DB_POOL_NAME", "CustomTestPool");

        HikariConfig config = DBConnection.createDefaultConfig(H2_URL, H2_USER, H2_PASSWORD);

        assertEquals(5, config.getMaximumPoolSize());
        assertEquals(1, config.getMinimumIdle());
        assertEquals(15000L, config.getConnectionTimeout());
        assertEquals(300000L, config.getIdleTimeout());
        assertEquals(900000L, config.getMaxLifetime());
        assertEquals("CustomTestPool", config.getPoolName());
    }

    @Test
    public void testInvalidPoolPropertiesFallbackToDefaults() {
        System.setProperty("DB_POOL_MAX_SIZE", "invalid");
        System.setProperty("DB_POOL_MIN_IDLE", "-3");
        System.setProperty("DB_POOL_CONNECTION_TIMEOUT_MS", "not-a-number");
        System.setProperty("DB_POOL_IDLE_TIMEOUT_MS", "-100");
        System.setProperty("DB_POOL_MAX_LIFETIME_MS", "xyz");
        System.setProperty("DB_POOL_NAME", "   ");

        HikariConfig config = DBConnection.createDefaultConfig(H2_URL, H2_USER, H2_PASSWORD);

        assertEquals(DBConnection.DEFAULT_MAX_POOL_SIZE, config.getMaximumPoolSize());
        assertEquals(DBConnection.DEFAULT_MIN_IDLE, config.getMinimumIdle());
        assertEquals(DBConnection.DEFAULT_CONNECTION_TIMEOUT_MS, config.getConnectionTimeout());
        assertEquals(DBConnection.DEFAULT_IDLE_TIMEOUT_MS, config.getIdleTimeout());
        assertEquals(DBConnection.DEFAULT_MAX_LIFETIME_MS, config.getMaxLifetime());
        assertEquals(DBConnection.DEFAULT_POOL_NAME, config.getPoolName());
    }

    @Test
    public void testBorrowAndReturnConnectionFromPool() throws Exception {
        System.setProperty("DB_URL", H2_URL);
        System.setProperty("DB_USERNAME", H2_USER);
        System.setProperty("DB_PASSWORD", H2_PASSWORD);

        // Borrow connection
        try (Connection conn = DBConnection.getConnection()) {
            assertNotNull(conn, "Borrowed connection must not be null");
            assertTrue(conn.isValid(2), "Borrowed connection must be valid");
            assertTrue(conn.getMetaData().getURL().startsWith("jdbc:h2:"), "Must use H2 in-memory database and never fall back to ambient credentials");
            assertTrue(DBConnection.isPoolInitialized(), "Pool must be marked as initialized");

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT 42 AS result")) {
                assertTrue(rs.next());
                assertEquals(42, rs.getInt("result"));
            }
        } // conn.close() returns connection to pool

        // Borrow connection again to verify returned connection is reusable
        try (Connection conn2 = DBConnection.getConnection()) {
            assertNotNull(conn2);
            assertTrue(conn2.isValid(2));
        }
    }

    @Test
    public void testMultipleConcurrentBorrowedConnectionsAreDistinct() throws Exception {
        System.setProperty("DB_URL", H2_URL);
        System.setProperty("DB_USERNAME", H2_USER);
        System.setProperty("DB_PASSWORD", H2_PASSWORD);

        try (Connection conn1 = DBConnection.getConnection();
             Connection conn2 = DBConnection.getConnection()) {

            assertNotNull(conn1);
            assertNotNull(conn2);
            assertTrue(conn1.getMetaData().getURL().startsWith("jdbc:h2:"), "Must use H2 in-memory database");
            assertNotSame(conn1, conn2, "Concurrent operations must receive distinct connection handles");

            // Execute queries on both concurrently
            try (Statement s1 = conn1.createStatement();
                 ResultSet r1 = s1.executeQuery("SELECT 10 AS v");
                 Statement s2 = conn2.createStatement();
                 ResultSet r2 = s2.executeQuery("SELECT 20 AS v")) {

                assertTrue(r1.next());
                assertEquals(10, r1.getInt("v"));

                assertTrue(r2.next());
                assertEquals(20, r2.getInt("v"));
            }
        }
    }

    @Test
    public void testTransactionAndRollbackBehavior() throws Exception {
        System.setProperty("DB_URL", H2_URL);
        System.setProperty("DB_USERNAME", H2_USER);
        System.setProperty("DB_PASSWORD", H2_PASSWORD);

        // Setup test table
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            assertTrue(conn.getMetaData().getURL().startsWith("jdbc:h2:"), "Must use H2 in-memory database");
            stmt.execute("CREATE TABLE IF NOT EXISTS test_tx (id INT PRIMARY KEY, name VARCHAR(50))");
            stmt.execute("DELETE FROM test_tx");
        }

        // Test Rollback
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO test_tx (id, name) VALUES (?, ?)")) {
                ps.setInt(1, 1);
                ps.setString(2, "RolledBack");
                ps.executeUpdate();
            }
            conn.rollback();
        }

        // Verify row was rolled back
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM test_tx WHERE id = 1")) {
            assertTrue(rs.next());
            assertEquals(0, rs.getInt(1), "Row must not exist after rollback");
        }

        // Test Commit
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO test_tx (id, name) VALUES (?, ?)")) {
                ps.setInt(1, 2);
                ps.setString(2, "Committed");
                ps.executeUpdate();
            }
            conn.commit();
        }

        // Verify row was committed
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM test_tx WHERE id = 2")) {
            assertTrue(rs.next());
            assertEquals(1, rs.getInt(1), "Row must exist after commit");
        }

        // Verify pool resets auto-commit on return
        try (Connection conn = DBConnection.getConnection()) {
            assertTrue(conn.getAutoCommit(), "Returned connection must have autoCommit reset to true");
        }
    }

    @Test
    public void testPoolShutdownIsCleanAndIdempotent() throws Exception {
        System.setProperty("DB_URL", H2_URL);
        System.setProperty("DB_USERNAME", H2_USER);
        System.setProperty("DB_PASSWORD", H2_PASSWORD);

        Connection conn = DBConnection.getConnection();
        assertNotNull(conn);
        conn.close();

        assertTrue(DBConnection.isPoolInitialized());

        // First shutdown
        DBConnection.shutdownPool();
        assertFalse(DBConnection.isPoolInitialized());
        assertNull(DBConnection.getDataSource());

        // Repeated shutdown is safe and idempotent
        assertDoesNotThrow(DBConnection::shutdownPool);
    }

    @Test
    public void testDatabaseContextListenerDestroysPool() throws Exception {
        System.setProperty("DB_URL", H2_URL);
        System.setProperty("DB_USERNAME", H2_USER);
        System.setProperty("DB_PASSWORD", H2_PASSWORD);

        Connection conn = DBConnection.getConnection();
        conn.close();
        assertTrue(DBConnection.isPoolInitialized());

        DatabaseContextListener listener = new DatabaseContextListener();
        ServletContextEvent mockEvent = mock(ServletContextEvent.class);

        listener.contextInitialized(mockEvent); // Must not throw
        listener.contextDestroyed(mockEvent);

        assertFalse(DBConnection.isPoolInitialized(), "Pool must be shut down upon ServletContext destroy");
    }

    @Test
    public void testInvalidH2DatabaseUrlThrowsDatabaseException() {
        // Deterministic local test: invalid H2 connection parameter fails fast without network request
        System.setProperty("DB_URL", "jdbc:h2:mem:pooltest;INVALID_SETTING=true");
        System.setProperty("DB_USERNAME", H2_USER);
        System.setProperty("DB_PASSWORD", H2_PASSWORD);

        DatabaseException ex = assertThrows(DatabaseException.class, DBConnection::getConnection);
        assertEquals("Failed to connect to the database", ex.getMessage());
        assertNotNull(ex.getCause());
    }

    @Test
    public void testLiveDatabaseBlockedInTestEnvironmentWithoutOptIn() {
        System.setProperty("DB_URL", "jdbc:postgresql://localhost:5432/coding_platform");
        System.setProperty("DB_USERNAME", "dummy");
        System.setProperty("DB_PASSWORD", "dummy");

        DatabaseException ex = assertThrows(DatabaseException.class, DBConnection::getConnection);
        assertTrue(ex.getMessage().contains("Live database access is blocked in TEST environment"));
        assertNull(ex.getCause(), "Must fail fast before attempting any network socket or HikariDataSource creation");
        assertFalse(DBConnection.isPoolInitialized(), "Pool must not be initialized when safety check fails");
    }

    @Test
    public void testLoadCredentialsFromExplicitExternalFile() throws Exception {
        DBConnection.setEnvLookup(key -> null);
        File tempFile = File.createTempFile("db-test-creds", ".properties");
        tempFile.deleteOnExit();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("DB_URL=" + H2_URL + "\n");
            writer.write("DB_USERNAME=" + H2_USER + "\n");
            writer.write("DB_PASSWORD=" + H2_PASSWORD + "\n");
            writer.write("DB_POOL_NAME=TestFilePool\n");
        }

        System.setProperty("DB_CREDENTIALS_FILE", tempFile.getAbsolutePath());

        try (Connection conn = DBConnection.getConnection()) {
            assertNotNull(conn, "Connection loaded via external credentials file must succeed");
            assertTrue(conn.isValid(2));
            assertTrue(DBConnection.isPoolInitialized());
        } finally {
            tempFile.delete();
        }
    }

    @Test
    public void testPrecedenceSystemPropertyOverExternalFile() throws Exception {
        DBConnection.setEnvLookup(key -> null);
        File tempFile = File.createTempFile("db-test-creds", ".properties");
        tempFile.deleteOnExit();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("DB_URL=" + H2_URL + "\n");
            writer.write("DB_USERNAME=fileUser\n");
            writer.write("DB_PASSWORD=" + H2_PASSWORD + "\n");
            writer.write("DB_POOL_NAME=FilePool\n");
        }

        System.setProperty("DB_CREDENTIALS_FILE", tempFile.getAbsolutePath());
        // System property should take precedence over external file
        System.setProperty("DB_POOL_NAME", "SysPropPool");

        HikariConfig config = DBConnection.createDefaultConfig(H2_URL, H2_USER, H2_PASSWORD);
        assertEquals("SysPropPool", config.getPoolName(), "System property must override external file property");

        tempFile.delete();
    }

    @Test
    public void testPrecedenceEnvVarOverExternalFile() throws Exception {
        File tempFile = File.createTempFile("db-test-creds", ".properties");
        tempFile.deleteOnExit();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("DB_URL=" + H2_URL + "\n");
            writer.write("DB_USERNAME=fileUser\n");
            writer.write("DB_PASSWORD=" + H2_PASSWORD + "\n");
            writer.write("DB_POOL_NAME=FilePool\n");
        }

        System.setProperty("DB_CREDENTIALS_FILE", tempFile.getAbsolutePath());
        // Environment variable should take precedence over external file
        DBConnection.setEnvLookup(key -> "DB_POOL_NAME".equals(key) ? "EnvPool" : null);

        HikariConfig config = DBConnection.createDefaultConfig(H2_URL, H2_USER, H2_PASSWORD);
        assertEquals("EnvPool", config.getPoolName(), "Environment variable must override external file property");

        tempFile.delete();
    }

    @Test
    public void testNonExistentCredentialsFileThrowsDatabaseException() {
        DBConnection.setEnvLookup(key -> null);
        System.setProperty("DB_CREDENTIALS_FILE", "C:/non/existent/path/creds.properties");
        DatabaseException exception = assertThrows(DatabaseException.class, DBConnection::getConnection);
        assertEquals("Database credentials are not configured in environment variables.", exception.getMessage());
    }

    @Test
    public void testExternalPropertiesAliases() throws Exception {
        File tempFile = File.createTempFile("db-test-creds-alias", ".properties");
        tempFile.deleteOnExit();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("db.url=" + H2_URL + "\n");
            writer.write("db.username=" + H2_USER + "\n");
            writer.write("db.password=" + H2_PASSWORD + "\n");
            writer.write("DB_POOL_NAME=AliasPool\n");
        }

        DBConnection.setEnvLookup(key -> null);
        System.setProperty("DB_CREDENTIALS_FILE", tempFile.getAbsolutePath());

        try (Connection conn = DBConnection.getConnection()) {
            assertNotNull(conn, "Connection should be established using db.* property aliases from external file");
            assertTrue(DBConnection.isPoolInitialized());
        } finally {
            DBConnection.shutdownPool();
            tempFile.delete();
        }
    }

    @Test
    public void testTomcatCatalinaBaseCredentialsResolution() throws Exception {
        File tempCatalinaDir = Files.createTempDirectory("mock-catalina").toFile();
        File confDir = new File(tempCatalinaDir, "conf");
        confDir.mkdirs();
        File credsFile = new File(confDir, "db-credentials.properties");
        try (FileWriter writer = new FileWriter(credsFile)) {
            writer.write("DB_URL=" + H2_URL + "\n");
            writer.write("DB_USERNAME=" + H2_USER + "\n");
            writer.write("DB_PASSWORD=" + H2_PASSWORD + "\n");
            writer.write("DB_POOL_NAME=TomcatConfPool\n");
        }

        DBConnection.setEnvLookup(key -> null);
        System.setProperty("catalina.base", tempCatalinaDir.getAbsolutePath());

        try (Connection conn = DBConnection.getConnection()) {
            assertNotNull(conn, "Connection should be established using ${catalina.base}/conf/db-credentials.properties");
            assertTrue(DBConnection.isPoolInitialized());
        } finally {
            DBConnection.shutdownPool();
            System.clearProperty("catalina.base");
            credsFile.delete();
            confDir.delete();
            tempCatalinaDir.delete();
        }
    }
}
