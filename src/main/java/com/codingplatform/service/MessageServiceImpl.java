package com.codingplatform.service;

import com.codingplatform.dao.MessageDAO;
import com.codingplatform.dao.UserDAO;
import com.codingplatform.model.Message;
import com.codingplatform.model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageServiceImpl implements MessageService {

    private final MessageDAO messageDAO;
    private final UserDAO userDAO;

    public MessageServiceImpl(MessageDAO messageDAO, UserDAO userDAO) {
        this.messageDAO = messageDAO;
        this.userDAO = userDAO;
    }

    @Override
    public Message sendMessage(Integer senderId, String senderRole, Integer receiverId, String messageText) {
        if (messageText == null || messageText.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty.");
        }
        if (messageText.length() > 5000) {
            throw new IllegalArgumentException("Message is too long.");
        }

        try {
            User receiver = userDAO.getUserById(receiverId);
            if (receiver == null) {
                throw new IllegalArgumentException("Receiver does not exist.");
            }

            // Security: Validating sender and receiver roles
            if ("STUDENT".equals(senderRole)) {
                if (!"INSTRUCTOR".equals(receiver.getRole())) {
                    throw new SecurityException("Students can only message instructors.");
                }
            } else if ("INSTRUCTOR".equals(senderRole)) {
                if (!"STUDENT".equals(receiver.getRole())) {
                    throw new SecurityException("Instructors can only message students.");
                }
            } else {
                throw new SecurityException("Admins cannot participate in messaging.");
            }

            Message message = new Message(senderId, receiverId, messageText.trim());
            return messageDAO.createMessage(message);
        } catch (SQLException | com.codingplatform.exception.DatabaseException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while sending message.", e);
        }
    }

    @Override
    public List<Message> getConversation(Integer currentUserId, Integer partnerId) {
        try {
            // Mark partner's unread messages to this user as read
            messageDAO.markMessagesAsRead(partnerId, currentUserId);
            return messageDAO.getMessagesBetweenUsers(currentUserId, partnerId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching conversation.", e);
        }
    }

    @Override
    public List<User> getAvailableInstructors() {
        try {
            List<User> instructors = new ArrayList<>();
            List<User> allUsers = userDAO.getAllUsers();
            for (User user : allUsers) {
                if ("INSTRUCTOR".equals(user.getRole())) {
                    instructors.add(user);
                }
            }
            return instructors;
        } catch (com.codingplatform.exception.DatabaseException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching instructors.", e);
        }
    }

    @Override
    public List<User> getStudentsWithConversations(Integer instructorId) {
        try {
            List<Integer> partnerIds = messageDAO.getConversationPartners(instructorId);
            List<User> students = new ArrayList<>();
            for (Integer partnerId : partnerIds) {
                User partner = userDAO.getUserById(partnerId);
                if (partner != null && "STUDENT".equals(partner.getRole())) {
                    students.add(partner);
                }
            }
            return students;
        } catch (SQLException | com.codingplatform.exception.DatabaseException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching students.", e);
        }
    }

    @Override
    public int getUnreadCount(Integer userId) {
        try {
            return messageDAO.getUnreadMessageCount(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching unread count.", e);
        }
    }
}
