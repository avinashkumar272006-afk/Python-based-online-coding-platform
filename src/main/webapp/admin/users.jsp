<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    if (request.getAttribute("users") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/users");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Users - Admin Dashboard</title>
</head>
<body>
    <h1>Manage Users</h1>
    <a href="${pageContext.request.contextPath}/admin/dashboard">Back to Dashboard</a>
    
    <c:if test="${not empty sessionScope.successMessage}">
        <p style="color: green;">${sessionScope.successMessage}</p>
        <c:remove var="successMessage" scope="session" />
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <p style="color: red;">${sessionScope.errorMessage}</p>
        <c:remove var="errorMessage" scope="session" />
    </c:if>

    <table border="1" cellpadding="5" cellspacing="0" style="margin-top: 20px; width: 100%;">
        <thead>
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Created At</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="user" items="${users}">
                <tr>
                    <td>${user.id}</td>
                    <td><c:out value="${user.name}" /></td>
                    <td><c:out value="${user.email}" /></td>
                    <td>${user.role}</td>
                    <td>${user.createdAt}</td>
                    <td>
                        <c:if test="${user.role != 'ADMIN'}">
                            <form action="${pageContext.request.contextPath}/admin/users" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="updateRole">
                                <input type="hidden" name="userId" value="${user.id}">
                                <select name="role">
                                    <option value="STUDENT" ${user.role == 'STUDENT' ? 'selected' : ''}>STUDENT</option>
                                    <option value="INSTRUCTOR" ${user.role == 'INSTRUCTOR' ? 'selected' : ''}>INSTRUCTOR</option>
                                </select>
                                <button type="submit">Update</button>
                            </form>
                        </c:if>
                        <c:if test="${user.role == 'ADMIN'}">
                            <em>ADMIN &mdash; Protected</em>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>
