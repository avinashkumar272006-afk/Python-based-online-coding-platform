package com.codingplatform.daoimpl;

import com.codingplatform.dao.SystemSettingsDAO;
import com.codingplatform.model.SystemSetting;
import com.codingplatform.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SystemSettingsDAOImpl implements SystemSettingsDAO {

    @Override
    public List<SystemSetting> getAllSettings() throws SQLException {
        List<SystemSetting> settings = new ArrayList<>();
        String sql = "SELECT s.*, u.name as updated_by_name FROM system_settings s LEFT JOIN users u ON s.updated_by = u.id ORDER BY s.setting_key ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                SystemSetting setting = extractSetting(rs);
                setting.setUpdatedByName(rs.getString("updated_by_name"));
                settings.add(setting);
            }
        } catch (com.codingplatform.exception.DatabaseException e) {
            throw new SQLException("Database connection error", e);
        }
        return settings;
    }

    @Override
    public SystemSetting getSettingByKey(String key) throws SQLException {
        String sql = "SELECT s.*, u.name as updated_by_name FROM system_settings s LEFT JOIN users u ON s.updated_by = u.id WHERE s.setting_key = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, key);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    SystemSetting setting = extractSetting(rs);
                    setting.setUpdatedByName(rs.getString("updated_by_name"));
                    return setting;
                }
            }
        } catch (com.codingplatform.exception.DatabaseException e) {
            throw new SQLException("Database connection error", e);
        }
        return null;
    }

    @Override
    public void updateSetting(String key, String value, Integer updatedBy) throws SQLException {
        String sql = "UPDATE system_settings SET setting_value = ?, updated_by = ?, updated_at = CURRENT_TIMESTAMP WHERE setting_key = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            if (updatedBy != null) {
                stmt.setInt(2, updatedBy);
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            stmt.setString(3, key);
            stmt.executeUpdate();
        } catch (com.codingplatform.exception.DatabaseException e) {
            throw new SQLException("Database connection error", e);
        }
    }

    @Override
    public void createSetting(SystemSetting setting) throws SQLException {
        String sql = "INSERT INTO system_settings (setting_key, setting_value, description, updated_by) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, setting.getSettingKey());
            stmt.setString(2, setting.getSettingValue());
            stmt.setString(3, setting.getDescription());
            if (setting.getUpdatedBy() != null) {
                stmt.setInt(4, setting.getUpdatedBy());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            stmt.executeUpdate();
        } catch (com.codingplatform.exception.DatabaseException e) {
            throw new SQLException("Database connection error", e);
        }
    }

    private SystemSetting extractSetting(ResultSet rs) throws SQLException {
        SystemSetting setting = new SystemSetting();
        setting.setId(rs.getLong("id"));
        setting.setSettingKey(rs.getString("setting_key"));
        setting.setSettingValue(rs.getString("setting_value"));
        setting.setDescription(rs.getString("description"));
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            setting.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        int updatedBy = rs.getInt("updated_by");
        if (!rs.wasNull()) {
            setting.setUpdatedBy(updatedBy);
        }
        return setting;
    }
}
