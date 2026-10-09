package com.codingplatform.servlet;

import com.codingplatform.daoimpl.AdminDAOImpl;
import com.codingplatform.daoimpl.UserDAOImpl;
import com.codingplatform.model.AdminDashboardStats;
import com.codingplatform.service.AdminService;
import com.codingplatform.service.AdminServiceImpl;
import com.codingplatform.exception.DatabaseException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        adminService = new AdminServiceImpl(new AdminDAOImpl(), new UserDAOImpl(), new com.codingplatform.daoimpl.SubmissionDAOImpl());
    }

    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
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
            AdminDashboardStats stats = adminService.getDashboardStats(userId);
            request.setAttribute("stats", stats);
            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
        } catch (DatabaseException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
