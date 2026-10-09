package com.codingplatform.servlet;


import com.codingplatform.service.SystemSettingsService;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.mockito.Mockito.*;

class SystemSettingsServletTest {

    @Mock
    private SystemSettingsService settingsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher requestDispatcher;

    private SystemSettingsServlet settingsServlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        settingsServlet = new SystemSettingsServlet(settingsService);
        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("");
    }

    @Test
    void doGet_AsAdmin_ForwardsToJsp() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("ADMIN");
        when(settingsService.getAllSettings(1, "ADMIN")).thenReturn(Collections.emptyList());
        when(request.getRequestDispatcher("/admin/settings.jsp")).thenReturn(requestDispatcher);

        settingsServlet.doGet(request, response);

        verify(request).setAttribute("settings", Collections.emptyList());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void doGet_AsStudent_SendsForbidden() throws Exception {
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("STUDENT");

        settingsServlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
    }

    @Test
    void doPost_AsAdmin_SuccessRedirects() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("ADMIN");
        when(request.getParameter("settingKey")).thenReturn("key1");
        when(request.getParameter("settingValue")).thenReturn("val1");

        settingsServlet.doPost(request, response);

        verify(settingsService).updateSetting(1, "ADMIN", "key1", "val1");
        verify(session).setAttribute(eq("successMessage"), anyString());
        verify(response).sendRedirect("/admin/settings");
    }

    @Test
    void doPost_AsAdmin_ExceptionSetsError() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("ADMIN");
        when(request.getParameter("settingKey")).thenReturn("key1");
        when(request.getParameter("settingValue")).thenReturn("val1");

        doThrow(new IllegalArgumentException("Invalid")).when(settingsService).updateSetting(1, "ADMIN", "key1", "val1");

        settingsServlet.doPost(request, response);

        verify(session).setAttribute("errorMessage", "Invalid");
        verify(response).sendRedirect("/admin/settings");
    }
}
