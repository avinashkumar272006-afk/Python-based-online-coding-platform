<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin - View Exercises</title>
    <style>
        table { border-collapse: collapse; width: 100%; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        a { text-decoration: none; color: #0066cc; }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <h1>Admin - Exercise Content Approval</h1>
    <p><a href="${pageContext.request.contextPath}/admin/dashboard">Back to Admin Dashboard</a></p>
    <c:if test="${not empty sessionScope.successMessage}">
        <div style="color: green; margin: 10px 0;">${sessionScope.successMessage}</div>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <div style="color: red; margin: 10px 0;">${sessionScope.errorMessage}</div>
        <c:remove var="errorMessage" scope="session"/>
    </c:if>

    <h2>Content Approval (Pending Exercises)</h2>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Difficulty</th>
                <th>Topic</th>
                <th>Created By (ID)</th>
                <th>Created At</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="exercise" items="${pendingExercises}">
                <tr>
                    <td>${exercise.id}</td>
                    <td><c:out value="${exercise.title}"/></td>
                    <td>${exercise.difficulty}</td>
                    <td>${exercise.topic}</td>
                    <td>${exercise.createdBy}</td>
                    <td><fmt:formatDate value="${exercise.createdAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td>${exercise.status}</td>
                    <td>
                        <form action="${pageContext.request.contextPath}/admin/exercises" method="post" style="display:inline;">
                            <input type="hidden" name="id" value="${exercise.id}">
                            <input type="hidden" name="action" value="approve">
                            <button type="submit" style="color: green;">Approve</button>
                        </form>
                        <form action="${pageContext.request.contextPath}/admin/exercises" method="post" style="display:inline;">
                            <input type="hidden" name="id" value="${exercise.id}">
                            <input type="hidden" name="action" value="reject">
                            <button type="submit" style="color: red;">Reject</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty pendingExercises}">
                <tr>
                    <td colspan="8">No pending exercises found.</td>
                </tr>
            </c:if>
        </tbody>
    </table>

    <h2>All Exercises (Read-Only)</h2>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Description</th>
                <th>Difficulty</th>
                <th>Topic</th>
                <th>Created By (ID)</th>
                <th>Created At</th>
                <th>Status</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="exercise" items="${exercises}">
                <tr>
                    <td>${exercise.id}</td>
                    <td><c:out value="${exercise.title}"/></td>
                    <td><c:out value="${exercise.description}"/></td>
                    <td>${exercise.difficulty}</td>
                    <td>${exercise.topic}</td>
                    <td>${exercise.createdBy}</td>
                    <td><fmt:formatDate value="${exercise.createdAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td>${exercise.status}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty exercises}">
                <tr>
                    <td colspan="8">No exercises found.</td>
                </tr>
            </c:if>
        </tbody>
    </table>
</body>
</html>
