package com.codingplatform.dao;

import com.codingplatform.model.AdminDashboardStats;
import com.codingplatform.exception.DatabaseException;

public interface AdminDAO {
    AdminDashboardStats getDashboardStats() throws DatabaseException;
}
