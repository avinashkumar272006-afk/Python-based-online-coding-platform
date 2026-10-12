<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%
    if (request.getAttribute("exercises") == null && request.getAttribute("pendingExercises") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/exercises");
        return;
    }
%>
<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="Exercise Management - Python Coding Platform" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activeNav" value="exercises" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageCategory" value="Administration" />
    <jsp:param name="pageTitle" value="Exercise Approvals" />
</jsp:include>

<%-- Metric derivation from existing model objects --%>
<c:set var="totalCount" value="${exercises != null ? exercises.size() : (pendingExercises != null ? pendingExercises.size() : 0)}" />
<c:set var="pendingCount" value="${pendingExercises != null ? pendingExercises.size() : 0}" />
<c:set var="approvedCount" value="0" />
<c:set var="rejectedCount" value="0" />
<c:set var="easyCount" value="0" />
<c:set var="mediumCount" value="0" />
<c:set var="hardCount" value="0" />

<c:if test="${not empty exercises}">
    <c:forEach var="ex" items="${exercises}">
        <c:choose>
            <c:when test="${ex.status == 'APPROVED' || ex.status == 'Approved' || ex.status == 'approved'}">
                <c:set var="approvedCount" value="${approvedCount + 1}" />
            </c:when>
            <c:when test="${ex.status == 'REJECTED' || ex.status == 'Rejected' || ex.status == 'rejected'}">
                <c:set var="rejectedCount" value="${rejectedCount + 1}" />
            </c:when>
            <c:when test="${ex.status == 'PENDING' || ex.status == 'Pending' || ex.status == 'pending'}">
                <c:if test="${pendingExercises == null}">
                    <c:set var="pendingCount" value="${pendingCount + 1}" />
                </c:if>
            </c:when>
        </c:choose>
        <c:choose>
            <c:when test="${ex.difficulty == 'EASY' || ex.difficulty == 'Easy' || ex.difficulty == 'easy'}">
                <c:set var="easyCount" value="${easyCount + 1}" />
            </c:when>
            <c:when test="${ex.difficulty == 'MEDIUM' || ex.difficulty == 'Medium' || ex.difficulty == 'medium'}">
                <c:set var="mediumCount" value="${mediumCount + 1}" />
            </c:when>
            <c:when test="${ex.difficulty == 'HARD' || ex.difficulty == 'Hard' || ex.difficulty == 'hard'}">
                <c:set var="hardCount" value="${hardCount + 1}" />
            </c:when>
        </c:choose>
    </c:forEach>
</c:if>

<div class="page-header">
    <div>
        <div style="display: flex; align-items: center; gap: 0.625rem; margin-bottom: 0.25rem;">
            <span style="font-size: 1.5rem;">📚</span>
            <h1 class="page-title" style="margin: 0;">Exercise Approvals &amp; Curriculum</h1>
            <span class="badge badge-info" style="font-size: 0.7rem;">Curriculum Admin</span>
        </div>
        <div class="page-subtitle" style="color: var(--text-secondary); font-size: var(--font-size-sm); max-width: 680px;">
            Moderate instructor-submitted coding challenges, approve or reject curriculum content, and inspect the platform problem library.
        </div>
    </div>
    <div style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-secondary btn-sm">
            &larr; Back to Dashboard
        </a>
        <a href="${pageContext.request.contextPath}/admin/challenges" class="btn btn-secondary btn-sm">
            🏆 Manage Challenges
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<%-- Summary KPI Metrics --%>
<div class="metrics-grid" style="margin-bottom: 1.75rem;">
    <div class="metric-card">
        <div class="metric-label">Total Exercises</div>
        <div class="metric-value"><c:out value="${totalCount}" /></div>
        <div class="metric-sub">Complete curriculum library</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Pending Approval</div>
        <div class="metric-value stat-warning"><c:out value="${pendingCount}" /></div>
        <div class="metric-sub">Awaiting moderation review</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Approved</div>
        <div class="metric-value stat-success"><c:out value="${approvedCount}" /></div>
        <div class="metric-sub">Active live exercises</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Difficulty Mix</div>
        <div class="metric-value" style="font-size: var(--font-size-xl); display: flex; gap: 0.5rem; align-items: center; margin-top: 0.25rem;">
            <span class="badge badge-success" title="Easy"><c:out value="${easyCount}" /> E</span>
            <span class="badge badge-warning" title="Medium"><c:out value="${mediumCount}" /> M</span>
            <span class="badge badge-danger" title="Hard"><c:out value="${hardCount}" /> H</span>
        </div>
        <div class="metric-sub" style="margin-top: 0.35rem;">Easy / Medium / Hard distribution</div>
    </div>
</div>

