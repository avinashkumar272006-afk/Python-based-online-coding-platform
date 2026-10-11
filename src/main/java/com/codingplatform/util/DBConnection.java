package com.codingplatform.util;

import com.codingplatform.config.AppEnvironment;
import com.codingplatform.config.EnvironmentType;
import com.codingplatform.exception.DatabaseException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Utility for managing database connectivity using a high-performance HikariCP connection pool.
 * Borrows connections for application operations and returns them upon Connection.close().
 * Supports external configuration through environment variables and system properties with sensible defaults.
 */
public class DBConnection {

    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    public static final String DEFAULT_POOL_NAME = "PythonCodingPlatformPool";
    public static final int DEFAULT_MAX_POOL_SIZE = 10;
    public static final int DEFAULT_MIN_IDLE = 2;
    public static final long DEFAULT_CONNECTION_TIMEOUT_MS = 30000L;
    public static final long DEFAULT_IDLE_TIMEOUT_MS = 600000L;
    public static final long DEFAULT_MAX_LIFETIME_MS = 1800000L;

    private static volatile HikariDataSource dataSource;
    private static final Object LOCK = new Object();
    private static Function<String, String> envLookup = System::getenv;

    // Package-private test seam to simulate environment-variable lookup
    static void setEnvLookup(Function<String, String> lookup) {
        envLookup = lookup != null ? lookup : System::getenv;
    }

    static void resetEnvLookup() {
        envLookup = System::getenv;
    }

    private DBConnection() {
        // Utility class
    }

