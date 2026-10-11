package com.codingplatform.util;

import com.codingplatform.config.EnvironmentType;
import com.codingplatform.exception.DatabaseException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DBConnectionSafetyTest {

    @BeforeEach
    public void setUp() {
        DBConnection.shutdownPool();
        DBConnection.resetEnvLookup();
    }

    @AfterEach
    public void tearDown() {
        DBConnection.shutdownPool();
        DBConnection.resetEnvLookup();
        System.clearProperty("DB_URL");
        System.clearProperty("DB_USERNAME");
        System.clearProperty("DB_PASSWORD");
    }

    @Test
    public void testTestEnvironmentAllowsEmbeddedH2WithoutOptIn() {
        assertDoesNotThrow(() ->
                DBConnection.validateDatabaseAccess("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", EnvironmentType.TEST, false));
        assertDoesNotThrow(() ->
                DBConnection.validateDatabaseAccess("jdbc:h2:mem:;DB_CLOSE_DELAY=-1", EnvironmentType.TEST, false));
        assertDoesNotThrow(() ->
                DBConnection.validateDatabaseAccess("jdbc:h2:file:./target/testdb", EnvironmentType.TEST, false));
        assertDoesNotThrow(() ->
                DBConnection.validateDatabaseAccess("jdbc:h2:~/testdb", EnvironmentType.TEST, false));
        assertDoesNotThrow(() ->
                DBConnection.validateDatabaseAccess("jdbc:h2:./testdb", EnvironmentType.TEST, false));
        assertDoesNotThrow(() ->
                DBConnection.validateDatabaseAccess("jdbc:h2:/var/testdb", EnvironmentType.TEST, false));
    }

    @Test
    public void testTestEnvironmentBlocksH2TcpClientWithoutOptIn() {
        DatabaseException ex1 = assertThrows(DatabaseException.class, () ->
                DBConnection.validateDatabaseAccess("jdbc:h2:tcp://localhost:9092/testdb", EnvironmentType.TEST, false));
        assertTrue(ex1.getMessage().contains("Live database access is blocked in TEST environment"));

        DatabaseException ex2 = assertThrows(DatabaseException.class, () ->
                DBConnection.validateDatabaseAccess("jdbc:h2:tcp://remote-host:9092/testdb", EnvironmentType.TEST, false));
        assertTrue(ex2.getMessage().contains("Live database access is blocked in TEST environment"));

        DatabaseException ex3 = assertThrows(DatabaseException.class, () ->
                DBConnection.validateDatabaseAccess("jdbc:h2:ssl://remote-host:9092/testdb", EnvironmentType.TEST, false));
        assertTrue(ex3.getMessage().contains("Live database access is blocked in TEST environment"));
    }

    @Test
    public void testTestEnvironmentAllowsH2TcpClientWhenOptedIn() {
        assertDoesNotThrow(() ->
                DBConnection.validateDatabaseAccess("jdbc:h2:tcp://localhost:9092/testdb", EnvironmentType.TEST, true));
        assertDoesNotThrow(() ->
                DBConnection.validateDatabaseAccess("jdbc:h2:tcp://remote-host:9092/testdb", EnvironmentType.TEST, true));
    }

    @Test
    public void testTestEnvironmentBlocksMalformedOrUnusualH2Urls() {
        assertThrows(DatabaseException.class, () ->
                DBConnection.validateDatabaseAccess("jdbc:h2:", EnvironmentType.TEST, false));
        assertThrows(DatabaseException.class, () ->
                DBConnection.validateDatabaseAccess("jdbc:h2://remote-host/db", EnvironmentType.TEST, false));
        assertThrows(DatabaseException.class, () ->
                DBConnection.validateDatabaseAccess("jdbc:h2:invalid_scheme", EnvironmentType.TEST, false));
    }

    @Test
    public void testTestEnvironmentBlocksPostgreSqlWhenNotOptedIn() {
        DatabaseException ex = assertThrows(DatabaseException.class, () ->
                DBConnection.validateDatabaseAccess("jdbc:postgresql://localhost:5432/coding_platform", EnvironmentType.TEST, false));

        assertTrue(ex.getMessage().contains("Live database access is blocked in TEST environment"));
        assertNull(ex.getCause());
    }

    @Test
    public void testTestEnvironmentBlocksRemotePostgreSqlWhenNotOptedIn() {
        DatabaseException ex = assertThrows(DatabaseException.class, () ->
                DBConnection.validateDatabaseAccess("jdbc:postgresql://db.supabase.co:5432/coding_platform", EnvironmentType.TEST, false));

        assertTrue(ex.getMessage().contains("Live database access is blocked in TEST environment"));
        assertNull(ex.getCause());
    }

    @Test
    public void testTestEnvironmentAllowsPostgreSqlWhenOptedIn() {
        assertDoesNotThrow(() ->
                DBConnection.validateDatabaseAccess("jdbc:postgresql://localhost:5432/coding_platform", EnvironmentType.TEST, true));
        assertDoesNotThrow(() ->
                DBConnection.validateDatabaseAccess("jdbc:postgresql://db.supabase.co:5432/coding_platform", EnvironmentType.TEST, true));
    }

    @Test
    public void testTestEnvironmentBlocksOtherRemoteDatabasesWithoutOptIn() {
        DatabaseException exMysql = assertThrows(DatabaseException.class, () ->
                DBConnection.validateDatabaseAccess("jdbc:mysql://localhost:3306/platform", EnvironmentType.TEST, false));
        assertTrue(exMysql.getMessage().contains("Live database access is blocked in TEST environment"));

        DatabaseException exOracle = assertThrows(DatabaseException.class, () ->
                DBConnection.validateDatabaseAccess("jdbc:oracle:thin:@localhost:1521:xe", EnvironmentType.TEST, false));
        assertTrue(exOracle.getMessage().contains("Live database access is blocked in TEST environment"));
    }

    @Test
    public void testLocalDevEnvironmentAllowsPostgreSql() {
        assertDoesNotThrow(() ->
                DBConnection.validateDatabaseAccess("jdbc:postgresql://localhost:5432/coding_platform", EnvironmentType.LOCAL_DEV, false));
        assertDoesNotThrow(() ->
                DBConnection.validateDatabaseAccess("jdbc:postgresql://remote-db.example.com:5432/coding_platform", EnvironmentType.LOCAL_DEV, false));
        assertDoesNotThrow(() ->
                DBConnection.validateDatabaseAccess("jdbc:h2:mem:localdev", EnvironmentType.LOCAL_DEV, false));
    }

    @Test
    public void testStagingEnvironmentAllowsPostgreSql() {
        assertDoesNotThrow(() ->
                DBConnection.validateDatabaseAccess("jdbc:postgresql://staging-db:5432/coding_platform", EnvironmentType.STAGING, false));
    }

    @Test
    public void testProductionEnvironmentAllowsPostgreSql() {
        assertDoesNotThrow(() ->
                DBConnection.validateDatabaseAccess("jdbc:postgresql://prod-db:5432/coding_platform", EnvironmentType.PRODUCTION, false));
    }

    @Test
    public void testProductionEnvironmentPermitsH2WithWarning() {
        assertDoesNotThrow(() ->
                DBConnection.validateDatabaseAccess("jdbc:h2:mem:proddb", EnvironmentType.PRODUCTION, false));
    }

    @Test
    public void testUnknownEnvironmentAllowsLocalhostAndLoopback() {
        assertDoesNotThrow(() ->
                DBConnection.validateDatabaseAccess("jdbc:postgresql://localhost:5432/coding_platform", EnvironmentType.UNKNOWN, false));
        assertDoesNotThrow(() ->
                DBConnection.validateDatabaseAccess("jdbc:postgresql://127.0.0.1:5432/coding_platform", EnvironmentType.UNKNOWN, false));
        assertDoesNotThrow(() ->
                DBConnection.validateDatabaseAccess("jdbc:postgresql://[::1]:5432/coding_platform", EnvironmentType.UNKNOWN, false));
        assertDoesNotThrow(() ->
                DBConnection.validateDatabaseAccess("jdbc:postgresql:///coding_platform", EnvironmentType.UNKNOWN, false));
        assertDoesNotThrow(() ->
                DBConnection.validateDatabaseAccess("jdbc:postgresql:coding_platform", EnvironmentType.UNKNOWN, false));
        assertDoesNotThrow(() ->
                DBConnection.validateDatabaseAccess("jdbc:h2:mem:test", EnvironmentType.UNKNOWN, false));
    }

    @Test
    public void testUnknownEnvironmentBlocksRemotePostgreSqlWithoutOptIn() {
        DatabaseException ex = assertThrows(DatabaseException.class, () ->
                DBConnection.validateDatabaseAccess("jdbc:postgresql://db.supabase.co:5432/coding_platform", EnvironmentType.UNKNOWN, false));

        assertTrue(ex.getMessage().contains("Remote database access is blocked when APP_ENV is UNKNOWN"));
        assertNull(ex.getCause());
    }

    @Test
    public void testUnknownEnvironmentBlocksRemotePostgreSqlEvenWithLiveDbTestOptIn() {
        DatabaseException ex = assertThrows(DatabaseException.class, () ->
                DBConnection.validateDatabaseAccess("jdbc:postgresql://db.supabase.co:5432/coding_platform", EnvironmentType.UNKNOWN, true));

        assertTrue(ex.getMessage().contains("Remote database access is blocked when APP_ENV is UNKNOWN"));
        assertNull(ex.getCause());
    }

    @Test
    public void testNullOrBlankUrlThrowsDatabaseException() {
        assertThrows(DatabaseException.class, () ->
                DBConnection.validateDatabaseAccess(null, EnvironmentType.TEST, false));
        assertThrows(DatabaseException.class, () ->
                DBConnection.validateDatabaseAccess("", EnvironmentType.TEST, false));
        assertThrows(DatabaseException.class, () ->
                DBConnection.validateDatabaseAccess("   ", EnvironmentType.TEST, false));
    }

    @Test
    public void testSafetyExceptionNeverDisclosesSensitiveUrlOrCredentials() {
        String sensitiveUrl = "jdbc:postgresql://secret_user:super_secret_pw@sensitive-cloud-host.internal:5432/confidential_db";

        DatabaseException exTest = assertThrows(DatabaseException.class, () ->
                DBConnection.validateDatabaseAccess(sensitiveUrl, EnvironmentType.TEST, false));
        assertFalse(exTest.getMessage().contains("secret_user"));
        assertFalse(exTest.getMessage().contains("super_secret_pw"));
        assertFalse(exTest.getMessage().contains("sensitive-cloud-host.internal"));
        assertFalse(exTest.getMessage().contains("confidential_db"));

        DatabaseException exUnknown = assertThrows(DatabaseException.class, () ->
                DBConnection.validateDatabaseAccess(sensitiveUrl, EnvironmentType.UNKNOWN, false));
        assertFalse(exUnknown.getMessage().contains("secret_user"));
        assertFalse(exUnknown.getMessage().contains("super_secret_pw"));
        assertFalse(exUnknown.getMessage().contains("sensitive-cloud-host.internal"));
        assertFalse(exUnknown.getMessage().contains("confidential_db"));
    }

    @Test
    public void testLocalOrLoopbackUrlDetection() {
        assertTrue(DBConnection.isLocalOrLoopbackUrl("jdbc:postgresql://localhost:5432/db"));
        assertTrue(DBConnection.isLocalOrLoopbackUrl("jdbc:postgresql://localhost/db"));
        assertTrue(DBConnection.isLocalOrLoopbackUrl("jdbc:postgresql://127.0.0.1:5432/db"));
        assertTrue(DBConnection.isLocalOrLoopbackUrl("jdbc:postgresql://0.0.0.0:5432/db"));
        assertTrue(DBConnection.isLocalOrLoopbackUrl("jdbc:postgresql://[::1]:5432/db"));
        assertTrue(DBConnection.isLocalOrLoopbackUrl("jdbc:postgresql://[0:0:0:0:0:0:0:1]:5432/db"));
        assertTrue(DBConnection.isLocalOrLoopbackUrl("jdbc:postgresql:///db"));
        assertTrue(DBConnection.isLocalOrLoopbackUrl("jdbc:postgresql:db"));
        assertTrue(DBConnection.isLocalOrLoopbackUrl("jdbc:h2:mem:db"));
        assertTrue(DBConnection.isLocalOrLoopbackUrl("jdbc:h2:tcp://localhost/~/test"));

        // Remote targets
        assertFalse(DBConnection.isLocalOrLoopbackUrl("jdbc:postgresql://db.supabase.co:5432/db"));
        assertFalse(DBConnection.isLocalOrLoopbackUrl("jdbc:postgresql://192.168.1.10:5432/db"));
        assertFalse(DBConnection.isLocalOrLoopbackUrl("jdbc:postgresql://10.0.0.1:5432/db"));
        assertFalse(DBConnection.isLocalOrLoopbackUrl("jdbc:postgresql://remotehost:5432/db"));
        assertFalse(DBConnection.isLocalOrLoopbackUrl("jdbc:h2:tcp://remotehost:9092/db"));

        // Null / blank
        assertFalse(DBConnection.isLocalOrLoopbackUrl(null));
        assertFalse(DBConnection.isLocalOrLoopbackUrl("   "));
    }

    @Test
    public void testH2DatabaseUrlDetection() {
        assertTrue(DBConnection.isH2DatabaseUrl("jdbc:h2:mem:test"));
        assertTrue(DBConnection.isH2DatabaseUrl("JDBC:H2:MEM:TEST"));
        assertTrue(DBConnection.isH2DatabaseUrl("jdbc:h2:file:/data/test"));
        assertTrue(DBConnection.isH2DatabaseUrl("jdbc:h2:tcp://localhost:9092/test"));
        assertFalse(DBConnection.isH2DatabaseUrl("jdbc:postgresql://localhost:5432/test"));
        assertFalse(DBConnection.isH2DatabaseUrl(null));
        assertFalse(DBConnection.isH2DatabaseUrl("   "));
    }

    @Test
    public void testEmbeddedH2DatabaseUrlDetection() {
        assertTrue(DBConnection.isEmbeddedH2DatabaseUrl("jdbc:h2:mem:test"));
        assertTrue(DBConnection.isEmbeddedH2DatabaseUrl("jdbc:h2:mem:;DB_CLOSE_DELAY=-1"));
        assertTrue(DBConnection.isEmbeddedH2DatabaseUrl("jdbc:h2:file:/path/to/db"));
        assertTrue(DBConnection.isEmbeddedH2DatabaseUrl("jdbc:h2:file:./target/testdb"));
        assertTrue(DBConnection.isEmbeddedH2DatabaseUrl("jdbc:h2:~/testdb"));
        assertTrue(DBConnection.isEmbeddedH2DatabaseUrl("jdbc:h2:./testdb"));
        assertTrue(DBConnection.isEmbeddedH2DatabaseUrl("jdbc:h2:/var/testdb"));
        assertTrue(DBConnection.isEmbeddedH2DatabaseUrl("jdbc:h2:C:/path/db"));

        // Remote TCP / SSL schemes must be rejected as embedded
        assertFalse(DBConnection.isEmbeddedH2DatabaseUrl("jdbc:h2:tcp://localhost:9092/test"));
        assertFalse(DBConnection.isEmbeddedH2DatabaseUrl("jdbc:h2:tcp://remote:9092/test"));
        assertFalse(DBConnection.isEmbeddedH2DatabaseUrl("jdbc:h2:ssl://remote:9092/test"));
        assertFalse(DBConnection.isEmbeddedH2DatabaseUrl("jdbc:h2://remote/test"));

        // Malformed / non-H2
        assertFalse(DBConnection.isEmbeddedH2DatabaseUrl("jdbc:h2:"));
        assertFalse(DBConnection.isEmbeddedH2DatabaseUrl("jdbc:postgresql://localhost:5432/test"));
        assertFalse(DBConnection.isEmbeddedH2DatabaseUrl(null));
        assertFalse(DBConnection.isEmbeddedH2DatabaseUrl("   "));
    }

    @Test
    public void testInitPoolEnforcesValidation() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/blocked_test");

        DatabaseException ex = assertThrows(DatabaseException.class, () -> DBConnection.initPool(config));
        assertTrue(ex.getMessage().contains("Live database access is blocked in TEST environment"));
        assertFalse(DBConnection.isPoolInitialized());
    }

    @Test
    public void testSetDataSourceEnforcesValidation() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:postgresql://localhost:5432/blocked_test");

        DatabaseException ex = assertThrows(DatabaseException.class, () -> DBConnection.setDataSource(ds));
        assertTrue(ex.getMessage().contains("Live database access is blocked in TEST environment"));
        assertFalse(DBConnection.isPoolInitialized());
    }
}
