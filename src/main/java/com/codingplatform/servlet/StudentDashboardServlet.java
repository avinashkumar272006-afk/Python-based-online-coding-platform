package com.codingplatform.servlet;

import com.codingplatform.daoimpl.ProgressDAOImpl;
import com.codingplatform.daoimpl.UserDAOImpl;
import com.codingplatform.model.StudentProgress;
import com.codingplatform.service.ProgressService;
import com.codingplatform.service.ProgressServiceImpl;
import com.codingplatform.exception.DatabaseException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/student/dashboard")
public class StudentDashboardServlet extends HttpServlet {

    private ProgressService progressService;

    @Override
    public void init() throws ServletException {
        progressService = new ProgressServiceImpl(new ProgressDAOImpl(), new UserDAOImpl());
    }

    // For testing
    public void setProgressService(ProgressService progressService) {
        this.progressService = progressService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        int userId = ((Number) session.getAttribute("userId")).intValue();

        try {
            StudentProgress progress = progressService.getStudentProgress(userId);
            request.setAttribute("progress", progress);
            request.getRequestDispatcher("/student/dashboard.jsp").forward(request, response);
        } catch (DatabaseException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
