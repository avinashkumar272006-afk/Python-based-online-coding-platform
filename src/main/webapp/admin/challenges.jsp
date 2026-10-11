<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%
    if (request.getAttribute("challenges") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/challenges");
        return;
    }
%>
<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="Challenge Management - Python Coding Platform" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activeNav" value="challenges" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageCategory" value="Administration" />
    <jsp:param name="pageTitle" value="Manage Challenges" />
</jsp:include>

<%-- Metric derivation from existing model objects --%>
<c:set var="totalCount" value="${challenges != null ? challenges.size() : 0}" />
<c:set var="pendingCount" value="0" />
<c:set var="approvedCount" value="0" />
<c:set var="rejectedCount" value="0" />
<c:set var="easyCount" value="0" />
<c:set var="mediumCount" value="0" />
<c:set var="hardCount" value="0" />

<c:if test="${not empty challenges}">
    <c:forEach var="ch" items="${challenges}">
        <c:choose>
            <c:when test="${ch.status == 'PENDING'}">
                <c:set var="pendingCount" value="${pendingCount + 1}" />
            </c:when>
            <c:when test="${ch.status == 'APPROVED'}">
                <c:set var="approvedCount" value="${approvedCount + 1}" />
            </c:when>
            <c:when test="${ch.status == 'REJECTED'}">
                <c:set var="rejectedCount" value="${rejectedCount + 1}" />
            </c:when>
        </c:choose>
        <c:choose>
            <c:when test="${ch.difficulty == 'EASY' || ch.difficulty == 'Easy'}">
                <c:set var="easyCount" value="${easyCount + 1}" />
            </c:when>
            <c:when test="${ch.difficulty == 'MEDIUM' || ch.difficulty == 'Medium'}">
                <c:set var="mediumCount" value="${mediumCount + 1}" />
            </c:when>
            <c:when test="${ch.difficulty == 'HARD' || ch.difficulty == 'Hard'}">
                <c:set var="hardCount" value="${hardCount + 1}" />
            </c:when>
        </c:choose>
    </c:forEach>
</c:if>

<div class="page-header">
    <div>
        <h1 class="page-title">Challenge Management</h1>
        <div class="page-subtitle">Review instructor-submitted challenges, approve or reject curriculum contests, and monitor platform competition schedules.</div>
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
        <div class="metric-label">Total Challenges</div>
        <div class="metric-value"><c:out value="${totalCount}" /></div>
        <div class="metric-sub">Platform curriculum contests</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Pending Approval</div>
        <div class="metric-value" style="color: var(--state-warning);"><c:out value="${pendingCount}" /></div>
        <div class="metric-sub">Awaiting moderation review</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Approved</div>
        <div class="metric-value" style="color: var(--state-success);"><c:out value="${approvedCount}" /></div>
        <div class="metric-sub">Active student competitions</div>
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

