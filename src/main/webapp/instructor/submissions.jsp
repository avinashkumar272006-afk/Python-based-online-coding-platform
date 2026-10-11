<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    if (request.getAttribute("submissions") == null) {
        response.sendRedirect(request.getContextPath() + "/instructor/submissions");
        return;
    }
%>
<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="Review Submissions - Python Coding Platform" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activeNav" value="submissions" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageCategory" value="Teaching" />
    <jsp:param name="pageTitle" value="Review Submissions" />
</jsp:include>

<div class="page-header">
    <div>
        <h1 class="page-title">Review Submissions</h1>
        <div class="page-subtitle">Evaluate student code, assign grading scores, and provide feedback on submitted exercises.</div>
    </div>
    <div style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
        <a href="${pageContext.request.contextPath}/instructor/dashboard" class="btn btn-secondary btn-sm">
            &larr; Back to Dashboard
        </a>
        <a href="${pageContext.request.contextPath}/instructor/exercises" class="btn btn-secondary btn-sm">
            Manage Exercises
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<div class="card">
    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
        <h2 class="card-title">Student Submissions</h2>
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
                                <th>Student Name</th>
                                <th>Status</th>
                                <th>Score</th>
                                <th>Submitted At</th>
                                <th style="text-align: right; width: 120px;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="sub" items="${submissions}">
                                <tr>
                                    <td>
                                        <div style="font-weight: 600; color: var(--text-primary);">
                                            <c:out value="${sub.exerciseTitle}" />
                                        </div>
                                    </td>
                                    <td>
                                        <span style="font-weight: 500; color: var(--text-primary);">
                                            <c:out value="${sub.studentName}" />
                                        </span>
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
                                                <span style="color: var(--text-muted); font-size: var(--font-size-xs); font-style: italic;">Not graded</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="white-space: nowrap; font-size: var(--font-size-xs); color: var(--text-secondary);">
                                        <c:out value="${sub.submittedAt}" />
                                    </td>
                                    <td style="text-align: right;">
                                        <a href="${pageContext.request.contextPath}/instructor/submission/review?id=${sub.id}" class="btn btn-primary btn-sm">
                                            Review
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div style="padding: 3.5rem 1.5rem; text-align: center; color: var(--text-secondary);">
                    <div style="font-size: 2.5rem; margin-bottom: 0.75rem;">📥</div>
                    <p style="font-size: var(--font-size-base); font-weight: 600; margin-bottom: 0.25rem;">No Submissions Yet</p>
                    <p style="font-size: var(--font-size-sm); color: var(--text-muted); margin-bottom: 1.25rem;">There are currently no student submissions awaiting review for your exercises.</p>
                    <a href="${pageContext.request.contextPath}/instructor/exercises" class="btn btn-secondary btn-sm">Manage Exercises</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
