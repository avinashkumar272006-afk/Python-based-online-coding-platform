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
<c:set var="totalCount" value="${exercises != null ? exercises.size() : 0}" />
<c:set var="pendingCount" value="${pendingExercises != null ? pendingExercises.size() : 0}" />
<c:set var="approvedCount" value="0" />
<c:set var="rejectedCount" value="0" />
<c:set var="easyCount" value="0" />
<c:set var="mediumCount" value="0" />
<c:set var="hardCount" value="0" />

<c:if test="${not empty exercises}">
    <c:forEach var="ex" items="${exercises}">
        <c:choose>
            <c:when test="${ex.status == 'APPROVED'}">
                <c:set var="approvedCount" value="${approvedCount + 1}" />
            </c:when>
            <c:when test="${ex.status == 'REJECTED'}">
                <c:set var="rejectedCount" value="${rejectedCount + 1}" />
            </c:when>
        </c:choose>
        <c:choose>
            <c:when test="${ex.difficulty == 'EASY' || ex.difficulty == 'Easy'}">
                <c:set var="easyCount" value="${easyCount + 1}" />
            </c:when>
            <c:when test="${ex.difficulty == 'MEDIUM' || ex.difficulty == 'Medium'}">
                <c:set var="mediumCount" value="${mediumCount + 1}" />
            </c:when>
            <c:when test="${ex.difficulty == 'HARD' || ex.difficulty == 'Hard'}">
                <c:set var="hardCount" value="${hardCount + 1}" />
            </c:when>
        </c:choose>
    </c:forEach>
</c:if>

<div class="page-header">
    <div>
        <h1 class="page-title">Exercise Management</h1>
        <div class="page-subtitle">Review instructor-submitted coding challenges, approve or reject curriculum content, and inspect the platform exercise repository.</div>
    </div>
    <div style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-secondary btn-sm">
            &larr; Back to Dashboard
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
        <div class="metric-value" style="color: var(--state-warning);"><c:out value="${pendingCount}" /></div>
        <div class="metric-sub">Awaiting moderation review</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Approved</div>
        <div class="metric-value" style="color: var(--state-success);"><c:out value="${approvedCount}" /></div>
        <div class="metric-sub">Live student exercises</div>
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

