package com.codingplatform.dao;

import com.codingplatform.model.Message;
import java.util.List;
import java.sql.SQLException;

public interface MessageDAO {
    Message createMessage(Message message) throws SQLException;
    List<Message> getMessagesBetweenUsers(Integer userId1, Integer userId2) throws SQLException;
    void markMessagesAsRead(Integer senderId, Integer receiverId) throws SQLException;
    int getUnreadMessageCount(Integer userId) throws SQLException;
    List<Integer> getConversationPartners(Integer userId) throws SQLException;
}
