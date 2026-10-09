package com.codingplatform.dao;

import com.codingplatform.model.InstructorPerformanceReport;
import com.codingplatform.exception.DatabaseException;

public interface InstructorPerformanceDAO {
    InstructorPerformanceReport getReportByInstructor(int instructorId) throws DatabaseException;
}
