<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    if (request.getAttribute("challenges") == null) {
        response.sendRedirect(request.getContextPath() + "/instructor/challenges");
        return;
    }
%>
<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="Manage Challenges - Python Coding Platform" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activeNav" value="challenges" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageCategory" value="Teaching" />
    <jsp:param name="pageTitle" value="Manage Challenges" />
</jsp:include>

<div class="page-header">
    <div>
        <h1 class="page-title">Manage Challenges</h1>
        <div class="page-subtitle">Create new programming challenges and track approval status.</div>
    </div>
    <div style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
        <a href="${pageContext.request.contextPath}/instructor/dashboard" class="btn btn-secondary btn-sm">
            &larr; Back to Dashboard
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<div class="alert alert-info" style="margin-bottom: 1.5rem;">
    <span><strong>Note:</strong> Newly created challenges are submitted with <code>PENDING</code> status and require administrator approval before becoming visible to students.</span>
</div>

<div class="card" style="margin-bottom: 2rem;">
    <div class="card-header">
        <h2 class="card-title">Create New Challenge</h2>
    </div>
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/instructor/challenges" method="post">
            <input type="hidden" name="csrfToken" value="${not empty csrfToken ? csrfToken : sessionScope.csrfToken}">
            <input type="hidden" name="action" value="create">

            <div class="form-group">
                <label for="title" class="form-label form-label-required">Challenge Title</label>
                <input type="text" id="title" name="title" class="form-control" required placeholder="e.g., Python Data Structures Sprint" />
                <div class="form-helper">Enter a descriptive title for this challenge.</div>
            </div>

            <div class="form-group">
                <label for="description" class="form-label form-label-required">Description &amp; Objectives</label>
                <textarea id="description" name="description" class="form-control" rows="4" required placeholder="Provide challenge overview, problem statement, rules, and learning objectives..."></textarea>
                <div class="form-helper">Instructions and guidelines for participating students.</div>
            </div>

            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 1.25rem; margin-bottom: 1.25rem;">
                <div class="form-group" style="margin-bottom: 0;">
                    <label for="difficulty" class="form-label form-label-required">Difficulty</label>
                    <select id="difficulty" name="difficulty" class="form-select">
                        <option value="EASY">EASY</option>
                        <option value="MEDIUM" selected>MEDIUM</option>
                        <option value="HARD">HARD</option>
                    </select>
                </div>

                <div class="form-group" style="margin-bottom: 0;">
                    <label for="startDate" class="form-label">Start Date (Optional)</label>
                    <input type="datetime-local" id="startDate" name="startDate" class="form-control" />
                    <div class="form-helper">Format: YYYY-MM-DDTHH:MM (e.g., 2026-10-15T09:00)</div>
                </div>

                <div class="form-group" style="margin-bottom: 0;">
                    <label for="endDate" class="form-label">End Date (Optional)</label>
                    <input type="datetime-local" id="endDate" name="endDate" class="form-control" />
                    <div class="form-helper">Format: YYYY-MM-DDTHH:MM (e.g., 2026-10-31T23:59)</div>
                </div>
            </div>

            <div class="form-actions" style="display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1.5rem; padding-top: 1.25rem; border-top: 1px solid var(--border-subtle);">
                <button type="reset" class="btn btn-secondary">
                    Clear Form
                </button>
                <button type="submit" class="btn btn-primary">
                    Create Challenge
                </button>
            </div>
        </form>
    </div>
</div>

<div style="margin-bottom: 1rem; display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 0.5rem;">
    <h2 style="font-size: var(--font-size-xl); font-weight: 700; color: var(--text-primary); margin: 0;">My Created Challenges</h2>
    <span class="badge badge-info"><c:out value="${challenges != null ? challenges.size() : 0}"/> Challenges</span>
</div>

<c:choose>
    <c:when test="${empty challenges}">
        <div class="card">
            <div class="card-body" style="padding: 3.5rem 1.5rem; text-align: center; color: var(--text-secondary);">
                <div style="font-size: 2.5rem; margin-bottom: 0.75rem;">🏆</div>
                <p style="font-size: var(--font-size-base); font-weight: 600; margin-bottom: 0.25rem;">No Challenges Created Yet</p>
                <p style="font-size: var(--font-size-sm); color: var(--text-muted); margin-bottom: 0;">Use the form above to create your first coding challenge. Once created, it will be submitted for admin approval.</p>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <c:forEach var="challenge" items="${challenges}">
            <div class="card" style="margin-bottom: 1.25rem;">
                <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
                    <div style="display: flex; align-items: center; gap: 0.75rem; flex-wrap: wrap;">
                        <h3 class="card-title" style="font-size: var(--font-size-lg); margin: 0;"><c:out value="${challenge.title}"/></h3>
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
                                    <span class="badge"><c:out value="${challenge.status}"/></span>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${challenge.difficulty == 'EASY' || challenge.difficulty == 'Easy'}">
                                <span class="badge badge-success"><c:out value="${challenge.difficulty}"/></span>
                            </c:when>
                            <c:when test="${challenge.difficulty == 'MEDIUM' || challenge.difficulty == 'Medium'}">
                                <span class="badge badge-warning"><c:out value="${challenge.difficulty}"/></span>
                            </c:when>
                            <c:when test="${challenge.difficulty == 'HARD' || challenge.difficulty == 'Hard'}">
                                <span class="badge badge-danger"><c:out value="${challenge.difficulty}"/></span>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${not empty challenge.difficulty}">
                                    <span class="badge"><c:out value="${challenge.difficulty}"/></span>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div style="font-size: var(--font-size-xs); color: var(--text-secondary); display: flex; gap: 1rem; align-items: center; flex-wrap: wrap;">
                        <span><strong>Start:</strong> <c:out value="${challenge.startDate != null ? challenge.startDate : 'Immediate'}"/></span>
                        <span><strong>End:</strong> <c:out value="${challenge.endDate != null ? challenge.endDate : 'No deadline'}"/></span>
                        <c:if test="${challenge.createdAt != null}">
                            <span><strong>Created:</strong> <c:out value="${challenge.createdAt}"/></span>
                        </c:if>
                    </div>
                </div>
                <div class="card-body">
                    <p style="color: var(--text-primary); margin: 0; line-height: 1.6; white-space: pre-wrap;"><c:out value="${challenge.description}"/></p>
                </div>
            </div>
        </c:forEach>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
