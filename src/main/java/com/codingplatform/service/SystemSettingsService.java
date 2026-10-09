package com.codingplatform.service;

import com.codingplatform.model.SystemSetting;
import java.util.List;

public interface SystemSettingsService {
    List<SystemSetting> getAllSettings(int adminId, String role);
    void updateSetting(int adminId, String role, String key, String value);
}
