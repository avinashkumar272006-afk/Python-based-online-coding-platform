package com.codingplatform.servlet;

import com.codingplatform.service.ChallengeService;
import com.codingplatform.model.Challenge;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.Mockito.*;

public class InstructorChallengeServletTest {

    private InstructorChallengeServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private ChallengeService challengeService;
    private RequestDispatcher dispatcher;

    @BeforeEach
    public void setUp() throws Exception {
        servlet = new InstructorChallengeServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        challengeService = mock(ChallengeService.class);
        dispatcher = mock(RequestDispatcher.class);

        servlet.setChallengeService(challengeService);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(session.getAttribute("userId")).thenReturn(10L);
    }

    @Test
    public void testDoGet() throws Exception {
        when(request.getRequestDispatcher("/instructor/challenges.jsp")).thenReturn(dispatcher);
        when(challengeService.getInstructorChallenges(10L)).thenReturn(Arrays.asList(new Challenge()));
        
        servlet.doGet(request, response);
        
        verify(request).setAttribute(eq("challenges"), anyList());
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testDoPost_Create() throws Exception {
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("title")).thenReturn("T");
        when(request.getParameter("description")).thenReturn("D");
        when(request.getParameter("difficulty")).thenReturn("EASY");
        
        servlet.doPost(request, response);
        
        verify(challengeService).createChallenge(any(Challenge.class), eq(10L));
        verify(response).sendRedirect(anyString());
    }

    @Test
    public void testDoPost_Create_Invalid() throws Exception {
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("title")).thenReturn("");
        when(request.getParameter("description")).thenReturn("D");
        when(request.getParameter("difficulty")).thenReturn("EASY");
        
        doThrow(new IllegalArgumentException("Title cannot be empty")).when(challengeService).createChallenge(any(Challenge.class), eq(10L));
        when(request.getRequestDispatcher("/instructor/challenges.jsp")).thenReturn(dispatcher);
        
        servlet.doPost(request, response);
        
        verify(request).setAttribute(eq("error"), anyString());
        verify(dispatcher).forward(request, response);
    }
}
