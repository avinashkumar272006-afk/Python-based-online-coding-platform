<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>Manage Submissions</title>
</head>
<body>
    <h2>Manage Submissions</h2>
    <a href="${pageContext.request.contextPath}/instructor/dashboard.jsp">Back to Dashboard</a>
    
    <c:if test="${param.success == 'true'}">
        <div style="color: green;">Submission reviewed successfully!</div>
    </c:if>

    <table border="1">
        <thead>
            <tr>
                <th>Exercise Title</th>
                <th>Student Name</th>
                <th>Status</th>
                <th>Submitted At</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="sub" items="${submissions}">
                <tr>
                    <td>${sub.exerciseTitle}</td>
                    <td>${sub.studentName}</td>
                    <td>${sub.status}</td>
                    <td>${sub.submittedAt}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/instructor/submission/review?id=${sub.id}">Review</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>
