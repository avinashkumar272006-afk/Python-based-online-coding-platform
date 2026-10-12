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
    
    // Determine the dashboard URL to go back (using verified controller servlets)
    String dashboardUrl = request.getContextPath() + "/student/dashboard"; // fallback
    if ("ADMIN".equals(role)) {
        dashboardUrl = request.getContextPath() + "/admin/dashboard";
    } else if ("INSTRUCTOR".equals(role)) {
        dashboardUrl = request.getContextPath() + "/instructor/dashboard";
    } else if ("STUDENT".equals(role)) {
        dashboardUrl = request.getContextPath() + "/student/dashboard";
    }

    String initial = "U";
    if (user != null && user.getName() != null && !user.getName().trim().isEmpty()) {
        initial = user.getName().trim().substring(0, 1).toUpperCase();
    }
    pageContext.setAttribute("avatarInitial", initial);
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
    <div>
        <a href="<%= dashboardUrl %>" class="btn btn-secondary btn-sm">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <line x1="19" y1="12" x2="5" y2="12"></line>
                <polyline points="12 19 5 12 12 5"></polyline>
            </svg>
            Back to Dashboard
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<div class="card profile-card">
    <div class="card-header">
        <h2 class="card-title">Profile Information</h2>
        <span class="badge ${sessionScope.role == 'ADMIN' ? 'badge-danger' : (sessionScope.role == 'INSTRUCTOR' ? 'badge-info' : 'badge-success')}">
            <c:out value="${not empty sessionScope.role ? sessionScope.role : 'USER'}" />
        </span>
    </div>
    <div class="card-body">
        <% if (user != null) { %>
            <div class="profile-avatar-header">
                <div class="profile-avatar-circle">
                    <c:out value="${avatarInitial}" />
                </div>
                <div class="profile-avatar-details">
                    <span class="profile-avatar-name"><c:out value="${user.name}" /></span>
                    <span class="profile-avatar-email"><c:out value="${user.email}" /></span>
                </div>
            </div>

            <form action="${pageContext.request.contextPath}/profile" method="post">
                <input type="hidden" name="csrfToken" value="${not empty csrfToken ? csrfToken : sessionScope.csrfToken}">

                <div class="form-group">
                    <label for="name" class="form-label form-label-required">Full Name</label>
                    <input type="text" id="name" name="name" class="form-control" value="<c:out value='${user.name}' />" required>
                </div>

                <div class="form-group">
                    <label for="email" class="form-label form-label-required">Email Address</label>
                    <input type="email" id="email" name="email" class="form-control" value="<c:out value='${user.email}' />" required>
                </div>

                <div class="form-group">
                    <label for="role" class="form-label">Role</label>
                    <input type="text" id="role" name="role" class="form-control" value="<c:out value='${user.role}' />" readonly disabled>
                    <div class="form-helper">Role cannot be modified directly. Contact an administrator to request a role change.</div>
                </div>

                <div class="form-actions" style="display: flex; flex-wrap: wrap; gap: 0.75rem; margin-top: 1.75rem;">
                    <button type="submit" class="btn btn-primary">Update Profile</button>
                    <a href="<%= dashboardUrl %>" class="btn btn-secondary">Back to Dashboard</a>
                </div>
            </form>
        <% } else { %>
            <div class="alert alert-error">
                <span>Could not load profile. Please try refreshing the page or logging in again.</span>
            </div>
            <p><a href="<%= dashboardUrl %>" class="btn btn-secondary">Back to Dashboard</a></p>
        <% } %>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