<c:if test="${pendingCount > 0}">
    <div style="display: flex; align-items: center; justify-content: space-between; padding: 0.875rem 1.125rem; background-color: var(--state-warning-bg); border: 1px solid var(--state-warning-border); border-radius: var(--radius-md); margin-bottom: 1.5rem; flex-wrap: wrap; gap: 0.75rem;">
        <div style="display: flex; align-items: flex-start; gap: 0.625rem;">
            <span style="font-size: 1.125rem; line-height: 1.2;">⚠️</span>
            <div>
                <div style="font-weight: 600; font-size: var(--font-size-sm); color: var(--state-warning-text);">
                    Action Required: <c:out value="${pendingCount}" /> Exercise<c:if test="${pendingCount != 1}">s</c:if> Awaiting Administrative Moderation
                </div>
                <div style="font-size: var(--font-size-xs); color: var(--state-warning-text); opacity: 0.9; margin-top: 0.125rem;">
                    Review submitted exercise specifications below. Approved challenges become instantly available to all learners across the platform.
                </div>
            </div>
        </div>
        <span class="badge badge-warning" style="font-size: 0.7rem;">Moderation Queue</span>
    </div>
</c:if>

<%-- Card 1: Content Approval (Pending Exercises) --%>
<div class="card" style="margin-bottom: 2rem;">
    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
        <div>
            <h2 class="card-title">Content Approval (Pending Moderation Queue)</h2>
            <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">
                Exercises authored by instructors requiring administrative approval before publication
            </div>
        </div>
        <c:choose>
            <c:when test="${pendingCount > 0}">
                <span class="badge badge-warning"><c:out value="${pendingCount}" /> Pending Approval</span>
            </c:when>
            <c:otherwise>
                <span class="badge badge-success">Queue Clean</span>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="card-body" style="padding: 0;">
        <c:choose>
            <c:when test="${not empty pendingExercises}">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th style="width: 80px;">ID</th>
                                <th>Exercise Title</th>
                                <th style="width: 110px;">Difficulty</th>
                                <th>Topic</th>
                                <th style="width: 140px;">Author</th>
                                <th style="width: 160px;">Submitted At</th>
                                <th style="width: 110px;">Status</th>
                                <th style="min-width: 180px; text-align: right;">Moderation Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="exercise" items="${pendingExercises}">
                                <tr>
                                    <td style="color: var(--text-muted); font-size: var(--font-size-xs); font-family: var(--font-mono);">
                                        #<c:out value="${exercise.id}" />
                                    </td>
                                    <td>
                                        <div style="font-weight: 600; color: var(--text-primary); font-size: var(--font-size-sm);">
                                            <c:out value="${exercise.title}" />
                                        </div>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${exercise.difficulty == 'EASY' || exercise.difficulty == 'Easy' || exercise.difficulty == 'easy'}">
                                                <span class="badge badge-success"><c:out value="${exercise.difficulty}" /></span>
                                            </c:when>
                                            <c:when test="${exercise.difficulty == 'MEDIUM' || exercise.difficulty == 'Medium' || exercise.difficulty == 'medium'}">
                                                <span class="badge badge-warning"><c:out value="${exercise.difficulty}" /></span>
                                            </c:when>
                                            <c:when test="${exercise.difficulty == 'HARD' || exercise.difficulty == 'Hard' || exercise.difficulty == 'hard'}">
                                                <span class="badge badge-danger"><c:out value="${exercise.difficulty}" /></span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge"><c:out value="${exercise.difficulty != null ? exercise.difficulty : 'N/A'}" /></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty exercise.topic}">
                                                <span class="badge badge-info" style="font-weight: 500;"><c:out value="${exercise.topic}" /></span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: var(--text-muted); font-size: var(--font-size-xs);">&mdash;</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <span style="font-family: var(--font-mono); font-size: var(--font-size-xs); color: var(--text-secondary);">
                                            Instructor #<c:out value="${exercise.createdBy}" />
                                        </span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty exercise.createdAt}">
                                                <span style="color: var(--text-secondary); font-size: var(--font-size-xs); white-space: nowrap;">
                                                    <fmt:formatDate value="${exercise.createdAt}" pattern="yyyy-MM-dd HH:mm:ss" />
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: var(--text-muted); font-size: var(--font-size-xs);">&mdash;</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <span class="badge badge-warning"><c:out value="${exercise.status != null ? exercise.status : 'PENDING'}" /></span>
                                    </td>
                                    <td style="text-align: right;">
                                        <div style="display: inline-flex; gap: 0.375rem; align-items: center; justify-content: flex-end;">
                                            <form action="${pageContext.request.contextPath}/admin/exercises" method="post" style="display: inline; margin: 0;">
                                                <input type="hidden" name="csrfToken" value="<c:out value='${not empty csrfToken ? csrfToken : sessionScope.csrfToken}' />">
                                                <input type="hidden" name="id" value="${exercise.id}">
                                                <input type="hidden" name="action" value="approve">
                                                <button type="submit" class="btn btn-success btn-sm" style="padding: 0.3125rem 0.625rem;">
                                                    ✓ Approve
                                                </button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/admin/exercises" method="post" style="display: inline; margin: 0;">
                                                <input type="hidden" name="csrfToken" value="<c:out value='${not empty csrfToken ? csrfToken : sessionScope.csrfToken}' />">
                                                <input type="hidden" name="id" value="${exercise.id}">
                                                <input type="hidden" name="action" value="reject">
                                                <button type="submit" class="btn btn-danger btn-sm" style="padding: 0.3125rem 0.625rem;" onclick="return confirm('Are you sure you want to reject this exercise?');">
                                                    ✕ Reject
                                                </button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state-box" style="margin: 2rem 1.5rem;">
                    <div style="font-size: 2.25rem; margin-bottom: 0.5rem;">✅</div>
                    <div class="empty-state-title">No Pending Exercises</div>
                    <div class="empty-state-text">All submitted curriculum exercises have been reviewed. There are currently no exercises awaiting administrative moderation.</div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%-- Card 2: All Exercises (Curriculum Library) --%>
