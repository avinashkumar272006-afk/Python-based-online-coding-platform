package com.codingplatform.service;

import com.codingplatform.model.User;
import com.codingplatform.exception.DatabaseException;

public interface UserService {
    boolean registerUser(User user) throws DatabaseException;
    User authenticateUser(String email, String password) throws DatabaseException;
    User getUserById(int userId) throws DatabaseException;
    boolean updateUserProfile(int userId, String name, String email) throws DatabaseException;
}
