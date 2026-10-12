package com.codingplatform.servlet;

import com.codingplatform.model.*;
import com.codingplatform.service.UserService;
import com.codingplatform.service.UserServiceImpl;
import com.codingplatform.exception.DatabaseException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(RegisterServlet.class.getName());
    private UserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = new UserServiceImpl();
    }
    
    // For testing purposes
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String role = request.getParameter("role");

        if (password == null || !password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if ("ADMIN".equalsIgnoreCase(role)) {
            request.setAttribute("errorMessage", "Registration with ADMIN role is not permitted.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (role != null && !role.trim().isEmpty() && !"STUDENT".equalsIgnoreCase(role) && !"INSTRUCTOR".equalsIgnoreCase(role)) {
            request.setAttribute("errorMessage", "Invalid role selected.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        String assignedRole = "STUDENT";
        if ("INSTRUCTOR".equalsIgnoreCase(role)) {
            assignedRole = "INSTRUCTOR";
        }

        User user;
        if ("INSTRUCTOR".equals(assignedRole)) {
            user = new Instructor();
        } else {
            user = new Student();
        }
        
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(assignedRole);

        try {
            boolean success = userService.registerUser(user);
            if (success) {
                request.setAttribute("successMessage", "Registration successful! Please login.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            } else {
                request.setAttribute("errorMessage", "Registration failed. Please try again.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "DatabaseException during registration for user: " + (user != null ? user.getEmail() : ""), e);
            request.setAttribute("errorMessage", "An internal error occurred during registration. Please try again later.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during registration for user: " + (user != null ? user.getEmail() : ""), e);
            request.setAttribute("errorMessage", "An internal error occurred during registration. Please try again later.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}