<%-- Card 1: Content Approval (Pending Exercises) --%>
<div class="card" style="margin-bottom: 2rem;">
    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
        <div>
            <h2 class="card-title">Content Approval (Pending Exercises)</h2>
            <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">
                Exercises submitted by instructors requiring administrative moderation.
            </div>
        </div>
        <c:choose>
            <c:when test="${pendingCount > 0}">
                <span class="badge badge-warning"><c:out value="${pendingCount}" /> Action Required</span>
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
                                <th>Title</th>
                                <th style="width: 110px;">Difficulty</th>
                                <th>Topic</th>
                                <th style="width: 140px;">Created By</th>
                                <th style="width: 160px;">Created At</th>
                                <th style="width: 110px;">Status</th>
                                <th style="min-width: 180px; text-align: right;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="exercise" items="${pendingExercises}">
                                <tr>
                                    <td style="color: var(--text-muted); font-size: var(--font-size-xs); font-family: var(--font-mono);">
                                        #<c:out value="${exercise.id}" />
                                    </td>
                                    <td>
                                        <div style="font-weight: 600; color: var(--text-primary);">
                                            <c:out value="${exercise.title}" />
                                        </div>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${exercise.difficulty == 'EASY' || exercise.difficulty == 'Easy'}">
                                                <span class="badge badge-success"><c:out value="${exercise.difficulty}" /></span>
                                            </c:when>
                                            <c:when test="${exercise.difficulty == 'MEDIUM' || exercise.difficulty == 'Medium'}">
                                                <span class="badge badge-warning"><c:out value="${exercise.difficulty}" /></span>
                                            </c:when>
                                            <c:when test="${exercise.difficulty == 'HARD' || exercise.difficulty == 'Hard'}">
                                                <span class="badge badge-danger"><c:out value="${exercise.difficulty}" /></span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge"><c:out value="${exercise.difficulty}" /></span>
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
                                            User #<c:out value="${exercise.createdBy}" />
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
                                            <c:when test="${exercise.status == 'APPROVED'}">
                                                <span class="badge badge-success"><c:out value="${exercise.status}" /></span>
                                            </c:when>
                                            <c:when test="${exercise.status == 'PENDING'}">
                                                <span class="badge badge-warning"><c:out value="${exercise.status}" /></span>
                                            </c:when>
                                            <c:when test="${exercise.status == 'REJECTED'}">
                                                <span class="badge badge-danger"><c:out value="${exercise.status}" /></span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge"><c:out value="${exercise.status}" /></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="text-align: right;">
                                        <div style="display: inline-flex; gap: 0.375rem; align-items: center; justify-content: flex-end;">
                                            <form action="${pageContext.request.contextPath}/admin/exercises" method="post" style="display: inline; margin: 0;">
                                                <input type="hidden" name="csrfToken" value="${not empty csrfToken ? csrfToken : sessionScope.csrfToken}">
                                                <input type="hidden" name="id" value="${exercise.id}">
                                                <input type="hidden" name="action" value="approve">
                                                <button type="submit" class="btn btn-success btn-sm">
                                                    Approve
                                                </button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/admin/exercises" method="post" style="display: inline; margin: 0;">
                                                <input type="hidden" name="csrfToken" value="${not empty csrfToken ? csrfToken : sessionScope.csrfToken}">
                                                <input type="hidden" name="id" value="${exercise.id}">
                                                <input type="hidden" name="action" value="reject">
                                                <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to reject this exercise?');">
                                                    Reject
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
                <div style="padding: 3rem 1.5rem; text-align: center; color: var(--text-secondary);">
                    <div style="font-size: 2.25rem; margin-bottom: 0.5rem;">✅</div>
                    <p style="font-size: var(--font-size-base); font-weight: 600; margin-bottom: 0.25rem;">No Pending Exercises</p>
                    <p style="font-size: var(--font-size-sm); color: var(--text-muted); margin-bottom: 0;">All submitted exercises have been reviewed. There are no exercises awaiting approval.</p>
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
                Read-only catalog of all platform coding exercises and curriculum items.
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
                                <th>Title</th>
                                <th>Description</th>
                                <th style="width: 110px;">Difficulty</th>
                                <th>Topic</th>
                                <th style="width: 140px;">Created By</th>
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
                                        <div style="font-weight: 600; color: var(--text-primary);">
                                            <c:out value="${exercise.title}" />
                                        </div>
                                    </td>
                                    <td>
                                        <div style="max-width: 320px; color: var(--text-secondary); font-size: var(--font-size-xs); line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; text-overflow: ellipsis;" title="<c:out value="${exercise.description}" />">
                                            <c:out value="${exercise.description}" />
                                        </div>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${exercise.difficulty == 'EASY' || exercise.difficulty == 'Easy'}">
                                                <span class="badge badge-success"><c:out value="${exercise.difficulty}" /></span>
                                            </c:when>
                                            <c:when test="${exercise.difficulty == 'MEDIUM' || exercise.difficulty == 'Medium'}">
                                                <span class="badge badge-warning"><c:out value="${exercise.difficulty}" /></span>
                                            </c:when>
                                            <c:when test="${exercise.difficulty == 'HARD' || exercise.difficulty == 'Hard'}">
                                                <span class="badge badge-danger"><c:out value="${exercise.difficulty}" /></span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge"><c:out value="${exercise.difficulty}" /></span>
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
                                            User #<c:out value="${exercise.createdBy}" />
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
                                            <c:when test="${exercise.status == 'APPROVED'}">
                                                <span class="badge badge-success"><c:out value="${exercise.status}" /></span>
                                            </c:when>
                                            <c:when test="${exercise.status == 'PENDING'}">
                                                <span class="badge badge-warning"><c:out value="${exercise.status}" /></span>
                                            </c:when>
                                            <c:when test="${exercise.status == 'REJECTED'}">
                                                <span class="badge badge-danger"><c:out value="${exercise.status}" /></span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge"><c:out value="${exercise.status}" /></span>
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
                    <div style="font-size: 2.5rem; margin-bottom: 0.75rem;">📚</div>
                    <p style="font-size: var(--font-size-base); font-weight: 600; margin-bottom: 0.25rem;">No Exercises Found</p>
                    <p style="font-size: var(--font-size-sm); color: var(--text-muted); margin-bottom: 0;">There are currently no exercises registered in the curriculum library.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
