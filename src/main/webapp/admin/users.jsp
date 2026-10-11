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
        <div class="metric-value" style="color: var(--state-success);"><c:out value="${studentCount}" /></div>
        <div class="metric-sub">Active learner accounts</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Instructors</div>
        <div class="metric-value" style="color: var(--brand-accent);"><c:out value="${instructorCount}" /></div>
        <div class="metric-sub">Educators &amp; content creators</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Administrators</div>
        <div class="metric-value" style="color: var(--state-danger);"><c:out value="${adminCount}" /></div>
        <div class="metric-sub">Protected system administrators</div>
    </div>
</div>

<div class="card">
    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
        <h2 class="card-title">All Registered Users</h2>
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
                                <th style="min-width: 200px;">Role Assignment</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="user" items="${users}">
                                <tr>
                                    <td style="color: var(--text-muted); font-size: var(--font-size-xs); font-family: var(--font-mono);">
                                        #<c:out value="${user.id}" />
                                    </td>
                                    <td>
                                        <div style="font-weight: 600; color: var(--text-primary);">
                                            <c:out value="${user.name}" />
                                        </div>
                                    </td>
                                    <td>
                                        <span style="color: var(--text-secondary);">
                                            <c:out value="${user.email}" />
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
                                                <span class="badge"><c:out value="${user.role}" /></span>
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
                                                    <input type="hidden" name="csrfToken" value="${not empty csrfToken ? csrfToken : sessionScope.csrfToken}">
                                                    <input type="hidden" name="action" value="updateRole">
                                                    <input type="hidden" name="userId" value="${user.id}">
                                                    <select name="role" class="form-select" style="padding: 0.3125rem 0.5rem; font-size: var(--font-size-xs); width: auto; min-width: 120px;">
                                                        <option value="STUDENT" ${user.role == 'STUDENT' ? 'selected' : ''}>STUDENT</option>
                                                        <option value="INSTRUCTOR" ${user.role == 'INSTRUCTOR' ? 'selected' : ''}>INSTRUCTOR</option>
                                                    </select>
                                                    <button type="submit" class="btn btn-secondary btn-sm" style="padding: 0.3125rem 0.625rem;">
                                                        Update
                                                    </button>
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="font-size: var(--font-size-xs); color: var(--text-muted); font-style: italic; display: inline-flex; align-items: center; gap: 0.25rem;">
                                                    🔒 Protected Admin
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
                <div style="padding: 3.5rem 1.5rem; text-align: center; color: var(--text-secondary);">
                    <div style="font-size: 2.5rem; margin-bottom: 0.75rem;">👥</div>
                    <p style="font-size: var(--font-size-base); font-weight: 600; margin-bottom: 0.25rem;">No Users Found</p>
                    <p style="font-size: var(--font-size-sm); color: var(--text-muted); margin-bottom: 0;">There are currently no registered users on the platform.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
