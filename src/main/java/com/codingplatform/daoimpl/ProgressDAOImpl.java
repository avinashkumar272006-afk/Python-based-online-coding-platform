package com.codingplatform.daoimpl;

import com.codingplatform.dao.ProgressDAO;
import com.codingplatform.model.StudentProgress;
import com.codingplatform.exception.DatabaseException;
import com.codingplatform.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProgressDAOImpl implements ProgressDAO {

    private Connection connection;

    public ProgressDAOImpl() {}

    public ProgressDAOImpl(Connection connection) {
        this.connection = connection;
    }

    protected Connection getConnection() throws DatabaseException, SQLException {
        if (this.connection != null) {
            return this.connection;
        }
        return DBConnection.getConnection();
    }

    @Override
    public StudentProgress getStudentProgress(int studentId) throws DatabaseException {
        String sql = "SELECT " +
                     "(SELECT COUNT(*) FROM exercises WHERE status = 'APPROVED') AS total_exercises, " +
                     "(SELECT COUNT(DISTINCT s.exercise_id) FROM submissions s JOIN exercises e ON s.exercise_id = e.id WHERE s.student_id = ? AND e.status = 'APPROVED') AS submitted_exercises, " +
                     "(SELECT COUNT(*) FROM submissions WHERE student_id = ? AND status = 'REVIEWED') AS reviewed_submissions, " +
                     "(SELECT COUNT(*) FROM submissions WHERE student_id = ? AND status = 'PENDING') AS pending_submissions, " +
                     "(SELECT AVG(score) FROM submissions WHERE student_id = ?) AS average_score";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, studentId);
            stmt.setInt(3, studentId);
            stmt.setInt(4, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int totalExercises = rs.getInt("total_exercises");
                    int submittedExercises = rs.getInt("submitted_exercises");
                    int reviewedSubmissions = rs.getInt("reviewed_submissions");
                    int pendingSubmissions = rs.getInt("pending_submissions");
                    
                    Double averageScore = null;
                    double avg = rs.getDouble("average_score");
                    if (!rs.wasNull()) {
                        averageScore = avg;
                    }

                    return new StudentProgress(totalExercises, submittedExercises, reviewedSubmissions, pendingSubmissions, averageScore);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving student progress", e);
        }
        return new StudentProgress(0, 0, 0, 0, null);
    }
}
