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

<div class="page-header">
    <div>
        <h1 class="page-title">Admin Dashboard</h1>
        <div class="page-subtitle">Welcome back, <c:out value="${sessionScope.name != null ? sessionScope.name : 'Administrator'}" />! Overview of platform users, curriculum, submissions, and system status.</div>
    </div>
    <div style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
        <span class="badge badge-danger">Role: <c:out value="${sessionScope.role != null ? sessionScope.role : 'ADMIN'}" /></span>
        <a href="${pageContext.request.contextPath}/admin/settings" class="btn btn-secondary btn-sm">
            ⚙ System Settings
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<%-- Top Overview Metric Cards (3 Themed Sections) --%>
<div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 1.25rem; margin-bottom: 2rem;">
    
    <%-- 1. Platform Users Overview --%>
    <div class="card" style="margin-bottom: 0;">
        <div class="card-header">
            <h2 class="card-title">User Distribution</h2>
            <span class="badge badge-info">Community</span>
        </div>
        <div class="card-body">
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div style="padding: 0.875rem 1rem; background: var(--bg-subtle); border-radius: var(--radius-md); border: 1px solid var(--border-subtle);">
                    <div style="font-size: var(--font-size-xs); color: var(--text-secondary); text-transform: uppercase; font-weight: 600; letter-spacing: 0.04em;">Total Users</div>
                    <div style="font-size: var(--font-size-2xl); font-weight: 700; color: var(--text-primary); margin-top: 0.25rem;">
                        <c:out value="${stats.totalUsers}" />
                    </div>
                </div>
                <div style="padding: 0.875rem 1rem; background: var(--bg-subtle); border-radius: var(--radius-md); border: 1px solid var(--border-subtle);">
                    <div style="font-size: var(--font-size-xs); color: var(--state-success-text); text-transform: uppercase; font-weight: 600; letter-spacing: 0.04em;">Students</div>
                    <div style="font-size: var(--font-size-2xl); font-weight: 700; color: var(--state-success); margin-top: 0.25rem;">
                        <c:out value="${stats.totalStudents}" />
                    </div>
                </div>
                <div style="padding: 0.875rem 1rem; background: var(--bg-subtle); border-radius: var(--radius-md); border: 1px solid var(--border-subtle);">
                    <div style="font-size: var(--font-size-xs); color: var(--brand-accent); text-transform: uppercase; font-weight: 600; letter-spacing: 0.04em;">Instructors</div>
                    <div style="font-size: var(--font-size-2xl); font-weight: 700; color: var(--brand-accent); margin-top: 0.25rem;">
                        <c:out value="${stats.totalInstructors}" />
                    </div>
                </div>
                <div style="padding: 0.875rem 1rem; background: var(--bg-subtle); border-radius: var(--radius-md); border: 1px solid var(--border-subtle);">
                    <div style="font-size: var(--font-size-xs); color: var(--state-danger-text); text-transform: uppercase; font-weight: 600; letter-spacing: 0.04em;">Admins</div>
                    <div style="font-size: var(--font-size-2xl); font-weight: 700; color: var(--state-danger); margin-top: 0.25rem;">
                        <c:out value="${stats.totalAdmins}" />
                    </div>
                </div>
            </div>
        </div>
    </div>

    <%-- 2. Curriculum & Exercise Overview --%>
    <div class="card" style="margin-bottom: 0;">
        <div class="card-header">
            <h2 class="card-title">Exercise Library</h2>
            <span class="badge badge-info">Curriculum</span>
        </div>
        <div class="card-body">
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div style="padding: 0.875rem 1rem; background: var(--bg-subtle); border-radius: var(--radius-md); border: 1px solid var(--border-subtle);">
                    <div style="font-size: var(--font-size-xs); color: var(--text-secondary); text-transform: uppercase; font-weight: 600; letter-spacing: 0.04em;">Total Exercises</div>
                    <div style="font-size: var(--font-size-2xl); font-weight: 700; color: var(--text-primary); margin-top: 0.25rem;">
                        <c:out value="${stats.totalExercises}" />
                    </div>
                </div>
                <div style="padding: 0.875rem 1rem; background: var(--bg-subtle); border-radius: var(--radius-md); border: 1px solid var(--border-subtle);">
                    <div style="font-size: var(--font-size-xs); color: var(--state-success-text); text-transform: uppercase; font-weight: 600; letter-spacing: 0.04em;">Easy Level</div>
                    <div style="font-size: var(--font-size-2xl); font-weight: 700; color: var(--state-success); margin-top: 0.25rem;">
                        <c:out value="${stats.easyExercises}" />
                    </div>
                </div>
                <div style="padding: 0.875rem 1rem; background: var(--bg-subtle); border-radius: var(--radius-md); border: 1px solid var(--border-subtle);">
                    <div style="font-size: var(--font-size-xs); color: var(--state-warning-text); text-transform: uppercase; font-weight: 600; letter-spacing: 0.04em;">Medium Level</div>
                    <div style="font-size: var(--font-size-2xl); font-weight: 700; color: var(--state-warning); margin-top: 0.25rem;">
                        <c:out value="${stats.mediumExercises}" />
                    </div>
                </div>
                <div style="padding: 0.875rem 1rem; background: var(--bg-subtle); border-radius: var(--radius-md); border: 1px solid var(--border-subtle);">
                    <div style="font-size: var(--font-size-xs); color: var(--state-danger-text); text-transform: uppercase; font-weight: 600; letter-spacing: 0.04em;">Hard Level</div>
                    <div style="font-size: var(--font-size-2xl); font-weight: 700; color: var(--state-danger); margin-top: 0.25rem;">
                        <c:out value="${stats.hardExercises}" />
                    </div>
                </div>
            </div>
        </div>
    </div>

    <%-- 3. Submissions & Grading Pipeline --%>
    <div class="card" style="margin-bottom: 0;">
        <div class="card-header">
            <h2 class="card-title">Submission Activity</h2>
            <span class="badge badge-info">Grading Pipeline</span>
        </div>
        <div class="card-body">
            <div style="display: grid; grid-template-columns: 1fr; gap: 0.75rem;">
                <div style="display: flex; justify-content: space-between; align-items: center; padding: 0.75rem 1rem; background: var(--bg-subtle); border-radius: var(--radius-md); border: 1px solid var(--border-subtle);">
                    <div>
                        <div style="font-size: var(--font-size-xs); color: var(--text-secondary); text-transform: uppercase; font-weight: 600; letter-spacing: 0.04em;">Total Submissions</div>
                        <div style="font-size: var(--font-size-xs); color: var(--text-muted); margin-top: 0.125rem;">All solution attempts</div>
                    </div>
                    <div style="font-size: var(--font-size-2xl); font-weight: 700; color: var(--text-primary);">
                        <c:out value="${stats.totalSubmissions}" />
                    </div>
                </div>
                <div style="display: flex; justify-content: space-between; align-items: center; padding: 0.75rem 1rem; background: var(--bg-subtle); border-radius: var(--radius-md); border: 1px solid var(--border-subtle);">
                    <div>
                        <div style="font-size: var(--font-size-xs); color: var(--state-warning-text); text-transform: uppercase; font-weight: 600; letter-spacing: 0.04em;">Pending Evaluation</div>
                        <div style="font-size: var(--font-size-xs); color: var(--text-muted); margin-top: 0.125rem;">Awaiting review</div>
                    </div>
                    <div style="font-size: var(--font-size-2xl); font-weight: 700; color: ${stats.pendingSubmissions > 0 ? 'var(--state-warning)' : 'var(--text-primary)'};">
                        <c:out value="${stats.pendingSubmissions}" />
                    </div>
                </div>
                <div style="display: flex; justify-content: space-between; align-items: center; padding: 0.75rem 1rem; background: var(--bg-subtle); border-radius: var(--radius-md); border: 1px solid var(--border-subtle);">
                    <div>
                        <div style="font-size: var(--font-size-xs); color: var(--state-success-text); text-transform: uppercase; font-weight: 600; letter-spacing: 0.04em;">Reviewed &amp; Graded</div>
                        <div style="font-size: var(--font-size-xs); color: var(--text-muted); margin-top: 0.125rem;">Feedback provided</div>
                    </div>
                    <div style="font-size: var(--font-size-2xl); font-weight: 700; color: var(--state-success);">
                        <c:out value="${stats.reviewedSubmissions}" />
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%-- Administration Quick Links --%>
<div class="card quick-links" style="margin-bottom: 2rem;">
    <div class="card-header">
        <h2 class="card-title">Administrative Actions &amp; Controls</h2>
    </div>
    <div class="card-body">
        <div class="quick-links-grid">
            <a href="${pageContext.request.contextPath}/admin/users" class="quick-link-card">
                <div>
                    <div style="font-weight: 600; font-size: var(--font-size-base); color: var(--text-primary); margin-bottom: 0.25rem;">Manage Users</div>
                    <div style="font-size: var(--font-size-xs); color: var(--text-secondary);">Manage accounts, roles, and administrative permissions</div>
                </div>
                <span style="font-size: 1.25rem; color: var(--brand-accent); font-weight: 700;">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/exercises" class="quick-link-card">
                <div>
                    <div style="font-weight: 600; font-size: var(--font-size-base); color: var(--text-primary); margin-bottom: 0.25rem;">Exercise Approvals</div>
                    <div style="font-size: var(--font-size-xs); color: var(--text-secondary);">Moderate, approve, and audit instructor exercise content</div>
                </div>
                <span style="font-size: 1.25rem; color: var(--brand-accent); font-weight: 700;">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/challenges" class="quick-link-card">
                <div>
                    <div style="font-weight: 600; font-size: var(--font-size-base); color: var(--text-primary); margin-bottom: 0.25rem;">Manage Challenges</div>
                    <div style="font-size: var(--font-size-xs); color: var(--text-secondary);">Approve or reject competitive coding challenges</div>
                </div>
                <span style="font-size: 1.25rem; color: var(--brand-accent); font-weight: 700;">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/submissions" class="quick-link-card">
                <div>
                    <div style="font-weight: 600; font-size: var(--font-size-base); color: var(--text-primary); margin-bottom: 0.25rem;">All Submissions</div>
                    <div style="font-size: var(--font-size-xs); color: var(--text-secondary);">Inspect global student solutions, evaluations, and grades</div>
                </div>
                <span style="font-size: 1.25rem; color: var(--brand-accent); font-weight: 700;">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/settings" class="quick-link-card">
                <div>
                    <div style="font-weight: 600; font-size: var(--font-size-base); color: var(--text-primary); margin-bottom: 0.25rem;">System Settings</div>
                    <div style="font-size: var(--font-size-xs); color: var(--text-secondary);">Configure platform parameters and runtime properties</div>
                </div>
                <span style="font-size: 1.25rem; color: var(--brand-accent); font-weight: 700;">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/profile" class="quick-link-card">
                <div>
                    <div style="font-weight: 600; font-size: var(--font-size-base); color: var(--text-primary); margin-bottom: 0.25rem;">Profile &amp; Account</div>
                    <div style="font-size: var(--font-size-xs); color: var(--text-secondary);">Manage your administrator credentials and profile settings</div>
                </div>
                <span style="font-size: 1.25rem; color: var(--brand-accent); font-weight: 700;">&rarr;</span>
            </a>
        </div>
    </div>
