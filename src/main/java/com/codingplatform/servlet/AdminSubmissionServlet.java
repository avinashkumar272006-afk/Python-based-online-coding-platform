package com.codingplatform.servlet;

import com.codingplatform.daoimpl.ExerciseDAOImpl;
import com.codingplatform.daoimpl.SubmissionDAOImpl;
import com.codingplatform.daoimpl.UserDAOImpl;
import com.codingplatform.model.Submission;
import com.codingplatform.service.SubmissionService;
import com.codingplatform.service.SubmissionServiceImpl;
import com.codingplatform.exception.DatabaseException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/submissions")
public class AdminSubmissionServlet extends HttpServlet {

    private SubmissionService submissionService;

    @Override
    public void init() throws ServletException {
        submissionService = new SubmissionServiceImpl(new SubmissionDAOImpl(), new UserDAOImpl(), new ExerciseDAOImpl());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Submission> submissions = submissionService.getAllSubmissions();
            request.setAttribute("submissions", submissions);
            request.getRequestDispatcher("/admin/submissions.jsp").forward(request, response);
        } catch (DatabaseException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while fetching submissions.");
        }
    }
}
