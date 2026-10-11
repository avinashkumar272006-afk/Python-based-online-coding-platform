<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    if (request.getAttribute("submission") == null) {
        response.sendRedirect(request.getContextPath() + "/instructor/submissions");
        return;
    }
%>
<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="Review Submission - Python Coding Platform" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activeNav" value="submissions" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageCategory" value="Teaching" />
    <jsp:param name="pageTitle" value="Review Submission" />
</jsp:include>

<div class="page-header">
    <div>
        <div style="display: flex; align-items: center; gap: 0.75rem; flex-wrap: wrap; margin-bottom: 0.25rem;">
            <h1 class="page-title">Review Submission #<c:out value="${submission.id}" /></h1>
            <c:choose>
                <c:when test="${submission.status == 'REVIEWED'}">
                    <span class="badge badge-success"><c:out value="${submission.status}" /></span>
                </c:when>
                <c:when test="${submission.status == 'PENDING'}">
                    <span class="badge badge-warning">Pending Review</span>
                </c:when>
                <c:otherwise>
                    <span class="badge"><c:out value="${submission.status}" /></span>
                </c:otherwise>
            </c:choose>
            <c:if test="${submission.score != null}">
                <span class="badge badge-info"><c:out value="${submission.score}" />/100</span>
            </c:if>
        </div>
        <div class="page-subtitle">Evaluate submitted Python code, assign a score, and provide feedback for <c:out value="${submission.studentName}" />.</div>
    </div>
    <div style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
        <a href="${pageContext.request.contextPath}/instructor/submissions" class="btn btn-secondary btn-sm">
            &larr; Back to Submissions
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<div class="card" style="margin-bottom: 1.5rem;">
    <div class="card-header">
        <h2 class="card-title">Submission Details &amp; Code</h2>
        <span class="badge badge-info" style="font-family: var(--font-mono);">Python 3</span>
    </div>
    <div class="card-body">
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 1rem; padding-bottom: 1.25rem; border-bottom: 1px solid var(--border-subtle); margin-bottom: 1.25rem;">
            <div>
                <div style="font-size: var(--font-size-xs); font-weight: 600; text-transform: uppercase; color: var(--text-muted); margin-bottom: 0.25rem;">Student Name</div>
                <div style="font-size: var(--font-size-sm); font-weight: 600; color: var(--text-primary);"><c:out value="${submission.studentName}" /></div>
            </div>
            <div>
                <div style="font-size: var(--font-size-xs); font-weight: 600; text-transform: uppercase; color: var(--text-muted); margin-bottom: 0.25rem;">Exercise Title</div>
                <div style="font-size: var(--font-size-sm); font-weight: 600; color: var(--brand-accent);"><c:out value="${submission.exerciseTitle}" /></div>
            </div>
            <div>
                <div style="font-size: var(--font-size-xs); font-weight: 600; text-transform: uppercase; color: var(--text-muted); margin-bottom: 0.25rem;">Submitted At</div>
                <div style="font-size: var(--font-size-sm); color: var(--text-secondary);"><c:out value="${submission.submittedAt}" /></div>
            </div>
            <div>
                <div style="font-size: var(--font-size-xs); font-weight: 600; text-transform: uppercase; color: var(--text-muted); margin-bottom: 0.25rem;">Current Status</div>
                <div>
                    <c:choose>
                        <c:when test="${submission.status == 'REVIEWED'}">
                            <span class="badge badge-success"><c:out value="${submission.status}" /></span>
                        </c:when>
                        <c:when test="${submission.status == 'PENDING'}">
                            <span class="badge badge-warning">Pending Review</span>
                        </c:when>
                        <c:otherwise>
                            <span class="badge"><c:out value="${submission.status}" /></span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <c:if test="${submission.reviewedAt != null}">
                <div>
                    <div style="font-size: var(--font-size-xs); font-weight: 600; text-transform: uppercase; color: var(--text-muted); margin-bottom: 0.25rem;">Last Reviewed</div>
                    <div style="font-size: var(--font-size-sm); color: var(--text-secondary);"><c:out value="${submission.reviewedAt}" /></div>
                </div>
            </c:if>
        </div>

        <div>
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
                <label class="form-label" style="font-weight: 600; margin-bottom: 0;">Submitted Python Code</label>
            </div>
            <c:choose>
                <c:when test="${not empty submission.submittedCode}">
                    <pre class="code-block" style="margin: 0; min-height: 120px; max-height: 480px; overflow-y: auto;"><code><c:out value="${submission.submittedCode}" /></code></pre>
                </c:when>
                <c:otherwise>
                    <div style="padding: 1.5rem; background: var(--bg-surface-alt, #0f172a); border: 1px dashed var(--border-subtle); border-radius: var(--radius-md); color: var(--text-muted); font-style: italic;">
                        No code was submitted for this exercise.
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<div class="card">
    <div class="card-header">
        <h2 class="card-title">Review &amp; Evaluation</h2>
    </div>
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/instructor/submission/review" method="post">
            <input type="hidden" name="csrfToken" value="${not empty csrfToken ? csrfToken : sessionScope.csrfToken}">
            <input type="hidden" name="id" value="${submission.id}">

            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 1.25rem; margin-bottom: 1.25rem;">
                <div class="form-group" style="margin-bottom: 0;">
                    <label for="status" class="form-label form-label-required">Review Status</label>
                    <select id="status" name="status" class="form-select">
                        <option value="REVIEWED" ${submission.status == 'REVIEWED' ? 'selected' : ''}>REVIEWED</option>
                        <option value="PENDING" ${submission.status == 'PENDING' ? 'selected' : ''}>PENDING</option>
                    </select>
                    <div class="form-helper">Set to REVIEWED when evaluation is complete.</div>
                </div>

                <div class="form-group" style="margin-bottom: 0;">
                    <label for="score" class="form-label">Score (0 &ndash; 100)</label>
                    <input type="number" id="score" name="score" class="form-control" min="0" max="100" step="1" value="<c:out value="${submission.score}" />" placeholder="e.g., 85" />
                    <div class="form-helper">Enter a score between 0 and 100 (optional).</div>
                </div>
            </div>

            <div class="form-group">
                <label for="feedback" class="form-label">Instructor Feedback</label>
                <textarea id="feedback" name="feedback" class="form-control" rows="6" placeholder="Provide constructive feedback, code suggestions, or grading explanation..."><c:out value="${submission.feedback}" /></textarea>
                <div class="form-helper">Feedback provided to the student regarding code quality, efficiency, and correctness.</div>
            </div>

            <div class="form-actions" style="display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1.5rem; padding-top: 1.25rem; border-top: 1px solid var(--border-subtle);">
                <a href="${pageContext.request.contextPath}/instructor/submissions" class="btn btn-secondary">
                    Cancel
                </a>
                <button type="submit" class="btn btn-primary">
                    Submit Review
                </button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
