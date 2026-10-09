package com.codingplatform.servlet;

import com.codingplatform.model.Student;
import com.codingplatform.model.User;
import com.codingplatform.service.UserService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class LoginServletTest {

    private LoginServlet servlet;
    private UserService mockUserService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private RequestDispatcher requestDispatcher;

    @BeforeEach
    public void setUp() {
        servlet = new LoginServlet();
        mockUserService = mock(UserService.class);
        servlet.setUserService(mockUserService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        requestDispatcher = mock(RequestDispatcher.class);
    }

    @Test
    public void testSuccessfulLoginCreatesSession() throws Exception {
        User user = new Student();
        user.setId(1);
        user.setName("Test Student");
        user.setEmail("student@test.com");
        
        when(request.getParameter("email")).thenReturn("student@test.com");
        when(request.getParameter("password")).thenReturn("password");
        when(mockUserService.authenticateUser("student@test.com", "password")).thenReturn(user);
        when(request.getSession(true)).thenReturn(session);
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(session).setAttribute("userId", 1);
        verify(session).setAttribute("name", "Test Student");
        verify(session).setAttribute("email", "student@test.com");
        verify(session).setAttribute("role", "STUDENT");
        verify(response).sendRedirect("/student/dashboard");
    }

    @Test
    public void testWrongPassword() throws Exception {
        when(request.getParameter("email")).thenReturn("student@test.com");
        when(request.getParameter("password")).thenReturn("wrongpassword");
        when(mockUserService.authenticateUser("student@test.com", "wrongpassword")).thenReturn(null);
        when(request.getRequestDispatcher("/login.jsp")).thenReturn(requestDispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute("errorMessage", "Invalid email or password.");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testUnknownEmail() throws Exception {
        when(request.getParameter("email")).thenReturn("unknown@test.com");
        when(request.getParameter("password")).thenReturn("password");
        when(mockUserService.authenticateUser("unknown@test.com", "password")).thenReturn(null);
        when(request.getRequestDispatcher("/login.jsp")).thenReturn(requestDispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute("errorMessage", "Invalid email or password.");
        verify(requestDispatcher).forward(request, response);
    }
}
