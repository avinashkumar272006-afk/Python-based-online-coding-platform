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

@WebServlet("/student/exercises")
public class StudentExerciseServlet extends HttpServlet {

    private ExerciseService exerciseService;

    public StudentExerciseServlet() {}

    public StudentExerciseServlet(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    public void setExerciseService(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @Override
    public void init() throws ServletException {
        if (exerciseService == null) {
            exerciseService = new ExerciseServiceImpl(new ExerciseDAOImpl(), new com.codingplatform.daoimpl.UserDAOImpl());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            if ("view".equals(action)) {
                viewExercise(request, response);
            } else {
                listExercises(request, response);
            }
        } catch (DatabaseException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    private void listExercises(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, DatabaseException {
        List<Exercise> exercises = exerciseService.getApprovedExercises();
        request.setAttribute("exercises", exercises);
        request.getRequestDispatcher("/student/exercises.jsp").forward(request, response);
    }

    private void viewExercise(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, DatabaseException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid exercise ID");
            return;
        }

        long id;
        try {
            id = Long.parseLong(idParam.trim());
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid exercise ID");
            return;
        }

        if (id <= 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid exercise ID");
            return;
        }

        Exercise exercise = exerciseService.getExerciseById(id);
        
        if (exercise == null || !"APPROVED".equals(exercise.getStatus())) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Exercise not found or not approved.");
            return;
        }
        
        request.setAttribute("exercise", exercise);
        request.getRequestDispatcher("/student/exercise-detail.jsp").forward(request, response);
    }
}
