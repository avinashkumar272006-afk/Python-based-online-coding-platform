<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.codingplatform.model.User" %>
<%
    if (session == null || session.getAttribute("userId") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    User user = (User) request.getAttribute("user");
    String role = (String) session.getAttribute("role");
    
    // Determine the dashboard URL to go back
    String dashboardUrl = request.getContextPath() + "/dashboard"; // fallback
    if ("ADMIN".equals(role)) {
        dashboardUrl = request.getContextPath() + "/admin/dashboard.jsp";
    } else if ("INSTRUCTOR".equals(role)) {
        dashboardUrl = request.getContextPath() + "/instructor/dashboard.jsp";
    } else if ("STUDENT".equals(role)) {
        dashboardUrl = request.getContextPath() + "/student/dashboard.jsp";
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Profile</title>
</head>
<body>
    <h2>Profile</h2>

    <% String errorMessage = (String) request.getAttribute("errorMessage");
       if (errorMessage != null) { %>
        <p style="color: red;"><%= errorMessage %></p>
    <% } %>

    <% String successMessage = (String) request.getAttribute("successMessage");
       if (successMessage != null) { %>
        <p style="color: green;"><%= successMessage %></p>
    <% } %>

    <% if (user != null) { %>
        <form action="<%= request.getContextPath() %>/profile" method="post">
            <p>
                <label for="name">Name:</label><br>
                <input type="text" id="name" name="name" value="<%= user.getName() != null ? user.getName() : "" %>" required>
            </p>
            <p>
                <label for="email">Email:</label><br>
                <input type="email" id="email" name="email" value="<%= user.getEmail() != null ? user.getEmail() : "" %>" required>
            </p>
            <p>
                <label for="role">Role:</label><br>
                <input type="text" id="role" name="role" value="<%= user.getRole() != null ? user.getRole() : "" %>" readonly disabled>
            </p>
            <p>
                <button type="submit">Update Profile</button>
            </p>
        </form>
    <% } else { %>
        <p>Could not load profile.</p>
    <% } %>

    <p><a href="<%= dashboardUrl %>">Back to Dashboard</a></p>
</body>
</html>
