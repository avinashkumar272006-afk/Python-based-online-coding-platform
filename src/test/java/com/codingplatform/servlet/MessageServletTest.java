package com.codingplatform.servlet;

import com.codingplatform.model.Message;
import com.codingplatform.service.MessageService;
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

class MessageServletTest {

    @Mock
    private MessageService messageService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher requestDispatcher;

    private MessageServlet messageServlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        messageServlet = new MessageServlet(messageService);
        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("");
    }

    @Test
    void doGet_Unauthenticated_SendsForbidden() throws Exception {
        when(session.getAttribute("userId")).thenReturn(null);
        
        messageServlet.doGet(request, response);
        
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
    }

    @Test
    void doGet_Admin_SendsForbidden() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("ADMIN");
        
        messageServlet.doGet(request, response);
        
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
    }

    @Test
    void doPost_ValidMessage_SendsRedirect() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(request.getParameter("receiverId")).thenReturn("2");
        when(request.getParameter("message")).thenReturn("Hello");

        messageServlet.doPost(request, response);

        verify(messageService).sendMessage(1, "STUDENT", 2, "Hello");
        verify(response).sendRedirect("/messages?partnerId=2");
    }

    @Test
    void doPost_EmptyMessage_SetsErrorAndRedirects() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(request.getParameter("receiverId")).thenReturn("2");
        when(request.getParameter("message")).thenReturn("");

        messageServlet.doPost(request, response);

        verify(session).setAttribute("errorMessage", "Message cannot be empty.");
        verify(response).sendRedirect("/messages?partnerId=2");
        verify(messageService, never()).sendMessage(anyInt(), anyString(), anyInt(), anyString());
    }
}
