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

    @Test
    public void testRegisterSuccess_StudentRole() throws Exception {
        when(request.getParameter("name")).thenReturn("Jane Student");
        when(request.getParameter("email")).thenReturn("student@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("confirmPassword")).thenReturn("password123");
        when(request.getParameter("role")).thenReturn("STUDENT");
        when(request.getRequestDispatcher("/login.jsp")).thenReturn(requestDispatcher);
        when(mockUserService.registerUser(any(User.class))).thenReturn(true);

        servlet.doPost(request, response);

        verify(mockUserService).registerUser(argThat(user -> 
            "STUDENT".equals(user.getRole()) && user instanceof com.codingplatform.model.Student
        ));
        verify(request).setAttribute("successMessage", "Registration successful! Please login.");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testRegisterSuccess_DefaultRoleWhenMissing() throws Exception {
        when(request.getParameter("name")).thenReturn("No Role User");
        when(request.getParameter("email")).thenReturn("norole@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("confirmPassword")).thenReturn("password123");
        when(request.getParameter("role")).thenReturn(null);
        when(request.getRequestDispatcher("/login.jsp")).thenReturn(requestDispatcher);
        when(mockUserService.registerUser(any(User.class))).thenReturn(true);

        servlet.doPost(request, response);

        verify(mockUserService).registerUser(argThat(user -> 
            "STUDENT".equals(user.getRole()) && user instanceof com.codingplatform.model.Student
        ));
        verify(request).setAttribute("successMessage", "Registration successful! Please login.");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testRegisterSuccess_DefaultRoleWhenEmpty() throws Exception {
        when(request.getParameter("name")).thenReturn("Empty Role User");
        when(request.getParameter("email")).thenReturn("emptyrole@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("confirmPassword")).thenReturn("password123");
        when(request.getParameter("role")).thenReturn("   ");
        when(request.getRequestDispatcher("/login.jsp")).thenReturn(requestDispatcher);
        when(mockUserService.registerUser(any(User.class))).thenReturn(true);

        servlet.doPost(request, response);

        verify(mockUserService).registerUser(argThat(user -> 
            "STUDENT".equals(user.getRole()) && user instanceof com.codingplatform.model.Student
        ));
        verify(request).setAttribute("successMessage", "Registration successful! Please login.");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testRegister_ForgedAdminRoleRejected() throws Exception {
        when(request.getParameter("name")).thenReturn("Malicious User");
        when(request.getParameter("email")).thenReturn("attacker@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("confirmPassword")).thenReturn("password123");
        when(request.getParameter("role")).thenReturn("ADMIN");
        when(request.getRequestDispatcher("/register.jsp")).thenReturn(requestDispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute("errorMessage", "Registration with ADMIN role is not permitted.");
        verify(requestDispatcher).forward(request, response);
        verify(mockUserService, never()).registerUser(any(User.class));
    }

    @Test
    public void testRegister_ForgedAdminRoleLowerCaseRejected() throws Exception {
        when(request.getParameter("name")).thenReturn("Malicious User 2");
        when(request.getParameter("email")).thenReturn("attacker2@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("confirmPassword")).thenReturn("password123");
        when(request.getParameter("role")).thenReturn("admin");
        when(request.getRequestDispatcher("/register.jsp")).thenReturn(requestDispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute("errorMessage", "Registration with ADMIN role is not permitted.");
        verify(requestDispatcher).forward(request, response);
        verify(mockUserService, never()).registerUser(any(User.class));
    }

    @Test
    public void testRegister_UnsupportedRoleRejected() throws Exception {
        when(request.getParameter("name")).thenReturn("Hacker User");
        when(request.getParameter("email")).thenReturn("hacker@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("confirmPassword")).thenReturn("password123");
        when(request.getParameter("role")).thenReturn("SUPERUSER");
        when(request.getRequestDispatcher("/register.jsp")).thenReturn(requestDispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute("errorMessage", "Invalid role selected.");
        verify(requestDispatcher).forward(request, response);
        verify(mockUserService, never()).registerUser(any(User.class));
    }

    @Test
    public void testRegisterSuccess_InstructorRole() throws Exception {
        when(request.getParameter("name")).thenReturn("Jane Instructor");
        when(request.getParameter("email")).thenReturn("instructor@example.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("confirmPassword")).thenReturn("password123");
        when(request.getParameter("role")).thenReturn("INSTRUCTOR");
        when(request.getRequestDispatcher("/login.jsp")).thenReturn(requestDispatcher);
        when(mockUserService.registerUser(any(User.class))).thenReturn(true);

        servlet.doPost(request, response);

        verify(mockUserService).registerUser(argThat(user -> 
            "INSTRUCTOR".equals(user.getRole()) && user instanceof com.codingplatform.model.Instructor
        ));
        verify(request).setAttribute("successMessage", "Registration successful! Please login.");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_ForwardsToRegisterJsp() throws Exception {
        when(request.getRequestDispatcher("/register.jsp")).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
    }
}
