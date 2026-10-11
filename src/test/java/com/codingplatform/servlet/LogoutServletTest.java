package com.codingplatform.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class LogoutServletTest {

    private LogoutServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    @BeforeEach
    public void setUp() {
        servlet = new LogoutServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        when(request.getContextPath()).thenReturn("");
    }

    @Test
    public void testDoGetDoesNotInvalidateSession_Student() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");

        servlet.doGet(request, response);

        verify(session, never()).invalidate();
        verify(response).sendRedirect("/student/dashboard");
    }

    @Test
    public void testDoGetDoesNotInvalidateSession_Instructor() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");

        servlet.doGet(request, response);

        verify(session, never()).invalidate();
        verify(response).sendRedirect("/instructor/dashboard");
    }

    @Test
    public void testDoGetDoesNotInvalidateSession_Admin() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(3);
        when(session.getAttribute("role")).thenReturn("ADMIN");

        servlet.doGet(request, response);

        verify(session, never()).invalidate();
        verify(response).sendRedirect("/admin/dashboard");
    }

    @Test
    public void testDoGetDoesNotInvalidateSession_Unauthenticated() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendRedirect("/login.jsp");
    }

    @Test
    public void testDoPostInvalidatesSessionAndRedirects() throws Exception {
        when(request.getSession(false)).thenReturn(session);

        servlet.doPost(request, response);

        verify(session).invalidate();
        verify(response).sendRedirect("/login.jsp?logout=true");
    }

    @Test
    public void testDoPostWhenSessionIsNull_RedirectsSafely() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/login.jsp?logout=true");
    }
}
