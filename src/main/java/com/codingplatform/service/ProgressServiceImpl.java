package com.codingplatform.service;

import com.codingplatform.dao.ProgressDAO;
import com.codingplatform.dao.UserDAO;
import com.codingplatform.model.StudentProgress;
import com.codingplatform.model.User;
import com.codingplatform.exception.DatabaseException;

public class ProgressServiceImpl implements ProgressService {

    private final ProgressDAO progressDAO;
    private final UserDAO userDAO;

    public ProgressServiceImpl(ProgressDAO progressDAO, UserDAO userDAO) {
        this.progressDAO = progressDAO;
        this.userDAO = userDAO;
    }

    @Override
    public StudentProgress getStudentProgress(int studentId) throws DatabaseException {
        User user = userDAO.getUserById(studentId);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }
        if (!"STUDENT".equals(user.getRole())) {
            throw new IllegalArgumentException("User is not a student.");
        }

        return progressDAO.getStudentProgress(studentId);
    }
}
