package com.codingplatform.servlet;

import com.codingplatform.daoimpl.ChallengeDAOImpl;
import com.codingplatform.daoimpl.ChallengeParticipantDAOImpl;
import com.codingplatform.model.Challenge;
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

@WebServlet("/admin/challenges")
public class AdminChallengeServlet extends HttpServlet {

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
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized");
            return;
        }

        try {
            List<Challenge> challenges = challengeService.getAllChallenges();
            request.setAttribute("challenges", challenges);
            request.getRequestDispatcher("/admin/challenges.jsp").forward(request, response);
        } catch (DatabaseException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized");
            return;
        }

        long adminId = ((Number) session.getAttribute("userId")).longValue();
        String action = request.getParameter("action");
        String idStr = request.getParameter("id");

        if (idStr == null || idStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing challenge ID");
            return;
        }

        try {
            long challengeId = Long.parseLong(idStr);
            if ("approve".equals(action)) {
                challengeService.updateChallengeStatus(challengeId, "APPROVED", adminId);
            } else if ("reject".equals(action)) {
                challengeService.updateChallengeStatus(challengeId, "REJECTED", adminId);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/admin/challenges?success=" + action);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        } catch (DatabaseException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
