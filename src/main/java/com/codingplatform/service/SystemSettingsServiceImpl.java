package com.codingplatform.service;

import com.codingplatform.dao.SystemSettingsDAO;
import com.codingplatform.dao.UserDAO;
import com.codingplatform.model.SystemSetting;
import com.codingplatform.model.User;

import java.sql.SQLException;
import java.util.List;

public class SystemSettingsServiceImpl implements SystemSettingsService {

    private final SystemSettingsDAO systemSettingsDAO;
    private final UserDAO userDAO;

    public SystemSettingsServiceImpl(SystemSettingsDAO systemSettingsDAO, UserDAO userDAO) {
        this.systemSettingsDAO = systemSettingsDAO;
        this.userDAO = userDAO;
    }

    private void verifyAdminAccess(int adminId, String role) {
        if (!"ADMIN".equals(role)) {
            throw new SecurityException("Only administrators can access system settings.");
        }
        try {
            User user = userDAO.getUserById(adminId);
            if (user == null || !"ADMIN".equals(user.getRole())) {
                throw new SecurityException("Invalid administrator account.");
            }
        } catch (com.codingplatform.exception.DatabaseException e) {
            throw new RuntimeException("Database error while verifying admin access", e);
        }
    }

    @Override
    public List<SystemSetting> getAllSettings(int adminId, String role) {
        verifyAdminAccess(adminId, role);
        try {
            return systemSettingsDAO.getAllSettings();
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving system settings", e);
        }
    }

    @Override
    public void updateSetting(int adminId, String role, String key, String value) {
        verifyAdminAccess(adminId, role);
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Setting key cannot be null or empty.");
        }
        
        try {
            SystemSetting existing = systemSettingsDAO.getSettingByKey(key.trim());
            if (existing == null) {
                throw new IllegalArgumentException("Setting key '" + key + "' does not exist.");
            }
            
            systemSettingsDAO.updateSetting(key.trim(), value, adminId);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating system setting", e);
        }
    }
}