</div>

<%-- Recent Submissions Table --%>
<div class="card" style="margin-bottom: 2rem;">
    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
        <h2 class="card-title">Recent Submissions</h2>
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
                                            <c:out value="${sub.studentName}" />
                                        </span>
                                    </td>
                                    <td>
                                        <span style="font-weight: 500; color: var(--brand-accent);">
                                            <c:out value="${sub.exerciseTitle}" />
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
                                                <span class="badge"><c:out value="${sub.status}" /></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="color: var(--text-secondary); font-size: var(--font-size-xs);">
                                        <c:out value="${sub.submittedAt}" />
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div style="padding: 2.5rem 1.5rem; text-align: center; color: var(--text-secondary);">
                    <div style="font-size: 2rem; margin-bottom: 0.5rem;">📝</div>
                    <p style="font-size: var(--font-size-base); font-weight: 600; margin-bottom: 0.25rem;">No Recent Submissions</p>
                    <p style="font-size: var(--font-size-sm); color: var(--text-muted); margin-bottom: 0;">Submissions from students will appear here as they solve exercises.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%-- Recent Users Table --%>
<div class="card" style="margin-bottom: 2rem;">
    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
        <h2 class="card-title">Recent Users</h2>
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
                                            <c:out value="${usr.name}" />
                                        </span>
                                    </td>
                                    <td>
                                        <span style="color: var(--text-secondary);">
                                            <c:out value="${usr.email}" />
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
                                                <span class="badge"><c:out value="${usr.role}" /></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="color: var(--text-secondary); font-size: var(--font-size-xs);">
                                        <c:out value="${usr.createdAt}" />
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div style="padding: 2.5rem 1.5rem; text-align: center; color: var(--text-secondary);">
                    <div style="font-size: 2rem; margin-bottom: 0.5rem;">👥</div>
                    <p style="font-size: var(--font-size-base); font-weight: 600; margin-bottom: 0.25rem;">No Recent Users</p>
                    <p style="font-size: var(--font-size-sm); color: var(--text-muted); margin-bottom: 0;">Newly registered users will be listed here.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
