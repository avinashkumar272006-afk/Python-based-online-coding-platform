package com.codingplatform.daoimpl;

import com.codingplatform.dao.InstructorPerformanceDAO;
import com.codingplatform.model.InstructorPerformanceReport;
import com.codingplatform.util.DBConnection;
import com.codingplatform.exception.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InstructorPerformanceDAOImpl implements InstructorPerformanceDAO {

    @Override
    public InstructorPerformanceReport getReportByInstructor(int instructorId) throws DatabaseException {
        String sql = "SELECT " +
                     "COUNT(s.id) AS total_submissions, " +
                     "COUNT(CASE WHEN s.status = 'PENDING' THEN 1 END) AS pending_submissions, " +
                     "COUNT(CASE WHEN s.status = 'REVIEWED' THEN 1 END) AS reviewed_submissions, " +
                     "AVG(s.score) AS average_score, " +
                     "MAX(s.score) AS highest_score, " +
                     "MIN(s.score) AS lowest_score " +
                     "FROM submissions s " +
                     "JOIN exercises e ON s.exercise_id = e.id " +
                     "WHERE e.created_by = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, instructorId);
            try (ResultSet rs = stmt.executeQuery()) {
                InstructorPerformanceReport report = new InstructorPerformanceReport();
                if (rs.next()) {
                    report.setTotalSubmissions(rs.getInt("total_submissions"));
                    report.setPendingSubmissions(rs.getInt("pending_submissions"));
                    report.setReviewedSubmissions(rs.getInt("reviewed_submissions"));
                    
                    double avg = rs.getDouble("average_score");
                    if (!rs.wasNull()) {
                        report.setAverageScore(avg);
                    }
                    
                    int max = rs.getInt("highest_score");
                    if (!rs.wasNull()) {
                        report.setHighestScore(max);
                    }
                    
                    int min = rs.getInt("lowest_score");
                    if (!rs.wasNull()) {
                        report.setLowestScore(min);
                    }
                }
                return report;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving instructor performance report", e);
        }
    }
}
