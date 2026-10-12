<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    if (request.getAttribute("stats") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        return;
    }
%>
<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="Admin Dashboard - Python Coding Platform" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activeNav" value="dashboard" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageCategory" value="Administration" />
    <jsp:param name="pageTitle" value="Dashboard" />
</jsp:include>

<div class="dashboard-welcome-card">
    <div class="dashboard-welcome-left">
        <div class="dashboard-welcome-avatar">🛡️</div>
        <div class="dashboard-welcome-info">
            <h1 class="dashboard-welcome-title">Welcome back, <c:out value="${sessionScope.name != null ? sessionScope.name : 'Administrator'}" />!</h1>
            <p class="dashboard-welcome-desc">Global platform overview — monitor user accounts, audit curriculum exercises, oversee submissions, and manage system configuration.</p>
            <div class="dashboard-welcome-tags">
                <span class="badge badge-danger">Role: <c:out value="${sessionScope.role != null ? sessionScope.role : 'ADMIN'}" /></span>
                <c:choose>
                    <c:when test="${stats != null and stats.pendingSubmissions > 0}">
                        <span class="badge badge-warning"><c:out value="${stats.pendingSubmissions}" /> Submissions Awaiting Evaluation</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge badge-success">Review Pipeline Healthy</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    <div class="dashboard-welcome-actions">
        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-primary">
            Manage Users &rarr;
        </a>
        <a href="${pageContext.request.contextPath}/admin/settings" class="btn btn-secondary">
            ⚙ System Settings
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<c:if test="${stats != null and stats.pendingSubmissions > 0}">
    <div style="display: flex; align-items: center; justify-content: space-between; padding: 0.875rem 1.125rem; background-color: var(--state-warning-bg); border: 1px solid var(--state-warning-border); border-radius: var(--radius-md); margin-bottom: 1.5rem; flex-wrap: wrap; gap: 0.75rem;">
        <div>
            <div style="font-weight: 600; font-size: var(--font-size-sm); color: var(--state-warning-text);">⚠️ Action Required: <c:out value="${stats.pendingSubmissions}" /> Student Submissions Pending Evaluation</div>
            <div style="font-size: var(--font-size-xs); color: var(--state-warning-text); opacity: 0.9;">Student code submissions are awaiting review and scoring across the platform.</div>
        </div>
        <a href="${pageContext.request.contextPath}/admin/submissions" class="btn btn-primary btn-sm">
            View All Submissions &rarr;
        </a>
    </div>
</c:if>

