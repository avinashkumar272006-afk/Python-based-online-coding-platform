<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.codingplatform.model.User" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
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
<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="Profile - Python Coding Platform" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activeNav" value="profile" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageCategory" value="Account" />
    <jsp:param name="pageTitle" value="User Profile" />
</jsp:include>

<div class="page-header">
    <div>
        <h1 class="page-title">User Profile</h1>
        <div class="page-subtitle">Manage your account information and email settings</div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<div class="card" style="max-width: 600px;">
    <div class="card-header">
        <h2 class="card-title">Profile Information</h2>
    </div>
    <div class="card-body">
        <% if (user != null) { %>
            <form action="<%= request.getContextPath() %>/profile" method="post">
                <input type="hidden" name="csrfToken" value="${not empty csrfToken ? csrfToken : sessionScope.csrfToken}">
                <div class="form-group">
                    <label for="name" class="form-label form-label-required">Full Name</label>
                    <input type="text" id="name" name="name" class="form-control" value="<%= user.getName() != null ? user.getName() : "" %>" required>
                </div>
                
                <div class="form-group">
                    <label for="email" class="form-label form-label-required">Email Address</label>
                    <input type="email" id="email" name="email" class="form-control" value="<%= user.getEmail() != null ? user.getEmail() : "" %>" required>
                </div>
                
                <div class="form-group">
                    <label for="role" class="form-label">Role</label>
                    <input type="text" id="role" name="role" class="form-control" value="<%= user.getRole() != null ? user.getRole() : "" %>" readonly disabled>
                    <div class="form-helper">Role cannot be modified directly. Contact an administrator to request a role change.</div>
                </div>
                
                <div class="form-actions" style="display: flex; gap: 0.75rem; margin-top: 1.5rem;">
                    <button type="submit" class="btn btn-primary">Update Profile</button>
                    <a href="<%= dashboardUrl %>" class="btn btn-secondary">Back to Dashboard</a>
                </div>
            </form>
        <% } else { %>
            <p>Could not load profile.</p>
            <p><a href="<%= dashboardUrl %>" class="btn btn-secondary">Back to Dashboard</a></p>
        <% } %>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