    /**
     * Borrows a connection from the pool. If the pool is not yet initialized,
     * it initializes the pool using environment or system property configuration.
     *
     * @return a borrowed JDBC connection
     * @throws DatabaseException if credentials are missing or the connection cannot be established
     */
    public static Connection getConnection() throws DatabaseException {
        HikariDataSource ds = dataSource;
        if (ds == null || ds.isClosed()) {
            synchronized (LOCK) {
                ds = dataSource;
                if (ds == null || ds.isClosed()) {
                    String url = getEnvOrProperty("DB_URL");
                    String user = getEnvOrProperty("DB_USERNAME");
                    String password = getEnvOrProperty("DB_PASSWORD");

                    if (url == null || user == null || password == null) {
                        throw new DatabaseException("Database credentials are not configured in environment variables.", null);
                    }

                    validateDatabaseAccess(url);

                    try {
                        HikariConfig config = createDefaultConfig(url, user, password);
                        ds = new HikariDataSource(config);
                        dataSource = ds;
                    } catch (Exception e) {
                        throw new DatabaseException("Failed to connect to the database", e);
                    }
                }
            }
        }

        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to the database", e);
        }
    }

    /**
     * Constructs a HikariConfig using the provided credentials and configurable pool properties.
     * Never logs or exposes credentials.
     */
    public static HikariConfig createDefaultConfig(String url, String user, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);

        if (url != null) {
            if (url.startsWith("jdbc:postgresql:")) {
                config.setDriverClassName("org.postgresql.Driver");
            } else if (url.startsWith("jdbc:h2:")) {
                config.setDriverClassName("org.h2.Driver");
            }
        }

        int maxPoolSize = parsePositiveInt(getEnvOrProperty("DB_POOL_MAX_SIZE"), DEFAULT_MAX_POOL_SIZE);
        int minIdle = parseNonNegativeInt(getEnvOrProperty("DB_POOL_MIN_IDLE"), DEFAULT_MIN_IDLE);
        long connectionTimeout = parsePositiveLong(getEnvOrProperty("DB_POOL_CONNECTION_TIMEOUT_MS"), DEFAULT_CONNECTION_TIMEOUT_MS);
        long idleTimeout = parsePositiveLong(getEnvOrProperty("DB_POOL_IDLE_TIMEOUT_MS"), DEFAULT_IDLE_TIMEOUT_MS);
        long maxLifetime = parsePositiveLong(getEnvOrProperty("DB_POOL_MAX_LIFETIME_MS"), DEFAULT_MAX_LIFETIME_MS);
        String poolName = getEnvOrProperty("DB_POOL_NAME");
        if (poolName == null || poolName.trim().isEmpty()) {
            poolName = DEFAULT_POOL_NAME;
        }

        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(Math.min(minIdle, maxPoolSize));
        config.setConnectionTimeout(connectionTimeout);
        config.setIdleTimeout(idleTimeout);
        config.setMaxLifetime(maxLifetime);
        config.setPoolName(poolName.trim());

        final String finalPoolName = poolName.trim();
        LOGGER.info(() -> String.format(
                "Configured HikariCP pool '%s' [maxSize=%d, minIdle=%d, connTimeout=%dms, idleTimeout=%dms, maxLifetime=%dms]",
                finalPoolName, maxPoolSize, Math.min(minIdle, maxPoolSize), connectionTimeout, idleTimeout, maxLifetime));

        return config;
    }

    /**
     * Initializes the pool with a custom HikariConfig.
     */
    public static synchronized void initPool(HikariConfig config) throws DatabaseException {
        if (config == null) {
            throw new DatabaseException("HikariConfig must not be null.", null);
        }
        validateDatabaseAccess(config.getJdbcUrl());
        shutdownPool();
        dataSource = new HikariDataSource(config);
    }

    /**
     * Sets or overrides the active DataSource (useful for testing).
     */
    public static synchronized void setDataSource(HikariDataSource ds) throws DatabaseException {
        if (ds != null && ds.getJdbcUrl() != null) {
            validateDatabaseAccess(ds.getJdbcUrl());
        }
        shutdownPool();
        dataSource = ds;
    }

    /**
     * Returns the underlying DataSource, or null if not initialized.
     */
    public static DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Checks if the pool is currently initialized and open.
     */
    public static boolean isPoolInitialized() {
        HikariDataSource ds = dataSource;
        return ds != null && !ds.isClosed();
    }

    /**
     * Shuts down the connection pool cleanly, closing all physical connections.
     */
    public static synchronized void shutdownPool() {
        if (dataSource != null) {
            if (!dataSource.isClosed()) {
                LOGGER.info("Shutting down database connection pool.");
                dataSource.close();
            }
            dataSource = null;
        }
    }

    private static String getEnvOrProperty(String name) {
        if (name == null) {
            return null;
        }
        String propVal = System.getProperty(name);
        if (propVal != null) {
            String trimmed = propVal.trim();
            // Explicitly present system property takes precedence.
            // If blank, it is treated as an intentional missing value rather than falling back to env var.
            return trimmed.isEmpty() ? null : trimmed;
        }

        String envVal = envLookup.apply(name);
        if (envVal != null && !envVal.trim().isEmpty()) {
            return envVal.trim();
        }
        return null;
    }

    private static int parsePositiveInt(String val, int defaultValue) {
        if (val != null) {
            try {
                int parsed = Integer.parseInt(val.trim());
                if (parsed > 0) {
                    return parsed;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    private static int parseNonNegativeInt(String val, int defaultValue) {
        if (val != null) {
            try {
                int parsed = Integer.parseInt(val.trim());
                if (parsed >= 0) {
                    return parsed;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    private static long parsePositiveLong(String val, long defaultValue) {
        if (val != null) {
            try {
                long parsed = Long.parseLong(val.trim());
                if (parsed > 0) {
                    return parsed;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    /**
     * Validates whether a database connection to the given URL is permitted
     * under the ambient application environment and test opt-in settings.
     * Never logs or discloses credentials or sensitive URL information.
     *
     * @param url the JDBC URL to validate
     * @throws DatabaseException if access to the database is disallowed
     */
    public static void validateDatabaseAccess(String url) throws DatabaseException {
        validateDatabaseAccess(url, AppEnvironment.getEnvironment(), isLiveDbTestOptIn());
    }

    static boolean isLiveDbTestOptIn() {
        return Boolean.getBoolean("live.db.test");
    }

    /**
     * Package-private deterministic validation seam.
     *
     * @param url the JDBC URL
     * @param env the target application environment
     * @param liveDbOptIn whether live database tests are explicitly enabled
     * @throws DatabaseException if access is not permitted
     */
    static void validateDatabaseAccess(String url, EnvironmentType env, boolean liveDbOptIn) throws DatabaseException {
        if (url == null || url.trim().isEmpty()) {
            throw new DatabaseException("Database URL is missing or empty.", null);
        }

        String trimmedUrl = url.trim();

        // 1. In-memory / embedded H2 database is always safe for testing and local work
        if (isEmbeddedH2DatabaseUrl(trimmedUrl)) {
            if (env != null && env.isProduction()) {
                LOGGER.warning("In-memory or embedded database (H2) configured in PRODUCTION environment.");
            }
            return;
        }

        // 2. Non-embedded-H2 and remote databases in TEST environment require explicit opt-in
        if (env == null || env.isTest()) {
            if (!liveDbOptIn) {
                throw new DatabaseException(
                        "Live database access is blocked in TEST environment. " +
                        "Set -Dlive.db.test=true or activate the live-db Maven profile to opt in.", null);
            }
            return;
        }

        // 3. UNKNOWN environment: allow local/loopback for developer backward compatibility;
        // strictly block remote database URLs (live.db.test does not authorize remote access in UNKNOWN)
        if (env.isUnknown()) {
            if (!isLocalOrLoopbackUrl(trimmedUrl)) {
                throw new DatabaseException(
                        "Remote database access is blocked when APP_ENV is UNKNOWN. " +
                        "Set APP_ENV to LOCAL_DEV, STAGING, or PRODUCTION to connect to a remote database.", null);
            }
            return;
        }

        // 4. LOCAL_DEV, STAGING, PRODUCTION: permitted
    }

    /**
     * Checks if the JDBC URL targets an H2 database.
     *
     * @param url the JDBC URL
     * @return true if H2 URL, false otherwise
     */
    public static boolean isH2DatabaseUrl(String url) {
        if (url == null) {
            return false;
        }
        return url.trim().regionMatches(true, 0, "jdbc:h2:", 0, 8);
    }

    /**
     * Checks if the JDBC URL targets an embedded or in-memory H2 database.
     * Strictly excludes remote H2 TCP/SSL client-server connection strings.
     *
     * @param url the JDBC URL
     * @return true if embedded/in-memory H2 URL, false otherwise
     */
    public static boolean isEmbeddedH2DatabaseUrl(String url) {
        if (!isH2DatabaseUrl(url)) {
            return false;
        }
        String cleanUrl = url.trim();
        String lower = cleanUrl.toLowerCase(Locale.ROOT);

        // Explicitly block remote client-server protocols (tcp, ssl, or protocol-relative //)
        if (lower.startsWith("jdbc:h2:tcp:") || lower.startsWith("jdbc:h2:ssl:") || lower.startsWith("jdbc:h2://")) {
            return false;
        }

        // Must match legitimate embedded / in-memory H2 forms
        return lower.startsWith("jdbc:h2:mem:")
                || lower.startsWith("jdbc:h2:mem;")
                || lower.startsWith("jdbc:h2:file:")
                || lower.startsWith("jdbc:h2:file;")
                || lower.startsWith("jdbc:h2:~/")
                || lower.startsWith("jdbc:h2:./")
                || lower.startsWith("jdbc:h2:/")
                || (lower.length() >= 11 && lower.charAt(8) >= 'a' && lower.charAt(8) <= 'z' && lower.charAt(9) == ':' && (lower.charAt(10) == '/' || lower.charAt(10) == '\\'));
    }

    /**
     * Checks if the JDBC URL targets localhost, loopback, or local in-memory storage.
     * Never exposes URL content in errors or logs.
     *
     * @param url the JDBC URL
     * @return true if target is local/loopback, false otherwise
     */
    public static boolean isLocalOrLoopbackUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        String cleanUrl = url.trim();

        if (isEmbeddedH2DatabaseUrl(cleanUrl)) {
            return true;
        }

        int doubleSlashIdx = cleanUrl.indexOf("//");
        if (doubleSlashIdx == -1) {
            // E.g. jdbc:postgresql:dbname (no host specified, defaults to local)
            return cleanUrl.regionMatches(true, 0, "jdbc:postgresql:", 0, 16);
        }

        int authorityStart = doubleSlashIdx + 2;
        int authorityEnd = cleanUrl.length();
        for (int i = authorityStart; i < cleanUrl.length(); i++) {
            char c = cleanUrl.charAt(i);
            if (c == '/' || c == '?' || c == '#') {
                authorityEnd = i;
                break;
            }
        }

        String authority = cleanUrl.substring(authorityStart, authorityEnd).trim();
        if (authority.isEmpty()) {
            return true;
        }

        // Split multiple comma-separated hosts in case of multi-host cluster configuration
        String[] hostPortPairs = authority.split(",");
        for (String pair : hostPortPairs) {
            String host = extractHostFromHostPort(pair.trim());
            if (!isLoopbackOrLocalHost(host)) {
                return false;
            }
        }
        return true;
    }

    private static String extractHostFromHostPort(String hostPort) {
        if (hostPort == null || hostPort.isEmpty()) {
            return "";
        }
        // Strip user info if present (e.g. user:pass@host:port)
        int atIdx = hostPort.lastIndexOf('@');
        if (atIdx != -1) {
            hostPort = hostPort.substring(atIdx + 1);
        }
        // Handle IPv6 literal format like [::1]:5432 or [::1]
        if (hostPort.startsWith("[")) {
            int closingBracket = hostPort.indexOf(']');
            if (closingBracket != -1) {
                return hostPort.substring(1, closingBracket);
            }
        }
        // Strip port if present
        int colonIdx = hostPort.indexOf(':');
        if (colonIdx != -1) {
            return hostPort.substring(0, colonIdx);
        }
        return hostPort;
    }

    private static boolean isLoopbackOrLocalHost(String host) {
        if (host == null || host.isEmpty()) {
            return true;
        }
        String normalized = host.toLowerCase(Locale.ROOT);
        return normalized.equals("localhost")
                || normalized.equals("127.0.0.1")
                || normalized.equals("::1")
                || normalized.equals("0:0:0:0:0:0:0:1")
                || normalized.equals("0.0.0.0");
    }
}
