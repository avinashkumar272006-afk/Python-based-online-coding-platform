package com.codingplatform.servlet;

import com.codingplatform.daoimpl.ExerciseDAOImpl;
import com.codingplatform.model.Exercise;
import com.codingplatform.service.ExerciseService;
import com.codingplatform.service.ExerciseServiceImpl;
import com.codingplatform.exception.DatabaseException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/exercises")
public class AdminExerciseServlet extends HttpServlet {

    private ExerciseService exerciseService;

    @Override
    public void init() throws ServletException {
        exerciseService = new ExerciseServiceImpl(new ExerciseDAOImpl(), new com.codingplatform.daoimpl.UserDAOImpl());
    }

    // For testing
    public void setExerciseService(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Exercise> pendingExercises = exerciseService.getPendingExercises();
            List<Exercise> allExercises = exerciseService.getAllExercises();
            
            request.setAttribute("pendingExercises", pendingExercises);
            request.setAttribute("exercises", allExercises);
            request.getRequestDispatcher("/admin/exercises.jsp").forward(request, response);
        } catch (DatabaseException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while fetching exercises.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String exerciseIdStr = request.getParameter("id");
        
        if (action == null || exerciseIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/admin/exercises");
            return;
        }
        
        try {
            long exerciseId = Long.parseLong(exerciseIdStr);
            Object userIdObj = request.getSession().getAttribute("userId");
            if (userIdObj == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            long adminUserId = ((Number) userIdObj).longValue();
            
            if ("approve".equals(action)) {
                exerciseService.approveExercise(exerciseId, adminUserId);
                request.getSession().setAttribute("successMessage", "Exercise approved successfully.");
            } else if ("reject".equals(action)) {
                exerciseService.rejectExercise(exerciseId, adminUserId);
                request.getSession().setAttribute("successMessage", "Exercise rejected successfully.");
            }
            
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid exercise ID.");
        } catch (DatabaseException | SecurityException e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            request.getSession().setAttribute("errorMessage", e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/exercises");
    }
}
