package com.codingplatform.service;

import com.codingplatform.dao.SystemSettingsDAO;
import com.codingplatform.dao.UserDAO;
import com.codingplatform.model.SystemSetting;
import com.codingplatform.model.User;
import com.codingplatform.model.Admin;
import com.codingplatform.model.Student;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SystemSettingsServiceTest {

    @Mock
    private SystemSettingsDAO systemSettingsDAO;

    @Mock
    private UserDAO userDAO;

    private SystemSettingsService settingsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        settingsService = new SystemSettingsServiceImpl(systemSettingsDAO, userDAO);
    }

    @Test
    void getAllSettings_AsAdmin_ReturnsSettings() throws Exception {
        User admin = new Admin();
        admin.setId(1);
        admin.setRole("ADMIN");
        when(userDAO.getUserById(1)).thenReturn(admin);

        List<SystemSetting> mockSettings = Arrays.asList(new SystemSetting("key1", "val1", "desc1"));
        when(systemSettingsDAO.getAllSettings()).thenReturn(mockSettings);

        List<SystemSetting> result = settingsService.getAllSettings(1, "ADMIN");
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("key1", result.get(0).getSettingKey());
    }

    @Test
    void getAllSettings_AsStudent_ThrowsSecurityException() throws Exception {
        assertThrows(SecurityException.class, () -> {
            settingsService.getAllSettings(2, "STUDENT");
        });
    }

    @Test
    void updateSetting_AsAdmin_Success() throws Exception {
        User admin = new Admin();
        admin.setId(1);
        admin.setRole("ADMIN");
        when(userDAO.getUserById(1)).thenReturn(admin);

        SystemSetting mockSetting = new SystemSetting("key1", "oldVal", "desc");
        when(systemSettingsDAO.getSettingByKey("key1")).thenReturn(mockSetting);

        settingsService.updateSetting(1, "ADMIN", "key1", "newVal");

        verify(systemSettingsDAO).updateSetting("key1", "newVal", 1);
    }

    @Test
    void updateSetting_InvalidKey_ThrowsException() throws Exception {
        User admin = new Admin();
        admin.setId(1);
        admin.setRole("ADMIN");
        when(userDAO.getUserById(1)).thenReturn(admin);

        assertThrows(IllegalArgumentException.class, () -> {
            settingsService.updateSetting(1, "ADMIN", "", "newVal");
        });
    }

    @Test
    void updateSetting_NonExistentKey_ThrowsException() throws Exception {
        User admin = new Admin();
        admin.setId(1);
        admin.setRole("ADMIN");
        when(userDAO.getUserById(1)).thenReturn(admin);

        when(systemSettingsDAO.getSettingByKey("missing")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            settingsService.updateSetting(1, "ADMIN", "missing", "newVal");
        });
    }
}
