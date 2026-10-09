package com.codingplatform.servlet;

import com.codingplatform.service.ProgressService;
import com.codingplatform.model.StudentProgress;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class StudentDashboardServletTest {

    private StudentDashboardServlet servlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private ProgressService progressService;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new StudentDashboardServlet() {
            @Override
            public void init() {
            }
        };
        servlet.setProgressService(progressService);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    public void testDoGetUnauthenticated() throws Exception {
        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("/test");
        
        servlet.doGet(request, response);
        verify(response).sendRedirect("/test/login");
    }

    @Test
    public void testDoGetAuthenticated() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(progressService.getStudentProgress(1)).thenReturn(new StudentProgress(10, 5, 2, 3, 90.0));
        
        servlet.doGet(request, response);
        
        verify(request).setAttribute(eq("progress"), any(StudentProgress.class));
        verify(request).getRequestDispatcher("/student/dashboard.jsp");
        verify(dispatcher).forward(request, response);
    }
}
