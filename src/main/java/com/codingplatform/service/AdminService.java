package com.codingplatform.service;

import com.codingplatform.model.AdminDashboardStats;
import com.codingplatform.model.User;
import com.codingplatform.exception.DatabaseException;
import java.util.List;

public interface AdminService {
    AdminDashboardStats getDashboardStats(int adminId) throws DatabaseException;
    List<User> getAllUsers(int adminId) throws DatabaseException;
    boolean updateUserRole(int adminId, int targetUserId, String newRole) throws DatabaseException;
}
