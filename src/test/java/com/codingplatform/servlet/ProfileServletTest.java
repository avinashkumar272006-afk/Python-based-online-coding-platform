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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class ProfileServletTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    @InjectMocks
    private ProfileServlet profileServlet;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        profileServlet.setUserService(userService);
        when(request.getSession(false)).thenReturn(session);
        when(request.getContextPath()).thenReturn("");
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    public void testDoGetUnauthenticated() throws Exception {
        when(request.getSession(false)).thenReturn(null);
        profileServlet.doGet(request, response);
        verify(response).sendRedirect("/login.jsp");
    }

    @Test
    public void testDoGetAuthenticatedStudent() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        User student = new Student();
        student.setId(1);
        student.setName("Alice");
        student.setEmail("alice@test.com");
        student.setRole("STUDENT");

        when(userService.getUserById(1)).thenReturn(student);

        profileServlet.doGet(request, response);

        verify(request).setAttribute("user", student);
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testDoPostUpdateSuccess() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(request.getParameter("name")).thenReturn("Alice Updated");
        when(request.getParameter("email")).thenReturn("alice2@test.com");
        
        when(userService.updateUserProfile(1, "Alice Updated", "alice2@test.com")).thenReturn(true);
        
        User updatedStudent = new Student();
        updatedStudent.setId(1);
        updatedStudent.setName("Alice Updated");
        updatedStudent.setEmail("alice2@test.com");
        when(userService.getUserById(1)).thenReturn(updatedStudent);

        profileServlet.doPost(request, response);

        verify(session).setAttribute("name", "Alice Updated");
        verify(session).setAttribute("email", "alice2@test.com");
        verify(request).setAttribute("successMessage", "Profile updated successfully.");
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testDoPostUpdateValidationError() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(request.getParameter("name")).thenReturn("");
        when(request.getParameter("email")).thenReturn("alice2@test.com");
        
        when(userService.updateUserProfile(1, "", "alice2@test.com"))
            .thenThrow(new IllegalArgumentException("Name is required."));
        
        User student = new Student();
        when(userService.getUserById(1)).thenReturn(student);

        profileServlet.doPost(request, response);

        verify(request).setAttribute("errorMessage", "Name is required.");
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testDoPostUpdateDuplicateEmail() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(request.getParameter("name")).thenReturn("Alice");
        when(request.getParameter("email")).thenReturn("existing@test.com");
        
        when(userService.updateUserProfile(1, "Alice", "existing@test.com"))
            .thenThrow(new IllegalArgumentException("Email is already registered by another user."));
        
        User student = new Student();
        when(userService.getUserById(1)).thenReturn(student);

        profileServlet.doPost(request, response);

        verify(request).setAttribute("errorMessage", "Email is already registered by another user.");
        verify(dispatcher).forward(request, response);
    }
}
