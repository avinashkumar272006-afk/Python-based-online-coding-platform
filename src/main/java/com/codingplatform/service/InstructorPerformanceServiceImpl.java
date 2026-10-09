package com.codingplatform.service;

import com.codingplatform.dao.InstructorPerformanceDAO;
import com.codingplatform.model.InstructorPerformanceReport;
import com.codingplatform.exception.DatabaseException;

public class InstructorPerformanceServiceImpl implements InstructorPerformanceService {

    private InstructorPerformanceDAO performanceDAO;

    public InstructorPerformanceServiceImpl(InstructorPerformanceDAO performanceDAO) {
        this.performanceDAO = performanceDAO;
    }

    @Override
    public InstructorPerformanceReport getReportByInstructor(int instructorId) throws DatabaseException {
        return performanceDAO.getReportByInstructor(instructorId);
    }
}
