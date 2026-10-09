<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (request.getAttribute("progress") == null) {
        response.sendRedirect(request.getContextPath() + "/student/dashboard");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Student Dashboard - Python Coding Platform</title>
</head>
<body>
    <h1>Python Coding Platform</h1>
    <h2>Student Dashboard</h2>
    <p>Welcome, <%= session.getAttribute("name") %>!</p>
    <p>Role: <%= session.getAttribute("role") %></p>
    <div class="progress-section" style="margin-bottom: 20px;">
        <h3>Your Progress</h3>
        <p>Total Exercises: ${progress.totalExercises}</p>
        <p>Submitted: ${progress.submittedExercises}</p>
        <p>Reviewed: ${progress.reviewedSubmissions}</p>
        <p>Pending Review: ${progress.pendingSubmissions}</p>
        <p>Average Score: ${progress.averageScore != null ? progress.averageScore : 'N/A'}</p>
        <h2>Overall Progress: ${progress.progressPercentage}%</h2>
    </div>

    <div class="quick-links" style="margin-bottom: 20px;">
        <h3>Platform Features</h3>
        <ul>
            <li><a href="${pageContext.request.contextPath}/student/exercises">Browse Exercises</a></li>
            <li><a href="${pageContext.request.contextPath}/student/submissions">My Submissions</a></li>
            <li><a href="${pageContext.request.contextPath}/student/challenges">Available Challenges</a></li>
            <li><a href="${pageContext.request.contextPath}/student/my-challenges">My Challenges</a></li>
            <li><a href="${pageContext.request.contextPath}/messages">Interact with Instructors</a></li>
            <li><a href="${pageContext.request.contextPath}/profile">Profile</a></li>
        </ul>
    </div>
    
    <a href="${pageContext.request.contextPath}/logout">Logout</a>
</body>
</html>
