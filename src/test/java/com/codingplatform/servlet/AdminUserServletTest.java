package com.codingplatform.servlet;

import com.codingplatform.service.AdminService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AdminUserServletTest {

    private AdminUserServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private RequestDispatcher dispatcher;
    private AdminService adminService;

    @BeforeEach
    public void setUp() throws Exception {
        servlet = new AdminUserServlet();
        adminService = mock(AdminService.class);
        servlet.setAdminService(adminService);
        
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);
        
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    public void testDoGet_AuthenticatedAdmin() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(adminService.getAllUsers(1)).thenReturn(Collections.emptyList());
        
        servlet.doGet(request, response);
        
        verify(request).setAttribute("users", Collections.emptyList());
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testDoPost_UpdateRole() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(request.getParameter("action")).thenReturn("updateRole");
        when(request.getParameter("userId")).thenReturn("3");
        when(request.getParameter("role")).thenReturn("INSTRUCTOR");
        when(request.getContextPath()).thenReturn("/app");
        
        when(adminService.updateUserRole(1, 3, "INSTRUCTOR")).thenReturn(true);
        
        servlet.doPost(request, response);
        
        verify(adminService).updateUserRole(1, 3, "INSTRUCTOR");
        verify(session).setAttribute(eq("successMessage"), anyString());
        verify(response).sendRedirect("/app/admin/users");
    }
}
