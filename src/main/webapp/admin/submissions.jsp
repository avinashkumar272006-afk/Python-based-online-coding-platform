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
<c:set var="totalScore" value="0" />
<c:set var="passedCount" value="0" />

<c:if test="${not empty submissions}">
    <c:forEach var="sub" items="${submissions}">
        <c:choose>
            <c:when test="${sub.status == 'PENDING' || sub.status == 'Pending' || sub.status == 'pending'}">
                <c:set var="pendingCount" value="${pendingCount + 1}" />
            </c:when>
            <c:when test="${sub.status == 'REVIEWED' || sub.status == 'Reviewed' || sub.status == 'reviewed'}">
                <c:set var="reviewedCount" value="${reviewedCount + 1}" />
            </c:when>
        </c:choose>
        <c:if test="${sub.score != null}">
            <c:set var="gradedCount" value="${gradedCount + 1}" />
            <c:set var="totalScore" value="${totalScore + sub.score}" />
            <c:if test="${sub.score >= 70}">
                <c:set var="passedCount" value="${passedCount + 1}" />
            </c:if>
        </c:if>
    </c:forEach>
</c:if>

<div class="page-header">
    <div>
        <div style="display: flex; align-items: center; gap: 0.625rem; margin-bottom: 0.25rem;">
            <span style="font-size: 1.5rem;">📋</span>
            <h1 class="page-title" style="margin: 0;">Platform Submissions &amp; Code Audit</h1>
            <span class="badge badge-info" style="font-size: 0.7rem;">Audit Log</span>
        </div>
        <div class="page-subtitle" style="color: var(--text-secondary); font-size: var(--font-size-sm); max-width: 680px;">
            Audit platform-wide student code submissions, track grading progress, inspect evaluation turnaround, and review feedback.
        </div>
    </div>
    <div style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-secondary btn-sm">
            &larr; Back to Dashboard
        </a>
        <a href="${pageContext.request.contextPath}/admin/exercises" class="btn btn-secondary btn-sm">
            📚 Manage Exercises
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
        <div class="metric-label">Total Submissions</div>
        <div class="metric-value"><c:out value="${totalCount}" /></div>
        <div class="metric-sub">Platform-wide student attempts</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Pending Review</div>
        <div class="metric-value stat-warning"><c:out value="${pendingCount}" /></div>
        <div class="metric-sub">Awaiting instructor evaluation</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Reviewed</div>
        <div class="metric-value stat-success"><c:out value="${reviewedCount}" /></div>
        <div class="metric-sub">Instructor evaluated submissions</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Graded Solutions</div>
        <div class="metric-value stat-accent">
            <c:choose>
                <c:when test="${gradedCount > 0}">
                    <fmt:formatNumber value="${totalScore / gradedCount}" maxFractionDigits="1" />/100
                </c:when>
                <c:otherwise>&mdash;</c:otherwise>
            </c:choose>
        </div>
        <div class="metric-sub">
            <c:choose>
                <c:when test="${gradedCount > 0}">
                    <c:out value="${gradedCount}" /> graded (<c:out value="${passedCount}" /> &ge; 70)
                </c:when>
                <c:otherwise>No graded submissions yet</c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<%-- Queue Alert if Pending Reviews Exist --%>
<c:if test="${pendingCount > 0}">
    <div style="display: flex; align-items: center; justify-content: space-between; padding: 0.875rem 1.125rem; background-color: var(--state-warning-bg); border: 1px solid var(--state-warning-border); border-radius: var(--radius-md); margin-bottom: 1.5rem; flex-wrap: wrap; gap: 0.75rem;">
        <div style="display: flex; align-items: flex-start; gap: 0.625rem;">
            <span style="font-size: 1.125rem; line-height: 1.2;">⏳</span>
            <div>
                <div style="font-weight: 600; font-size: var(--font-size-sm); color: var(--state-warning-text);">
                    Queue Alert: <c:out value="${pendingCount}" /> Submission<c:if test="${pendingCount != 1}">s</c:if> Awaiting Instructor Review
                </div>
                <div style="font-size: var(--font-size-xs); color: var(--state-warning-text); opacity: 0.9; margin-top: 0.125rem;">
                    Students have submitted code solutions awaiting evaluation by course instructors. Administrators have read-only audit visibility.
                </div>
            </div>
        </div>
        <span class="badge badge-warning" style="font-size: 0.7rem;"><c:out value="${pendingCount}" /> Pending</span>
    </div>
