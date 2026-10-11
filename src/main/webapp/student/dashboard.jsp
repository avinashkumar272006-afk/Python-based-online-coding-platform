<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    if (request.getAttribute("progress") == null) {
        response.sendRedirect(request.getContextPath() + "/student/dashboard");
        return;
    }
%>
<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="Student Dashboard - Python Coding Platform" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activeNav" value="dashboard" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageCategory" value="Student" />
    <jsp:param name="pageTitle" value="Dashboard" />
</jsp:include>

<div class="page-header">
    <div>
        <h1 class="page-title">Student Dashboard</h1>
        <div class="page-subtitle">Welcome, <c:out value="${sessionScope.name != null ? sessionScope.name : 'Student'}" />! Track your learning progress and explore exercises.</div>
    </div>
    <div>
        <span class="badge badge-success">Role: <c:out value="${sessionScope.role != null ? sessionScope.role : 'STUDENT'}" /></span>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<div class="card progress-section">
    <div class="card-header">
        <h2 class="card-title">Your Progress</h2>
        <span class="badge badge-info">Completion: ${progress.progressPercentage}%</span>
    </div>
    <div class="card-body">
        <div style="margin-bottom: 1.5rem;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
                <span style="font-weight: 600; color: var(--text-primary);">Overall Progress: ${progress.progressPercentage}%</span>
                <span style="font-size: var(--font-size-sm); color: var(--text-secondary);">${progress.submittedExercises} / ${progress.totalExercises} Exercises</span>
            </div>
            <div class="progress-bar-container">
                <div class="progress-bar-fill" style="width: ${progress.progressPercentage}%;"></div>
            </div>
        </div>

        <div class="metrics-grid" style="margin-bottom: 0;">
            <div class="metric-card">
                <div class="metric-label">Total Exercises</div>
                <div class="metric-value">${progress.totalExercises}</div>
                <div class="metric-sub">Curriculum total</div>
            </div>
            <div class="metric-card">
                <div class="metric-label">Submitted</div>
                <div class="metric-value">${progress.submittedExercises}</div>
                <div class="metric-sub">Completed submissions</div>
            </div>
            <div class="metric-card">
                <div class="metric-label">Reviewed</div>
                <div class="metric-value">${progress.reviewedSubmissions}</div>
                <div class="metric-sub">Evaluated by instructors</div>
            </div>
            <div class="metric-card">
                <div class="metric-label">Pending Review</div>
                <div class="metric-value">${progress.pendingSubmissions}</div>
                <div class="metric-sub">Awaiting feedback</div>
            </div>
            <div class="metric-card">
                <div class="metric-label">Average Score</div>
                <div class="metric-value">${progress.averageScore != null ? progress.averageScore : 'N/A'}</div>
                <div class="metric-sub">Across graded submissions</div>
            </div>
        </div>
    </div>
</div>

<div class="card quick-links">
    <div class="card-header">
        <h2 class="card-title">Platform Features</h2>
    </div>
    <div class="card-body">
        <div class="quick-links-grid">
            <a href="${pageContext.request.contextPath}/student/exercises" class="quick-link-card">
                <div>
                    <div style="font-weight: 600; font-size: var(--font-size-base); color: var(--text-primary); margin-bottom: 0.25rem;">Browse Exercises</div>
                    <div style="font-size: var(--font-size-xs); color: var(--text-secondary);">Practice Python problems and test your skills</div>
                </div>
                <span style="font-size: 1.25rem; color: var(--brand-accent); font-weight: 700;">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/student/submissions" class="quick-link-card">
                <div>
                    <div style="font-weight: 600; font-size: var(--font-size-base); color: var(--text-primary); margin-bottom: 0.25rem;">My Submissions</div>
                    <div style="font-size: var(--font-size-xs); color: var(--text-secondary);">View submission history, test outputs, and feedback</div>
                </div>
                <span style="font-size: 1.25rem; color: var(--brand-accent); font-weight: 700;">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/student/challenges" class="quick-link-card">
                <div>
                    <div style="font-weight: 600; font-size: var(--font-size-base); color: var(--text-primary); margin-bottom: 0.25rem;">Available Challenges</div>
                    <div style="font-size: var(--font-size-xs); color: var(--text-secondary);">Compete in timed programming contests</div>
                </div>
                <span style="font-size: 1.25rem; color: var(--brand-accent); font-weight: 700;">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/student/my-challenges" class="quick-link-card">
                <div>
                    <div style="font-weight: 600; font-size: var(--font-size-base); color: var(--text-primary); margin-bottom: 0.25rem;">My Challenges</div>
                    <div style="font-size: var(--font-size-xs); color: var(--text-secondary);">Track enrolled challenges and submissions</div>
                </div>
                <span style="font-size: 1.25rem; color: var(--brand-accent); font-weight: 700;">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/messages" class="quick-link-card">
                <div>
                    <div style="font-weight: 600; font-size: var(--font-size-base); color: var(--text-primary); margin-bottom: 0.25rem;">Interact with Instructors</div>
                    <div style="font-size: var(--font-size-xs); color: var(--text-secondary);">Ask questions and exchange feedback via messaging</div>
                </div>
                <span style="font-size: 1.25rem; color: var(--brand-accent); font-weight: 700;">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/profile" class="quick-link-card">
                <div>
                    <div style="font-weight: 600; font-size: var(--font-size-base); color: var(--text-primary); margin-bottom: 0.25rem;">Profile</div>
                    <div style="font-size: var(--font-size-xs); color: var(--text-secondary);">Manage account information and credentials</div>
                </div>
                <span style="font-size: 1.25rem; color: var(--brand-accent); font-weight: 700;">&rarr;</span>
            </a>
        </div>
    </div>
    <div class="card-footer" style="display: flex; justify-content: flex-end;">
        <form action="${pageContext.request.contextPath}/logout" method="post" style="display: inline; margin: 0;">
            <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken != null ? csrfToken : sessionScope.csrfToken}' />" />
            <button type="submit" class="btn btn-secondary btn-sm">Logout</button>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
