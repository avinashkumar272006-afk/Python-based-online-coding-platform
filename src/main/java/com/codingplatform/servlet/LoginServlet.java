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
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    private UserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserServiceImpl();
    }
    
    // For testing
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("true".equals(request.getParameter("logout"))) {
            request.setAttribute("successMessage", "You have been successfully logged out.");
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            User user = userService.authenticateUser(email, password);
            if (user != null) {
                // Prevent session fixation by invalidating pre-existing session
                HttpSession existingSession = request.getSession(false);
                if (existingSession != null) {
                    existingSession.invalidate();
                }

                HttpSession session = request.getSession(true);
                session.setAttribute("userId", user.getId());
                session.setAttribute("name", user.getName());
                session.setAttribute("email", user.getEmail());
                session.setAttribute("role", user.getRole());
                
                // Initialize fresh CSRF token for the authenticated session
                com.codingplatform.security.CsrfUtil.refreshToken(session);
                
                response.sendRedirect(request.getContextPath() + user.getDashboardUrl());
            } else {
                // Failed login
                request.setAttribute("errorMessage", "Invalid email or password.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "DatabaseException during login for user: " + email, e);
            request.setAttribute("errorMessage", "An internal error occurred. Please try again later.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected exception during login for user: " + email, e);
            request.setAttribute("errorMessage", "An internal error occurred. Please try again later.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
