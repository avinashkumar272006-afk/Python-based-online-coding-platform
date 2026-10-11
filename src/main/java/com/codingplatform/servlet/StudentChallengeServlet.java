package com.codingplatform.servlet;

import com.codingplatform.daoimpl.ChallengeDAOImpl;
import com.codingplatform.daoimpl.ChallengeParticipantDAOImpl;
import com.codingplatform.model.Challenge;
import com.codingplatform.model.ChallengeParticipant;
import com.codingplatform.service.ChallengeService;
import com.codingplatform.service.ChallengeServiceImpl;
import com.codingplatform.exception.DatabaseException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/student/challenges", "/student/my-challenges"})
public class StudentChallengeServlet extends HttpServlet {

    private ChallengeService challengeService;

    @Override
    public void init() throws ServletException {
        challengeService = new ChallengeServiceImpl(new ChallengeDAOImpl(), new ChallengeParticipantDAOImpl());
    }

    // For testing
    public void setChallengeService(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !"STUDENT".equals(session.getAttribute("role"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized");
            return;
        }

        long studentId;
        try {
            studentId = ((Number) session.getAttribute("userId")).longValue();
            if (studentId <= 0) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized");
                return;
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized");
            return;
        }

        String path = request.getServletPath();

        try {
            if ("/student/my-challenges".equals(path)) {
                List<ChallengeParticipant> myChallenges = challengeService.getStudentChallenges(studentId);
                request.setAttribute("myChallenges", myChallenges);
                request.getRequestDispatcher("/student/my-challenges.jsp").forward(request, response);
            } else {
                List<Challenge> challenges = challengeService.getApprovedChallenges();
                request.setAttribute("challenges", challenges);
                request.getRequestDispatcher("/student/challenges.jsp").forward(request, response);
            }
        } catch (DatabaseException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null || !"STUDENT".equals(session.getAttribute("role"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized");
            return;
        }

        long studentId;
        try {
            studentId = ((Number) session.getAttribute("userId")).longValue();
            if (studentId <= 0) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized");
                return;
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized");
            return;
        }

        String action = request.getParameter("action");
        String idStr = request.getParameter("id");

        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing challenge ID");
            return;
        }

        long challengeId;
        try {
            challengeId = Long.parseLong(idStr.trim());
            if (challengeId <= 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid challenge ID");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
            return;
        }

        try {
            if ("join".equals(action)) {
                challengeService.joinChallenge(challengeId, studentId);
                response.sendRedirect(request.getContextPath() + "/student/my-challenges?success=joined");
            } else if ("complete".equals(action)) {
                challengeService.updateParticipantStatus(challengeId, studentId, "COMPLETED");
                response.sendRedirect(request.getContextPath() + "/student/my-challenges?success=completed");
            } else if ("drop".equals(action)) {
                challengeService.updateParticipantStatus(challengeId, studentId, "DROPPED");
                response.sendRedirect(request.getContextPath() + "/student/my-challenges?success=dropped");
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (DatabaseException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        } catch (IllegalArgumentException e) {
            // Handle duplicate join, invalid transitions, date restrictions, etc. gracefully
            request.setAttribute("error", e.getMessage());
            if ("complete".equals(action) || "drop".equals(action)) {
                try {
                    List<ChallengeParticipant> myChallenges = challengeService.getStudentChallenges(studentId);
                    request.setAttribute("myChallenges", myChallenges);
                    request.getRequestDispatcher("/student/my-challenges.jsp").forward(request, response);
                } catch (DatabaseException de) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
                }
            } else {
                doGet(request, response);
            }
        }
    }
}
