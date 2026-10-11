package com.codingplatform.util;

import com.codingplatform.exception.DatabaseException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
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
    public static synchronized void initPool(HikariConfig config) {
        shutdownPool();
        dataSource = new HikariDataSource(config);
    }

    /**
     * Sets or overrides the active DataSource (useful for testing).
     */
    public static synchronized void setDataSource(HikariDataSource ds) {
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
}
