package com.codingplatform.service;

import com.codingplatform.dao.MessageDAO;
import com.codingplatform.dao.UserDAO;
import com.codingplatform.model.Message;
import com.codingplatform.model.User;
import com.codingplatform.model.Student;
import com.codingplatform.model.Instructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MessageServiceTest {

    @Mock
    private MessageDAO messageDAO;

    @Mock
    private UserDAO userDAO;

    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        messageService = new MessageServiceImpl(messageDAO, userDAO);
    }

    @Test
    void sendMessage_StudentToInstructor_Success() throws SQLException, com.codingplatform.exception.DatabaseException {
        User receiver = new Instructor();
        receiver.setId(2);
        receiver.setRole("INSTRUCTOR");
        when(userDAO.getUserById(2)).thenReturn(receiver);

        Message mockMessage = new Message(1, 2, "Hello");
        when(messageDAO.createMessage(any(Message.class))).thenReturn(mockMessage);

        Message result = messageService.sendMessage(1, "STUDENT", 2, "Hello");

        assertNotNull(result);
        assertEquals(1, result.getSenderId());
        verify(messageDAO).createMessage(any(Message.class));
    }

    @Test
    void sendMessage_InstructorToStudent_Success() throws SQLException, com.codingplatform.exception.DatabaseException {
        User receiver = new Student();
        receiver.setId(1);
        receiver.setRole("STUDENT");
        when(userDAO.getUserById(1)).thenReturn(receiver);

        Message mockMessage = new Message(2, 1, "Hi");
        when(messageDAO.createMessage(any(Message.class))).thenReturn(mockMessage);

        Message result = messageService.sendMessage(2, "INSTRUCTOR", 1, "Hi");

        assertNotNull(result);
        assertEquals(2, result.getSenderId());
        verify(messageDAO).createMessage(any(Message.class));
    }

    @Test
    void sendMessage_StudentToStudent_ThrowsException() throws SQLException, com.codingplatform.exception.DatabaseException {
        User receiver = new Student();
        receiver.setId(3);
        receiver.setRole("STUDENT");
        when(userDAO.getUserById(3)).thenReturn(receiver);

        assertThrows(SecurityException.class, () -> {
            messageService.sendMessage(1, "STUDENT", 3, "Hello");
        });
    }

    @Test
    void sendMessage_EmptyMessage_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            messageService.sendMessage(1, "STUDENT", 2, "   ");
        });
    }

    @Test
    void getConversation_MarksAsReadAndReturnsMessages() throws SQLException, com.codingplatform.exception.DatabaseException {
        User user1 = new Student();
        user1.setId(1);
        user1.setRole("STUDENT");
        User user2 = new Instructor();
        user2.setId(2);
        user2.setRole("INSTRUCTOR");
        when(userDAO.getUserById(1)).thenReturn(user1);
        when(userDAO.getUserById(2)).thenReturn(user2);

        List<Message> mockMessages = Arrays.asList(new Message(1, 2, "Hi"));
        when(messageDAO.getMessagesBetweenUsers(1, 2)).thenReturn(mockMessages);

        List<Message> result = messageService.getConversation(1, 2);

        assertEquals(1, result.size());
        verify(messageDAO).markMessagesAsRead(2, 1); // Marks messages from 2 to 1 as read
        verify(messageDAO).getMessagesBetweenUsers(1, 2);
    }

    @Test
    void getConversation_StudentToInstructor_Success() throws SQLException, com.codingplatform.exception.DatabaseException {
        User instructor = new Instructor();
        instructor.setId(2);
        instructor.setRole("INSTRUCTOR");
        when(userDAO.getUserById(2)).thenReturn(instructor);

        List<Message> mockMessages = Arrays.asList(new Message(1, 2, "Question about exercise"));
        when(messageDAO.getMessagesBetweenUsers(1, 2)).thenReturn(mockMessages);

        List<Message> result = messageService.getConversation(1, "STUDENT", 2);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(messageDAO).markMessagesAsRead(2, 1);
        verify(messageDAO).getMessagesBetweenUsers(1, 2);
    }

    @Test
    void getConversation_InstructorToStudent_Success() throws SQLException, com.codingplatform.exception.DatabaseException {
        User student = new Student();
        student.setId(1);
        student.setRole("STUDENT");
        when(userDAO.getUserById(1)).thenReturn(student);

        List<Message> mockMessages = Arrays.asList(new Message(2, 1, "Feedback on code"));
        when(messageDAO.getMessagesBetweenUsers(2, 1)).thenReturn(mockMessages);

        List<Message> result = messageService.getConversation(2, "INSTRUCTOR", 1);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(messageDAO).markMessagesAsRead(1, 2);
        verify(messageDAO).getMessagesBetweenUsers(2, 1);
    }

    @Test
    void getConversation_StudentToStudent_ThrowsSecurityException() throws com.codingplatform.exception.DatabaseException {
        User otherStudent = new Student();
        otherStudent.setId(3);
        otherStudent.setRole("STUDENT");
        when(userDAO.getUserById(3)).thenReturn(otherStudent);

        assertThrows(SecurityException.class, () -> {
            messageService.getConversation(1, "STUDENT", 3);
        });
    }

    @Test
    void getConversation_StudentToAdmin_ThrowsSecurityException() throws com.codingplatform.exception.DatabaseException {
        User admin = new com.codingplatform.model.Admin();
        admin.setId(99);
        admin.setRole("ADMIN");
        when(userDAO.getUserById(99)).thenReturn(admin);

        assertThrows(SecurityException.class, () -> {
            messageService.getConversation(1, "STUDENT", 99);
        });
    }

    @Test
    void getConversation_InstructorToInstructor_ThrowsSecurityException() throws com.codingplatform.exception.DatabaseException {
        User otherInstructor = new Instructor();
        otherInstructor.setId(4);
        otherInstructor.setRole("INSTRUCTOR");
        when(userDAO.getUserById(4)).thenReturn(otherInstructor);

        assertThrows(SecurityException.class, () -> {
            messageService.getConversation(2, "INSTRUCTOR", 4);
        });
    }

    @Test
    void getConversation_InstructorToAdmin_ThrowsSecurityException() throws com.codingplatform.exception.DatabaseException {
        User admin = new com.codingplatform.model.Admin();
        admin.setId(99);
        admin.setRole("ADMIN");
        when(userDAO.getUserById(99)).thenReturn(admin);

        assertThrows(SecurityException.class, () -> {
            messageService.getConversation(2, "INSTRUCTOR", 99);
        });
    }

    @Test
    void getConversation_MissingPartner_ThrowsIllegalArgumentException() throws com.codingplatform.exception.DatabaseException {
        when(userDAO.getUserById(999)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            messageService.getConversation(1, "STUDENT", 999);
        });
    }

    @Test
    void getConversation_DirectCall_CannotBypassRoleRules() throws com.codingplatform.exception.DatabaseException {
        User student1 = new Student();
        student1.setId(1);
        student1.setRole("STUDENT");
        User student2 = new Student();
        student2.setId(3);
        student2.setRole("STUDENT");

        when(userDAO.getUserById(1)).thenReturn(student1);
        when(userDAO.getUserById(3)).thenReturn(student2);

        // Calling direct two-argument getConversation cannot bypass student-to-student rule
        assertThrows(SecurityException.class, () -> {
            messageService.getConversation(1, 3);
        });
    }

    @Test
    void getConversation_AdminUser_ThrowsSecurityException() throws com.codingplatform.exception.DatabaseException {
        User student = new Student();
        student.setId(1);
        student.setRole("STUDENT");
        when(userDAO.getUserById(1)).thenReturn(student);

        assertThrows(SecurityException.class, () -> {
            messageService.getConversation(99, "ADMIN", 1);
        });
    }

    @Test
    void sendMessage_MessageTooLong_ThrowsException() {
        StringBuilder longMsg = new StringBuilder();
        for (int i = 0; i < 5001; i++) {
            longMsg.append("a");
        }
        assertThrows(IllegalArgumentException.class, () -> {
            messageService.sendMessage(1, "STUDENT", 2, longMsg.toString());
        });
    }

    @Test
    void sendMessage_NullSenderOrReceiver_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            messageService.sendMessage(null, "STUDENT", 2, "Hello");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            messageService.sendMessage(1, "STUDENT", null, "Hello");
        });
    }

    @Test
    void getConversationPartner_StudentToInstructor_Success() throws com.codingplatform.exception.DatabaseException {
        User instructor = new Instructor();
        instructor.setId(2);
        instructor.setName("Instructor Bob");
        instructor.setRole("INSTRUCTOR");
        when(userDAO.getUserById(2)).thenReturn(instructor);

        User result = messageService.getConversationPartner(1, "STUDENT", 2);

        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals("INSTRUCTOR", result.getRole());
        assertEquals("Instructor Bob", result.getName());
    }

    @Test
    void getConversationPartner_InstructorToStudent_Success() throws com.codingplatform.exception.DatabaseException {
        User student = new Student();
        student.setId(1);
        student.setName("Student Alice");
        student.setRole("STUDENT");
        when(userDAO.getUserById(1)).thenReturn(student);

        User result = messageService.getConversationPartner(2, "INSTRUCTOR", 1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("STUDENT", result.getRole());
        assertEquals("Student Alice", result.getName());
    }

    @Test
    void getConversationPartner_StudentToStudent_ThrowsSecurityException() throws com.codingplatform.exception.DatabaseException {
        User otherStudent = new Student();
        otherStudent.setId(3);
        otherStudent.setRole("STUDENT");
        when(userDAO.getUserById(3)).thenReturn(otherStudent);

        SecurityException ex = assertThrows(SecurityException.class, () -> {
            messageService.getConversationPartner(1, "STUDENT", 3);
        });
        assertEquals("Students can only converse with instructors.", ex.getMessage());
    }

    @Test
    void getConversationPartner_StudentToAdmin_ThrowsSecurityException() throws com.codingplatform.exception.DatabaseException {
        User admin = new com.codingplatform.model.Admin();
        admin.setId(99);
        admin.setRole("ADMIN");
        when(userDAO.getUserById(99)).thenReturn(admin);

        SecurityException ex = assertThrows(SecurityException.class, () -> {
            messageService.getConversationPartner(1, "STUDENT", 99);
        });
        assertEquals("Students can only converse with instructors.", ex.getMessage());
    }

    @Test
    void getConversationPartner_InstructorToInstructor_ThrowsSecurityException() throws com.codingplatform.exception.DatabaseException {
        User otherInstructor = new Instructor();
        otherInstructor.setId(4);
        otherInstructor.setRole("INSTRUCTOR");
        when(userDAO.getUserById(4)).thenReturn(otherInstructor);

        SecurityException ex = assertThrows(SecurityException.class, () -> {
            messageService.getConversationPartner(2, "INSTRUCTOR", 4);
        });
        assertEquals("Instructors can only converse with students.", ex.getMessage());
    }

    @Test
    void getConversationPartner_InstructorToAdmin_ThrowsSecurityException() throws com.codingplatform.exception.DatabaseException {
        User admin = new com.codingplatform.model.Admin();
        admin.setId(99);
        admin.setRole("ADMIN");
        when(userDAO.getUserById(99)).thenReturn(admin);

        SecurityException ex = assertThrows(SecurityException.class, () -> {
            messageService.getConversationPartner(2, "INSTRUCTOR", 99);
        });
        assertEquals("Instructors can only converse with students.", ex.getMessage());
    }

    @Test
    void getConversationPartner_SelfConversation_ThrowsSecurityException() {
        SecurityException ex = assertThrows(SecurityException.class, () -> {
            messageService.getConversationPartner(1, "STUDENT", 1);
        });
        assertEquals("Users cannot converse with themselves.", ex.getMessage());
    }

    @Test
    void getConversationPartner_MissingPartner_ThrowsIllegalArgumentException() throws com.codingplatform.exception.DatabaseException {
        when(userDAO.getUserById(999)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            messageService.getConversationPartner(1, "STUDENT", 999);
        });
        assertEquals("Conversation partner not found.", ex.getMessage());
    }

    @Test
    void getConversationPartner_NullPartnerId_ThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            messageService.getConversationPartner(1, "STUDENT", null);
        });
        assertEquals("Partner ID cannot be null.", ex.getMessage());
    }

    @Test
    void getConversationPartner_NullRole_ThrowsSecurityException() {
        SecurityException ex = assertThrows(SecurityException.class, () -> {
            messageService.getConversationPartner(1, null, 2);
        });
        assertEquals("User role must be specified.", ex.getMessage());
    }

    @Test
    void getConversationPartner_AdminRole_ThrowsSecurityException() throws com.codingplatform.exception.DatabaseException {
        User student = new Student();
        student.setId(1);
        student.setRole("STUDENT");
        when(userDAO.getUserById(1)).thenReturn(student);

        SecurityException ex = assertThrows(SecurityException.class, () -> {
            messageService.getConversationPartner(99, "ADMIN", 1);
        });
        assertEquals("Admins cannot participate in messaging.", ex.getMessage());
    }

    @Test
    void getAvailableStudents_InstructorRole_Success() throws com.codingplatform.exception.DatabaseException {
        User student1 = new Student();
        student1.setId(1);
        student1.setRole("STUDENT");
        student1.setName("Alice");

        User student2 = new Student();
        student2.setId(3);
        student2.setRole("STUDENT");
        student2.setName("Charlie");

        when(userDAO.getUsersByRole("STUDENT")).thenReturn(Arrays.asList(student1, student2));

        List<User> students = messageService.getAvailableStudents("INSTRUCTOR");

        assertNotNull(students);
        assertEquals(2, students.size());
        assertEquals("Alice", students.get(0).getName());
        assertEquals("Charlie", students.get(1).getName());
        verify(userDAO).getUsersByRole("STUDENT");
    }

    @Test
    void getAvailableStudents_NoConversationsExist_ReturnsAllStudents() throws com.codingplatform.exception.DatabaseException {
        User student = new Student();
        student.setId(5);
        student.setRole("STUDENT");
        student.setName("Eve");

        when(userDAO.getUsersByRole("STUDENT")).thenReturn(Collections.singletonList(student));

        List<User> students = messageService.getAvailableStudents("INSTRUCTOR");

        assertEquals(1, students.size());
        assertEquals(5, students.get(0).getId());
        verifyNoInteractions(messageDAO);
    }

    @Test
    void getAvailableStudents_StudentRole_ThrowsSecurityException() {
        SecurityException ex = assertThrows(SecurityException.class, () -> {
            messageService.getAvailableStudents("STUDENT");
        });
        assertEquals("Only instructors can view the student directory.", ex.getMessage());
    }

    @Test
    void getAvailableStudents_AdminRole_ThrowsSecurityException() {
        SecurityException ex = assertThrows(SecurityException.class, () -> {
            messageService.getAvailableStudents("ADMIN");
        });
        assertEquals("Only instructors can view the student directory.", ex.getMessage());
    }

    @Test
    void getAvailableStudents_NullRole_ThrowsSecurityException() {
        SecurityException ex = assertThrows(SecurityException.class, () -> {
            messageService.getAvailableStudents(null);
        });
        assertEquals("User role must be specified.", ex.getMessage());
    }

    @Test
    void getAvailableStudents_NoArg_Success() throws com.codingplatform.exception.DatabaseException {
        User student = new Student();
        student.setId(1);
        student.setRole("STUDENT");
        when(userDAO.getUsersByRole("STUDENT")).thenReturn(Collections.singletonList(student));

        List<User> students = messageService.getAvailableStudents();
        assertEquals(1, students.size());
        assertEquals(1, students.get(0).getId());
    }

    @Test
    void getConversation_InstructorWithStudent_NoMessages_ReturnsEmptyList() throws SQLException, com.codingplatform.exception.DatabaseException {
        User student = new Student();
        student.setId(1);
        student.setRole("STUDENT");
        when(userDAO.getUserById(1)).thenReturn(student);
        when(messageDAO.getMessagesBetweenUsers(2, 1)).thenReturn(Collections.emptyList());

        List<Message> messages = messageService.getConversation(2, "INSTRUCTOR", 1);

        assertNotNull(messages);
        assertTrue(messages.isEmpty());
        verify(messageDAO).markMessagesAsRead(1, 2);
        verify(messageDAO).getMessagesBetweenUsers(2, 1);
    }

    @Test
    void sendMessage_InstructorToStudent_InitiatesConversation() throws SQLException, com.codingplatform.exception.DatabaseException {
        User student = new Student();
        student.setId(10);
        student.setRole("STUDENT");
        when(userDAO.getUserById(10)).thenReturn(student);

        Message createdMessage = new Message(2, 10, "Welcome to the course!");
        when(messageDAO.createMessage(any(Message.class))).thenReturn(createdMessage);

        Message result = messageService.sendMessage(2, "INSTRUCTOR", 10, "Welcome to the course!");

        assertNotNull(result);
        assertEquals(2, result.getSenderId());
        assertEquals(10, result.getReceiverId());
        assertEquals("Welcome to the course!", result.getMessage());
        verify(messageDAO).createMessage(any(Message.class));
    }
}
