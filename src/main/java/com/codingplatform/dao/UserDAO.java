package com.codingplatform.dao;

import com.codingplatform.model.User;
import com.codingplatform.exception.DatabaseException;
import java.util.List;

public interface UserDAO {
    User getUserById(int id) throws DatabaseException;
    User getUserByEmail(String email) throws DatabaseException;
    boolean createUser(User user) throws DatabaseException;
    List<User> getAllUsers() throws DatabaseException;
    List<User> getRecentUsers(int limit) throws DatabaseException;
    boolean updateUserRole(int userId, String newRole) throws DatabaseException;
    int countAdmins() throws DatabaseException;
    boolean updateUserProfile(int userId, String name, String email) throws DatabaseException;

    default List<User> getUsersByRole(String role) throws DatabaseException {
        List<User> filtered = new java.util.ArrayList<>();
        List<User> all = getAllUsers();
        if (all != null) {
            for (User u : all) {
                if (role != null && role.equals(u.getRole())) {
                    filtered.add(u);
                }
            }
        }
        return filtered;
    }
}
