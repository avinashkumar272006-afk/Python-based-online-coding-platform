package com.codingplatform.servlet;

import com.codingplatform.daoimpl.ExerciseDAOImpl;
import com.codingplatform.daoimpl.SubmissionDAOImpl;
import com.codingplatform.daoimpl.UserDAOImpl;
import com.codingplatform.model.Exercise;
import com.codingplatform.model.Submission;
import com.codingplatform.service.ExerciseService;
import com.codingplatform.service.ExerciseServiceImpl;
import com.codingplatform.service.SubmissionService;
import com.codingplatform.service.SubmissionServiceImpl;
import com.codingplatform.exception.DatabaseException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "StudentSubmissionServlet", urlPatterns = {"/student/submit", "/student/submissions"})
public class StudentSubmissionServlet extends HttpServlet {

    private SubmissionService submissionService;
    private ExerciseService exerciseService;

    @Override
    public void init() throws ServletException {
        submissionService = new SubmissionServiceImpl(new SubmissionDAOImpl(), new UserDAOImpl(), new ExerciseDAOImpl());
        exerciseService = new ExerciseServiceImpl(new ExerciseDAOImpl(), new UserDAOImpl());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"STUDENT".equals(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }
        int userId = ((Number) session.getAttribute("userId")).intValue();

        if ("/student/submissions".equals(path)) {
            try {
                List<Submission> submissions = submissionService.getSubmissionsByStudent(userId);
                request.setAttribute("submissions", submissions);
                request.getRequestDispatcher("/student/submissions.jsp").forward(request, response);
            } catch (DatabaseException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
            }
        } else if ("/student/submit".equals(path)) {
            String exerciseIdParam = request.getParameter("exerciseId");
            if (exerciseIdParam != null) {
                try {
                    long exerciseId = Long.parseLong(exerciseIdParam);
                    Exercise exercise = exerciseService.getExerciseById(exerciseId);
                    if (exercise == null) {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Exercise not found");
                        return;
                    }
                    request.setAttribute("exercise", exercise);
                    request.getRequestDispatcher("/student/submit-exercise.jsp").forward(request, response);
                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid exercise ID");
                } catch (DatabaseException e) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/student/exercises");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"STUDENT".equals(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }
        
        int userId = ((Number) session.getAttribute("userId")).intValue();

        String path = request.getServletPath();
        if ("/student/submit".equals(path)) {
            String exerciseIdParam = request.getParameter("exerciseId");
            String submittedCode = request.getParameter("submittedCode");

            try {
                long exerciseId = Long.parseLong(exerciseIdParam);

                Submission submission = new Submission();
                submission.setExerciseId(exerciseId);
                submission.setStudentId(userId);
                submission.setSubmittedCode(submittedCode);

                submissionService.createSubmission(submission);
                response.sendRedirect(request.getContextPath() + "/student/submissions?success=true");
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid input");
            } catch (IllegalArgumentException e) {
                request.setAttribute("error", e.getMessage());
                // Reload the exercise details to populate the form again
                try {
                    long exerciseId = Long.parseLong(exerciseIdParam);
                    Exercise exercise = exerciseService.getExerciseById(exerciseId);
                    request.setAttribute("exercise", exercise);
                } catch (NumberFormatException | DatabaseException ignored) {}
                request.getRequestDispatcher("/student/submit-exercise.jsp").forward(request, response);
            } catch (DatabaseException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
            }
        }
    }
}
