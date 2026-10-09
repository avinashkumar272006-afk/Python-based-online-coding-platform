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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet("/instructor/challenges")
public class InstructorChallengeServlet extends HttpServlet {

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
        if (session == null || !"INSTRUCTOR".equals(session.getAttribute("role"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized");
            return;
        }

        long instructorId = ((Number) session.getAttribute("userId")).longValue();

        try {
            List<Challenge> challenges = challengeService.getInstructorChallenges(instructorId);
            request.setAttribute("challenges", challenges);
            request.getRequestDispatcher("/instructor/challenges.jsp").forward(request, response);
        } catch (DatabaseException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"INSTRUCTOR".equals(session.getAttribute("role"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized");
            return;
        }

        long instructorId = ((Number) session.getAttribute("userId")).longValue();
        String action = request.getParameter("action");

        if ("create".equals(action)) {
            try {
                String title = request.getParameter("title");
                String description = request.getParameter("description");
                String difficulty = request.getParameter("difficulty");
                String startDateStr = request.getParameter("startDate");
                String endDateStr = request.getParameter("endDate");

                Challenge challenge = new Challenge();
                challenge.setTitle(title);
                challenge.setDescription(description);
                challenge.setDifficulty(difficulty);

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                if (startDateStr != null && !startDateStr.isEmpty()) {
                    challenge.setStartDate(new Timestamp(format.parse(startDateStr).getTime()));
                }
                if (endDateStr != null && !endDateStr.isEmpty()) {
                    challenge.setEndDate(new Timestamp(format.parse(endDateStr).getTime()));
                }

                challengeService.createChallenge(challenge, instructorId);
                response.sendRedirect(request.getContextPath() + "/instructor/challenges?success=created");
            } catch (DatabaseException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
            } catch (IllegalArgumentException | ParseException e) {
                request.setAttribute("error", e.getMessage());
                doGet(request, response); // re-render page with error
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }
}
