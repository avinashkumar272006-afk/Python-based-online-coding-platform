package com.codingplatform.dao;

import com.codingplatform.model.Exercise;
import com.codingplatform.exception.DatabaseException;
import java.util.List;

public interface ExerciseDAO {
    void createExercise(Exercise exercise) throws DatabaseException;
    Exercise getExerciseById(long id) throws DatabaseException;
    List<Exercise> getAllExercises() throws DatabaseException;
    List<Exercise> getApprovedExercises() throws DatabaseException;
    List<Exercise> getPendingExercises() throws DatabaseException;
    List<Exercise> getExercisesByInstructor(long instructorId) throws DatabaseException;
    void updateExercise(Exercise exercise) throws DatabaseException;
    void updateExerciseStatus(long id, String status) throws DatabaseException;
    void deleteExercise(long id) throws DatabaseException;
}
