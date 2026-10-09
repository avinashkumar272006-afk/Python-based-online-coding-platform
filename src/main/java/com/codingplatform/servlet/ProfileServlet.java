package com.codingplatform.servlet;

import com.codingplatform.model.User;
import com.codingplatform.service.UserService;
import com.codingplatform.service.UserServiceImpl;
import com.codingplatform.exception.DatabaseException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = new UserServiceImpl();
    }

    // For testing
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Object userIdObj = session.getAttribute("userId");
        int userId = ((Number) userIdObj).intValue();

        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            request.setAttribute("user", user);
            request.getRequestDispatcher("/profile.jsp").forward(request, response);
        } catch (DatabaseException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while loading your profile.");
            request.getRequestDispatcher("/profile.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Object userIdObj = session.getAttribute("userId");
        int userId = ((Number) userIdObj).intValue();

        String name = request.getParameter("name");
        String email = request.getParameter("email");

        try {
            boolean updated = userService.updateUserProfile(userId, name, email);
            if (updated) {
                // Update session
                session.setAttribute("name", name.trim());
                session.setAttribute("email", email.trim());

                request.setAttribute("successMessage", "Profile updated successfully.");
            } else {
                request.setAttribute("errorMessage", "Failed to update profile.");
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
        } catch (DatabaseException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while updating your profile.");
        }

        // Reload user to display the updated (or unchanged) info correctly
        try {
            User user = userService.getUserById(userId);
            // Overwrite name/email with submitted ones so they stay in form if there's an error
            if (request.getAttribute("errorMessage") != null) {
                user.setName(name);
                user.setEmail(email);
            }
            request.setAttribute("user", user);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        request.getRequestDispatcher("/profile.jsp").forward(request, response);
    }
}
