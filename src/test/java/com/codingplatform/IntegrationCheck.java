package com.codingplatform;

import com.codingplatform.util.DBConnection;
import org.junit.jupiter.api.Test;
import java.sql.*;

public class IntegrationCheck {
    @Test
    public void testDb() throws Exception {
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM system_settings")) {
            System.out.println("--- DB CONTENTS ---");
            while (rs.next()) {
                System.out.println("!!! KEY: " + rs.getString("setting_key") + " | VALUE: " + rs.getString("setting_value") + " | BY: " + rs.getInt("updated_by"));
            }
            System.out.println("-------------------");
        }
    }
}
