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
        verify(response).sendRedirect(anyString());
    }
}