<%-- Platform Overview Metric Grids --%>
<div class="overview-grid">
    
    <%-- 1. Platform Users Overview --%>
    <div class="card" style="margin-bottom: 0;">
        <div class="card-header">
            <div>
                <h2 class="card-title">User Distribution</h2>
                <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">Active platform community by account role</div>
            </div>
            <span class="badge badge-info">Community</span>
        </div>
        <div class="card-body">
            <div class="stat-grid-2col">
                <div class="stat-tile">
                    <div class="stat-tile-label">Total Users</div>
                    <div class="stat-tile-value">
                        <c:out value="${stats.totalUsers}" />
                    </div>
                    <div class="stat-tile-sub">All registered accounts</div>
                </div>
                <div class="stat-tile">
                    <div class="stat-tile-label">Students</div>
                    <div class="stat-tile-value stat-success">
                        <c:out value="${stats.totalStudents}" />
                    </div>
                    <div class="stat-tile-sub">Learner accounts</div>
                </div>
                <div class="stat-tile">
                    <div class="stat-tile-label">Instructors</div>
                    <div class="stat-tile-value stat-accent">
                        <c:out value="${stats.totalInstructors}" />
                    </div>
                    <div class="stat-tile-sub">Course educators</div>
                </div>
                <div class="stat-tile">
                    <div class="stat-tile-label">Admins</div>
                    <div class="stat-tile-value stat-danger">
                        <c:out value="${stats.totalAdmins}" />
                    </div>
                    <div class="stat-tile-sub">System administrators</div>
                </div>
            </div>
        </div>
        <div class="card-footer" style="display: flex; justify-content: flex-end;">
            <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary btn-sm">
                Manage Accounts &rarr;
            </a>
        </div>
    </div>

    <%-- 2. Curriculum & Exercise Overview --%>
    <div class="card" style="margin-bottom: 0;">
        <div class="card-header">
            <div>
                <h2 class="card-title">Exercise Library</h2>
                <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">Curriculum problems grouped by difficulty tier</div>
            </div>
            <span class="badge badge-info">Curriculum</span>
        </div>
        <div class="card-body">
            <div class="stat-grid-2col">
                <div class="stat-tile">
                    <div class="stat-tile-label">Total Exercises</div>
                    <div class="stat-tile-value">
                        <c:out value="${stats.totalExercises}" />
                    </div>
                    <div class="stat-tile-sub">Published problems</div>
                </div>
                <div class="stat-tile">
                    <div class="stat-tile-label">Easy Level</div>
                    <div class="stat-tile-value stat-success">
                        <c:out value="${stats.easyExercises}" />
                    </div>
                    <div class="stat-tile-sub">Beginner exercises</div>
                </div>
                <div class="stat-tile">
                    <div class="stat-tile-label">Medium Level</div>
                    <div class="stat-tile-value stat-warning">
                        <c:out value="${stats.mediumExercises}" />
                    </div>
                    <div class="stat-tile-sub">Intermediate exercises</div>
                </div>
                <div class="stat-tile">
                    <div class="stat-tile-label">Hard Level</div>
                    <div class="stat-tile-value stat-danger">
                        <c:out value="${stats.hardExercises}" />
                    </div>
                    <div class="stat-tile-sub">Advanced exercises</div>
                </div>
            </div>
        </div>
        <div class="card-footer" style="display: flex; justify-content: flex-end;">
            <a href="${pageContext.request.contextPath}/admin/exercises" class="btn btn-secondary btn-sm">
                Moderate Exercises &rarr;
            </a>
        </div>
    </div>

    <%-- 3. Submissions & Grading Pipeline --%>
    <div class="card" style="margin-bottom: 0;">
        <div class="card-header">
            <div>
                <h2 class="card-title">Submission Activity</h2>
                <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">Global solution attempts and evaluation status</div>
            </div>
            <span class="badge badge-info">Grading Pipeline</span>
        </div>
        <div class="card-body">
            <div class="stat-row-list">
                <div class="stat-row">
                    <div class="stat-row-content">
                        <div class="stat-row-label">Total Submissions</div>
                        <div class="stat-row-sub">All student solution attempts</div>
                    </div>
                    <div class="stat-row-value">
                        <c:out value="${stats.totalSubmissions}" />
                    </div>
                </div>
                <div class="stat-row">
                    <div class="stat-row-content">
                        <div class="stat-row-label">Pending Evaluation</div>
                        <div class="stat-row-sub">Awaiting instructor review</div>
                    </div>
                    <div class="stat-row-value ${stats.pendingSubmissions > 0 ? 'stat-warning' : ''}">
                        <c:out value="${stats.pendingSubmissions}" />
                    </div>
                </div>
                <div class="stat-row">
                    <div class="stat-row-content">
                        <div class="stat-row-label">Reviewed &amp; Graded</div>
                        <div class="stat-row-sub">Completed instructor feedback</div>
                    </div>
                    <div class="stat-row-value stat-success">
                        <c:out value="${stats.reviewedSubmissions}" />
                    </div>
                </div>
            </div>
        </div>
        <div class="card-footer" style="display: flex; justify-content: flex-end;">
            <a href="${pageContext.request.contextPath}/admin/submissions" class="btn btn-secondary btn-sm">
                Inspect Submissions &rarr;
            </a>
        </div>
    </div>
</div>

