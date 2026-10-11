package com.codingplatform.servlet;

import com.codingplatform.service.ChallengeService;
import com.codingplatform.model.Challenge;
import com.codingplatform.model.ChallengeParticipant;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.Mockito.*;

public class StudentChallengeServletTest {

    private StudentChallengeServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private ChallengeService challengeService;
    private RequestDispatcher dispatcher;

    @BeforeEach
    public void setUp() throws Exception {
        servlet = new StudentChallengeServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        challengeService = mock(ChallengeService.class);
        dispatcher = mock(RequestDispatcher.class);

        servlet.setChallengeService(challengeService);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(session.getAttribute("userId")).thenReturn(20L);
    }

    @Test
    public void testDoGet_Challenges() throws Exception {
        when(request.getServletPath()).thenReturn("/student/challenges");
        when(request.getRequestDispatcher("/student/challenges.jsp")).thenReturn(dispatcher);
        when(challengeService.getApprovedChallenges()).thenReturn(Arrays.asList(new Challenge()));
        
        servlet.doGet(request, response);
        
        verify(request).setAttribute(eq("challenges"), anyList());
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_MyChallenges() throws Exception {
        when(request.getServletPath()).thenReturn("/student/my-challenges");
        when(request.getRequestDispatcher("/student/my-challenges.jsp")).thenReturn(dispatcher);
        when(challengeService.getStudentChallenges(20L)).thenReturn(Arrays.asList(new ChallengeParticipant()));
        
        servlet.doGet(request, response);
        
        verify(request).setAttribute(eq("myChallenges"), anyList());
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testDoPost_Join() throws Exception {
        when(request.getParameter("action")).thenReturn("join");
        when(request.getParameter("id")).thenReturn("1");
        
        servlet.doPost(request, response);
        
        verify(challengeService).joinChallenge(1L, 20L);
        verify(response).sendRedirect(anyString());
    }

    @Test
    public void testDoPost_Complete() throws Exception {
        when(request.getParameter("action")).thenReturn("complete");
        when(request.getParameter("id")).thenReturn("1");
        
        servlet.doPost(request, response);
        
        verify(challengeService).updateParticipantStatus(1L, 20L, "COMPLETED");
        verify(response).sendRedirect(contains("success=completed"));
    }

    @Test
    public void testDoPost_Drop() throws Exception {
        when(request.getParameter("action")).thenReturn("drop");
        when(request.getParameter("id")).thenReturn("1");
        
        servlet.doPost(request, response);
        
        verify(challengeService).updateParticipantStatus(1L, 20L, "DROPPED");
        verify(response).sendRedirect(contains("success=dropped"));
    }

    @Test
    public void testDoPost_IgnoresForgedStudentIdParameter() throws Exception {
        when(request.getParameter("action")).thenReturn("complete");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("studentId")).thenReturn("999"); // Attacker attempting to forge studentId
        
        servlet.doPost(request, response);
        
        // Must derive student identity strictly from authenticated session (20L), not parameter (999L)
        verify(challengeService).updateParticipantStatus(1L, 20L, "COMPLETED");
        verify(challengeService, never()).updateParticipantStatus(eq(1L), eq(999L), anyString());
    }

    @Test
    public void testDoPost_IgnoresForgedCompletedParameter() throws Exception {
        when(request.getParameter("action")).thenReturn("invalidAction");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("completed")).thenReturn("true");
        
        servlet.doPost(request, response);
        
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        verifyNoInteractions(challengeService);
    }

    @Test
    public void testDoPost_Unauthenticated_NullSession() throws Exception {
        when(request.getSession(false)).thenReturn(null);
        
        servlet.doPost(request, response);
        
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized");
        verifyNoInteractions(challengeService);
    }

    @Test
    public void testDoPost_Unauthenticated_NullUserId() throws Exception {
        when(session.getAttribute("userId")).thenReturn(null);
        
        servlet.doPost(request, response);
        
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized");
        verifyNoInteractions(challengeService);
    }

    @Test
    public void testDoPost_NonStudent_Forbidden() throws Exception {
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        
        servlet.doPost(request, response);
        
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized");
        verifyNoInteractions(challengeService);
    }

    @Test
    public void testDoPost_MissingChallengeId_BadRequest() throws Exception {
        when(request.getParameter("action")).thenReturn("complete");
        when(request.getParameter("id")).thenReturn("");
        
        servlet.doPost(request, response);
        
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing challenge ID");
        verifyNoInteractions(challengeService);
    }

    @Test
    public void testDoPost_MalformedChallengeId_BadRequest() throws Exception {
        when(request.getParameter("action")).thenReturn("complete");
        when(request.getParameter("id")).thenReturn("abc");
        
        servlet.doPost(request, response);
        
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
        verifyNoInteractions(challengeService);
    }

    @Test
    public void testDoPost_NegativeChallengeId_BadRequest() throws Exception {
        when(request.getParameter("action")).thenReturn("complete");
        when(request.getParameter("id")).thenReturn("-5");
        
        servlet.doPost(request, response);
        
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid challenge ID");
        verifyNoInteractions(challengeService);
    }

    @Test
    public void testDoPost_Complete_ServiceErrorHandledGracefully() throws Exception {
        when(request.getParameter("action")).thenReturn("complete");
        when(request.getParameter("id")).thenReturn("1");
        doThrow(new IllegalArgumentException("Challenge is already completed"))
                .when(challengeService).updateParticipantStatus(1L, 20L, "COMPLETED");
        when(request.getRequestDispatcher("/student/my-challenges.jsp")).thenReturn(dispatcher);
        
        servlet.doPost(request, response);
        
        verify(request).setAttribute("error", "Challenge is already completed");
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testDoPost_Join_ServiceErrorHandledGracefully() throws Exception {
        when(request.getParameter("action")).thenReturn("join");
        when(request.getParameter("id")).thenReturn("1");
        doThrow(new IllegalArgumentException("Challenge has already ended"))
                .when(challengeService).joinChallenge(1L, 20L);
        when(request.getServletPath()).thenReturn("/student/challenges");
        when(request.getRequestDispatcher("/student/challenges.jsp")).thenReturn(dispatcher);
        
        servlet.doPost(request, response);
        
        verify(request).setAttribute("error", "Challenge has already ended");
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testDoPost_DatabaseException_InternalServerError() throws Exception {
        when(request.getParameter("action")).thenReturn("complete");
        when(request.getParameter("id")).thenReturn("1");
        doThrow(new com.codingplatform.exception.DatabaseException("DB error", null))
                .when(challengeService).updateParticipantStatus(1L, 20L, "COMPLETED");
        
        servlet.doPost(request, response);
        
        verify(response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
    }

    @Test
    public void testDoGet_Unauthenticated() throws Exception {
        when(request.getSession(false)).thenReturn(null);
        
        servlet.doGet(request, response);
        
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized");
    }

    @Test
    public void testDoGet_NonStudent() throws Exception {
        when(session.getAttribute("role")).thenReturn("ADMIN");
        
        servlet.doGet(request, response);
        
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized");
    }
}
