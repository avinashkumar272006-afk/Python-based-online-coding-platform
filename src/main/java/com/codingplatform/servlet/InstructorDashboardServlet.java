package com.codingplatform.servlet;

import com.codingplatform.daoimpl.InstructorPerformanceDAOImpl;
import com.codingplatform.model.InstructorPerformanceReport;
import com.codingplatform.service.InstructorPerformanceService;
import com.codingplatform.service.InstructorPerformanceServiceImpl;
import com.codingplatform.exception.DatabaseException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/instructor/dashboard")
public class InstructorDashboardServlet extends HttpServlet {

    private InstructorPerformanceService performanceService;

    @Override
    public void init() throws ServletException {
        this.performanceService = new InstructorPerformanceServiceImpl(new InstructorPerformanceDAOImpl());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"INSTRUCTOR".equals(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        int instructorId = ((Number) session.getAttribute("userId")).intValue();

        try {
            InstructorPerformanceReport report = performanceService.getReportByInstructor(instructorId);
            request.setAttribute("performanceReport", report);
        } catch (DatabaseException e) {
            // Non-fatal for dashboard rendering, but can log or set an attribute
            request.setAttribute("performanceError", "Could not load performance report.");
        }

        request.getRequestDispatcher("/instructor/dashboard.jsp").forward(request, response);
    }
}
