<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    if (request.getAttribute("users") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/users");
        return;
    }
%>
<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="User Management - Python Coding Platform" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activeNav" value="users" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageCategory" value="Administration" />
    <jsp:param name="pageTitle" value="Manage Users" />
</jsp:include>

<c:set var="studentCount" value="0" />
<c:set var="instructorCount" value="0" />
<c:set var="adminCount" value="0" />
<c:if test="${not empty users}">
    <c:forEach var="u" items="${users}">
        <c:choose>
            <c:when test="${u.role == 'STUDENT'}">
                <c:set var="studentCount" value="${studentCount + 1}" />
            </c:when>
            <c:when test="${u.role == 'INSTRUCTOR'}">
                <c:set var="instructorCount" value="${instructorCount + 1}" />
            </c:when>
            <c:when test="${u.role == 'ADMIN'}">
                <c:set var="adminCount" value="${adminCount + 1}" />
            </c:when>
        </c:choose>
    </c:forEach>
</c:if>

<div class="page-header">
    <div>
        <h1 class="page-title">User Management</h1>
        <div class="page-subtitle">Manage registered platform accounts, audit user access, and configure role assignments.</div>
    </div>
    <div style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-secondary btn-sm">
            &larr; Back to Dashboard
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<%-- Summary KPI Cards --%>
<div class="metrics-grid" style="margin-bottom: 1.75rem;">
    <div class="metric-card">
        <div class="metric-label">Total Users</div>
        <div class="metric-value"><c:out value="${users != null ? users.size() : 0}" /></div>
        <div class="metric-sub">Registered accounts across platform</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Students</div>
        <div class="metric-value stat-success"><c:out value="${studentCount}" /></div>
        <div class="metric-sub">Active learner accounts</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Instructors</div>
        <div class="metric-value stat-accent"><c:out value="${instructorCount}" /></div>
        <div class="metric-sub">Educators &amp; content creators</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Administrators</div>
        <div class="metric-value stat-danger"><c:out value="${adminCount}" /></div>
        <div class="metric-sub">Protected system administrators</div>
    </div>
</div>

<%-- Role Governance Policy Banner --%>
<div style="display: flex; align-items: center; justify-content: space-between; padding: 0.875rem 1.125rem; background-color: var(--state-info-bg); border: 1px solid var(--state-info-border); border-radius: var(--radius-md); margin-bottom: 1.5rem; gap: 0.75rem; flex-wrap: wrap;">
    <div style="display: flex; align-items: flex-start; gap: 0.625rem;">
        <span style="font-size: 1.125rem; line-height: 1.2;">ℹ️</span>
        <div>
            <div style="font-weight: 600; font-size: var(--font-size-sm); color: var(--state-info-text);">
                Role Assignment Policy
            </div>
            <div style="font-size: var(--font-size-xs); color: var(--state-info-text); opacity: 0.9; margin-top: 0.125rem;">
                Administrator accounts (<code>ADMIN</code>) are protected and cannot have their roles changed. Standard accounts can be updated between <strong>STUDENT</strong> and <strong>INSTRUCTOR</strong>.
            </div>
        </div>
    </div>
    <span class="badge badge-info" style="font-size: 0.7rem;">Access Control</span>
</div>

<div class="card" style="margin-bottom: 2rem;">
    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
        <div>
            <h2 class="card-title">All Registered Users</h2>
            <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">
                Directory of platform users with current roles, contact details, and account creation dates
            </div>
        </div>
        <span class="badge badge-info"><c:out value="${users != null ? users.size() : 0}" /> Accounts</span>
    </div>
    <div class="card-body" style="padding: 0;">
        <c:choose>
            <c:when test="${not empty users}">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th style="width: 80px;">User ID</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Current Role</th>
                                <th>Created At</th>
                                <th style="min-width: 220px;">Role Assignment</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="user" items="${users}">
                                <tr>
                                    <td style="color: var(--text-muted); font-size: var(--font-size-xs); font-family: var(--font-mono);">
                                        #<c:out value="${user.id}" />
                                    </td>
                                    <td>
                                        <div style="display: flex; align-items: center; gap: 0.625rem;">
                                            <div style="width: 28px; height: 28px; border-radius: var(--radius-full); background: var(--brand-accent-light); color: var(--brand-accent); display: flex; align-items: center; justify-content: center; font-size: var(--font-size-xs); border: 1px solid var(--brand-accent-subtle); flex-shrink: 0;">
                                                👤
                                            </div>
                                            <div style="font-weight: 600; color: var(--text-primary); font-size: var(--font-size-sm);">
                                                <c:out value="${user.name != null ? user.name : 'Unknown User'}" />
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <span style="color: var(--text-secondary); font-size: var(--font-size-sm);">
                                            <c:out value="${user.email != null ? user.email : 'N/A'}" />
                                        </span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${user.role == 'ADMIN'}">
                                                <span class="badge badge-danger">ADMIN</span>
                                            </c:when>
                                            <c:when test="${user.role == 'INSTRUCTOR'}">
                                                <span class="badge badge-info">INSTRUCTOR</span>
                                            </c:when>
                                            <c:when test="${user.role == 'STUDENT'}">
                                                <span class="badge badge-success">STUDENT</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge"><c:out value="${user.role != null ? user.role : 'N/A'}" /></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="color: var(--text-secondary); font-size: var(--font-size-xs);">
                                        <c:out value="${user.createdAt != null ? user.createdAt : 'N/A'}" />
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${user.role != 'ADMIN'}">
                                                <form action="${pageContext.request.contextPath}/admin/users" method="post" style="display: flex; align-items: center; gap: 0.5rem; margin: 0;">
                                                    <input type="hidden" name="csrfToken" value="<c:out value='${not empty csrfToken ? csrfToken : sessionScope.csrfToken}' />">
                                                    <input type="hidden" name="action" value="updateRole">
                                                    <input type="hidden" name="userId" value="${user.id}">
                                                    <select name="role" class="form-select" style="padding: 0.3125rem 0.5rem; font-size: var(--font-size-xs); width: auto; min-width: 125px;" aria-label="Role for user <c:out value='${user.id}' />">
                                                        <option value="STUDENT" ${user.role == 'STUDENT' ? 'selected' : ''}>STUDENT</option>
                                                        <option value="INSTRUCTOR" ${user.role == 'INSTRUCTOR' ? 'selected' : ''}>INSTRUCTOR</option>
                                                    </select>
                                                    <button type="submit" class="btn btn-secondary btn-sm" style="padding: 0.3125rem 0.625rem;">
                                                        Update
                                                    </button>
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="font-size: var(--font-size-xs); color: var(--text-muted); display: inline-flex; align-items: center; gap: 0.35rem; background: var(--bg-subtle); padding: 0.25rem 0.5rem; border-radius: var(--radius-sm); border: 1px solid var(--border-subtle);">
                                                    <span>🔒</span>
                                                    <span style="font-weight: 500;">Protected Admin</span>
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state-box" style="margin: 2rem 1.5rem;">
                    <div style="font-size: 2.5rem; margin-bottom: 0.75rem;">👥</div>
                    <div class="empty-state-title">No Users Found</div>
                    <div class="empty-state-text">There are currently no registered users on the platform.</div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
