package com.codingplatform.dao;

import com.codingplatform.model.SystemSetting;
import java.sql.SQLException;
import java.util.List;

public interface SystemSettingsDAO {
    List<SystemSetting> getAllSettings() throws SQLException;
    SystemSetting getSettingByKey(String key) throws SQLException;
    void updateSetting(String key, String value, Integer updatedBy) throws SQLException;
    void createSetting(SystemSetting setting) throws SQLException;
}
