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
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/instructor/exercises")
public class InstructorExerciseServlet extends HttpServlet {
    
    private ExerciseService exerciseService;

    @Override
    public void init() throws ServletException {
        if (exerciseService == null) {
            exerciseService = new ExerciseServiceImpl(new ExerciseDAOImpl(), new com.codingplatform.daoimpl.UserDAOImpl());
        }
    }

    public InstructorExerciseServlet() {}

    public InstructorExerciseServlet(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    public void setExerciseService(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "new":
                    showNewForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                default:
                    listExercises(request, response);
                    break;
            }
        } catch (DatabaseException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            switch (action) {
                case "create":
                    createExercise(request, response);
                    break;
                case "update":
                    updateExercise(request, response);
                    break;
                case "delete":
                    deleteExercise(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/instructor/exercises");
                    break;
            }
        } catch (DatabaseException e) {
            throw new ServletException(e);
        }
    }

    private void listExercises(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, DatabaseException {
        HttpSession session = request.getSession();
        int instructorId = ((Number) session.getAttribute("userId")).intValue();
        List<Exercise> exercises = exerciseService.getExercisesByInstructor(instructorId);
        request.setAttribute("exercises", exercises);
        request.getRequestDispatcher("/instructor/exercises.jsp").forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/instructor/create-exercise.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, DatabaseException {
        HttpSession session = request.getSession();
        Object userIdObj = session != null ? session.getAttribute("userId") : null;
        if (userIdObj == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        long instructorId = ((Number) userIdObj).longValue();

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/instructor/exercises");
            return;
        }

        long id;
        try {
            id = Long.parseLong(idParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/instructor/exercises");
            return;
        }

        if (id <= 0) {
            response.sendRedirect(request.getContextPath() + "/instructor/exercises");
            return;
        }

        Exercise exercise = exerciseService.getExerciseById(id);
        if (exercise == null || exercise.getCreatedBy() != instructorId) {
            response.sendRedirect(request.getContextPath() + "/instructor/exercises");
            return;
        }

        request.setAttribute("exercise", exercise);
        request.getRequestDispatcher("/instructor/edit-exercise.jsp").forward(request, response);
    }

    private void createExercise(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        long instructorId = ((Number) session.getAttribute("userId")).longValue();
        
        Exercise exercise = new Exercise();
        exercise.setTitle(request.getParameter("title"));
        exercise.setDescription(request.getParameter("description"));
        exercise.setDifficulty(request.getParameter("difficulty"));
        exercise.setTopic(request.getParameter("topic"));
        exercise.setStarterCode(request.getParameter("starterCode"));
        exercise.setExpectedOutput(request.getParameter("expectedOutput"));
        exercise.setCreatedBy(instructorId);
        
        try {
            exerciseService.createExercise(exercise);
            response.sendRedirect(request.getContextPath() + "/instructor/exercises");
        } catch (IllegalArgumentException | DatabaseException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/instructor/create-exercise.jsp").forward(request, response);
        }
    }

    private void updateExercise(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        long instructorId = ((Number) session.getAttribute("userId")).longValue();
        
        long id = Long.parseLong(request.getParameter("id"));
        Exercise exercise = new Exercise();
        exercise.setId(id);
        exercise.setTitle(request.getParameter("title"));
        exercise.setDescription(request.getParameter("description"));
        exercise.setDifficulty(request.getParameter("difficulty"));
        exercise.setTopic(request.getParameter("topic"));
        exercise.setStarterCode(request.getParameter("starterCode"));
        exercise.setExpectedOutput(request.getParameter("expectedOutput"));
        
        try {
            exerciseService.updateExercise(exercise, instructorId);
            response.sendRedirect(request.getContextPath() + "/instructor/exercises");
        } catch (IllegalArgumentException | SecurityException | DatabaseException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("exercise", exercise);
            request.getRequestDispatcher("/instructor/edit-exercise.jsp").forward(request, response);
        }
    }

    private void deleteExercise(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, DatabaseException {
        HttpSession session = request.getSession();
        long instructorId = ((Number) session.getAttribute("userId")).longValue();
        long id = Long.parseLong(request.getParameter("id"));
        
        try {
            exerciseService.deleteExercise(id, instructorId);
        } catch (SecurityException e) {
            // Access denied
        }
        
        response.sendRedirect(request.getContextPath() + "/instructor/exercises");
    }
}
