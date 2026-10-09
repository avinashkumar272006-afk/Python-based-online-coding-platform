package com.codingplatform.service;

import com.codingplatform.model.StudentProgress;
import com.codingplatform.exception.DatabaseException;

public interface ProgressService {
    StudentProgress getStudentProgress(int studentId) throws DatabaseException;
}
