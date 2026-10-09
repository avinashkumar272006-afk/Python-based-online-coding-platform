package com.codingplatform.service;

import com.codingplatform.dao.InstructorPerformanceDAO;
import com.codingplatform.model.InstructorPerformanceReport;
import com.codingplatform.exception.DatabaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InstructorPerformanceServiceImplTest {

    private InstructorPerformanceService performanceService;
    private MockInstructorPerformanceDAO mockDAO;

    @BeforeEach
    public void setup() {
        mockDAO = new MockInstructorPerformanceDAO();
        performanceService = new InstructorPerformanceServiceImpl(mockDAO);
    }

    @Test
    public void testGetReportByInstructor() throws DatabaseException {
        InstructorPerformanceReport report = performanceService.getReportByInstructor(1);
        assertNotNull(report);
        assertEquals(10, report.getTotalSubmissions());
        assertEquals(85.5, report.getAverageScore());
    }

}

class MockInstructorPerformanceDAO implements InstructorPerformanceDAO {
    @Override
    public InstructorPerformanceReport getReportByInstructor(int instructorId) throws DatabaseException {
        InstructorPerformanceReport report = new InstructorPerformanceReport();
        report.setTotalSubmissions(10);
        report.setAverageScore(85.5);
        return report;
    }
}
