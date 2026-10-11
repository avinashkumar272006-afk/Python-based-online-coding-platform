<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%
    if (request.getAttribute("submissions") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/submissions");
        return;
    }
%>
<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="All Submissions - Python Coding Platform" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activeNav" value="submissions" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageCategory" value="Administration" />
    <jsp:param name="pageTitle" value="All Submissions" />
</jsp:include>

<%-- Metric derivation from existing model objects --%>
<c:set var="totalCount" value="${submissions != null ? submissions.size() : 0}" />
<c:set var="pendingCount" value="0" />
<c:set var="reviewedCount" value="0" />
<c:set var="gradedCount" value="0" />

<c:if test="${not empty submissions}">
    <c:forEach var="sub" items="${submissions}">
        <c:choose>
            <c:when test="${sub.status == 'PENDING'}">
                <c:set var="pendingCount" value="${pendingCount + 1}" />
            </c:when>
            <c:when test="${sub.status == 'REVIEWED'}">
                <c:set var="reviewedCount" value="${reviewedCount + 1}" />
            </c:when>
        </c:choose>
        <c:if test="${sub.score != null}">
            <c:set var="gradedCount" value="${gradedCount + 1}" />
        </c:if>
    </c:forEach>
</c:if>

<div class="page-header">
    <div>
        <h1 class="page-title">All Submissions</h1>
        <div class="page-subtitle">Audit platform-wide code submissions, track grading progress, and inspect student learning activity.</div>
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
        <div class="metric-label">Total Submissions</div>
        <div class="metric-value"><c:out value="${totalCount}" /></div>
        <div class="metric-sub">Platform-wide student attempts</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Pending Review</div>
        <div class="metric-value" style="color: var(--state-warning);"><c:out value="${pendingCount}" /></div>
        <div class="metric-sub">Awaiting instructor evaluation</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Reviewed</div>
        <div class="metric-value" style="color: var(--state-success);"><c:out value="${reviewedCount}" /></div>
        <div class="metric-sub">Evaluated by instructors</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Graded Submissions</div>
        <div class="metric-value" style="color: var(--brand-accent);"><c:out value="${gradedCount}" /></div>
        <div class="metric-sub">Submissions with scores assigned</div>
    </div>
</div>

<%-- Submissions Audit Log Table --%>
<div class="card">
    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
        <div>
            <h2 class="card-title">All Submissions</h2>
            <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">
                Platform-wide student submission audit log (read-only administrative view).
            </div>
        </div>
        <div style="display: flex; gap: 0.5rem; align-items: center; flex-wrap: wrap;">
            <c:if test="${pendingCount > 0}">
                <span class="badge badge-warning"><c:out value="${pendingCount}" /> Pending</span>
            </c:if>
            <span class="badge badge-info"><c:out value="${totalCount}" /> Total</span>
        </div>
    </div>
    <div class="card-body" style="padding: 0;">
        <c:choose>
            <c:when test="${not empty submissions}">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th style="width: 80px;">ID</th>
                                <th style="min-width: 150px;">Student</th>
                                <th style="min-width: 170px;">Exercise</th>
                                <th style="width: 120px;">Status</th>
                                <th style="width: 110px;">Score</th>
                                <th style="min-width: 160px;">Submitted At</th>
                                <th style="min-width: 160px;">Reviewed At</th>
                                <th style="min-width: 200px;">Feedback</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="sub" items="${submissions}">
                                <tr>
                                    <td style="color: var(--text-muted); font-size: var(--font-size-xs); font-family: var(--font-mono);">
                                        #<c:out value="${sub.id}" />
                                    </td>
                                    <td>
                                        <div style="font-weight: 600; color: var(--text-primary);">
                                            <c:out value="${sub.studentName}" />
                                        </div>
                                        <div style="font-family: var(--font-mono); font-size: var(--font-size-xs); color: var(--text-muted); margin-top: 2px;">
                                            User #<c:out value="${sub.studentId}" />
                                        </div>
                                    </td>
                                    <td>
                                        <div style="font-weight: 600; color: var(--text-primary);">
                                            <c:out value="${sub.exerciseTitle}" />
                                        </div>
                                        <div style="font-family: var(--font-mono); font-size: var(--font-size-xs); color: var(--text-muted); margin-top: 2px;">
                                            Ex #<c:out value="${sub.exerciseId}" />
                                        </div>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${sub.status == 'REVIEWED'}">
                                                <span class="badge badge-success"><c:out value="${sub.status}" /></span>
                                            </c:when>
                                            <c:when test="${sub.status == 'PENDING'}">
                                                <span class="badge badge-warning">Pending Review</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge"><c:out value="${sub.status}" /></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${sub.score != null}">
                                                <span style="font-weight: 700; color: ${sub.score >= 70 ? 'var(--state-success)' : 'var(--text-primary)'};">
                                                    <c:out value="${sub.score}" />/100
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: var(--text-muted); font-size: var(--font-size-xs); font-style: italic;">
                                                    Not graded
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty sub.submittedAt}">
                                                <span style="color: var(--text-secondary); font-size: var(--font-size-xs); white-space: nowrap;">
                                                    <fmt:formatDate value="${sub.submittedAt}" pattern="yyyy-MM-dd HH:mm:ss" />
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: var(--text-muted); font-size: var(--font-size-xs);">&mdash;</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty sub.reviewedAt}">
                                                <span style="color: var(--text-secondary); font-size: var(--font-size-xs); white-space: nowrap;">
                                                    <fmt:formatDate value="${sub.reviewedAt}" pattern="yyyy-MM-dd HH:mm:ss" />
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: var(--text-muted); font-size: var(--font-size-xs); font-style: italic;">
                                                    Pending
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty sub.feedback}">
                                                <div style="max-width: 280px; color: var(--text-secondary); font-size: var(--font-size-xs); line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; text-overflow: ellipsis;" title="<c:out value="${sub.feedback}" />">
                                                    <c:out value="${sub.feedback}" />
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: var(--text-muted); font-size: var(--font-size-xs);">&mdash;</span>
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
                    <div style="font-size: 2.5rem; margin-bottom: 0.75rem;">📝</div>
                    <p style="font-size: var(--font-size-base); font-weight: 600; margin-bottom: 0.25rem;">No Submissions Found</p>
                    <p style="font-size: var(--font-size-sm); color: var(--text-muted); margin-bottom: 0;">There are currently no student exercise submissions recorded on the platform.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
