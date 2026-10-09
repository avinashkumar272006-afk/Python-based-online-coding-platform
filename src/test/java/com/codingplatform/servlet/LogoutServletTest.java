package com.codingplatform.servlet;

import jakarta.servlet.RequestDispatcher;
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
    private RequestDispatcher requestDispatcher;

    @BeforeEach
    public void setUp() {
        servlet = new LogoutServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        requestDispatcher = mock(RequestDispatcher.class);
    }

    @Test
    public void testLogoutInvalidatesSession() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(request.getRequestDispatcher("/login.jsp")).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        verify(session).invalidate();
        verify(request).setAttribute("successMessage", "You have been successfully logged out.");
        verify(requestDispatcher).forward(request, response);
    }
}
