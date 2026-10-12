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

<div class="dashboard-welcome-card">
    <div class="dashboard-welcome-left">
        <div class="dashboard-welcome-avatar">🐍</div>
        <div class="dashboard-welcome-info">
            <h1 class="dashboard-welcome-title">Welcome, <c:out value="${sessionScope.name != null ? sessionScope.name : 'Student'}" />!</h1>
            <p class="dashboard-welcome-desc">Track your Python learning journey, submit exercises for instructor review, and monitor your feedback.</p>
            <div class="dashboard-welcome-tags">
                <span class="badge badge-success">Role: <c:out value="${sessionScope.role != null ? sessionScope.role : 'STUDENT'}" /></span>
                <span class="badge badge-info"><c:out value="${progress.progressPercentage}" />% Curriculum Completed</span>
            </div>
        </div>
    </div>
    <div class="dashboard-welcome-actions">
        <a href="${pageContext.request.contextPath}/student/exercises" class="btn btn-primary">
            Browse Exercises &rarr;
        </a>
        <a href="${pageContext.request.contextPath}/student/submissions" class="btn btn-secondary">
            My Submissions
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<div class="card progress-section">
    <div class="card-header">
        <h2 class="card-title">Curriculum Progress</h2>
        <span class="badge ${progress.progressPercentage == 100 ? 'badge-success' : 'badge-info'}">Completion: ${progress.progressPercentage}%</span>
    </div>
    <div class="card-body">
        <div style="margin-bottom: 1.5rem;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem; flex-wrap: wrap; gap: 0.5rem;">
                <span style="font-weight: 600; color: var(--text-primary);">Overall Progress: ${progress.progressPercentage}%</span>
                <span style="font-size: var(--font-size-sm); color: var(--text-secondary);">${progress.submittedExercises} of ${progress.totalExercises} Exercises Submitted</span>
            </div>
            <div class="progress-bar-container">
                <div class="progress-bar-fill" style="width: ${progress.progressPercentage}%;"></div>
            </div>
        </div>

        <c:if test="${progress.totalExercises == 0}">
            <div class="empty-state-box">
                <div class="empty-state-title">No Curriculum Exercises Available</div>
                <div class="empty-state-text">Exercises are currently being prepared by instructors. Please check back shortly.</div>
            </div>
        </c:if>

        <c:if test="${progress.totalExercises > 0 && progress.submittedExercises == 0}">
            <div class="empty-state-box">
                <div class="empty-state-title">Ready to Start Practicing?</div>
                <div class="empty-state-text">You haven't submitted any Python exercises yet. Explore the curriculum problems and submit your solutions for instructor review.</div>
                <div style="margin-top: 1rem;">
                    <a href="${pageContext.request.contextPath}/student/exercises" class="btn btn-primary btn-sm">
                        Start First Exercise &rarr;
                    </a>
                </div>
            </div>
        </c:if>

        <div class="metrics-grid" style="margin-bottom: 0;">
            <div class="metric-card">
                <div class="metric-label">Total Exercises</div>
                <div class="metric-value">${progress.totalExercises}</div>
                <div class="metric-sub">Curriculum total</div>
            </div>
            <div class="metric-card">
                <div class="metric-label">Submitted</div>
                <div class="metric-value stat-accent">${progress.submittedExercises}</div>
                <div class="metric-sub">Completed submissions</div>
            </div>
            <div class="metric-card">
                <div class="metric-label">Reviewed</div>
                <div class="metric-value stat-success">${progress.reviewedSubmissions}</div>
                <div class="metric-sub">Evaluated by instructors</div>
            </div>
            <div class="metric-card">
                <div class="metric-label">Pending Review</div>
                <div class="metric-value stat-warning">${progress.pendingSubmissions}</div>
                <div class="metric-sub">Awaiting instructor review</div>
            </div>
            <div class="metric-card">
                <div class="metric-label">Average Score</div>
                <div class="metric-value">${progress.averageScore != null ? progress.averageScore : 'N/A'}</div>
                <div class="metric-sub">${progress.averageScore != null ? 'Across graded submissions' : 'Pending graded reviews'}</div>
            </div>
        </div>
    </div>
