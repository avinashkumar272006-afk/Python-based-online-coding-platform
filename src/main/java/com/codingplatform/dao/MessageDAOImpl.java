package com.codingplatform.dao;

import com.codingplatform.model.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAOImpl implements MessageDAO {

    public MessageDAOImpl() {
    }

    @Override
    public Message createMessage(Message message) throws SQLException {
        String sql = "INSERT INTO messages (sender_id, receiver_id, message) VALUES (?, ?, ?) RETURNING id, is_read, sent_at";
        try (Connection connection = com.codingplatform.util.DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, message.getSenderId());
            stmt.setInt(2, message.getReceiverId());
            stmt.setString(3, message.getMessage());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    message.setId(rs.getInt("id"));
                    message.setRead(rs.getBoolean("is_read"));
                    Timestamp sentAt = rs.getTimestamp("sent_at");
                    if (sentAt != null) {
                        message.setSentAt(sentAt.toLocalDateTime());
                    }
                    return message;
                }
            }
        } catch (com.codingplatform.exception.DatabaseException e) {
            throw new SQLException("Database connection error", e);
        }
        return null;
    }

    @Override
    public List<Message> getMessagesBetweenUsers(Integer userId1, Integer userId2) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT m.*, s.name as sender_name, r.name as receiver_name " +
                     "FROM messages m " +
                     "JOIN users s ON m.sender_id = s.id " +
                     "JOIN users r ON m.receiver_id = r.id " +
                     "WHERE (m.sender_id = ? AND m.receiver_id = ?) OR (m.sender_id = ? AND m.receiver_id = ?) " +
                     "ORDER BY m.sent_at ASC";
        try (Connection connection = com.codingplatform.util.DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId1);
            stmt.setInt(2, userId2);
            stmt.setInt(3, userId2);
            stmt.setInt(4, userId1);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Message msg = new Message();
                    msg.setId(rs.getInt("id"));
                    msg.setSenderId(rs.getInt("sender_id"));
                    msg.setReceiverId(rs.getInt("receiver_id"));
                    msg.setMessage(rs.getString("message"));
                    msg.setRead(rs.getBoolean("is_read"));
                    Timestamp sentAt = rs.getTimestamp("sent_at");
                    if (sentAt != null) {
                        msg.setSentAt(sentAt.toLocalDateTime());
                    }
                    msg.setSenderName(rs.getString("sender_name"));
                    msg.setReceiverName(rs.getString("receiver_name"));
                    messages.add(msg);
                }
            }
        } catch (com.codingplatform.exception.DatabaseException e) {
            throw new SQLException("Database connection error", e);
        }
        return messages;
    }

    @Override
    public void markMessagesAsRead(Integer senderId, Integer receiverId) throws SQLException {
        String sql = "UPDATE messages SET is_read = true WHERE sender_id = ? AND receiver_id = ? AND is_read = false";
        try (Connection connection = com.codingplatform.util.DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.executeUpdate();
        } catch (com.codingplatform.exception.DatabaseException e) {
            throw new SQLException("Database connection error", e);
        }
    }

    @Override
    public int getUnreadMessageCount(Integer userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM messages WHERE receiver_id = ? AND is_read = false";
        try (Connection connection = com.codingplatform.util.DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (com.codingplatform.exception.DatabaseException e) {
            throw new SQLException("Database connection error", e);
        }
        return 0;
    }

    @Override
    public List<Integer> getConversationPartners(Integer userId) throws SQLException {
        List<Integer> partners = new ArrayList<>();
        // Get all distinct users that have either sent a message to or received a message from this user
        String sql = "SELECT DISTINCT partner_id FROM (" +
                     "  SELECT receiver_id AS partner_id FROM messages WHERE sender_id = ? " +
                     "  UNION " +
                     "  SELECT sender_id AS partner_id FROM messages WHERE receiver_id = ?" +
                     ") AS partners";
        try (Connection connection = com.codingplatform.util.DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    partners.add(rs.getInt("partner_id"));
                }
            }
        } catch (com.codingplatform.exception.DatabaseException e) {
            throw new SQLException("Database connection error", e);
        }
        return partners;
    }
}
