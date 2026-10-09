<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Instructor Dashboard - Python Coding Platform</title>
</head>
<body>
    <h1>Python Coding Platform</h1>
    <h2>Instructor Dashboard</h2>
    <p>Welcome, <%= session.getAttribute("name") %>!</p>
    <p>Role: <%= session.getAttribute("role") %></p>
    <div class="quick-links" style="margin-bottom: 20px;">
        <h3>Exercise Management</h3>
        <ul>
            <li><a href="${pageContext.request.contextPath}/instructor/exercises">Manage Exercises</a></li>
            <li><a href="${pageContext.request.contextPath}/instructor/exercises?action=new">Create Exercise</a></li>
        </ul>
        <h3>Submissions</h3>
        <ul>
            <li><a href="${pageContext.request.contextPath}/instructor/submissions">Review Submissions</a></li>
        </ul>
        <h3>Interaction</h3>
        <ul>
            <li><a href="${pageContext.request.contextPath}/messages">Student Interaction</a></li>
        </ul>
        <h3>Challenges</h3>
        <ul>
            <li><a href="${pageContext.request.contextPath}/instructor/challenges">Manage Challenges</a></li>
        </ul>
        <h3>Account</h3>
        <ul>
            <li><a href="${pageContext.request.contextPath}/profile">Profile</a></li>
        </ul>
    </div>
    
    <div class="performance-report" style="margin-bottom: 20px; border: 1px solid #ccc; padding: 10px;">
        <h3>Performance Report</h3>
        <c:if test="${not empty performanceError}">
            <p style="color: red;">${performanceError}</p>
        </c:if>
        <c:if test="${empty performanceError and performanceReport != null}">
            <ul>
                <li><strong>Total Submissions:</strong> ${performanceReport.totalSubmissions}</li>
                <li><strong>Pending Reviews:</strong> ${performanceReport.pendingSubmissions}</li>
                <li><strong>Reviewed Submissions:</strong> ${performanceReport.reviewedSubmissions}</li>
                <li><strong>Average Score:</strong> ${performanceReport.averageScore != null ? performanceReport.averageScore : 'N/A'}</li>
                <li><strong>Highest Score:</strong> ${performanceReport.highestScore != null ? performanceReport.highestScore : 'N/A'}</li>
                <li><strong>Lowest Score:</strong> ${performanceReport.lowestScore != null ? performanceReport.lowestScore : 'N/A'}</li>
            </ul>
        </c:if>
    </div>
    
    <a href="${pageContext.request.contextPath}/logout">Logout</a>
</body>
</html>
