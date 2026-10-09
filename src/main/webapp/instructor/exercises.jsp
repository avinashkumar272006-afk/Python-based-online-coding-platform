<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>Manage Exercises</title>
</head>
<body>
    <h2>Instructor Exercises</h2>
    <a href="${pageContext.request.contextPath}/instructor/dashboard.jsp">Back to Dashboard</a> |
    <a href="${pageContext.request.contextPath}/instructor/exercises?action=new">Create New Exercise</a>
    
    <br><br>
    <table border="1" cellpadding="5">
        <thead>
            <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Difficulty</th>
                <th>Topic</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="exercise" items="${exercises}">
                <tr>
                    <td>${exercise.id}</td>
                    <td>${exercise.title}</td>
                    <td>${exercise.difficulty}</td>
                    <td>${exercise.topic}</td>
                    <td>${exercise.status}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/instructor/exercises?action=edit&id=${exercise.id}">Edit</a>
                        |
                        <form action="${pageContext.request.contextPath}/instructor/exercises" method="post" style="display:inline;">
                            <input type="hidden" name="action" value="delete"/>
                            <input type="hidden" name="id" value="${exercise.id}"/>
                            <button type="submit" onclick="return confirm('Are you sure you want to delete this exercise?');">Delete</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>
