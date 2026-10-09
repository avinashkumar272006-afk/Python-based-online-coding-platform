<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Messages</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .partner-list {
            list-style: none;
            padding: 0;
        }
        .partner-list li {
            padding: 10px;
            border-bottom: 1px solid #ccc;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>
            <c>
                <c:if test="${sessionScope.role == 'STUDENT'}">Interact with Instructors</c:if>
                <c:if test="${sessionScope.role == 'INSTRUCTOR'}">Student Interaction</c:if>
            </c>
        </h2>

        <c:if test="${not empty sessionScope.errorMessage}">
            <p style="color: red;">${sessionScope.errorMessage}</p>
            <c:remove var="errorMessage" scope="session" />
        </c:if>
        
        <c:if test="${empty partners}">
            <p>No contacts available.</p>
        </c:if>
        <c:if test="${not empty partners}">
            <ul class="partner-list">
                <c:forEach var="partner" items="${partners}">
                    <li>
                        <span>${partner.name} (${partner.role})</span>
                        <a href="${pageContext.request.contextPath}/messages?partnerId=${partner.id}" class="button">Open Conversation</a>
                    </li>
                </c:forEach>
            </ul>
        </c:if>

        <p><a href="${pageContext.request.contextPath}/${sessionScope.role == 'STUDENT' ? 'student' : 'instructor'}/dashboard.jsp">Back to Dashboard</a></p>
    </div>
</body>
</html>
