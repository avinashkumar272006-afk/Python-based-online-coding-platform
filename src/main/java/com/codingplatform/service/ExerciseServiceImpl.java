package com.codingplatform.service;

import com.codingplatform.dao.ExerciseDAO;
import com.codingplatform.model.Exercise;
import com.codingplatform.exception.DatabaseException;
import java.util.List;

public class ExerciseServiceImpl implements ExerciseService {

    private ExerciseDAO exerciseDAO;
    private com.codingplatform.dao.UserDAO userDAO;

    public ExerciseServiceImpl(ExerciseDAO exerciseDAO) {
        this.exerciseDAO = exerciseDAO;
    }

    public ExerciseServiceImpl(ExerciseDAO exerciseDAO, com.codingplatform.dao.UserDAO userDAO) {
        this.exerciseDAO = exerciseDAO;
        this.userDAO = userDAO;
    }

    @Override
    public void createExercise(Exercise exercise) throws DatabaseException, IllegalArgumentException {
        validateExercise(exercise);
        
        if (exercise.getCreatedBy() <= 0) {
            throw new IllegalArgumentException("Invalid creator ID.");
        }
        
        if (userDAO != null) {
            com.codingplatform.model.User user = userDAO.getUserById((int) exercise.getCreatedBy());
            if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
                throw new IllegalArgumentException("Instructor must exist and be an instructor.");
            }
        }
        
        exerciseDAO.createExercise(exercise);
    }

    @Override
    public Exercise getExerciseById(long id) throws DatabaseException {
        return exerciseDAO.getExerciseById(id);
    }

    @Override
    public List<Exercise> getAllExercises() throws DatabaseException {
        return exerciseDAO.getAllExercises();
    }

    @Override
    public List<Exercise> getApprovedExercises() throws DatabaseException {
        return exerciseDAO.getApprovedExercises();
    }

    @Override
    public List<Exercise> getPendingExercises() throws DatabaseException {
        return exerciseDAO.getPendingExercises();
    }

    @Override
    public List<Exercise> getExercisesByInstructor(long instructorId) throws DatabaseException {
        return exerciseDAO.getExercisesByInstructor(instructorId);
    }

    @Override
    public void updateExercise(Exercise exercise, long instructorId) throws DatabaseException, IllegalArgumentException, SecurityException {
        validateExercise(exercise);
        
        Exercise existing = exerciseDAO.getExerciseById(exercise.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Exercise not found.");
        }
        
        if (existing.getCreatedBy() != instructorId) {
            throw new SecurityException("Instructor does not have permission to update this exercise.");
        }
        
        exerciseDAO.updateExercise(exercise);
    }

    @Override
    public void deleteExercise(long id, long instructorId) throws DatabaseException, SecurityException {
        Exercise existing = exerciseDAO.getExerciseById(id);
        if (existing != null) {
            if (existing.getCreatedBy() != instructorId) {
                throw new SecurityException("Instructor does not have permission to delete this exercise.");
            }
            exerciseDAO.deleteExercise(id);
        }
    }

    @Override
    public void approveExercise(long id, long adminId) throws DatabaseException, SecurityException, IllegalArgumentException, IllegalStateException {
        verifyAdmin(adminId);
        Exercise existing = exerciseDAO.getExerciseById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Exercise not found.");
        }
        if (!"PENDING".equalsIgnoreCase(existing.getStatus())) {
            throw new IllegalStateException("Only PENDING exercises can be moderated. Current status: " + existing.getStatus());
        }
        boolean updated = exerciseDAO.updateExerciseStatusIfPending(id, "APPROVED");
        if (!updated) {
            Exercise latest = exerciseDAO.getExerciseById(id);
            if (latest == null) {
                throw new IllegalArgumentException("Exercise not found.");
            }
            throw new IllegalStateException("Exercise is no longer PENDING (current status: " + latest.getStatus() + ")");
        }
    }

    @Override
    public void rejectExercise(long id, long adminId) throws DatabaseException, SecurityException, IllegalArgumentException, IllegalStateException {
        verifyAdmin(adminId);
        Exercise existing = exerciseDAO.getExerciseById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Exercise not found.");
        }
        if (!"PENDING".equalsIgnoreCase(existing.getStatus())) {
            throw new IllegalStateException("Only PENDING exercises can be moderated. Current status: " + existing.getStatus());
        }
        boolean updated = exerciseDAO.updateExerciseStatusIfPending(id, "REJECTED");
        if (!updated) {
            Exercise latest = exerciseDAO.getExerciseById(id);
            if (latest == null) {
                throw new IllegalArgumentException("Exercise not found.");
            }
            throw new IllegalStateException("Exercise is no longer PENDING (current status: " + latest.getStatus() + ")");
        }
    }

    private void verifyAdmin(long adminId) throws SecurityException {
        if (userDAO != null) {
            try {
                com.codingplatform.model.User user = userDAO.getUserById((int) adminId);
                if (user == null || !"ADMIN".equals(user.getRole())) {
                    throw new SecurityException("Only administrators can perform this action.");
                }
            } catch (DatabaseException e) {
                throw new SecurityException("Failed to verify admin privileges.");
            }
        }
    }
    
    private void validateExercise(Exercise exercise) {
        if (exercise.getTitle() == null || exercise.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required.");
        }
        if (exercise.getDescription() == null || exercise.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required.");
        }
        if (exercise.getDifficulty() == null || 
            (!exercise.getDifficulty().equals("EASY") && 
             !exercise.getDifficulty().equals("MEDIUM") && 
             !exercise.getDifficulty().equals("HARD"))) {
            throw new IllegalArgumentException("Valid difficulty is required (EASY, MEDIUM, HARD).");
        }
    }
}
