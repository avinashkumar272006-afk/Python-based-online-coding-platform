package com.codingplatform.service;

import com.codingplatform.dao.AdminDAO;
import com.codingplatform.dao.UserDAO;
import com.codingplatform.model.AdminDashboardStats;
import com.codingplatform.model.User;
import com.codingplatform.exception.DatabaseException;

import java.util.List;

public class AdminServiceImpl implements AdminService {

    private final AdminDAO adminDAO;
    private final UserDAO userDAO;
    private final com.codingplatform.dao.SubmissionDAO submissionDAO;

    public AdminServiceImpl(AdminDAO adminDAO, UserDAO userDAO, com.codingplatform.dao.SubmissionDAO submissionDAO) {
        this.adminDAO = adminDAO;
        this.userDAO = userDAO;
        this.submissionDAO = submissionDAO;
    }

    private void verifyAdmin(int adminId) throws DatabaseException {
        User user = userDAO.getUserById(adminId);
        if (user == null || !"ADMIN".equals(user.getRole())) {
            throw new IllegalArgumentException("User is not an authorized admin.");
        }
    }

    @Override
    public AdminDashboardStats getDashboardStats(int adminId) throws DatabaseException {
        verifyAdmin(adminId);
        AdminDashboardStats stats = adminDAO.getDashboardStats();
        
        stats.setRecentUsers(userDAO.getRecentUsers(5));
        stats.setRecentSubmissions(submissionDAO.getRecentSubmissions(5));
        
        return stats;
    }

    @Override
    public List<User> getAllUsers(int adminId) throws DatabaseException {
        verifyAdmin(adminId);
        return userDAO.getAllUsers();
    }

    @Override
    public boolean updateUserRole(int adminId, int targetUserId, String newRole) throws DatabaseException {
        verifyAdmin(adminId);

        if (newRole == null || (!newRole.equals("STUDENT") && !newRole.equals("INSTRUCTOR"))) {
            throw new IllegalArgumentException("Invalid role provided. Only STUDENT and INSTRUCTOR are allowed.");
        }

        User targetUser = userDAO.getUserById(targetUserId);
        if (targetUser == null) {
            throw new IllegalArgumentException("Target user not found.");
        }

        // Prevent changing the role of an ADMIN
        if ("ADMIN".equals(targetUser.getRole())) {
            int adminCount = userDAO.countAdmins();
            if (adminCount <= 1) {
                throw new IllegalArgumentException("Cannot change the role of the last administrator.");
            }
            throw new IllegalArgumentException("Cannot change the role of an administrator.");
        }

        return userDAO.updateUserRole(targetUserId, newRole);
    }
}
