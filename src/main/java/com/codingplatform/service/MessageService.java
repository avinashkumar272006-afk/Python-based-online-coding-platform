package com.codingplatform.service;

import com.codingplatform.model.Message;
import com.codingplatform.model.User;
import java.util.List;

public interface MessageService {
    Message sendMessage(Integer senderId, String senderRole, Integer receiverId, String message);
    List<Message> getConversation(Integer currentUserId, Integer partnerId);
    List<User> getAvailableInstructors();
    List<User> getStudentsWithConversations(Integer instructorId);
    int getUnreadCount(Integer userId);
}
