package com.codingplatform.listener;

import com.codingplatform.config.AppEnvironment;
import com.codingplatform.config.EnvironmentType;
import com.codingplatform.util.DBConnection;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.logging.Logger;

/**
 * Application lifecycle listener that manages database connection pool cleanup
 * and logs application environment initialization.
 * Shuts down the HikariCP pool cleanly when the servlet context is destroyed
 * upon application redeployment or container shutdown.
 */
@WebListener
public class DatabaseContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(DatabaseContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        EnvironmentType env = AppEnvironment.getEnvironment();
        if (env.isUnknown()) {
            LOGGER.warning(() -> "Application initialized with UNKNOWN environment (APP_ENV is missing, blank, or invalid).");
        } else {
            LOGGER.info(() -> "Application initialized in " + env.getCanonicalName() + " environment.");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("DatabaseContextListener: ServletContext destroyed. Shutting down connection pool.");
        DBConnection.shutdownPool();
    }
}
