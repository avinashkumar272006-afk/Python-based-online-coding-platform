package com.codingplatform.service;

import com.codingplatform.model.Exercise;
import com.codingplatform.exception.DatabaseException;
import java.util.List;

public interface ExerciseService {
    void createExercise(Exercise exercise) throws DatabaseException, IllegalArgumentException;
    Exercise getExerciseById(long id) throws DatabaseException;
    List<Exercise> getAllExercises() throws DatabaseException;
    List<Exercise> getApprovedExercises() throws DatabaseException;
    List<Exercise> getPendingExercises() throws DatabaseException;
    List<Exercise> getExercisesByInstructor(long instructorId) throws DatabaseException;
    void updateExercise(Exercise exercise, long instructorId) throws DatabaseException, IllegalArgumentException, SecurityException;
    void approveExercise(long id, long adminId) throws DatabaseException, SecurityException;
    void rejectExercise(long id, long adminId) throws DatabaseException, SecurityException;
    void deleteExercise(long id, long instructorId) throws DatabaseException, SecurityException;
}
