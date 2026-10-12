<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    if (request.getAttribute("performanceReport") == null && request.getAttribute("performanceError") == null) {
        response.sendRedirect(request.getContextPath() + "/instructor/dashboard");
        return;
    }
%>
<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="Instructor Dashboard - Python Coding Platform" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activeNav" value="dashboard" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageCategory" value="Instructor" />
    <jsp:param name="pageTitle" value="Dashboard" />
</jsp:include>

<div class="dashboard-welcome-card">
    <div class="dashboard-welcome-left">
        <div class="dashboard-welcome-avatar">🎓</div>
        <div class="dashboard-welcome-info">
            <h1 class="dashboard-welcome-title">Welcome back, <c:out value="${sessionScope.name != null ? sessionScope.name : 'Instructor'}" />!</h1>
            <p class="dashboard-welcome-desc">Manage your Python curriculum, evaluate student code submissions, and track course performance.</p>
            <div class="dashboard-welcome-tags">
                <span class="badge badge-info">Role: <c:out value="${sessionScope.role != null ? sessionScope.role : 'INSTRUCTOR'}" /></span>
                <c:choose>
                    <c:when test="${performanceReport != null and performanceReport.pendingSubmissions > 0}">
                        <span class="badge badge-warning"><c:out value="${performanceReport.pendingSubmissions}" /> Submissions Need Review</span>
                    </c:when>
                    <c:when test="${performanceReport != null}">
                        <span class="badge badge-success">Reviews Up to Date</span>
                    </c:when>
                </c:choose>
            </div>
        </div>
    </div>
    <div class="dashboard-welcome-actions">
        <a href="${pageContext.request.contextPath}/instructor/submissions" class="btn btn-primary">
            Review Submissions &rarr;
        </a>
        <a href="${pageContext.request.contextPath}/instructor/exercises?action=new" class="btn btn-secondary">
            + Create Exercise
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<c:if test="${not empty performanceError}">
    <div class="alert alert-warning">
        <c:out value="${performanceError}" />
    </div>
</c:if>