<div class="card">
    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
        <div>
            <h2 class="card-title">All Exercises (Curriculum Library)</h2>
            <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">
                Complete repository of all curriculum exercises across approved, pending, and rejected states
            </div>
        </div>
        <span class="badge badge-info"><c:out value="${totalCount}" /> Total Exercises</span>
    </div>
    <div class="card-body" style="padding: 0;">
        <c:choose>
            <c:when test="${not empty exercises}">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th style="width: 80px;">ID</th>
                                <th>Exercise Title</th>
                                <th>Description</th>
                                <th style="width: 110px;">Difficulty</th>
                                <th>Topic</th>
                                <th style="width: 140px;">Author</th>
                                <th style="width: 160px;">Created At</th>
                                <th style="width: 110px;">Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="exercise" items="${exercises}">
                                <tr>
                                    <td style="color: var(--text-muted); font-size: var(--font-size-xs); font-family: var(--font-mono);">
                                        #<c:out value="${exercise.id}" />
                                    </td>
                                    <td>
                                        <div style="font-weight: 600; color: var(--text-primary); font-size: var(--font-size-sm);">
                                            <c:out value="${exercise.title}" />
                                        </div>
                                    </td>
                                    <td>
                                        <div style="max-width: 320px; color: var(--text-secondary); font-size: var(--font-size-xs); line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; text-overflow: ellipsis;" title="<c:out value="${exercise.description}" />">
                                            <c:out value="${exercise.description != null ? exercise.description : 'No description provided.'}" />
                                        </div>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${exercise.difficulty == 'EASY' || exercise.difficulty == 'Easy' || exercise.difficulty == 'easy'}">
                                                <span class="badge badge-success"><c:out value="${exercise.difficulty}" /></span>
                                            </c:when>
                                            <c:when test="${exercise.difficulty == 'MEDIUM' || exercise.difficulty == 'Medium' || exercise.difficulty == 'medium'}">
                                                <span class="badge badge-warning"><c:out value="${exercise.difficulty}" /></span>
                                            </c:when>
                                            <c:when test="${exercise.difficulty == 'HARD' || exercise.difficulty == 'Hard' || exercise.difficulty == 'hard'}">
                                                <span class="badge badge-danger"><c:out value="${exercise.difficulty}" /></span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge"><c:out value="${exercise.difficulty != null ? exercise.difficulty : 'N/A'}" /></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty exercise.topic}">
                                                <span class="badge badge-info" style="font-weight: 500;"><c:out value="${exercise.topic}" /></span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: var(--text-muted); font-size: var(--font-size-xs);">&mdash;</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <span style="font-family: var(--font-mono); font-size: var(--font-size-xs); color: var(--text-secondary);">
                                            Instructor #<c:out value="${exercise.createdBy}" />
                                        </span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty exercise.createdAt}">
                                                <span style="color: var(--text-secondary); font-size: var(--font-size-xs); white-space: nowrap;">
                                                    <fmt:formatDate value="${exercise.createdAt}" pattern="yyyy-MM-dd HH:mm:ss" />
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: var(--text-muted); font-size: var(--font-size-xs);">&mdash;</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${exercise.status == 'APPROVED' || exercise.status == 'Approved' || exercise.status == 'approved'}">
                                                <span class="badge badge-success">Approved</span>
                                            </c:when>
                                            <c:when test="${exercise.status == 'PENDING' || exercise.status == 'Pending' || exercise.status == 'pending'}">
                                                <span class="badge badge-warning">Pending</span>
                                            </c:when>
                                            <c:when test="${exercise.status == 'REJECTED' || exercise.status == 'Rejected' || exercise.status == 'rejected'}">
                                                <span class="badge badge-danger">Rejected</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge"><c:out value="${exercise.status != null ? exercise.status : 'N/A'}" /></span>
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
                    <div style="font-size: 2.25rem; margin-bottom: 0.5rem;">📚</div>
                    <div class="empty-state-title">No Exercises Found</div>
                    <div class="empty-state-text">There are currently no exercises registered in the curriculum library.</div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