<%-- Administration Quick Links --%>
<div class="card quick-links" style="margin-bottom: 2rem;">
    <div class="card-header">
        <div>
            <h2 class="card-title">Administrative Actions &amp; Controls</h2>
            <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">Direct navigation to user management, curriculum auditing, and system configuration</div>
        </div>
    </div>
    <div class="card-body">
        <div class="quick-links-grid">
            <a href="${pageContext.request.contextPath}/admin/users" class="quick-link-card">
                <div class="quick-link-content">
                    <div class="quick-link-icon-box">👥</div>
                    <div>
                        <div class="quick-link-title">Manage Users</div>
                        <div class="quick-link-desc">Manage accounts, change roles, and audit permissions</div>
                    </div>
                </div>
                <span class="quick-link-arrow">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/exercises" class="quick-link-card">
                <div class="quick-link-content">
                    <div class="quick-link-icon-box">📚</div>
                    <div>
                        <div class="quick-link-title">Exercise Approvals</div>
                        <div class="quick-link-desc">Moderate, approve, and audit instructor exercise content</div>
                    </div>
                </div>
                <span class="quick-link-arrow">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/challenges" class="quick-link-card">
                <div class="quick-link-content">
                    <div class="quick-link-icon-box">🏆</div>
                    <div>
                        <div class="quick-link-title">Manage Challenges</div>
                        <div class="quick-link-desc">Approve or reject competitive coding challenges</div>
                    </div>
                </div>
                <span class="quick-link-arrow">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/submissions" class="quick-link-card">
                <div class="quick-link-content">
                    <div class="quick-link-icon-box">📝</div>
                    <div>
                        <div class="quick-link-title">All Submissions</div>
                        <div class="quick-link-desc">Inspect global student solutions, evaluations, and grades</div>
                    </div>
                </div>
                <span class="quick-link-arrow">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/settings" class="quick-link-card">
                <div class="quick-link-content">
                    <div class="quick-link-icon-box">⚙️</div>
                    <div>
                        <div class="quick-link-title">System Settings</div>
                        <div class="quick-link-desc">Configure platform parameters and runtime properties</div>
                    </div>
                </div>
                <span class="quick-link-arrow">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/profile" class="quick-link-card">
                <div class="quick-link-content">
                    <div class="quick-link-icon-box">👤</div>
                    <div>
                        <div class="quick-link-title">Profile &amp; Account</div>
                        <div class="quick-link-desc">Manage your administrator credentials and security settings</div>
                    </div>
                </div>
                <span class="quick-link-arrow">&rarr;</span>
            </a>
        </div>
    </div>
</div>

<%-- Recent Submissions Table --%>
<div class="card" style="margin-bottom: 2rem;">
    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
        <div>
            <h2 class="card-title">Recent Submissions</h2>
            <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">Latest solution attempts submitted by students across all exercises</div>
        </div>
        <a href="${pageContext.request.contextPath}/admin/submissions" class="btn btn-secondary btn-sm">
            View All Submissions &rarr;
        </a>
    </div>
    <div class="card-body" style="padding: 0;">
        <c:choose>
            <c:when test="${not empty stats.recentSubmissions}">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th style="width: 80px;">ID</th>
                                <th>Student</th>
                                <th>Exercise</th>
                                <th>Status</th>
                                <th>Submitted At</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="sub" items="${stats.recentSubmissions}">
                                <tr>
                                    <td style="color: var(--text-muted); font-size: var(--font-size-xs); font-family: var(--font-mono);">
                                        #<c:out value="${sub.id}" />
                                    </td>
                                    <td>
                                        <span style="font-weight: 600; color: var(--text-primary);">
                                            <c:out value="${sub.studentName != null ? sub.studentName : 'Student #' += sub.studentId}" />
                                        </span>
                                    </td>
                                    <td>
                                        <span style="font-weight: 500; color: var(--brand-accent);">
                                            <c:out value="${sub.exerciseTitle != null ? sub.exerciseTitle : 'Exercise #' += sub.exerciseId}" />
                                        </span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${sub.status == 'REVIEWED'}">
                                                <span class="badge badge-success"><c:out value="${sub.status}" /></span>
                                            </c:when>
                                            <c:when test="${sub.status == 'PENDING'}">
                                                <span class="badge badge-warning">Pending</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge"><c:out value="${sub.status != null ? sub.status : 'N/A'}" /></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="color: var(--text-secondary); font-size: var(--font-size-xs);">
                                        <c:out value="${sub.submittedAt != null ? sub.submittedAt : 'N/A'}" />
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state-box" style="margin: 1.5rem;">
                    <div style="font-size: 2rem; margin-bottom: 0.5rem;">📝</div>
                    <div class="empty-state-title">No Recent Submissions</div>
                    <div class="empty-state-text">Student code submissions will appear here automatically once learners start attempting curriculum exercises.</div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%-- Recent Users Table --%>
