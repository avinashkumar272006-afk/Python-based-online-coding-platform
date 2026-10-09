<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>My Submissions</title>
</head>
<body>
    <h2>My Submissions</h2>
    <a href="${pageContext.request.contextPath}/student/dashboard">Back to Dashboard</a>
    
    <c:if test="${param.success == 'true'}">
        <div style="color: green;">Submission created successfully!</div>
    </c:if>

    <table border="1">
        <thead>
            <tr>
                <th>Exercise Title</th>
                <th>Status</th>
                <th>Score</th>
                <th>Submitted At</th>
                <th>Reviewed At</th>
                <th>Feedback</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="sub" items="${submissions}">
                <tr>
                    <td>${sub.exerciseTitle}</td>
                    <td>${sub.status}</td>
                    <td>${sub.score != null ? sub.score.toString().concat('/100') : 'Not graded yet'}</td>
                    <td>${sub.submittedAt}</td>
                    <td>${sub.reviewedAt}</td>
                    <td>${sub.feedback != null ? sub.feedback : 'No feedback yet'}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>
