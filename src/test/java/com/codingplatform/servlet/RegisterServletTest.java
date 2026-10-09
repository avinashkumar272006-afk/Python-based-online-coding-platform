package com.codingplatform.servlet;

import com.codingplatform.model.User;
import com.codingplatform.service.UserService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class RegisterServletTest {

    private RegisterServlet servlet;
    private UserService mockUserService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher requestDispatcher;

    @BeforeEach
    public void setUp() {
        servlet = new RegisterServlet();
        mockUserService = mock(UserService.class);
        servlet.setUserService(mockUserService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        requestDispatcher = mock(RequestDispatcher.class);
    }

    @Test
    public void testPasswordMismatch() throws Exception {
        when(request.getParameter("name")).thenReturn("Test User");
        when(request.getParameter("email")).thenReturn("test@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("confirmPassword")).thenReturn("password456");
        when(request.getParameter("role")).thenReturn("STUDENT");
        when(request.getRequestDispatcher("/register.jsp")).thenReturn(requestDispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute("errorMessage", "Passwords do not match.");
        verify(requestDispatcher).forward(request, response);
        verify(mockUserService, never()).registerUser(any(User.class));
    }
}
