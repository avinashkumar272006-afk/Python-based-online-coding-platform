package com.codingplatform.listener;

import com.codingplatform.util.DBConnection;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.logging.Logger;

/**
 * Application lifecycle listener that manages database connection pool cleanup.
 * Shuts down the HikariCP pool cleanly when the servlet context is destroyed
 * upon application redeployment or container shutdown.
 */
@WebListener
public class DatabaseContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(DatabaseContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("DatabaseContextListener: ServletContext initialized.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("DatabaseContextListener: ServletContext destroyed. Shutting down connection pool.");
        DBConnection.shutdownPool();
    }
}
