package com.codingplatform.servlet;

import com.codingplatform.service.ChallengeService;
import com.codingplatform.model.Challenge;
import com.codingplatform.exception.DatabaseException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.Mockito.*;

public class AdminChallengeServletTest {

    private AdminChallengeServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private ChallengeService challengeService;
    private RequestDispatcher dispatcher;

    @BeforeEach
    public void setUp() throws Exception {
        servlet = new AdminChallengeServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        challengeService = mock(ChallengeService.class);
        dispatcher = mock(RequestDispatcher.class);

        servlet.setChallengeService(challengeService);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("ADMIN");
        when(session.getAttribute("userId")).thenReturn(100L);
    }

    @Test
    public void testDoGet_Unauthorized() throws Exception {
        when(session.getAttribute("role")).thenReturn("STUDENT");
        servlet.doGet(request, response);
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized");
    }

    @Test
    public void testDoGet_Authorized() throws Exception {
        when(request.getRequestDispatcher("/admin/challenges.jsp")).thenReturn(dispatcher);
        when(challengeService.getAllChallenges()).thenReturn(Arrays.asList(new Challenge()));
        
        servlet.doGet(request, response);
        
        verify(request).setAttribute(eq("challenges"), anyList());
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testDoPost_Approve() throws Exception {
        when(request.getParameter("action")).thenReturn("approve");
        when(request.getParameter("id")).thenReturn("1");
        
        servlet.doPost(request, response);
        
        verify(challengeService).updateChallengeStatus(1L, "APPROVED", 100L);
        verify(response).sendRedirect(anyString());
    }

    @Test
    public void testDoPost_Reject() throws Exception {
        when(request.getParameter("action")).thenReturn("reject");
        when(request.getParameter("id")).thenReturn("1");
        
        servlet.doPost(request, response);
        
        verify(challengeService).updateChallengeStatus(1L, "REJECTED", 100L);
        verify(response).sendRedirect(anyString());
    }

    @Test
    public void testDoPost_AlreadyApproved_ReturnsBadRequest() throws Exception {
        when(request.getParameter("action")).thenReturn("approve");
        when(request.getParameter("id")).thenReturn("1");
        doThrow(new IllegalStateException("Only PENDING challenges can be moderated. Current status: APPROVED"))
                .when(challengeService).updateChallengeStatus(1L, "APPROVED", 100L);

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Only PENDING challenges can be moderated. Current status: APPROVED");
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void testDoPost_AlreadyRejected_ReturnsBadRequest() throws Exception {
        when(request.getParameter("action")).thenReturn("reject");
        when(request.getParameter("id")).thenReturn("1");
        doThrow(new IllegalStateException("Only PENDING challenges can be moderated. Current status: REJECTED"))
                .when(challengeService).updateChallengeStatus(1L, "REJECTED", 100L);

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Only PENDING challenges can be moderated. Current status: REJECTED");
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void testDoPost_NonexistentChallenge_ReturnsBadRequest() throws Exception {
        when(request.getParameter("action")).thenReturn("approve");
        when(request.getParameter("id")).thenReturn("999");
        doThrow(new IllegalArgumentException("Challenge not found"))
                .when(challengeService).updateChallengeStatus(999L, "APPROVED", 100L);

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Challenge not found");
        verify(response, never()).sendRedirect(anyString());
    }
}
