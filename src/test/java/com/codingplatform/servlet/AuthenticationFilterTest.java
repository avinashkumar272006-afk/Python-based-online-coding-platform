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

    @Test
    public void testUnauthenticatedUserCannotAccessMessagesRoute() throws Exception {
        when(request.getRequestURI()).thenReturn("/messages");
        when(request.getSession(false)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/login.jsp");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testUnauthenticatedUserCannotAccessMessagesListJsp() throws Exception {
        when(request.getRequestURI()).thenReturn("/messages/list.jsp");
        when(request.getSession(false)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/login.jsp");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testUnauthenticatedUserCannotAccessMessagesConversationJsp() throws Exception {
        when(request.getRequestURI()).thenReturn("/messages/conversation.jsp");
        when(request.getSession(false)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/login.jsp");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testStudentCanAccessMessagesRoute() throws Exception {
        when(request.getRequestURI()).thenReturn("/messages");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");

        filter.doFilter(request, response, chain);

        verify(response, never()).sendRedirect(anyString());
        verify(chain).doFilter(request, response);
    }

    @Test
    public void testStudentCanAccessMessagesConversationJsp() throws Exception {
        when(request.getRequestURI()).thenReturn("/messages/conversation.jsp");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");

        filter.doFilter(request, response, chain);

        verify(response, never()).sendRedirect(anyString());
        verify(chain).doFilter(request, response);
    }

    @Test
    public void testInstructorCanAccessMessagesRoute() throws Exception {
        when(request.getRequestURI()).thenReturn("/messages");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");

        filter.doFilter(request, response, chain);

        verify(response, never()).sendRedirect(anyString());
        verify(chain).doFilter(request, response);
    }

    @Test
    public void testInstructorCanAccessMessagesListJsp() throws Exception {
        when(request.getRequestURI()).thenReturn("/messages/list.jsp");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");

        filter.doFilter(request, response, chain);

        verify(response, never()).sendRedirect(anyString());
        verify(chain).doFilter(request, response);
    }

    @Test
    public void testAdminCannotAccessMessagesRoute() throws Exception {
        when(request.getRequestURI()).thenReturn("/messages");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(3);
        when(session.getAttribute("role")).thenReturn("ADMIN");

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/login.jsp");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testAdminCannotAccessMessagesListJsp() throws Exception {
        when(request.getRequestURI()).thenReturn("/messages/list.jsp");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(3);
        when(session.getAttribute("role")).thenReturn("ADMIN");

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/login.jsp");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testAdminCannotAccessMessagesConversationJsp() throws Exception {
        when(request.getRequestURI()).thenReturn("/messages/conversation.jsp");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(3);
        when(session.getAttribute("role")).thenReturn("ADMIN");

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/login.jsp");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testUnsupportedRoleCannotAccessMessagesRoute() throws Exception {
        when(request.getRequestURI()).thenReturn("/messages");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(4);
        when(session.getAttribute("role")).thenReturn("GUEST");

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/login.jsp");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testUnauthenticatedUserCannotAccessProfileRoute() throws Exception {
        when(request.getRequestURI()).thenReturn("/profile");
        when(request.getSession(false)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/login.jsp");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testUnauthenticatedUserCannotAccessProfileJsp() throws Exception {
        when(request.getRequestURI()).thenReturn("/profile.jsp");
        when(request.getSession(false)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/login.jsp");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testStudentCanAccessProfileRoute() throws Exception {
        when(request.getRequestURI()).thenReturn("/profile");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");

        filter.doFilter(request, response, chain);

        verify(response, never()).sendRedirect(anyString());
        verify(chain).doFilter(request, response);
    }

    @Test
    public void testInstructorCanAccessProfileRoute() throws Exception {
        when(request.getRequestURI()).thenReturn("/profile");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");

        filter.doFilter(request, response, chain);

        verify(response, never()).sendRedirect(anyString());
        verify(chain).doFilter(request, response);
    }

    @Test
    public void testAdminCanAccessProfileRoute() throws Exception {
        when(request.getRequestURI()).thenReturn("/profile");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(3);
        when(session.getAttribute("role")).thenReturn("ADMIN");

        filter.doFilter(request, response, chain);

        verify(response, never()).sendRedirect(anyString());
        verify(chain).doFilter(request, response);
    }

    @Test
    public void testStudentCanAccessProfileJsp() throws Exception {
        when(request.getRequestURI()).thenReturn("/profile.jsp");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");

        filter.doFilter(request, response, chain);

        verify(response, never()).sendRedirect(anyString());
        verify(chain).doFilter(request, response);
    }

    @Test
    public void testInstructorCanAccessProfileJsp() throws Exception {
        when(request.getRequestURI()).thenReturn("/profile.jsp");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");

        filter.doFilter(request, response, chain);

        verify(response, never()).sendRedirect(anyString());
        verify(chain).doFilter(request, response);
    }

    @Test
    public void testAdminCanAccessProfileJsp() throws Exception {
        when(request.getRequestURI()).thenReturn("/profile.jsp");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(3);
        when(session.getAttribute("role")).thenReturn("ADMIN");

        filter.doFilter(request, response, chain);

        verify(response, never()).sendRedirect(anyString());
        verify(chain).doFilter(request, response);
    }
}
