<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="Conversation - Python Coding Platform" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activeNav" value="messages" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageCategory" value="Community" />
    <jsp:param name="pageTitle" value="Conversation" />
</jsp:include>

<div class="page-header">
    <div>
        <h1 class="page-title">Conversation with <c:out value="${partnerName}" /></h1>
        <div class="page-subtitle">Direct communication history and messages</div>
    </div>
    <div style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
        <a href="${pageContext.request.contextPath}/messages" class="btn btn-secondary btn-sm">
            &larr; Back to Contacts
        </a>
        <c:choose>
            <c:when test="${sessionScope.role == 'STUDENT'}">
                <a href="${pageContext.request.contextPath}/student/dashboard" class="btn btn-secondary btn-sm">
                    Dashboard
                </a>
            </c:when>
            <c:when test="${sessionScope.role == 'INSTRUCTOR'}">
                <a href="${pageContext.request.contextPath}/instructor/dashboard" class="btn btn-secondary btn-sm">
                    Dashboard
                </a>
            </c:when>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<div class="card">
    <div class="card-header">
        <h2 class="card-title">Messages</h2>
        <span class="badge badge-info"><c:out value="${conversation != null ? conversation.size() : 0}" /> Messages</span>
    </div>
    <div class="card-body">
        <div class="conversation-box" id="conversationBox">
            <c:if test="${empty conversation}">
                <div style="margin: auto; text-align: center; color: var(--text-muted); padding: 2rem 0;">
                    <div style="font-size: 2rem; margin-bottom: 0.5rem;">💬</div>
                    <p style="margin: 0; font-weight: 500;">No messages yet. Start the conversation!</p>
                </div>
            </c:if>
            <c:forEach var="msg" items="${conversation}">
                <div class="message ${msg.senderId == sessionScope.userId ? 'me' : 'them'}">
                    <div class="sender">
                        <c:choose>
                            <c:when test="${msg.senderId == sessionScope.userId}">You</c:when>
                            <c:otherwise><c:out value="${msg.senderName}" /></c:otherwise>
                        </c:choose>
                    </div>
                    <div class="text"><c:out value="${msg.message}" /></div>
                </div>
            </c:forEach>
        </div>

        <form class="message-form" action="${pageContext.request.contextPath}/messages" method="post">
            <input type="hidden" name="csrfToken" value="${not empty csrfToken ? csrfToken : sessionScope.csrfToken}">
            <input type="hidden" name="receiverId" value="<c:out value='${partnerId}' />">
            <div class="form-group" style="margin-bottom: 0.75rem;">
                <textarea name="message" class="form-control" required placeholder="Write your message..." rows="3" style="resize: vertical;"></textarea>
            </div>
            <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.5rem;">
                <button type="submit" class="btn btn-primary">Send Message</button>
                <a href="${pageContext.request.contextPath}/messages" class="btn btn-secondary btn-sm">Back to Contacts</a>
            </div>
        </form>
    </div>
</div>

<script>
    // Scroll to the bottom of the conversation box
    var conversationBox = document.getElementById("conversationBox");
    if (conversationBox) {
        conversationBox.scrollTop = conversationBox.scrollHeight;
    }
</script>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