</div>

<div class="card quick-links">
    <div class="card-header">
        <div>
            <h2 class="card-title">Learning & Practice Hub</h2>
            <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">Explore courses, exercises, challenges, and mentor interactions</div>
        </div>
    </div>
    <div class="card-body">
        <div class="quick-links-grid">
            <a href="${pageContext.request.contextPath}/student/exercises" class="quick-link-card">
                <div class="quick-link-content">
                    <div class="quick-link-icon-box">💻</div>
                    <div>
                        <div class="quick-link-title">Browse Exercises</div>
                        <div class="quick-link-desc">Practice Python problems and submit solutions for review</div>
                    </div>
                </div>
                <span class="quick-link-arrow">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/student/submissions" class="quick-link-card">
                <div class="quick-link-content">
                    <div class="quick-link-icon-box">📝</div>
                    <div>
                        <div class="quick-link-title">My Submissions</div>
                        <div class="quick-link-desc">View submission history, instructor scores, and feedback</div>
                    </div>
                </div>
                <span class="quick-link-arrow">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/student/challenges" class="quick-link-card">
                <div class="quick-link-content">
                    <div class="quick-link-icon-box">🏆</div>
                    <div>
                        <div class="quick-link-title">Available Challenges</div>
                        <div class="quick-link-desc">Compete in timed programming contests and challenges</div>
                    </div>
                </div>
                <span class="quick-link-arrow">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/student/my-challenges" class="quick-link-card">
                <div class="quick-link-content">
                    <div class="quick-link-icon-box">🎯</div>
                    <div>
                        <div class="quick-link-title">My Challenges</div>
                        <div class="quick-link-desc">Track enrolled challenges and contest standings</div>
                    </div>
                </div>
                <span class="quick-link-arrow">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/messages" class="quick-link-card">
                <div class="quick-link-content">
                    <div class="quick-link-icon-box">💬</div>
                    <div>
                        <div class="quick-link-title">Interact with Instructors</div>
                        <div class="quick-link-desc">Ask questions and exchange feedback via messaging</div>
                    </div>
                </div>
                <span class="quick-link-arrow">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/profile" class="quick-link-card">
                <div class="quick-link-content">
                    <div class="quick-link-icon-box">👤</div>
                    <div>
                        <div class="quick-link-title">My Profile</div>
                        <div class="quick-link-desc">Manage account information, credentials, and settings</div>
                    </div>
                </div>
                <span class="quick-link-arrow">&rarr;</span>
            </a>
        </div>
    </div>
</div>

<div class="card">
    <div class="card-header">
        <div>
            <h2 class="card-title">How Learning Works</h2>
            <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">Instructor-reviewed learning methodology</div>
        </div>
        <span class="badge badge-info">Instructor Feedback Cycle</span>
    </div>
    <div class="card-body">
        <div class="workflow-steps-grid">
            <div class="workflow-step-card">
                <div class="workflow-step-badge">Step 1</div>
                <h3 class="workflow-step-title">Practice & Solve</h3>
                <p class="workflow-step-desc">Pick an exercise from the Python curriculum and write your solution code.</p>
            </div>
            <div class="workflow-step-card">
                <div class="workflow-step-badge">Step 2</div>
                <h3 class="workflow-step-title">Submit Solution</h3>
                <p class="workflow-step-desc">Submit your Python code. Solutions are queued for manual instructor evaluation.</p>
            </div>
            <div class="workflow-step-card">
                <div class="workflow-step-badge">Step 3</div>
                <h3 class="workflow-step-title">Review & Feedback</h3>
                <p class="workflow-step-desc">Instructors review your code quality, assign a grade score, and provide feedback.</p>
            </div>
        </div>
    </div>
    <div class="card-footer" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
        <div>
            <a href="${pageContext.request.contextPath}/profile" class="btn btn-secondary btn-sm">
                View Profile & Settings
            </a>
        </div>
        <form action="${pageContext.request.contextPath}/logout" method="post" style="display: inline; margin: 0;">
            <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken != null ? csrfToken : sessionScope.csrfToken}' />" />
            <button type="submit" class="btn btn-secondary btn-sm">Logout</button>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