</c:if>

<%-- Administrative Policy & Scope Callout --%>
<div style="display: flex; align-items: center; justify-content: space-between; padding: 0.875rem 1.125rem; background-color: var(--state-info-bg); border: 1px solid var(--state-info-border); border-radius: var(--radius-md); margin-bottom: 1.5rem; gap: 0.75rem; flex-wrap: wrap;">
    <div style="display: flex; align-items: flex-start; gap: 0.625rem;">
        <span style="font-size: 1.125rem; line-height: 1.2;">🛡️</span>
        <div>
            <div style="font-weight: 600; font-size: var(--font-size-sm); color: var(--state-info-text);">
                Administrative Oversight Scope
            </div>
            <div style="font-size: var(--font-size-xs); color: var(--state-info-text); opacity: 0.9; margin-top: 0.125rem;">
                This audit log records student code submissions and instructor grading history. Instructors evaluate code directly via their instructor portal.
            </div>
        </div>
    </div>
    <span class="badge badge-info" style="font-size: 0.7rem;">Read-Only Audit</span>
</div>

<%-- Submissions Audit Log Table Card --%>
<div class="card" style="margin-bottom: 2rem;">
    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
        <div>
            <h2 class="card-title">All Submissions Audit Log</h2>
            <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">
                Comprehensive student submission history with execution code, evaluation turnaround, and feedback.
            </div>
        </div>
        <div style="display: flex; gap: 0.5rem; align-items: center; flex-wrap: wrap;">
            <c:if test="${pendingCount > 0}">
                <span class="badge badge-warning"><c:out value="${pendingCount}" /> Pending</span>
            </c:if>
            <span class="badge badge-info"><c:out value="${totalCount}" /> Records</span>
        </div>
    </div>

    <c:if test="${not empty submissions}">
        <%-- Interactive Filter Bar --%>
        <div style="padding: 0.875rem 1.25rem; background-color: var(--bg-subtle); border-bottom: 1px solid var(--border-subtle); display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
            <div style="display: flex; align-items: center; gap: 0.75rem; flex: 1; min-width: 260px; max-width: 480px;">
                <input type="text" id="submissionSearchInput" class="form-control" style="font-size: var(--font-size-xs); padding: 0.4375rem 0.75rem;" placeholder="Search by student, exercise title, or ID..." onkeyup="filterSubmissionsTable()" />
            </div>
            <div style="display: flex; gap: 0.375rem; align-items: center;">
                <button type="button" class="btn btn-primary btn-sm submission-filter-btn" data-status="ALL" onclick="setStatusFilter('ALL', this)">All (<c:out value="${totalCount}" />)</button>
                <button type="button" class="btn btn-secondary btn-sm submission-filter-btn" data-status="PENDING" onclick="setStatusFilter('PENDING', this)">Pending (<c:out value="${pendingCount}" />)</button>
                <button type="button" class="btn btn-secondary btn-sm submission-filter-btn" data-status="REVIEWED" onclick="setStatusFilter('REVIEWED', this)">Reviewed (<c:out value="${reviewedCount}" />)</button>
            </div>
        </div>
    </c:if>

    <div class="card-body" style="padding: 0;">
        <c:choose>
            <c:when test="${not empty submissions}">
                <div class="table-responsive">
                    <table class="data-table" id="submissionsAuditTable">
                        <thead>
                            <tr>
                                <th style="width: 75px;">ID</th>
                                <th style="min-width: 160px;">Student</th>
                                <th style="min-width: 170px;">Exercise</th>
                                <th style="width: 120px;">Status</th>
                                <th style="width: 110px;">Score</th>
                                <th style="min-width: 150px;">Submitted</th>
                                <th style="min-width: 150px;">Reviewed</th>
                                <th style="min-width: 180px;">Feedback</th>
                                <th style="width: 110px; text-align: right;">Code Audit</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="sub" items="${submissions}">
                                <c:set var="normalizedStatus" value="${sub.status != null ? sub.status.toUpperCase() : 'UNKNOWN'}" />
                                <tr class="submission-data-row" data-id="${sub.id}" data-status="${normalizedStatus}" data-search="<c:out value='${sub.id} ${sub.studentName} ${sub.studentId} ${sub.exerciseTitle} ${sub.exerciseId} ${sub.status}' />">
                                    <td style="color: var(--text-muted); font-size: var(--font-size-xs); font-family: var(--font-mono);">
                                        #<c:out value="${sub.id}" />
                                    </td>
                                    <td>
                                        <div style="display: flex; align-items: center; gap: 0.5rem;">
                                            <div style="width: 26px; height: 26px; border-radius: var(--radius-full); background: var(--brand-accent-light); color: var(--brand-accent); display: flex; align-items: center; justify-content: center; font-size: 0.7rem; border: 1px solid var(--brand-accent-subtle); flex-shrink: 0;">
                                                👤
                                            </div>
                                            <div>
                                                <div style="font-weight: 600; color: var(--text-primary); font-size: var(--font-size-sm);">
                                                    <c:choose>
                                                        <c:when test="${not empty sub.studentName}">
                                                            <c:out value="${sub.studentName}" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            Student #<c:out value="${sub.studentId}" />
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <div style="font-family: var(--font-mono); font-size: 0.7rem; color: var(--text-muted); margin-top: 1px;">
                                                    User #<c:out value="${sub.studentId}" />
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <div style="font-weight: 600; color: var(--text-primary); font-size: var(--font-size-sm);">
                                            <c:choose>
                                                <c:when test="${not empty sub.exerciseTitle}">
                                                    <c:out value="${sub.exerciseTitle}" />
                                                </c:when>
                                                <c:otherwise>
                                                    Exercise #<c:out value="${sub.exerciseId}" />
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div style="font-family: var(--font-mono); font-size: 0.7rem; color: var(--text-muted); margin-top: 1px;">
                                            Ex #<c:out value="${sub.exerciseId}" />
                                        </div>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${normalizedStatus == 'REVIEWED'}">
                                                <span class="badge badge-success">Reviewed</span>
                                            </c:when>
                                            <c:when test="${normalizedStatus == 'PENDING'}">
                                                <span class="badge badge-warning">Pending Review</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge"><c:out value="${sub.status != null ? sub.status : 'N/A'}" /></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${sub.score != null}">
                                                <span class="badge ${sub.score >= 70 ? 'badge-success' : (sub.score >= 40 ? 'badge-warning' : 'badge-danger')}" style="font-size: var(--font-size-xs); font-weight: 700;">
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
                                                    <fmt:formatDate value="${sub.submittedAt}" pattern="yyyy-MM-dd HH:mm" />
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
                                                    <fmt:formatDate value="${sub.reviewedAt}" pattern="yyyy-MM-dd HH:mm" />
                                                </span>
                                                <c:if test="${sub.reviewedBy != null}">
                                                    <div style="font-family: var(--font-mono); font-size: 0.6875rem; color: var(--text-muted); margin-top: 2px;">
                                                        By Inst #<c:out value="${sub.reviewedBy}" />
                                                    </div>
                                                </c:if>
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
                                                <div style="max-width: 240px; color: var(--text-secondary); font-size: var(--font-size-xs); line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; text-overflow: ellipsis;" title="<c:out value="${sub.feedback}" />">
                                                    <c:out value="${sub.feedback}" />
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: var(--text-muted); font-size: var(--font-size-xs); font-style: italic;">No feedback</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="text-align: right;">
                                        <button type="button" class="btn btn-secondary btn-sm" id="btn-toggle-${sub.id}" onclick="toggleSubmissionDetails('${sub.id}')" style="padding: 0.25rem 0.5rem; font-size: 0.75rem;">
                                            👁 Code
                                        </button>
                                    </td>
                                </tr>
                                <%-- Expandable Details & Code Audit Row --%>
                                <tr id="details-${sub.id}" class="submission-details-drawer" style="display: none; background-color: #f8fafc;">
                                    <td colspan="9" style="padding: 1.25rem 1.5rem; border-bottom: 2px solid var(--border-subtle);">
                                        <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1rem; flex-wrap: wrap; gap: 0.75rem;">
                                            <div>
                                                <div style="font-weight: 600; font-size: var(--font-size-sm); color: var(--text-primary); display: flex; align-items: center; gap: 0.5rem;">
                                                    <span>Submission #<c:out value="${sub.id}" /> Code Inspection</span>
                                                    <c:choose>
                                                        <c:when test="${normalizedStatus == 'REVIEWED'}">
                                                            <span class="badge badge-success">Reviewed</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge badge-warning">Pending Review</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <div style="font-size: var(--font-size-xs); color: var(--text-muted); margin-top: 0.25rem;">
                                                    Student: <strong><c:out value="${not empty sub.studentName ? sub.studentName : 'User #' + sub.studentId}" /></strong> (User #<c:out value="${sub.studentId}" />) &bull; Exercise: <strong><c:out value="${not empty sub.exerciseTitle ? sub.exerciseTitle : 'Exercise #' + sub.exerciseId}" /></strong> (Ex #<c:out value="${sub.exerciseId}" />)
                                                </div>
                                            </div>
                                            <div style="display: flex; gap: 0.5rem; align-items: center;">
                                                <button type="button" class="btn btn-secondary btn-sm" onclick="toggleSubmissionDetails('${sub.id}')">
                                                    ✕ Close
                                                </button>
                                            </div>
                                        </div>

                                        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 1rem; margin-bottom: 1rem;">
                                            <div style="background: var(--bg-surface); border: 1px solid var(--border-subtle); border-radius: var(--radius-md); padding: 0.875rem 1rem;">
                                                <div style="font-size: var(--font-size-xs); font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em; color: var(--text-muted); margin-bottom: 0.375rem;">
                                                    Evaluation Details
                                                </div>
                                                <div style="font-size: var(--font-size-xs); line-height: 1.6; color: var(--text-secondary);">
                                                    <div><strong>Assigned Score:</strong>
                                                        <c:choose>
                                                            <c:when test="${sub.score != null}">
                                                                <span style="font-weight: 700; color: ${sub.score >= 70 ? 'var(--state-success)' : 'var(--text-primary)'};"><c:out value="${sub.score}" />/100</span>
                                                            </c:when>
                                                            <c:otherwise><span style="font-style: italic; color: var(--text-muted);">Not yet graded</span></c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                    <div><strong>Evaluator:</strong>
                                                        <c:choose>
                                                            <c:when test="${sub.reviewedBy != null}">
                                                                Instructor #<c:out value="${sub.reviewedBy}" />
                                                            </c:when>
                                                            <c:otherwise><span style="font-style: italic; color: var(--text-muted);">Unassigned</span></c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                    <div><strong>Reviewed At:</strong>
                                                        <c:choose>
                                                            <c:when test="${not empty sub.reviewedAt}">
                                                                <fmt:formatDate value="${sub.reviewedAt}" pattern="yyyy-MM-dd HH:mm:ss" />
                                                            </c:when>
                                                            <c:otherwise><span style="font-style: italic; color: var(--text-muted);">Pending</span></c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                            </div>

                                            <div style="background: var(--bg-surface); border: 1px solid var(--border-subtle); border-radius: var(--radius-md); padding: 0.875rem 1rem;">
                                                <div style="font-size: var(--font-size-xs); font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em; color: var(--text-muted); margin-bottom: 0.375rem;">
                                                    Instructor Feedback
                                                </div>
                                                <c:choose>
                                                    <c:when test="${not empty sub.feedback}">
                                                        <div style="font-size: var(--font-size-xs); line-height: 1.5; color: var(--text-secondary); white-space: pre-wrap;">
                                                            <c:out value="${sub.feedback}" />
                                                        </div>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div style="font-size: var(--font-size-xs); color: var(--text-muted); font-style: italic;">
                                                            No written evaluation feedback has been provided for this submission.
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>

                                        <div>
                                            <div style="font-size: var(--font-size-xs); font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em; color: var(--text-muted); margin-bottom: 0.5rem; display: flex; justify-content: space-between; align-items: center;">
                                                <span>Submitted Python Solution</span>
                                                <span style="font-family: var(--font-mono); font-size: 0.6875rem; text-transform: none; color: var(--text-muted);">Python 3</span>
                                            </div>
                                            <c:choose>
                                                <c:when test="${not empty sub.submittedCode}">
                                                    <pre class="code-block" style="margin: 0; min-height: 100px; max-height: 380px; overflow-y: auto;"><code><c:out value="${sub.submittedCode}" /></code></pre>
                                                </c:when>
                                                <c:otherwise>
                                                    <div style="padding: 1.5rem; background: var(--bg-surface); border: 1px dashed var(--border-subtle); border-radius: var(--radius-md); color: var(--text-muted); font-style: italic; text-align: center; font-size: var(--font-size-xs);">
                                                        No source code recorded for this submission entry.
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <%-- Dynamic Zero-Results Message for Client-Side Filtering --%>
                <div id="noFilterResultsMessage" style="display: none; padding: 3rem 1.5rem; text-align: center; color: var(--text-secondary);">
                    <div style="font-size: 2rem; margin-bottom: 0.5rem;">🔍</div>
                    <p style="font-size: var(--font-size-base); font-weight: 600; margin-bottom: 0.25rem;">No Submissions Match Your Filter</p>
                    <p style="font-size: var(--font-size-sm); color: var(--text-muted); margin-bottom: 0.75rem;">Try adjusting your search terms or status criteria.</p>
                    <button type="button" class="btn btn-secondary btn-sm" onclick="resetSubmissionFilters()">Reset Filters</button>
                </div>
            </c:when>
            <c:otherwise>
                <div style="padding: 3.5rem 1.5rem; text-align: center; color: var(--text-secondary);">
                    <div style="font-size: 2.5rem; margin-bottom: 0.75rem;">📝</div>
                    <p style="font-size: var(--font-size-base); font-weight: 600; margin-bottom: 0.25rem;">No Submissions Recorded</p>
                    <p style="font-size: var(--font-size-sm); color: var(--text-muted); margin-bottom: 0;">There are currently no student exercise submissions recorded on the platform.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script>
    var currentStatusFilter = 'ALL';

    function toggleSubmissionDetails(id) {
        var row = document.getElementById('details-' + id);
        var btn = document.getElementById('btn-toggle-' + id);
        if (!row) return;

        if (row.style.display === 'none' || row.style.display === '') {
            row.style.display = 'table-row';
            if (btn) btn.innerHTML = '✕ Close';
        } else {
            row.style.display = 'none';
            if (btn) btn.innerHTML = '👁 Code';
        }
    }

    function setStatusFilter(status, btnElement) {
        currentStatusFilter = status;
        var filterBtns = document.querySelectorAll('.submission-filter-btn');
        filterBtns.forEach(function(b) {
            b.classList.remove('btn-primary');
            b.classList.add('btn-secondary');
        });
        if (btnElement) {
            btnElement.classList.remove('btn-secondary');
            btnElement.classList.add('btn-primary');
        }
        filterSubmissionsTable();
    }

    function filterSubmissionsTable() {
        var searchInput = document.getElementById('submissionSearchInput');
        var query = searchInput ? searchInput.value.toLowerCase().trim() : '';
        var rows = document.querySelectorAll('.submission-data-row');
        var visibleCount = 0;

        rows.forEach(function(row) {
            var subId = row.getAttribute('data-id');
            var rowStatus = row.getAttribute('data-status') || '';
            var rowSearch = (row.getAttribute('data-search') || '').toLowerCase();
            var matchesStatus = (currentStatusFilter === 'ALL') || (rowStatus === currentStatusFilter);
            var matchesQuery = (query === '') || (rowSearch.indexOf(query) !== -1);

            if (matchesStatus && matchesQuery) {
                row.style.display = '';
                visibleCount++;
            } else {
                row.style.display = 'none';
                if (subId) {
                    var detailRow = document.getElementById('details-' + subId);
                    var toggleBtn = document.getElementById('btn-toggle-' + subId);
                    if (detailRow) detailRow.style.display = 'none';
                    if (toggleBtn) toggleBtn.innerHTML = '👁 Code';
                }
            }
        });

        var noResults = document.getElementById('noFilterResultsMessage');
        var table = document.getElementById('submissionsAuditTable');
        if (noResults) {
            if (visibleCount === 0 && rows.length > 0) {
                noResults.style.display = 'block';
                if (table) table.style.display = 'none';
            } else {
                noResults.style.display = 'none';
                if (table) table.style.display = '';
            }
        }
    }

    function resetSubmissionFilters() {
        var searchInput = document.getElementById('submissionSearchInput');
        if (searchInput) searchInput.value = '';
        currentStatusFilter = 'ALL';
        var allBtn = document.querySelector('.submission-filter-btn[data-status="ALL"]');
        var filterBtns = document.querySelectorAll('.submission-filter-btn');
        filterBtns.forEach(function(b) {
            b.classList.remove('btn-primary');
            b.classList.add('btn-secondary');
        });
        if (allBtn) {
            allBtn.classList.remove('btn-secondary');
            allBtn.classList.add('btn-primary');
        }
        filterSubmissionsTable();
    }
</script>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
