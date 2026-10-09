package com.codingplatform.daoimpl;

import com.codingplatform.dao.UserDAO;
import com.codingplatform.model.User;
import com.codingplatform.model.Student;
import com.codingplatform.model.Instructor;
import com.codingplatform.model.Admin;
import com.codingplatform.exception.DatabaseException;
import com.codingplatform.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

import java.util.List;

public class UserDAOImpl implements UserDAO {

    @Override
    public User getUserById(int id) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    User user;
                    if ("ADMIN".equals(role)) {
                        user = new Admin();
                    } else if ("INSTRUCTOR".equals(role)) {
                        user = new Instructor();
                    } else {
                        user = new Student();
                    }
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(role);
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving user by id", e);
        }
        return null;
    }

    @Override
    public User getUserByEmail(String email) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    User user;
                    if ("ADMIN".equals(role)) {
                        user = new Admin();
                    } else if ("INSTRUCTOR".equals(role)) {
                        user = new Instructor();
                    } else {
                        user = new Student();
                    }
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(role);
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving user by email", e);
        }
        return null;
    }

    @Override
    public boolean createUser(User user) throws DatabaseException {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getRole());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                throw new DatabaseException("Database constraint violated (e.g. duplicate email).", e);
            }
            throw new DatabaseException("Error creating user", e);
        }
    }

    @Override
    public List<User> getAllUsers() throws DatabaseException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                String role = rs.getString("role");
                User user;
                if ("ADMIN".equals(role)) {
                    user = new Admin();
                } else if ("INSTRUCTOR".equals(role)) {
                    user = new Instructor();
                } else {
                    user = new Student();
                }
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                // Do not expose password hashes in general lists
                user.setPassword(""); 
                user.setRole(role);
                user.setCreatedAt(rs.getTimestamp("created_at"));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving all users", e);
        }
        return users;
    }

    @Override
    public List<User> getRecentUsers(int limit) throws DatabaseException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String role = rs.getString("role");
                    User user;
                    if ("ADMIN".equals(role)) {
                        user = new Admin();
                    } else if ("INSTRUCTOR".equals(role)) {
                        user = new Instructor();
                    } else {
                        user = new Student();
                    }
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(""); 
                    user.setRole(role);
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving recent users", e);
        }
        return users;
    }

    @Override
    public boolean updateUserRole(int userId, String newRole) throws DatabaseException {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, newRole);
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating user role", e);
        }
    }

    @Override
    public int countAdmins() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error counting admins", e);
        }
        return 0;
    }

    @Override
    public boolean updateUserProfile(int userId, String name, String email) throws DatabaseException {
        String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setInt(3, userId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                throw new DatabaseException("Database constraint violated (e.g. duplicate email).", e);
            }
            throw new DatabaseException("Error updating user profile", e);
        }
    }
}
