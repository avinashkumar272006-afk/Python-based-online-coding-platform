package com.codingplatform.daoimpl;

import com.codingplatform.dao.SubmissionDAO;
import com.codingplatform.model.Submission;
import com.codingplatform.util.DBConnection;
import com.codingplatform.exception.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubmissionDAOImpl implements SubmissionDAO {

    @Override
    public boolean createSubmission(Submission submission) {
        String sql = "INSERT INTO submissions (exercise_id, student_id, submitted_code, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, submission.getExerciseId());
            stmt.setInt(2, submission.getStudentId());
            stmt.setString(3, submission.getSubmittedCode());
            stmt.setString(4, submission.getStatus());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        submission.setId(rs.getLong(1));
                    }
                }
                return true;
            }
        } catch (SQLException | DatabaseException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Submission getSubmissionById(long id) {
        String sql = "SELECT s.*, u.name AS student_name, e.title AS exercise_title " +
                     "FROM submissions s " +
                     "JOIN users u ON s.student_id = u.id " +
                     "JOIN exercises e ON s.exercise_id = e.id " +
                     "WHERE s.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToSubmission(rs);
                }
            }
        } catch (SQLException | DatabaseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Submission> getSubmissionsByStudent(int studentId) {
        List<Submission> submissions = new ArrayList<>();
        String sql = "SELECT s.*, u.name AS student_name, e.title AS exercise_title " +
                     "FROM submissions s " +
                     "JOIN users u ON s.student_id = u.id " +
                     "JOIN exercises e ON s.exercise_id = e.id " +
                     "WHERE s.student_id = ? " +
                     "ORDER BY s.submitted_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    submissions.add(mapRowToSubmission(rs));
                }
            }
        } catch (SQLException | DatabaseException e) {
            e.printStackTrace();
        }
        return submissions;
    }

    @Override
    public List<Submission> getSubmissionsByExercise(long exerciseId) {
        List<Submission> submissions = new ArrayList<>();
        String sql = "SELECT s.*, u.name AS student_name, e.title AS exercise_title " +
                     "FROM submissions s " +
                     "JOIN users u ON s.student_id = u.id " +
                     "JOIN exercises e ON s.exercise_id = e.id " +
                     "WHERE s.exercise_id = ? " +
                     "ORDER BY s.submitted_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, exerciseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    submissions.add(mapRowToSubmission(rs));
                }
            }
        } catch (SQLException | DatabaseException e) {
            e.printStackTrace();
        }
        return submissions;
    }

    @Override
    public List<Submission> getSubmissionsByInstructor(int instructorId) {
        List<Submission> submissions = new ArrayList<>();
        String sql = "SELECT s.*, u.name AS student_name, e.title AS exercise_title " +
                     "FROM submissions s " +
                     "JOIN users u ON s.student_id = u.id " +
                     "JOIN exercises e ON s.exercise_id = e.id " +
                     "WHERE e.created_by = ? " +
                     "ORDER BY s.submitted_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, instructorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    submissions.add(mapRowToSubmission(rs));
                }
            }
        } catch (SQLException | DatabaseException e) {
            e.printStackTrace();
        }
        return submissions;
    }

    @Override
    public List<Submission> getAllSubmissions() {
        List<Submission> submissions = new ArrayList<>();
        String sql = "SELECT s.*, u.name AS student_name, e.title AS exercise_title " +
                     "FROM submissions s " +
                     "JOIN users u ON s.student_id = u.id " +
                     "JOIN exercises e ON s.exercise_id = e.id " +
                     "ORDER BY s.submitted_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                submissions.add(mapRowToSubmission(rs));
            }
        } catch (SQLException | DatabaseException e) {
            e.printStackTrace();
        }
        return submissions;
    }

    @Override
    public List<Submission> getRecentSubmissions(int limit) {
        List<Submission> submissions = new ArrayList<>();
        String sql = "SELECT s.*, u.name AS student_name, e.title AS exercise_title " +
                     "FROM submissions s " +
                     "JOIN users u ON s.student_id = u.id " +
                     "JOIN exercises e ON s.exercise_id = e.id " +
                     "ORDER BY s.submitted_at DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    submissions.add(mapRowToSubmission(rs));
                }
            }
        } catch (SQLException | DatabaseException e) {
            e.printStackTrace();
        }
        return submissions;
    }

    @Override
    public boolean updateSubmissionReview(Submission submission) {
        String sql = "UPDATE submissions SET status = ?, feedback = ?, reviewed_at = ?, reviewed_by = ?, score = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, submission.getStatus());
            stmt.setString(2, submission.getFeedback());
            stmt.setTimestamp(3, submission.getReviewedAt());
            if (submission.getReviewedBy() != null) {
                stmt.setInt(4, submission.getReviewedBy());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            if (submission.getScore() != null) {
                stmt.setInt(5, submission.getScore());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.setLong(6, submission.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException | DatabaseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Submission mapRowToSubmission(ResultSet rs) throws SQLException {
        Submission s = new Submission();
        s.setId(rs.getLong("id"));
        s.setExerciseId(rs.getLong("exercise_id"));
        s.setStudentId(rs.getInt("student_id"));
        s.setSubmittedCode(rs.getString("submitted_code"));
        s.setStatus(rs.getString("status"));
        s.setFeedback(rs.getString("feedback"));
        s.setSubmittedAt(rs.getTimestamp("submitted_at"));
        s.setReviewedAt(rs.getTimestamp("reviewed_at"));
        int reviewedBy = rs.getInt("reviewed_by");
        if (!rs.wasNull()) {
            s.setReviewedBy(reviewedBy);
        }
        int scoreValue = rs.getInt("score");
        if (!rs.wasNull()) {
            s.setScore(scoreValue);
        }
        s.setStudentName(rs.getString("student_name"));
        s.setExerciseTitle(rs.getString("exercise_title"));
        return s;
    }
}