<%-- Challenge Management Table --%>
<div class="card">
    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
        <div>
            <h2 class="card-title">All Challenges</h2>
            <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">
                Audit platform contests, track scheduling windows, and manage administrative approvals.
            </div>
        </div>
        <div style="display: flex; gap: 0.5rem; align-items: center; flex-wrap: wrap;">
            <c:if test="${pendingCount > 0}">
                <span class="badge badge-warning"><c:out value="${pendingCount}" /> Action Required</span>
            </c:if>
            <span class="badge badge-info"><c:out value="${totalCount}" /> Total</span>
        </div>
    </div>
    <div class="card-body" style="padding: 0;">
        <c:choose>
            <c:when test="${not empty challenges}">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th style="width: 80px;">ID</th>
                                <th>Title &amp; Description</th>
                                <th style="width: 110px;">Difficulty</th>
                                <th style="width: 140px;">Status</th>
                                <th style="min-width: 170px;">Schedule</th>
                                <th style="min-width: 140px;">Created By</th>
                                <th style="min-width: 180px; text-align: right;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="challenge" items="${challenges}">
                                <tr>
                                    <td style="color: var(--text-muted); font-size: var(--font-size-xs); font-family: var(--font-mono);">
                                        #<c:out value="${challenge.id}" />
                                    </td>
                                    <td>
                                        <div style="font-weight: 600; color: var(--text-primary);">
                                            <c:out value="${challenge.title}" />
                                        </div>
                                        <c:if test="${not empty challenge.description}">
                                            <div style="max-width: 320px; color: var(--text-secondary); font-size: var(--font-size-xs); line-height: 1.4; margin-top: 0.25rem; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; text-overflow: ellipsis;" title="<c:out value="${challenge.description}" />">
                                                <c:out value="${challenge.description}" />
                                            </div>
                                        </c:if>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${challenge.difficulty == 'EASY' || challenge.difficulty == 'Easy'}">
                                                <span class="badge badge-success"><c:out value="${challenge.difficulty}" /></span>
                                            </c:when>
                                            <c:when test="${challenge.difficulty == 'MEDIUM' || challenge.difficulty == 'Medium'}">
                                                <span class="badge badge-warning"><c:out value="${challenge.difficulty}" /></span>
                                            </c:when>
                                            <c:when test="${challenge.difficulty == 'HARD' || challenge.difficulty == 'Hard'}">
                                                <span class="badge badge-danger"><c:out value="${challenge.difficulty}" /></span>
                                            </c:when>
                                            <c:otherwise>
                                                <c:if test="${not empty challenge.difficulty}">
                                                    <span class="badge"><c:out value="${challenge.difficulty}" /></span>
                                                </c:if>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${challenge.status == 'APPROVED'}">
                                                <span class="badge badge-success">Approved</span>
                                            </c:when>
                                            <c:when test="${challenge.status == 'PENDING'}">
                                                <span class="badge badge-warning">Pending Approval</span>
                                            </c:when>
                                            <c:when test="${challenge.status == 'REJECTED'}">
                                                <span class="badge badge-danger">Rejected</span>
                                            </c:when>
                                            <c:otherwise>
                                                <c:if test="${not empty challenge.status}">
                                                    <span class="badge"><c:out value="${challenge.status}" /></span>
                                                </c:if>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div style="font-size: var(--font-size-xs); line-height: 1.4;">
                                            <div style="white-space: nowrap;">
                                                <span style="color: var(--text-muted); font-weight: 500;">Start:</span>
                                                <c:choose>
                                                    <c:when test="${not empty challenge.startDate}">
                                                        <span style="color: var(--text-primary);"><fmt:formatDate value="${challenge.startDate}" pattern="yyyy-MM-dd HH:mm" /></span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span style="color: var(--text-muted); font-style: italic;">Immediate</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div style="white-space: nowrap; margin-top: 2px;">
                                                <span style="color: var(--text-muted); font-weight: 500;">End:</span>
                                                <c:choose>
                                                    <c:when test="${not empty challenge.endDate}">
                                                        <span style="color: var(--text-primary);"><fmt:formatDate value="${challenge.endDate}" pattern="yyyy-MM-dd HH:mm" /></span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span style="color: var(--text-muted); font-style: italic;">No deadline</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <div style="font-size: var(--font-size-xs); line-height: 1.4;">
                                            <div style="font-family: var(--font-mono); color: var(--text-secondary); font-weight: 500;">
                                                User #<c:out value="${challenge.createdBy}" />
                                            </div>
                                            <c:if test="${not empty challenge.createdAt}">
                                                <div style="color: var(--text-muted); white-space: nowrap; margin-top: 2px;">
                                                    <fmt:formatDate value="${challenge.createdAt}" pattern="yyyy-MM-dd HH:mm" />
                                                </div>
                                            </c:if>
                                        </div>
                                    </td>
                                    <td style="text-align: right;">
                                        <c:choose>
                                            <c:when test="${challenge.status == 'PENDING'}">
                                                <div style="display: inline-flex; gap: 0.375rem; align-items: center; justify-content: flex-end;">
                                                    <form action="${pageContext.request.contextPath}/admin/challenges" method="post" style="display: inline; margin: 0;">
                                                        <input type="hidden" name="csrfToken" value="${not empty csrfToken ? csrfToken : sessionScope.csrfToken}">
                                                        <input type="hidden" name="action" value="approve">
                                                        <input type="hidden" name="id" value="${challenge.id}">
                                                        <button type="submit" class="btn btn-success btn-sm">
                                                            Approve
                                                        </button>
                                                    </form>
                                                    <form action="${pageContext.request.contextPath}/admin/challenges" method="post" style="display: inline; margin: 0;">
                                                        <input type="hidden" name="csrfToken" value="${not empty csrfToken ? csrfToken : sessionScope.csrfToken}">
                                                        <input type="hidden" name="action" value="reject">
                                                        <input type="hidden" name="id" value="${challenge.id}">
                                                        <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to reject this challenge?');">
                                                            Reject
                                                        </button>
                                                    </form>
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="font-size: var(--font-size-xs); color: var(--text-muted); font-style: italic;">
                                                    Completed
                                                </span>
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
                    <div style="font-size: 2.5rem; margin-bottom: 0.75rem;">🏆</div>
                    <p style="font-size: var(--font-size-base); font-weight: 600; margin-bottom: 0.25rem;">No Challenges Found</p>
                    <p style="font-size: var(--font-size-sm); color: var(--text-muted); margin-bottom: 0;">There are currently no coding challenges registered in the system.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
