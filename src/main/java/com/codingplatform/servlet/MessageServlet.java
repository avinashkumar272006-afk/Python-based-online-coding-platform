package com.codingplatform.servlet;

import com.codingplatform.dao.MessageDAOImpl;
import com.codingplatform.daoimpl.UserDAOImpl;
import com.codingplatform.model.Message;
import com.codingplatform.model.User;
import com.codingplatform.service.MessageService;
import com.codingplatform.service.MessageServiceImpl;
import com.codingplatform.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;

import java.util.List;

@WebServlet("/messages")
public class MessageServlet extends HttpServlet {

    private MessageService messageService;

    @Override
    public void init() throws ServletException {
        if (messageService == null) {
            messageService = new MessageServiceImpl(new MessageDAOImpl(), new UserDAOImpl());
        }
    }

    public MessageServlet() {}

    // Constructor for testing
    public MessageServlet(MessageService messageService) {
        this.messageService = messageService;
    }

    public MessageServlet(MessageService messageService, com.codingplatform.dao.UserDAO userDAO) {
        this.messageService = messageService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Object userIdObj = session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userIdObj == null || role == null || "ADMIN".equals(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        int userId = ((Number) userIdObj).intValue();
        String partnerIdParam = request.getParameter("partnerId");

        if (partnerIdParam != null && !partnerIdParam.isEmpty()) {
            try {
                int partnerId = Integer.parseInt(partnerIdParam);

                User partner = messageService.getConversationPartner(userId, role, partnerId);
                List<Message> conversation = messageService.getConversation(userId, role, partnerId);

                request.setAttribute("conversation", conversation);
                request.setAttribute("partnerId", partnerId);
                request.setAttribute("partnerName", partner.getName());

                request.getRequestDispatcher("/messages/conversation.jsp").forward(request, response);
            } catch (NumberFormatException e) {
                session.setAttribute("errorMessage", "Invalid partner ID.");
                response.sendRedirect(request.getContextPath() + "/messages");
            } catch (SecurityException | IllegalArgumentException e) {
                session.setAttribute("errorMessage", e.getMessage());
                response.sendRedirect(request.getContextPath() + "/messages");
            } catch (RuntimeException e) {
                session.setAttribute("errorMessage", "An error occurred while loading the conversation.");
                response.sendRedirect(request.getContextPath() + "/messages");
            }
        } else {
            String action = request.getParameter("action");
            if ("students".equals(action) || "directory".equals(action)) {
                if (!"INSTRUCTOR".equals(role)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                    return;
                }
            }

            if ("STUDENT".equals(role)) {
                List<User> instructors = messageService.getAvailableInstructors();
                request.setAttribute("partners", instructors);
            } else if ("INSTRUCTOR".equals(role)) {
                List<User> students = messageService.getAvailableStudents(role);
                request.setAttribute("partners", students);
            }
            request.getRequestDispatcher("/messages/list.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Object userIdObj = session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userIdObj == null || role == null || "ADMIN".equals(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        int senderId = ((Number) userIdObj).intValue();
        String receiverIdParam = request.getParameter("receiverId");
        String messageText = request.getParameter("message");

        if (receiverIdParam == null || receiverIdParam.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Invalid receiver ID.");
            response.sendRedirect(request.getContextPath() + "/messages");
            return;
        }

        if (messageText == null || messageText.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Message cannot be empty.");
            response.sendRedirect(request.getContextPath() + "/messages?partnerId=" + receiverIdParam);
            return;
        }

        try {
            int receiverId = Integer.parseInt(receiverIdParam);
            messageService.sendMessage(senderId, role, receiverId, messageText);
            response.sendRedirect(request.getContextPath() + "/messages?partnerId=" + receiverId);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid receiver ID.");
            response.sendRedirect(request.getContextPath() + "/messages");
        } catch (IllegalArgumentException | SecurityException e) {
            session.setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/messages");
        } catch (RuntimeException e) {
            session.setAttribute("errorMessage", "An error occurred while sending the message.");
            response.sendRedirect(request.getContextPath() + "/messages");
        }
    }
}
