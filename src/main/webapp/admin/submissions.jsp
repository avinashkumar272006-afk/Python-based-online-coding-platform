<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin - View Submissions</title>
    <style>
        table { border-collapse: collapse; width: 100%; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        a { text-decoration: none; color: #0066cc; }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <h1>All Submissions (Read-Only)</h1>
    <p><a href="${pageContext.request.contextPath}/admin/dashboard">Back to Admin Dashboard</a></p>
    
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Student</th>
                <th>Exercise</th>
                <th>Status</th>
                <th>Submitted At</th>
                <th>Reviewed At</th>
                <th>Feedback</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="sub" items="${submissions}">
                <tr>
                    <td>${sub.id}</td>
                    <td><c:out value="${sub.studentName}"/></td>
                    <td><c:out value="${sub.exerciseTitle}"/></td>
                    <td>${sub.status}</td>
                    <td><fmt:formatDate value="${sub.submittedAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td><fmt:formatDate value="${sub.reviewedAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td><c:out value="${sub.feedback != null ? sub.feedback : '-'}"/></td>
                </tr>
            </c:forEach>
            <c:if test="${empty submissions}">
                <tr>
                    <td colspan="7">No submissions found.</td>
                </tr>
            </c:if>
        </tbody>
    </table>
</body>
</html>
