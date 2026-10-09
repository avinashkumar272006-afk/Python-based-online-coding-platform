package com.codingplatform.servlet;

import com.codingplatform.daoimpl.SystemSettingsDAOImpl;
import com.codingplatform.daoimpl.UserDAOImpl;
import com.codingplatform.model.SystemSetting;
import com.codingplatform.service.SystemSettingsService;
import com.codingplatform.service.SystemSettingsServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/settings")
public class SystemSettingsServlet extends HttpServlet {

    private SystemSettingsService settingsService;

    @Override
    public void init() throws ServletException {
        settingsService = new SystemSettingsServiceImpl(new SystemSettingsDAOImpl(), new UserDAOImpl());
    }

    // Constructor for testing
    public SystemSettingsServlet(SystemSettingsService settingsService) {
        this.settingsService = settingsService;
    }
    
    public SystemSettingsServlet() {}

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Object userIdObj = session.getAttribute("userId");
        String role = (String) session.getAttribute("role");
        if (userIdObj == null || !"ADMIN".equals(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        try {
            int adminId = ((Number) userIdObj).intValue();
            List<SystemSetting> settings = settingsService.getAllSettings(adminId, role);
            request.setAttribute("settings", settings);
            request.getRequestDispatcher("/admin/settings.jsp").forward(request, response);
        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: " + e.getMessage());
        } catch (RuntimeException e) {
            throw new ServletException("Error loading settings", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Object userIdObj = session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userIdObj == null || !"ADMIN".equals(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        int adminId = ((Number) userIdObj).intValue();
        String key = request.getParameter("settingKey");
        String value = request.getParameter("settingValue");

        try {
            settingsService.updateSetting(adminId, role, key, value);
            session.setAttribute("successMessage", "Setting '" + key + "' updated successfully.");
        } catch (IllegalArgumentException | SecurityException e) {
            session.setAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            session.setAttribute("errorMessage", "Failed to update setting due to an internal error.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/settings");
    }
}
