package com.codingplatform.dao;

import com.codingplatform.model.StudentProgress;
import com.codingplatform.exception.DatabaseException;

public interface ProgressDAO {
    StudentProgress getStudentProgress(int studentId) throws DatabaseException;
}
