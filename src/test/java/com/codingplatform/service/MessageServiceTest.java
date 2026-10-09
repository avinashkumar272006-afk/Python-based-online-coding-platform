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
    void getConversation_MarksAsReadAndReturnsMessages() throws SQLException {
        List<Message> mockMessages = Arrays.asList(new Message(1, 2, "Hi"));
        when(messageDAO.getMessagesBetweenUsers(1, 2)).thenReturn(mockMessages);

        List<Message> result = messageService.getConversation(1, 2);

        assertEquals(1, result.size());
        verify(messageDAO).markMessagesAsRead(2, 1); // Marks messages from 2 to 1 as read
        verify(messageDAO).getMessagesBetweenUsers(1, 2);
    }
}
