package com.codingplatform.service;

import com.codingplatform.dao.UserDAO;
import com.codingplatform.daoimpl.UserDAOImpl;
import com.codingplatform.model.User;
import com.codingplatform.exception.DatabaseException;
import org.mindrot.jbcrypt.BCrypt;

public class UserServiceImpl implements UserService {

    private UserDAO userDAO;

    public UserServiceImpl() {
        this.userDAO = new UserDAOImpl();
    }

    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public boolean registerUser(User user) throws DatabaseException {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Valid email is required.");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password is required and must be at least 6 characters.");
        }
        if (user instanceof com.codingplatform.model.Admin || "ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new IllegalArgumentException("Registration with ADMIN role is not permitted.");
        }
        if (user.getRole() == null || (!user.getRole().equals("STUDENT") && !user.getRole().equals("INSTRUCTOR"))) {
            throw new IllegalArgumentException("Invalid role selected.");
        }
        
        User existingUser = userDAO.getUserByEmail(user.getEmail());
        if (existingUser != null) {
            throw new IllegalArgumentException("Email is already registered.");
        }
        
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10));
        user.setPassword(hashedPassword);
        
        return userDAO.createUser(user);
    }

    @Override
    public User authenticateUser(String email, String password) throws DatabaseException {
        User user = userDAO.getUserByEmail(email);
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    @Override
    public User getUserById(int userId) throws DatabaseException {
        return userDAO.getUserById(userId);
    }

    @Override
    public boolean updateUserProfile(int userId, String name, String email) throws DatabaseException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Valid email is required.");
        }
        
        name = name.trim();
        email = email.trim();

        User existingUser = userDAO.getUserByEmail(email);
        if (existingUser != null && existingUser.getId() != userId) {
            throw new IllegalArgumentException("Email is already registered by another user.");
        }

        return userDAO.updateUserProfile(userId, name, email);
    }
}
