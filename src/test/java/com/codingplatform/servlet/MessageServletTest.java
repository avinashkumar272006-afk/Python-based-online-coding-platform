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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @Mock
    private com.codingplatform.dao.UserDAO userDAO;

    private MessageServlet messageServlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        messageServlet = new MessageServlet(messageService);
        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("");
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
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
    void doGet_StudentOpensConversationWithInstructor_Allowed() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(request.getParameter("partnerId")).thenReturn("2");

        com.codingplatform.model.User instructor = new com.codingplatform.model.Instructor();
        instructor.setId(2);
        instructor.setName("Instructor Bob");
        instructor.setRole("INSTRUCTOR");
        when(messageService.getConversationPartner(1, "STUDENT", 2)).thenReturn(instructor);

        List<Message> mockConversation = Collections.singletonList(new Message(1, 2, "Hello"));
        when(messageService.getConversation(1, "STUDENT", 2)).thenReturn(mockConversation);

        messageServlet.doGet(request, response);

        verify(messageService).getConversationPartner(1, "STUDENT", 2);
        verify(request).setAttribute("conversation", mockConversation);
        verify(request).setAttribute("partnerId", 2);
        verify(request).setAttribute("partnerName", "Instructor Bob");
        verify(request).getRequestDispatcher("/messages/conversation.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void doGet_InstructorOpensConversationWithStudent_Allowed() throws Exception {
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getParameter("partnerId")).thenReturn("1");

        com.codingplatform.model.User student = new com.codingplatform.model.Student();
        student.setId(1);
        student.setName("Student Alice");
        student.setRole("STUDENT");
        when(messageService.getConversationPartner(2, "INSTRUCTOR", 1)).thenReturn(student);

        List<Message> mockConversation = Collections.singletonList(new Message(1, 2, "Hello"));
        when(messageService.getConversation(2, "INSTRUCTOR", 1)).thenReturn(mockConversation);

        messageServlet.doGet(request, response);

        verify(messageService).getConversationPartner(2, "INSTRUCTOR", 1);
        verify(request).setAttribute("conversation", mockConversation);
        verify(request).setAttribute("partnerId", 1);
        verify(request).setAttribute("partnerName", "Student Alice");
        verify(request).getRequestDispatcher("/messages/conversation.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void doGet_StudentSelectsAnotherStudent_Rejected() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(request.getParameter("partnerId")).thenReturn("3");

        when(messageService.getConversationPartner(1, "STUDENT", 3))
                .thenThrow(new SecurityException("Students can only converse with instructors."));

        messageServlet.doGet(request, response);

        verify(messageService).getConversationPartner(1, "STUDENT", 3);
        verify(session).setAttribute("errorMessage", "Students can only converse with instructors.");
        verify(response).sendRedirect("/messages");
        verify(request, never()).setAttribute(eq("partnerName"), anyString());
        verify(requestDispatcher, never()).forward(request, response);
    }

    @Test
    void doGet_StudentSelectsAdmin_Rejected() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(request.getParameter("partnerId")).thenReturn("99");

        when(messageService.getConversationPartner(1, "STUDENT", 99))
                .thenThrow(new SecurityException("Students can only converse with instructors."));

        messageServlet.doGet(request, response);

        verify(messageService).getConversationPartner(1, "STUDENT", 99);
        verify(session).setAttribute("errorMessage", "Students can only converse with instructors.");
        verify(response).sendRedirect("/messages");
        verify(request, never()).setAttribute(eq("partnerName"), anyString());
        verify(requestDispatcher, never()).forward(request, response);
    }

    @Test
    void doGet_InstructorSelectsAnotherInstructor_Rejected() throws Exception {
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getParameter("partnerId")).thenReturn("4");

        when(messageService.getConversationPartner(2, "INSTRUCTOR", 4))
                .thenThrow(new SecurityException("Instructors can only converse with students."));

        messageServlet.doGet(request, response);

        verify(messageService).getConversationPartner(2, "INSTRUCTOR", 4);
        verify(session).setAttribute("errorMessage", "Instructors can only converse with students.");
        verify(response).sendRedirect("/messages");
        verify(request, never()).setAttribute(eq("partnerName"), anyString());
        verify(requestDispatcher, never()).forward(request, response);
    }

    @Test
    void doGet_InstructorSelectsAdmin_Rejected() throws Exception {
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getParameter("partnerId")).thenReturn("99");

        when(messageService.getConversationPartner(2, "INSTRUCTOR", 99))
                .thenThrow(new SecurityException("Instructors can only converse with students."));

        messageServlet.doGet(request, response);

        verify(messageService).getConversationPartner(2, "INSTRUCTOR", 99);
        verify(session).setAttribute("errorMessage", "Instructors can only converse with students.");
        verify(response).sendRedirect("/messages");
        verify(request, never()).setAttribute(eq("partnerName"), anyString());
        verify(requestDispatcher, never()).forward(request, response);
    }

    @Test
    void doGet_MissingPartner_Rejected() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(request.getParameter("partnerId")).thenReturn("999");
        when(messageService.getConversationPartner(1, "STUDENT", 999))
                .thenThrow(new IllegalArgumentException("Conversation partner not found."));

        messageServlet.doGet(request, response);

        verify(messageService).getConversationPartner(1, "STUDENT", 999);
        verify(session).setAttribute("errorMessage", "Conversation partner not found.");
        verify(response).sendRedirect("/messages");
        verify(requestDispatcher, never()).forward(request, response);
    }

    @Test
    void doGet_InvalidPartnerId_Rejected() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(request.getParameter("partnerId")).thenReturn("invalid_id");

        messageServlet.doGet(request, response);

        verify(session).setAttribute("errorMessage", "Invalid partner ID.");
        verify(response).sendRedirect("/messages");
        verify(messageService, never()).getConversationPartner(anyInt(), anyString(), anyInt());
        verify(requestDispatcher, never()).forward(request, response);
    }

    @Test
    void doGet_StudentListPartners_Success() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(request.getParameter("partnerId")).thenReturn(null);

        com.codingplatform.model.User instructor = new com.codingplatform.model.Instructor();
        when(messageService.getAvailableInstructors()).thenReturn(Collections.singletonList(instructor));

        messageServlet.doGet(request, response);

        verify(request).setAttribute(eq("partners"), anyList());
        verify(request).getRequestDispatcher("/messages/list.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void doGet_InstructorListPartners_Success() throws Exception {
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getParameter("partnerId")).thenReturn(null);

        com.codingplatform.model.User student = new com.codingplatform.model.Student();
        when(messageService.getAvailableStudents("INSTRUCTOR")).thenReturn(Collections.singletonList(student));

        messageServlet.doGet(request, response);

        verify(messageService).getAvailableStudents("INSTRUCTOR");
        verify(request).setAttribute(eq("partners"), anyList());
        verify(request).getRequestDispatcher("/messages/list.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void doGet_InstructorListsStudentsWhenNoConversationsExist() throws Exception {
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getParameter("partnerId")).thenReturn(null);

        com.codingplatform.model.User student1 = new com.codingplatform.model.Student();
        student1.setId(10);
        student1.setName("Student 1");
        com.codingplatform.model.User student2 = new com.codingplatform.model.Student();
        student2.setId(11);
        student2.setName("Student 2");

        List<com.codingplatform.model.User> availableStudents = java.util.Arrays.asList(student1, student2);
        when(messageService.getAvailableStudents("INSTRUCTOR")).thenReturn(availableStudents);

        messageServlet.doGet(request, response);

        verify(messageService).getAvailableStudents("INSTRUCTOR");
        verify(request).setAttribute("partners", availableStudents);
        verify(request).getRequestDispatcher("/messages/list.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void doGet_InstructorInitiatesConversationWithStudent_NoPreviousMessages() throws Exception {
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getParameter("partnerId")).thenReturn("5");

        com.codingplatform.model.User newStudent = new com.codingplatform.model.Student();
        newStudent.setId(5);
        newStudent.setName("New Student");
        newStudent.setRole("STUDENT");

        when(messageService.getConversationPartner(2, "INSTRUCTOR", 5)).thenReturn(newStudent);
        when(messageService.getConversation(2, "INSTRUCTOR", 5)).thenReturn(Collections.emptyList());

        messageServlet.doGet(request, response);

        verify(messageService).getConversationPartner(2, "INSTRUCTOR", 5);
        verify(messageService).getConversation(2, "INSTRUCTOR", 5);
        verify(request).setAttribute("conversation", Collections.emptyList());
        verify(request).setAttribute("partnerId", 5);
        verify(request).setAttribute("partnerName", "New Student");
        verify(request).getRequestDispatcher("/messages/conversation.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void doGet_InstructorOpensExistingConversation_HistoryPreserved() throws Exception {
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getParameter("partnerId")).thenReturn("1");

        com.codingplatform.model.User student = new com.codingplatform.model.Student();
        student.setId(1);
        student.setName("Student Alice");
        student.setRole("STUDENT");

        List<Message> history = java.util.Arrays.asList(
                new Message(1, 2, "Question 1"),
                new Message(2, 1, "Answer 1")
        );

        when(messageService.getConversationPartner(2, "INSTRUCTOR", 1)).thenReturn(student);
        when(messageService.getConversation(2, "INSTRUCTOR", 1)).thenReturn(history);

        messageServlet.doGet(request, response);

        verify(messageService).getConversationPartner(2, "INSTRUCTOR", 1);
        verify(messageService).getConversation(2, "INSTRUCTOR", 1);
        verify(request).setAttribute("conversation", history);
        verify(request).setAttribute("partnerId", 1);
        verify(request).setAttribute("partnerName", "Student Alice");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void doGet_StudentCannotUseStudentDirectoryFeature() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(request.getParameter("action")).thenReturn("students");

        messageServlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        verify(messageService, never()).getAvailableStudents(anyString());
        verify(requestDispatcher, never()).forward(request, response);
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

    @Test
    void doPost_InvalidReceiverId_SetsErrorAndRedirects() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(request.getParameter("receiverId")).thenReturn("abc");
        when(request.getParameter("message")).thenReturn("Hello");

        messageServlet.doPost(request, response);

        verify(session).setAttribute("errorMessage", "Invalid receiver ID.");
        verify(response).sendRedirect("/messages");
        verify(messageService, never()).sendMessage(anyInt(), anyString(), anyInt(), anyString());
    }

    @Test
    void doPost_MissingReceiverId_SetsErrorAndRedirects() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(request.getParameter("receiverId")).thenReturn(null);
        when(request.getParameter("message")).thenReturn("Hello");

        messageServlet.doPost(request, response);

        verify(session).setAttribute("errorMessage", "Invalid receiver ID.");
        verify(response).sendRedirect("/messages");
        verify(messageService, never()).sendMessage(anyInt(), anyString(), anyInt(), anyString());
    }

    @Test
    void doPost_SecurityException_RedirectsWithError() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(request.getParameter("receiverId")).thenReturn("3");
        when(request.getParameter("message")).thenReturn("Hello");
        doThrow(new SecurityException("Students can only message instructors."))
                .when(messageService).sendMessage(1, "STUDENT", 3, "Hello");

        messageServlet.doPost(request, response);

        verify(session).setAttribute("errorMessage", "Students can only message instructors.");
        verify(response).sendRedirect("/messages");
    }

    @Test
    void doGet_SelfConversation_Rejected() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(request.getParameter("partnerId")).thenReturn("1");

        when(messageService.getConversationPartner(1, "STUDENT", 1))
                .thenThrow(new SecurityException("Users cannot converse with themselves."));

        messageServlet.doGet(request, response);

        verify(messageService).getConversationPartner(1, "STUDENT", 1);
        verify(session).setAttribute("errorMessage", "Users cannot converse with themselves.");
        verify(response).sendRedirect("/messages");
        verify(request, never()).setAttribute(eq("partnerName"), anyString());
        verify(requestDispatcher, never()).forward(request, response);
    }

    @Test
    void testConstructorsAndServiceInjection() {
        MessageServlet defaultServlet = new MessageServlet();
        assertNotNull(defaultServlet);

        MessageServlet singleArgServlet = new MessageServlet(messageService);
        assertNotNull(singleArgServlet);

        MessageServlet twoArgServlet = new MessageServlet(messageService, userDAO);
        assertNotNull(twoArgServlet);

        defaultServlet.setMessageService(messageService);
    }
}

