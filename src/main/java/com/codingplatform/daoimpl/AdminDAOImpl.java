package com.codingplatform.daoimpl;

import com.codingplatform.dao.AdminDAO;
import com.codingplatform.model.AdminDashboardStats;
import com.codingplatform.exception.DatabaseException;
import com.codingplatform.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAOImpl implements AdminDAO {

    @Override
    public AdminDashboardStats getDashboardStats() throws DatabaseException {
        String sql = "SELECT " +
                     "(SELECT COUNT(*) FROM users) AS total_users, " +
                     "(SELECT COUNT(*) FROM users WHERE role = 'STUDENT') AS total_students, " +
                     "(SELECT COUNT(*) FROM users WHERE role = 'INSTRUCTOR') AS total_instructors, " +
                     "(SELECT COUNT(*) FROM users WHERE role = 'ADMIN') AS total_admins, " +
                     "(SELECT COUNT(*) FROM exercises) AS total_exercises, " +
                     "(SELECT COUNT(*) FROM submissions) AS total_submissions, " +
                     "(SELECT COUNT(*) FROM submissions WHERE status = 'PENDING') AS pending_submissions, " +
                     "(SELECT COUNT(*) FROM submissions WHERE status = 'REVIEWED') AS reviewed_submissions, " +
                     "(SELECT COUNT(*) FROM exercises WHERE difficulty = 'EASY') AS easy_exercises, " +
                     "(SELECT COUNT(*) FROM exercises WHERE difficulty = 'MEDIUM') AS medium_exercises, " +
                     "(SELECT COUNT(*) FROM exercises WHERE difficulty = 'HARD') AS hard_exercises";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                AdminDashboardStats stats = new AdminDashboardStats();
                stats.setTotalUsers(rs.getInt("total_users"));
                stats.setTotalStudents(rs.getInt("total_students"));
                stats.setTotalInstructors(rs.getInt("total_instructors"));
                stats.setTotalAdmins(rs.getInt("total_admins"));
                stats.setTotalExercises(rs.getInt("total_exercises"));
                stats.setTotalSubmissions(rs.getInt("total_submissions"));
                stats.setPendingSubmissions(rs.getInt("pending_submissions"));
                stats.setReviewedSubmissions(rs.getInt("reviewed_submissions"));
                stats.setEasyExercises(rs.getInt("easy_exercises"));
                stats.setMediumExercises(rs.getInt("medium_exercises"));
                stats.setHardExercises(rs.getInt("hard_exercises"));
                return stats;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving admin dashboard stats", e);
        }
        return new AdminDashboardStats();
    }
}