<div class="card" style="margin-bottom: 1.75rem;">
    <div class="card-header">
        <div>
            <h2 class="card-title">Evaluation & Performance Overview</h2>
            <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">Analytics across your authored exercises and student submissions</div>
        </div>
        <span class="badge badge-info">Course Analytics</span>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${performanceReport != null}">
                <c:if test="${performanceReport.pendingSubmissions > 0}">
                    <div style="display: flex; align-items: center; justify-content: space-between; padding: 0.875rem 1.125rem; background-color: var(--state-warning-bg); border: 1px solid var(--state-warning-border); border-radius: var(--radius-md); margin-bottom: 1.25rem; flex-wrap: wrap; gap: 0.75rem;">
                        <div>
                            <div style="font-weight: 600; font-size: var(--font-size-sm); color: var(--state-warning-text);">⚠️ Action Required: <c:out value="${performanceReport.pendingSubmissions}" /> Student Submissions Pending Review</div>
                            <div style="font-size: var(--font-size-xs); color: var(--state-warning-text); opacity: 0.9;">Student code submissions are awaiting instructor evaluation, score assignment, and feedback.</div>
                        </div>
                        <a href="${pageContext.request.contextPath}/instructor/submissions" class="btn btn-primary btn-sm">
                            Open Review Queue &rarr;
                        </a>
                    </div>
                </c:if>

                <div class="metrics-grid" style="margin-bottom: 0;">
                    <div class="metric-card">
                        <div class="metric-label">Total Submissions</div>
                        <div class="metric-value"><c:out value="${performanceReport.totalSubmissions}" /></div>
                        <div class="metric-sub">Across created exercises</div>
                    </div>
                    <div class="metric-card">
                        <div class="metric-label">Pending Reviews</div>
                        <div class="metric-value ${performanceReport.pendingSubmissions > 0 ? 'stat-warning' : ''}">
                            <c:out value="${performanceReport.pendingSubmissions}" />
                        </div>
                        <div class="metric-sub">Awaiting your evaluation</div>
                    </div>
                    <div class="metric-card">
                        <div class="metric-label">Reviewed Submissions</div>
                        <div class="metric-value stat-success"><c:out value="${performanceReport.reviewedSubmissions}" /></div>
                        <div class="metric-sub">Graded & feedback provided</div>
                    </div>
                    <div class="metric-card">
                        <div class="metric-label">Average Score</div>
                        <div class="metric-value stat-accent">
                            <c:out value="${performanceReport.averageScore != null ? performanceReport.averageScore : 'N/A'}" />
                        </div>
                        <div class="metric-sub">${performanceReport.averageScore != null ? 'Across graded submissions' : 'Pending first graded review'}</div>
                    </div>
                    <div class="metric-card">
                        <div class="metric-label">Highest Score</div>
                        <div class="metric-value stat-success">
                            <c:out value="${performanceReport.highestScore != null ? performanceReport.highestScore : 'N/A'}" />
                        </div>
                        <div class="metric-sub">${performanceReport.highestScore != null ? 'Top student achievement' : 'Pending evaluation'}</div>
                    </div>
                    <div class="metric-card">
                        <div class="metric-label">Lowest Score</div>
                        <div class="metric-value">
                            <c:out value="${performanceReport.lowestScore != null ? performanceReport.lowestScore : 'N/A'}" />
                        </div>
                        <div class="metric-sub">${performanceReport.lowestScore != null ? 'Minimum score recorded' : 'Pending evaluation'}</div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state-box">
                    <div style="font-size: 2rem; margin-bottom: 0.5rem;">📊</div>
                    <div class="empty-state-title">No Performance Data Available</div>
                    <div class="empty-state-text">Performance metrics will populate automatically once students start submitting solutions to your exercises.</div>
                    <div style="margin-top: 1rem;">
                        <a href="${pageContext.request.contextPath}/instructor/exercises?action=new" class="btn btn-primary btn-sm">
                            Create First Exercise &rarr;
                        </a>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<div class="card quick-links">
    <div class="card-header">
        <div>
            <h2 class="card-title">Teaching & Curriculum Hub</h2>
            <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">Direct access to exercise management, review queues, and messaging</div>
        </div>
    </div>
    <div class="card-body">
        <div class="quick-links-grid">
            <a href="${pageContext.request.contextPath}/instructor/submissions" class="quick-link-card">
                <div class="quick-link-content">
                    <div class="quick-link-icon-box">📝</div>
                    <div>
                        <div class="quick-link-title">Review Submissions</div>
                        <div class="quick-link-desc">Evaluate student code solutions, assign scores, and leave feedback</div>
                    </div>
                </div>
                <span class="quick-link-arrow">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/instructor/exercises" class="quick-link-card">
                <div class="quick-link-content">
                    <div class="quick-link-icon-box">📚</div>
                    <div>
                        <div class="quick-link-title">Manage Exercises</div>
                        <div class="quick-link-desc">View, edit, and organize your Python exercise curriculum</div>
                    </div>
                </div>
                <span class="quick-link-arrow">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/instructor/exercises?action=new" class="quick-link-card">
                <div class="quick-link-content">
                    <div class="quick-link-icon-box">➕</div>
                    <div>
                        <div class="quick-link-title">Create Exercise</div>
                        <div class="quick-link-desc">Author new coding problems with descriptions and test parameters</div>
                    </div>
                </div>
                <span class="quick-link-arrow">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/instructor/challenges" class="quick-link-card">
                <div class="quick-link-content">
                    <div class="quick-link-icon-box">🏆</div>
                    <div>
                        <div class="quick-link-title">Manage Challenges</div>
                        <div class="quick-link-desc">Create and supervise competitive timed programming contests</div>
                    </div>
                </div>
                <span class="quick-link-arrow">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/messages" class="quick-link-card">
                <div class="quick-link-content">
                    <div class="quick-link-icon-box">💬</div>
                    <div>
                        <div class="quick-link-title">Student Interaction</div>
                        <div class="quick-link-desc">Communicate with students and address exercise inquiries</div>
                    </div>
                </div>
                <span class="quick-link-arrow">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/profile" class="quick-link-card">
                <div class="quick-link-content">
                    <div class="quick-link-icon-box">👤</div>
                    <div>
                        <div class="quick-link-title">Profile & Settings</div>
                        <div class="quick-link-desc">Manage instructor credentials, personal information, and preferences</div>
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
            <h2 class="card-title">Instructor Workflow & Guidelines</h2>
            <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">Best practices for curriculum authoring and code evaluation</div>
        </div>
        <span class="badge badge-info">Teaching Guide</span>
    </div>
    <div class="card-body">
        <div class="workflow-steps-grid">
            <div class="workflow-step-card">
                <div class="workflow-step-badge">Stage 1</div>
                <h3 class="workflow-step-title">Curriculum Authoring</h3>
                <p class="workflow-step-desc">Publish well-structured Python exercises with precise instructions and difficulty ratings.</p>
            </div>
            <div class="workflow-step-card">
                <div class="workflow-step-badge">Stage 2</div>
                <h3 class="workflow-step-title">Code Inspection</h3>
                <p class="workflow-step-desc">Review submitted solutions for algorithmic correctness, code quality, and Pythonic idioms.</p>
            </div>
            <div class="workflow-step-card">
                <div class="workflow-step-badge">Stage 3</div>
                <h3 class="workflow-step-title">Constructive Feedback</h3>
                <p class="workflow-step-desc">Assign scores out of 100 and write helpful feedback notes to guide student improvement.</p>
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
