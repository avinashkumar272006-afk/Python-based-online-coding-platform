package com.codingplatform.servlet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class AuthenticationFilterTest {

    private AuthenticationFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;
    private HttpSession session;

    @BeforeEach
    public void setUp() throws Exception {
        filter = new AuthenticationFilter();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
        session = mock(HttpSession.class);
        when(request.getContextPath()).thenReturn("");
    }

    @Test
    public void testUnauthenticatedUserCannotAccessProtectedDashboard() throws Exception {
        when(request.getRequestURI()).thenReturn("/student/dashboard.jsp");
        when(request.getSession(false)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/login.jsp");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testStudentCannotAccessAdminDashboard() throws Exception {
        when(request.getRequestURI()).thenReturn("/admin/dashboard.jsp");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/login.jsp");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testInstructorCannotAccessStudentDashboard() throws Exception {
        when(request.getRequestURI()).thenReturn("/student/dashboard.jsp");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/login.jsp");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testCorrectRoleCanAccessItsDashboard() throws Exception {
        when(request.getRequestURI()).thenReturn("/student/dashboard.jsp");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(3);
        when(session.getAttribute("role")).thenReturn("STUDENT");

        filter.doFilter(request, response, chain);

        verify(response, never()).sendRedirect(anyString());
        verify(chain).doFilter(request, response);
    }

    @Test
    public void testStudentCannotAccessInstructorExercises() throws Exception {
        when(request.getRequestURI()).thenReturn("/instructor/exercises");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/login.jsp");
        verify(chain, never()).doFilter(request, response);
    }
}
