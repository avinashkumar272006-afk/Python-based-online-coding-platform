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
        if (senderId == null || receiverId == null) {
            throw new IllegalArgumentException("Sender ID and receiver ID cannot be null.");
        }
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
        if (currentUserId == null || partnerId == null) {
            throw new IllegalArgumentException("User IDs cannot be null.");
        }
        try {
            User currentUser = userDAO.getUserById(currentUserId);
            if (currentUser == null) {
                throw new IllegalArgumentException("Current user does not exist.");
            }
            return getConversation(currentUserId, currentUser.getRole(), partnerId);
        } catch (com.codingplatform.exception.DatabaseException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching conversation.", e);
        }
    }

    @Override
    public List<Message> getConversation(Integer currentUserId, String currentUserRole, Integer partnerId) {
        if (currentUserId == null || partnerId == null) {
            throw new IllegalArgumentException("User IDs cannot be null.");
        }
        // Validates partner existence, self-conversation prevention, and reciprocal role authorization
        getConversationPartner(currentUserId, currentUserRole, partnerId);

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
    public User getConversationPartner(String currentUserRole, Integer partnerId) {
        return getConversationPartner(null, currentUserRole, partnerId);
    }

    @Override
    public User getConversationPartner(Integer currentUserId, String currentUserRole, Integer partnerId) {
        if (partnerId == null) {
            throw new IllegalArgumentException("Partner ID cannot be null.");
        }
        if (currentUserRole == null) {
            throw new SecurityException("User role must be specified.");
        }
        if (currentUserId != null && currentUserId.equals(partnerId)) {
            throw new SecurityException("Users cannot converse with themselves.");
        }

        try {
            User partner = userDAO.getUserById(partnerId);
            if (partner == null) {
                throw new IllegalArgumentException("Conversation partner not found.");
            }

            // Security: Validating current user and partner roles
            if ("STUDENT".equals(currentUserRole)) {
                if (!"INSTRUCTOR".equals(partner.getRole())) {
                    throw new SecurityException("Students can only converse with instructors.");
                }
            } else if ("INSTRUCTOR".equals(currentUserRole)) {
                if (!"STUDENT".equals(partner.getRole())) {
                    throw new SecurityException("Instructors can only converse with students.");
                }
            } else {
                throw new SecurityException("Admins cannot participate in messaging.");
            }

            return partner;
        } catch (com.codingplatform.exception.DatabaseException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching partner.", e);
        }
    }

    @Override
    public List<User> getAvailableInstructors() {
        try {
            return userDAO.getUsersByRole("INSTRUCTOR");
        } catch (com.codingplatform.exception.DatabaseException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching instructors.", e);
        }
    }

    @Override
    public List<User> getAvailableStudents() {
        return getAvailableStudents("INSTRUCTOR");
    }

    @Override
    public List<User> getAvailableStudents(String currentUserRole) {
        if (currentUserRole == null) {
            throw new SecurityException("User role must be specified.");
        }
        if (!"INSTRUCTOR".equals(currentUserRole)) {
            throw new SecurityException("Only instructors can view the student directory.");
        }
        try {
            return userDAO.getUsersByRole("STUDENT");
        } catch (com.codingplatform.exception.DatabaseException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error while fetching students.", e);
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
