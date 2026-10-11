package com.codingplatform.servlet;

import com.codingplatform.daoimpl.ExerciseDAOImpl;
import com.codingplatform.daoimpl.SubmissionDAOImpl;
import com.codingplatform.daoimpl.UserDAOImpl;
import com.codingplatform.model.Submission;
import com.codingplatform.exception.DatabaseException;
import com.codingplatform.service.SubmissionService;
import com.codingplatform.service.SubmissionServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "InstructorSubmissionServlet", urlPatterns = {"/instructor/submissions", "/instructor/submission/review"})
public class InstructorSubmissionServlet extends HttpServlet {

    private SubmissionService submissionService;

    public InstructorSubmissionServlet() {}

    public InstructorSubmissionServlet(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    public void setSubmissionService(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @Override
    public void init() throws ServletException {
        if (submissionService == null) {
            submissionService = new SubmissionServiceImpl(new SubmissionDAOImpl(), new UserDAOImpl(), new ExerciseDAOImpl());
        }
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
        if (!"INSTRUCTOR".equals(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }
        int userId = ((Number) session.getAttribute("userId")).intValue();

        if ("/instructor/submissions".equals(path)) {
            try {
                List<Submission> submissions = submissionService.getSubmissionsByInstructor(userId);
                request.setAttribute("submissions", submissions);
                request.getRequestDispatcher("/instructor/submissions.jsp").forward(request, response);
            } catch (DatabaseException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
            }
        } else if ("/instructor/submission/review".equals(path)) {
            String submissionIdParam = request.getParameter("id");
            if (submissionIdParam == null || submissionIdParam.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/instructor/submissions");
                return;
            }

            try {
                long submissionId = Long.parseLong(submissionIdParam.trim());
                if (submissionId <= 0) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid submission ID");
                    return;
                }

                Submission submission = submissionService.getSubmissionForInstructor(submissionId, userId);
                if (submission == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Submission not found");
                    return;
                }
                request.setAttribute("submission", submission);
                request.getRequestDispatcher("/instructor/review-submission.jsp").forward(request, response);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid submission ID");
            } catch (SecurityException e) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            } catch (DatabaseException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
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
        if (!"INSTRUCTOR".equals(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }
        int userId = ((Number) session.getAttribute("userId")).intValue();

        String path = request.getServletPath();
        if ("/instructor/submission/review".equals(path)) {
            String submissionIdParam = request.getParameter("id");
            String feedback = request.getParameter("feedback");
            String status = request.getParameter("status");
            String scoreParam = request.getParameter("score");

            if (submissionIdParam == null || submissionIdParam.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid input");
                return;
            }

            try {
                long submissionId = Long.parseLong(submissionIdParam.trim());
                if (submissionId <= 0) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid input");
                    return;
                }

                Integer score = null;
                if (scoreParam != null && !scoreParam.trim().isEmpty()) {
                    score = Integer.parseInt(scoreParam.trim());
                }
                
                submissionService.reviewSubmission(submissionId, userId, feedback, status, score);
                response.sendRedirect(request.getContextPath() + "/instructor/submissions?success=true");
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid input");
            } catch (IllegalArgumentException e) {
                if ("Instructor can only review submissions for exercises they created.".equals(e.getMessage())) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                    return;
                }
                if ("Submission does not exist.".equals(e.getMessage())) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Submission not found");
                    return;
                }
                request.setAttribute("error", e.getMessage());
                try {
                    long submissionId = Long.parseLong(submissionIdParam.trim());
                    Submission submission = submissionService.getSubmissionForInstructor(submissionId, userId);
                    request.setAttribute("submission", submission);
                } catch (Exception ignored) {}
                request.getRequestDispatcher("/instructor/review-submission.jsp").forward(request, response);
            } catch (DatabaseException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
            }
        }
    }
}
