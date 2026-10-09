<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Conversation</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .conversation-box {
            border: 1px solid #ccc;
            padding: 15px;
            height: 400px;
            overflow-y: scroll;
            margin-bottom: 20px;
            background-color: #f9f9f9;
        }
        .message {
            margin-bottom: 15px;
        }
        .message .sender {
            font-weight: bold;
        }
        .message.me .sender {
            color: #007bff;
        }
        .message.them .sender {
            color: #28a745;
        }
        .message-form textarea {
            width: 100%;
            height: 100px;
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Conversation with ${partnerName}</h2>

        <c:if test="${not empty sessionScope.errorMessage}">
            <p style="color: red;">${sessionScope.errorMessage}</p>
            <c:remove var="errorMessage" scope="session" />
        </c:if>
        
        <div class="conversation-box" id="conversationBox">
            <c:if test="${empty conversation}">
                <p>No messages yet. Start the conversation!</p>
            </c:if>
            <c:forEach var="msg" items="${conversation}">
                <div class="message ${msg.senderId == sessionScope.userId ? 'me' : 'them'}">
                    <div class="sender">
                        <c:choose>
                            <c:when test="${msg.senderId == sessionScope.userId}">You</c:when>
                            <c:otherwise>${msg.senderName}</c:otherwise>
                        </c:choose>
                    </div>
                    <div class="text">${msg.message}</div>
                </div>
            </c:forEach>
        </div>

        <form class="message-form" action="${pageContext.request.contextPath}/messages" method="post">
            <input type="hidden" name="receiverId" value="${partnerId}">
            <textarea name="message" required placeholder="Write your message..."></textarea><br/>
            <button type="submit" class="button">Send Message</button>
        </form>

        <p><a href="${pageContext.request.contextPath}/messages">Back to Contacts</a></p>
    </div>
    
    <script>
        // Scroll to the bottom of the conversation box
        var conversationBox = document.getElementById("conversationBox");
        conversationBox.scrollTop = conversationBox.scrollHeight;
    </script>
</body>
</html>
