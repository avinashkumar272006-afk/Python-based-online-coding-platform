package com.codingplatform.service;

import com.codingplatform.model.InstructorPerformanceReport;
import com.codingplatform.exception.DatabaseException;

public interface InstructorPerformanceService {
    InstructorPerformanceReport getReportByInstructor(int instructorId) throws DatabaseException;
}
