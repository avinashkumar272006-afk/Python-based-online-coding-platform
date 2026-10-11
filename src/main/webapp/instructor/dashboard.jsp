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

<div class="page-header">
    <div>
        <h1 class="page-title">Instructor Dashboard</h1>
        <div class="page-subtitle">Welcome back, <c:out value="${sessionScope.name != null ? sessionScope.name : 'Instructor'}" />! Manage your curriculum, review submissions, and track teaching performance.</div>
    </div>
    <div style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
        <span class="badge badge-info">Role: <c:out value="${sessionScope.role != null ? sessionScope.role : 'INSTRUCTOR'}" /></span>
        <a href="${pageContext.request.contextPath}/instructor/exercises?action=new" class="btn btn-primary btn-sm">
            + Create Exercise
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<c:choose>
    <c:when test="${empty performanceError and performanceReport != null}">
        <div class="card" style="margin-bottom: 1.75rem;">
            <div class="card-header">
                <h2 class="card-title">Performance Overview</h2>
                <span class="badge badge-info">Course Analytics</span>
            </div>
            <div class="card-body">
                <div class="metrics-grid" style="margin-bottom: 0;">
                    <div class="metric-card">
                        <div class="metric-label">Total Submissions</div>
                        <div class="metric-value"><c:out value="${performanceReport.totalSubmissions}" /></div>
                        <div class="metric-sub">Across all created exercises</div>
                    </div>
                    <div class="metric-card">
                        <div class="metric-label">Pending Reviews</div>
                        <div class="metric-value" style="color: ${performanceReport.pendingSubmissions > 0 ? 'var(--state-warning)' : 'inherit'};">
                            <c:out value="${performanceReport.pendingSubmissions}" />
                        </div>
                        <div class="metric-sub">Awaiting instructor evaluation</div>
                    </div>
                    <div class="metric-card">
                        <div class="metric-label">Reviewed Submissions</div>
                        <div class="metric-value"><c:out value="${performanceReport.reviewedSubmissions}" /></div>
                        <div class="metric-sub">Graded & feedback provided</div>
                    </div>
                    <div class="metric-card">
                        <div class="metric-label">Average Score</div>
                        <div class="metric-value"><c:out value="${performanceReport.averageScore != null ? performanceReport.averageScore : 'N/A'}" /></div>
                        <div class="metric-sub">Average score achieved</div>
                    </div>
                    <div class="metric-card">
                        <div class="metric-label">Highest Score</div>
                        <div class="metric-value" style="color: var(--state-success);"><c:out value="${performanceReport.highestScore != null ? performanceReport.highestScore : 'N/A'}" /></div>
                        <div class="metric-sub">Top student score</div>
                    </div>
                    <div class="metric-card">
                        <div class="metric-label">Lowest Score</div>
                        <div class="metric-value"><c:out value="${performanceReport.lowestScore != null ? performanceReport.lowestScore : 'N/A'}" /></div>
                        <div class="metric-sub">Minimum score recorded</div>
                    </div>
                </div>
            </div>
        </div>
    </c:when>
    <c:when test="${empty performanceError and performanceReport == null}">
        <div class="card" style="margin-bottom: 1.75rem;">
            <div class="card-body" style="padding: 2.5rem 1.5rem; text-align: center; color: var(--text-secondary);">
                <div style="font-size: 2rem; margin-bottom: 0.5rem;">📊</div>
                <p style="font-size: var(--font-size-base); font-weight: 600; margin-bottom: 0.25rem;">No Performance Data Available</p>
                <p style="font-size: var(--font-size-sm); color: var(--text-muted); margin-bottom: 0;">Performance metrics will appear once students begin submitting solutions for your exercises.</p>
            </div>
        </div>
    </c:when>
</c:choose>

<div class="card quick-links">
    <div class="card-header">
        <h2 class="card-title">Teaching & Platform Management</h2>
    </div>
    <div class="card-body">
        <div class="quick-links-grid">
            <a href="${pageContext.request.contextPath}/instructor/exercises" class="quick-link-card">
                <div>
                    <div style="font-weight: 600; font-size: var(--font-size-base); color: var(--text-primary); margin-bottom: 0.25rem;">Manage Exercises</div>
                    <div style="font-size: var(--font-size-xs); color: var(--text-secondary);">View, edit, and organize your Python exercise library</div>
                </div>
                <span style="font-size: 1.25rem; color: var(--brand-accent); font-weight: 700;">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/instructor/exercises?action=new" class="quick-link-card">
                <div>
                    <div style="font-weight: 600; font-size: var(--font-size-base); color: var(--text-primary); margin-bottom: 0.25rem;">Create Exercise</div>
                    <div style="font-size: var(--font-size-xs); color: var(--text-secondary);">Author new coding challenges with test cases</div>
                </div>
                <span style="font-size: 1.25rem; color: var(--brand-accent); font-weight: 700;">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/instructor/submissions" class="quick-link-card">
                <div>
                    <div style="font-weight: 600; font-size: var(--font-size-base); color: var(--text-primary); margin-bottom: 0.25rem;">Review Submissions</div>
                    <div style="font-size: var(--font-size-xs); color: var(--text-secondary);">Evaluate student code submissions and provide grades</div>
                </div>
                <span style="font-size: 1.25rem; color: var(--brand-accent); font-weight: 700;">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/instructor/challenges" class="quick-link-card">
                <div>
                    <div style="font-weight: 600; font-size: var(--font-size-base); color: var(--text-primary); margin-bottom: 0.25rem;">Manage Challenges</div>
                    <div style="font-size: var(--font-size-xs); color: var(--text-secondary);">Create and monitor competitive coding challenges</div>
                </div>
                <span style="font-size: 1.25rem; color: var(--brand-accent); font-weight: 700;">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/messages" class="quick-link-card">
                <div>
                    <div style="font-weight: 600; font-size: var(--font-size-base); color: var(--text-primary); margin-bottom: 0.25rem;">Student Interaction</div>
                    <div style="font-size: var(--font-size-xs); color: var(--text-secondary);">Engage with students and address questions via messaging</div>
                </div>
                <span style="font-size: 1.25rem; color: var(--brand-accent); font-weight: 700;">&rarr;</span>
            </a>
            <a href="${pageContext.request.contextPath}/profile" class="quick-link-card">
                <div>
                    <div style="font-weight: 600; font-size: var(--font-size-base); color: var(--text-primary); margin-bottom: 0.25rem;">Profile & Account</div>
                    <div style="font-size: var(--font-size-xs); color: var(--text-secondary);">Manage account credentials and personal preferences</div>
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
