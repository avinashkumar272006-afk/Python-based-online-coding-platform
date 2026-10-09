<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>Exercises</title>
</head>
<body>
    <h2>Available Exercises</h2>
    <a href="${pageContext.request.contextPath}/student/dashboard">Back to Dashboard</a>
    
    <br><br>
    <table border="1" cellpadding="5">
        <thead>
            <tr>
                <th>Title</th>
                <th>Difficulty</th>
                <th>Topic</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="exercise" items="${exercises}">
                <tr>
                    <td>${exercise.title}</td>
                    <td>${exercise.difficulty}</td>
                    <td>${exercise.topic}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/student/exercises?action=view&id=${exercise.id}">View Details</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>
