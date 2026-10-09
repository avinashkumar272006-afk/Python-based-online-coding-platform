package com.codingplatform.service;

import com.codingplatform.dao.AdminDAO;
import com.codingplatform.dao.UserDAO;
import com.codingplatform.model.Admin;
import com.codingplatform.model.Student;
import com.codingplatform.model.AdminDashboardStats;
import com.codingplatform.model.Instructor;
import com.codingplatform.model.User;
import com.codingplatform.exception.DatabaseException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AdminServiceImplTest {

    private AdminDAO adminDAO;
    private UserDAO userDAO;
    private com.codingplatform.dao.SubmissionDAO submissionDAO;
    private AdminServiceImpl adminService;

    @BeforeEach
    public void setUp() {
        adminDAO = mock(AdminDAO.class);
        userDAO = mock(UserDAO.class);
        submissionDAO = mock(com.codingplatform.dao.SubmissionDAO.class);
        adminService = new AdminServiceImpl(adminDAO, userDAO, submissionDAO);
    }

    @Test
    public void testGetDashboardStats_AdminUser() throws Exception {
        Admin admin = new Admin();
        admin.setId(1);
        admin.setRole("ADMIN");
        when(userDAO.getUserById(1)).thenReturn(admin);
        
        AdminDashboardStats mockStats = new AdminDashboardStats();
        mockStats.setTotalUsers(10);
        when(adminDAO.getDashboardStats()).thenReturn(mockStats);
        
        when(userDAO.getRecentUsers(5)).thenReturn(java.util.Collections.emptyList());
        when(submissionDAO.getRecentSubmissions(5)).thenReturn(java.util.Collections.emptyList());
        
        AdminDashboardStats result = adminService.getDashboardStats(1);
        assertNotNull(result);
        assertEquals(10, result.getTotalUsers());
    }

    @Test
    public void testGetDashboardStats_NonAdminUser() throws Exception {
        Student student = new Student();
        student.setId(2);
        student.setRole("STUDENT");
        when(userDAO.getUserById(2)).thenReturn(student);
        
        assertThrows(IllegalArgumentException.class, () -> adminService.getDashboardStats(2));
    }

    @Test
    public void testUpdateUserRole_Valid() throws Exception {
        Admin admin = new Admin();
        admin.setId(1);
        admin.setRole("ADMIN");
        when(userDAO.getUserById(1)).thenReturn(admin);
        
        Student targetUser = new Student();
        targetUser.setId(3);
        targetUser.setRole("STUDENT");
        when(userDAO.getUserById(3)).thenReturn(targetUser);
        
        when(userDAO.updateUserRole(3, "INSTRUCTOR")).thenReturn(true);
        
        boolean result = adminService.updateUserRole(1, 3, "INSTRUCTOR");
        assertTrue(result);
        verify(userDAO).updateUserRole(3, "INSTRUCTOR");
    }

    @Test
    public void testUpdateUserRole_AdminProtected() throws Exception {
        Admin admin = new Admin();
        admin.setId(1);
        admin.setRole("ADMIN");
        when(userDAO.getUserById(1)).thenReturn(admin);
        
        Admin targetAdmin = new Admin();
        targetAdmin.setId(2);
        targetAdmin.setRole("ADMIN");
        when(userDAO.getUserById(2)).thenReturn(targetAdmin);
        
        when(userDAO.countAdmins()).thenReturn(2);
        
        assertThrows(IllegalArgumentException.class, () -> adminService.updateUserRole(1, 2, "STUDENT"));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        Admin admin = new Admin();
        admin.setId(1);
        admin.setRole("ADMIN");
        when(userDAO.getUserById(1)).thenReturn(admin);
        
        List<User> mockUsers = Arrays.asList(admin, new Student());
        when(userDAO.getAllUsers()).thenReturn(mockUsers);
        
        List<User> result = adminService.getAllUsers(1);
        assertEquals(2, result.size());
        verify(userDAO).getAllUsers();
    }

    @Test
    public void testUpdateUserRole_InstructorToStudent() throws Exception {
        Admin admin = new Admin();
        admin.setId(1);
        admin.setRole("ADMIN");
        when(userDAO.getUserById(1)).thenReturn(admin);
        
        Instructor targetUser = new Instructor();
        targetUser.setId(4);
        targetUser.setRole("INSTRUCTOR");
        when(userDAO.getUserById(4)).thenReturn(targetUser);
        
        when(userDAO.updateUserRole(4, "STUDENT")).thenReturn(true);
        
        boolean result = adminService.updateUserRole(1, 4, "STUDENT");
        assertTrue(result);
        verify(userDAO).updateUserRole(4, "STUDENT");
    }

    @Test
    public void testUpdateUserRole_InvalidRole() throws Exception {
        Admin admin = new Admin();
        admin.setId(1);
        admin.setRole("ADMIN");
        when(userDAO.getUserById(1)).thenReturn(admin);
        
        assertThrows(IllegalArgumentException.class, () -> adminService.updateUserRole(1, 3, "GUEST"));
    }

    @Test
    public void testUpdateUserRole_NonExistingUser() throws Exception {
        Admin admin = new Admin();
        admin.setId(1);
        admin.setRole("ADMIN");
        when(userDAO.getUserById(1)).thenReturn(admin);
        
        when(userDAO.getUserById(99)).thenReturn(null);
        
        assertThrows(IllegalArgumentException.class, () -> adminService.updateUserRole(1, 99, "STUDENT"));
    }
}
