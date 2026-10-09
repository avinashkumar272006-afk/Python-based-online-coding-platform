package com.codingplatform.service;

import com.codingplatform.dao.ProgressDAO;
import com.codingplatform.dao.UserDAO;
import com.codingplatform.model.StudentProgress;
import com.codingplatform.model.User;
import com.codingplatform.exception.DatabaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProgressServiceImplTest {

    private ProgressServiceImpl progressService;

    @Mock
    private ProgressDAO progressDAO;

    @Mock
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        progressService = new ProgressServiceImpl(progressDAO, userDAO);
    }

    @Test
    public void testGetStudentProgressSuccess() throws DatabaseException {
        User user = new com.codingplatform.model.Student();
        user.setId(1);
        user.setRole("STUDENT");
        
        when(userDAO.getUserById(1)).thenReturn(user);
        when(progressDAO.getStudentProgress(1)).thenReturn(new StudentProgress(10, 5, 2, 3, 85.0));
        
        StudentProgress progress = progressService.getStudentProgress(1);
        assertNotNull(progress);
        assertEquals(10, progress.getTotalExercises());
        assertEquals(5, progress.getSubmittedExercises());
    }

    @Test
    public void testGetStudentProgressUserNotFound() throws DatabaseException {
        when(userDAO.getUserById(1)).thenReturn(null);
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            progressService.getStudentProgress(1);
        });
        assertEquals("User not found.", ex.getMessage());
    }

    @Test
    public void testGetStudentProgressNotStudent() throws DatabaseException {
        User user = new com.codingplatform.model.Student();
        user.setId(1);
        user.setRole("INSTRUCTOR");
        
        when(userDAO.getUserById(1)).thenReturn(user);
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            progressService.getStudentProgress(1);
        });
        assertEquals("User is not a student.", ex.getMessage());
    }
}
