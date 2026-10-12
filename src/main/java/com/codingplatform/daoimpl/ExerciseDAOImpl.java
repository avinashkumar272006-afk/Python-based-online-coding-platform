package com.codingplatform.daoimpl;

import com.codingplatform.dao.ExerciseDAO;
import com.codingplatform.model.Exercise;
import com.codingplatform.exception.DatabaseException;
import com.codingplatform.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExerciseDAOImpl implements ExerciseDAO {

    @Override
    public void createExercise(Exercise exercise) throws DatabaseException {
        String sql = "INSERT INTO exercises (title, description, difficulty, topic, starter_code, expected_output, created_by) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, exercise.getTitle());
            pstmt.setString(2, exercise.getDescription());
            pstmt.setString(3, exercise.getDifficulty());
            pstmt.setString(4, exercise.getTopic());
            pstmt.setString(5, exercise.getStarterCode());
            pstmt.setString(6, exercise.getExpectedOutput());
            pstmt.setLong(7, exercise.getCreatedBy());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Creating exercise failed, no rows affected.", null);
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    exercise.setId(generatedKeys.getLong(1));
                } else {
                    throw new DatabaseException("Creating exercise failed, no ID obtained.", null);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error creating exercise", e);
        }
    }

    @Override
    public Exercise getExerciseById(long id) throws DatabaseException {
        String sql = "SELECT * FROM exercises WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToExercise(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting exercise by id", e);
        }
        return null;
    }

    @Override
    public List<Exercise> getAllExercises() throws DatabaseException {
        String sql = "SELECT * FROM exercises ORDER BY created_at DESC";
        List<Exercise> exercises = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                exercises.add(mapRowToExercise(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting all exercises", e);
        }
        return exercises;
    }

    @Override
    public List<Exercise> getApprovedExercises() throws DatabaseException {
        String sql = "SELECT * FROM exercises WHERE status = 'APPROVED' ORDER BY created_at DESC";
        List<Exercise> exercises = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                exercises.add(mapRowToExercise(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting approved exercises", e);
        }
        return exercises;
    }

    @Override
    public List<Exercise> getPendingExercises() throws DatabaseException {
        String sql = "SELECT * FROM exercises WHERE status = 'PENDING' ORDER BY created_at DESC";
        List<Exercise> exercises = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                exercises.add(mapRowToExercise(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting pending exercises", e);
        }
        return exercises;
    }

    @Override
    public List<Exercise> getExercisesByInstructor(long instructorId) throws DatabaseException {
        String sql = "SELECT * FROM exercises WHERE created_by = ? ORDER BY created_at DESC";
        List<Exercise> exercises = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, instructorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    exercises.add(mapRowToExercise(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting exercises by instructor", e);
        }
        return exercises;
    }

    @Override
    public void updateExercise(Exercise exercise) throws DatabaseException {
        String sql = "UPDATE exercises SET title = ?, description = ?, difficulty = ?, topic = ?, starter_code = ?, expected_output = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, exercise.getTitle());
            pstmt.setString(2, exercise.getDescription());
            pstmt.setString(3, exercise.getDifficulty());
            pstmt.setString(4, exercise.getTopic());
            pstmt.setString(5, exercise.getStarterCode());
            pstmt.setString(6, exercise.getExpectedOutput());
            pstmt.setLong(7, exercise.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating exercise", e);
        }
    }

    @Override
    public void updateExerciseStatus(long id, String status) throws DatabaseException {
        String sql = "UPDATE exercises SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setLong(2, id);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating exercise status", e);
        }
    }

    @Override
    public boolean updateExerciseStatusIfPending(long id, String status) throws DatabaseException {
        String sql = "UPDATE exercises SET status = ? WHERE id = ? AND status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setLong(2, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating exercise status", e);
        }
    }

    @Override
    public void deleteExercise(long id) throws DatabaseException {
        String sql = "DELETE FROM exercises WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting exercise", e);
        }
    }

    private Exercise mapRowToExercise(ResultSet rs) throws SQLException {
        Exercise exercise = new Exercise();
        exercise.setId(rs.getLong("id"));
        exercise.setTitle(rs.getString("title"));
        exercise.setDescription(rs.getString("description"));
        exercise.setDifficulty(rs.getString("difficulty"));
        exercise.setTopic(rs.getString("topic"));
        exercise.setStarterCode(rs.getString("starter_code"));
        exercise.setExpectedOutput(rs.getString("expected_output"));
        exercise.setCreatedBy(rs.getLong("created_by"));
        exercise.setCreatedAt(rs.getTimestamp("created_at"));
        
        try {
            exercise.setStatus(rs.getString("status"));
        } catch (SQLException e) {
            // Ignore if column does not exist during migration phase
        }
        
        return exercise;
    }
}