<div class="card" style="margin-bottom: 2rem;">
    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
        <div>
            <h2 class="card-title">Recent Users</h2>
            <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">Newly registered platform accounts across all roles</div>
        </div>
        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary btn-sm">
            Manage Users &rarr;
        </a>
    </div>
    <div class="card-body" style="padding: 0;">
        <c:choose>
            <c:when test="${not empty stats.recentUsers}">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th style="width: 80px;">ID</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Role</th>
                                <th>Joined At</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="usr" items="${stats.recentUsers}">
                                <tr>
                                    <td style="color: var(--text-muted); font-size: var(--font-size-xs); font-family: var(--font-mono);">
                                        #<c:out value="${usr.id}" />
                                    </td>
                                    <td>
                                        <span style="font-weight: 600; color: var(--text-primary);">
                                            <c:out value="${usr.name != null ? usr.name : 'Unknown'}" />
                                        </span>
                                    </td>
                                    <td>
                                        <span style="color: var(--text-secondary);">
                                            <c:out value="${usr.email != null ? usr.email : 'N/A'}" />
                                        </span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${usr.role == 'ADMIN'}">
                                                <span class="badge badge-danger">ADMIN</span>
                                            </c:when>
                                            <c:when test="${usr.role == 'INSTRUCTOR'}">
                                                <span class="badge badge-info">INSTRUCTOR</span>
                                            </c:when>
                                            <c:when test="${usr.role == 'STUDENT'}">
                                                <span class="badge badge-success">STUDENT</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge"><c:out value="${usr.role != null ? usr.role : 'N/A'}" /></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="color: var(--text-secondary); font-size: var(--font-size-xs);">
                                        <c:out value="${usr.createdAt != null ? usr.createdAt : 'N/A'}" />
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state-box" style="margin: 1.5rem;">
                    <div style="font-size: 2rem; margin-bottom: 0.5rem;">👥</div>
                    <div class="empty-state-title">No Recent Users</div>
                    <div class="empty-state-text">Newly registered users will be listed here as accounts are created on the platform.</div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%-- Platform Governance & Administration Controls --%>
<div class="card">
    <div class="card-header">
        <div>
            <h2 class="card-title">Platform Governance &amp; Administration</h2>
            <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">Standard operational guidelines and security practices</div>
        </div>
        <span class="badge badge-danger">Governance</span>
    </div>
    <div class="card-body">
        <div class="workflow-steps-grid">
            <div class="workflow-step-card">
                <div class="workflow-step-badge">Pillar 1</div>
                <h3 class="workflow-step-title">Role-Based Access</h3>
                <p class="workflow-step-desc">Enforce principle of least privilege across Student, Instructor, and Admin privileges.</p>
            </div>
            <div class="workflow-step-card">
                <div class="workflow-step-badge">Pillar 2</div>
                <h3 class="workflow-step-title">Curriculum Moderation</h3>
                <p class="workflow-step-desc">Audit exercises and challenges to maintain consistent educational rigor and difficulty.</p>
            </div>
            <div class="workflow-step-card">
                <div class="workflow-step-badge">Pillar 3</div>
                <h3 class="workflow-step-title">System &amp; Data Integrity</h3>
                <p class="workflow-step-desc">Monitor database operations, session security, and evaluate submission workflows.</p>
            </div>
        </div>
    </div>
    <div class="card-footer" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
        <div>
            <a href="${pageContext.request.contextPath}/admin/settings" class="btn btn-secondary btn-sm">
                ⚙ System Settings
            </a>
        </div>
        <form action="${pageContext.request.contextPath}/logout" method="post" style="display: inline; margin: 0;">
            <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken != null ? csrfToken : sessionScope.csrfToken}' />" />
            <button type="submit" class="btn btn-secondary btn-sm">Logout</button>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
