package com.codingplatform.servlet;

import com.codingplatform.daoimpl.AdminDAOImpl;
import com.codingplatform.daoimpl.UserDAOImpl;
import com.codingplatform.model.User;
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
import java.util.List;

@WebServlet("/admin/users")
public class AdminUserServlet extends HttpServlet {

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
            List<User> users = adminService.getAllUsers(userId);
            request.setAttribute("users", users);
            request.getRequestDispatcher("/admin/users.jsp").forward(request, response);
        } catch (DatabaseException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        int adminId = ((Number) session.getAttribute("userId")).intValue();

        String action = request.getParameter("action");
        if ("updateRole".equals(action)) {
            try {
                int targetUserId = Integer.parseInt(request.getParameter("userId"));
                String newRole = request.getParameter("role");
                
                adminService.updateUserRole(adminId, targetUserId, newRole);
                request.getSession().setAttribute("successMessage", "Role updated successfully.");
                response.sendRedirect(request.getContextPath() + "/admin/users");
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("errorMessage", "Invalid user ID format.");
                response.sendRedirect(request.getContextPath() + "/admin/users");
            } catch (DatabaseException e) {
                request.getSession().setAttribute("errorMessage", "Database error occurred.");
                response.sendRedirect(request.getContextPath() + "/admin/users");
            } catch (IllegalArgumentException e) {
                request.getSession().setAttribute("errorMessage", e.getMessage());
                response.sendRedirect(request.getContextPath() + "/admin/users");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }
}
