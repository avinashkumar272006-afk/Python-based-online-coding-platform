package com.codingplatform.service;

import com.codingplatform.dao.ExerciseDAO;
import com.codingplatform.dao.UserDAO;
import com.codingplatform.exception.DatabaseException;
import com.codingplatform.model.Exercise;
import com.codingplatform.model.User;
import com.codingplatform.model.Instructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExerciseServiceTest {

    @Mock
    private ExerciseDAO exerciseDAO;
    
    @Mock
    private UserDAO userDAO;

    private ExerciseService exerciseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        exerciseService = new ExerciseServiceImpl(exerciseDAO, userDAO);
    }

    // 1. Exercise creation
    @Test
    void testCreateExerciseSuccess() throws DatabaseException {
        Exercise ex = new Exercise(0, "Title", "Desc", "EASY", "Topic", "code", "out", 1, null, "PENDING");
        
        Instructor mockInstructor = new Instructor();
        mockInstructor.setId(1);
        mockInstructor.setRole("INSTRUCTOR");
        when(userDAO.getUserById(1)).thenReturn(mockInstructor);
        
        exerciseService.createExercise(ex);
        verify(exerciseDAO, times(1)).createExercise(ex);
    }

    // 2. Missing title validation
    @Test
    void testCreateExerciseMissingTitle() {
        Exercise ex = new Exercise(0, "", "Desc", "EASY", "Topic", "code", "out", 1, null, "PENDING");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> exerciseService.createExercise(ex));
        assertTrue(thrown.getMessage().contains("Title is required"));
    }

    // 3. Missing description validation
    @Test
    void testCreateExerciseMissingDescription() {
        Exercise ex = new Exercise(0, "Title", null, "EASY", "Topic", "code", "out", 1, null, "PENDING");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> exerciseService.createExercise(ex));
        assertTrue(thrown.getMessage().contains("Description is required"));
    }

    // 4. Invalid difficulty
    @Test
    void testCreateExerciseInvalidDifficulty() {
        Exercise ex = new Exercise(0, "Title", "Desc", "IMPOSSIBLE", "Topic", "code", "out", 1, null, "PENDING");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> exerciseService.createExercise(ex));
        assertTrue(thrown.getMessage().contains("Valid difficulty is required"));
    }
    
    @Test
    void testCreateExerciseCreatorNotInstructor() throws DatabaseException {
        Exercise ex = new Exercise(0, "Title", "Desc", "EASY", "Topic", "code", "out", 1, null, "PENDING");
        
        User mockStudent = new com.codingplatform.model.Student();
        mockStudent.setId(1);
        mockStudent.setRole("STUDENT");
        when(userDAO.getUserById(1)).thenReturn(mockStudent);
        
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> exerciseService.createExercise(ex));
        assertTrue(thrown.getMessage().contains("Instructor must exist and be an instructor"));
    }

    // 5. Getting exercise by ID
    @Test
    void testGetExerciseById() throws DatabaseException {
        Exercise mockEx = new Exercise(10, "T", "D", "EASY", "T", "S", "O", 1, null, "PENDING");
        when(exerciseDAO.getExerciseById(10)).thenReturn(mockEx);
        
        Exercise result = exerciseService.getExerciseById(10);
        assertNotNull(result);
        assertEquals(10, result.getId());
    }

    // 6. Listing exercises
    @Test
    void testGetAllExercises() throws DatabaseException {
        Exercise mockEx = new Exercise(10, "T", "D", "EASY", "T", "S", "O", 1, null, "PENDING");
        when(exerciseDAO.getAllExercises()).thenReturn(Arrays.asList(mockEx));
        
        List<Exercise> result = exerciseService.getAllExercises();
        assertEquals(1, result.size());
    }

    // 7. Instructor ownership validation (Update)
    @Test
    void testUpdateExerciseInstructorOwnershipValidation() throws DatabaseException {
        Exercise existing = new Exercise(10, "Old Title", "Old Desc", "EASY", "T", "S", "O", 1, null, "PENDING"); // owned by 1
        when(exerciseDAO.getExerciseById(10)).thenReturn(existing);
        
        Exercise updateData = new Exercise(10, "New Title", "New Desc", "EASY", "T", "S", "O", 2, null, "PENDING"); // try to update by instructor 2
        
        SecurityException thrown = assertThrows(SecurityException.class, () -> exerciseService.updateExercise(updateData, 2));
        assertTrue(thrown.getMessage().contains("permission"));
    }
    
    // 7. Instructor ownership validation (Delete)
    @Test
    void testDeleteExerciseInstructorOwnershipValidation() throws DatabaseException {
        Exercise existing = new Exercise(10, "T", "D", "EASY", "T", "S", "O", 1, null, "PENDING"); // owned by 1
        when(exerciseDAO.getExerciseById(10)).thenReturn(existing);
        
        SecurityException thrown = assertThrows(SecurityException.class, () -> exerciseService.deleteExercise(10, 2));
        assertTrue(thrown.getMessage().contains("permission"));
    }

    // 10. Exercise update
    @Test
    void testUpdateExerciseSuccess() throws DatabaseException {
        Exercise existing = new Exercise(10, "Old Title", "Old Desc", "EASY", "T", "S", "O", 1, null, "PENDING"); // owned by 1
        when(exerciseDAO.getExerciseById(10)).thenReturn(existing);
        
        Exercise updateData = new Exercise(10, "New Title", "New Desc", "EASY", "T", "S", "O", 1, null, "PENDING");
        exerciseService.updateExercise(updateData, 1);
        
        verify(exerciseDAO, times(1)).updateExercise(updateData);
    }

    // 11. Exercise deletion
    @Test
    void testDeleteExerciseSuccess() throws DatabaseException {
        Exercise existing = new Exercise(10, "T", "D", "EASY", "T", "S", "O", 1, null, "PENDING"); // owned by 1
        when(exerciseDAO.getExerciseById(10)).thenReturn(existing);
        
        exerciseService.deleteExercise(10, 1);
        verify(exerciseDAO, times(1)).deleteExercise(10);
    }

    @Test
    void testApproveExerciseSuccess() throws DatabaseException {
        User mockAdmin = new com.codingplatform.model.Admin();
        mockAdmin.setId(5);
        mockAdmin.setRole("ADMIN");
        when(userDAO.getUserById(5)).thenReturn(mockAdmin);
        
        exerciseService.approveExercise(10, 5);
        verify(exerciseDAO, times(1)).updateExerciseStatus(10, "APPROVED");
    }

    @Test
    void testRejectExerciseSuccess() throws DatabaseException {
        User mockAdmin = new com.codingplatform.model.Admin();
        mockAdmin.setId(5);
        mockAdmin.setRole("ADMIN");
        when(userDAO.getUserById(5)).thenReturn(mockAdmin);
        
        exerciseService.rejectExercise(10, 5);
        verify(exerciseDAO, times(1)).updateExerciseStatus(10, "REJECTED");
    }

    @Test
    void testApproveExerciseNonAdmin() throws DatabaseException {
        User mockStudent = new com.codingplatform.model.Student();
        mockStudent.setId(5);
        mockStudent.setRole("STUDENT");
        when(userDAO.getUserById(5)).thenReturn(mockStudent);
        
        SecurityException thrown = assertThrows(SecurityException.class, () -> exerciseService.approveExercise(10, 5));
        assertTrue(thrown.getMessage().contains("Only administrators"));
    }
}
