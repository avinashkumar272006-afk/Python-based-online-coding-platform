package com.codingplatform.servlet;

import com.codingplatform.model.AdminDashboardStats;
import com.codingplatform.service.AdminService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AdminDashboardServletTest {

    private AdminDashboardServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private RequestDispatcher dispatcher;
    private AdminService adminService;

    @BeforeEach
    public void setUp() throws Exception {
        servlet = new AdminDashboardServlet();
        adminService = mock(AdminService.class);
        servlet.setAdminService(adminService);
        
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);
        
        when(request.getSession(false)).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    public void testDoGet_AuthenticatedAdmin() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        AdminDashboardStats stats = new AdminDashboardStats();
        when(adminService.getDashboardStats(1)).thenReturn(stats);
        
        servlet.doGet(request, response);
        
        verify(request).setAttribute("stats", stats);
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_Unauthenticated() throws Exception {
        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("/app");
        
        servlet.doGet(request, response);
        
        verify(response).sendRedirect("/app/login");
        verify(dispatcher, never()).forward(request, response);
    }
}
