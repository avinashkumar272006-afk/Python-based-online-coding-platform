<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    if (request.getAttribute("submissions") == null) {
        response.sendRedirect(request.getContextPath() + "/student/submissions");
        return;
    }
%>
<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="My Submissions - Python Coding Platform" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activeNav" value="submissions" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageCategory" value="Learning" />
    <jsp:param name="pageTitle" value="My Submissions" />
</jsp:include>

<div class="page-header">
    <div>
        <h1 class="page-title">My Submissions</h1>
        <div class="page-subtitle">Track the status, instructor feedback, and grading scores of your submitted Python exercises.</div>
    </div>
    <div style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
        <a href="${pageContext.request.contextPath}/student/dashboard" class="btn btn-secondary btn-sm">
            &larr; Back to Dashboard
        </a>
        <a href="${pageContext.request.contextPath}/student/exercises" class="btn btn-primary btn-sm">
            Browse Exercises
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<div class="card">
    <div class="card-header">
        <h2 class="card-title">Submission History</h2>
        <span class="badge badge-info"><c:out value="${submissions != null ? submissions.size() : 0}" /> Submissions</span>
    </div>
    <div class="card-body" style="padding: 0;">
        <c:choose>
            <c:when test="${not empty submissions}">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Exercise Title</th>
                                <th>Status</th>
                                <th>Score</th>
                                <th>Submitted At</th>
                                <th>Reviewed At</th>
                                <th>Feedback</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="sub" items="${submissions}">
                                <tr>
                                    <td>
                                        <c:choose>
                                            <c:when test="${sub.exerciseId > 0}">
                                                <a href="${pageContext.request.contextPath}/student/exercises?action=view&id=${sub.exerciseId}" style="font-weight: 600; color: var(--brand-accent); text-decoration: none;">
                                                    <c:out value="${sub.exerciseTitle}" />
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="font-weight: 600; color: var(--text-primary);"><c:out value="${sub.exerciseTitle}" /></span>
                                            </c:otherwise>
                                        </c:choose>
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
                                                <span style="color: var(--text-muted); font-size: var(--font-size-xs); font-style: italic;">Not graded yet</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="white-space: nowrap; font-size: var(--font-size-xs); color: var(--text-secondary);">
                                        <c:out value="${sub.submittedAt}" />
                                    </td>
                                    <td style="white-space: nowrap; font-size: var(--font-size-xs); color: var(--text-secondary);">
                                        <c:choose>
                                            <c:when test="${sub.reviewedAt != null}">
                                                <c:out value="${sub.reviewedAt}" />
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: var(--text-muted);">&mdash;</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="max-width: 280px;">
                                        <c:choose>
                                            <c:when test="${not empty sub.feedback}">
                                                <div style="font-size: var(--font-size-sm); color: var(--text-primary); line-height: 1.4;">
                                                    <c:out value="${sub.feedback}" />
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: var(--text-muted); font-size: var(--font-size-xs); font-style: italic;">No feedback yet</span>
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
                    <p style="font-size: var(--font-size-base); font-weight: 600; margin-bottom: 0.25rem;">No Submissions Yet</p>
                    <p style="font-size: var(--font-size-sm); color: var(--text-muted); margin-bottom: 1.5rem;">You have not submitted any exercise solutions yet. Explore available exercises and test your coding skills!</p>
                    <a href="${pageContext.request.contextPath}/student/exercises" class="btn btn-primary btn-sm">
                        Browse Exercises &rarr;
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
