<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    if (request.getAttribute("exercises") == null) {
        response.sendRedirect(request.getContextPath() + "/instructor/exercises");
        return;
    }
%>
<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="Manage Exercises - Python Coding Platform" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activeNav" value="exercises" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageCategory" value="Teaching" />
    <jsp:param name="pageTitle" value="Manage Exercises" />
</jsp:include>

<div class="page-header">
    <div>
        <h1 class="page-title">Manage Exercises</h1>
        <div class="page-subtitle">Create, update, and manage your curriculum coding exercises and test cases.</div>
    </div>
    <div style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
        <a href="${pageContext.request.contextPath}/instructor/dashboard" class="btn btn-secondary btn-sm">
            &larr; Back to Dashboard
        </a>
        <a href="${pageContext.request.contextPath}/instructor/exercises?action=new" class="btn btn-primary btn-sm">
            + Create New Exercise
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<div class="card">
    <div class="card-header">
        <h2 class="card-title">Exercise Library</h2>
        <span class="badge badge-info"><c:out value="${exercises != null ? exercises.size() : 0}" /> Exercises</span>
    </div>
    <div class="card-body" style="padding: 0;">
        <c:choose>
            <c:when test="${not empty exercises}">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th style="width: 70px;">ID</th>
                                <th>Title</th>
                                <th>Difficulty</th>
                                <th>Topic</th>
                                <th>Status</th>
                                <th style="text-align: right; width: 170px;">Actions</th>
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
                                        <c:if test="${not empty exercise.description}">
                                            <div style="font-size: var(--font-size-xs); color: var(--text-muted); margin-top: 0.125rem; max-width: 420px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                                                <c:out value="${exercise.description}" />
                                            </div>
                                        </c:if>
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
                                        <div style="display: inline-flex; gap: 0.5rem; align-items: center; justify-content: flex-end;">
                                            <a href="${pageContext.request.contextPath}/instructor/exercises?action=edit&id=${exercise.id}" class="btn btn-secondary btn-sm">
                                                Edit
                                            </a>
                                            <form action="${pageContext.request.contextPath}/instructor/exercises" method="post" style="display: inline; margin: 0;">
                                                <input type="hidden" name="csrfToken" value="${not empty csrfToken ? csrfToken : sessionScope.csrfToken}">
                                                <input type="hidden" name="action" value="delete"/>
                                                <input type="hidden" name="id" value="${exercise.id}"/>
                                                <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to delete this exercise?');">
                                                    Delete
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
                <div style="padding: 3.5rem 1.5rem; text-align: center; color: var(--text-secondary);">
                    <div style="font-size: 2.5rem; margin-bottom: 0.75rem;">📝</div>
                    <p style="font-size: var(--font-size-base); font-weight: 600; margin-bottom: 0.25rem;">No Exercises Created Yet</p>
                    <p style="font-size: var(--font-size-sm); color: var(--text-muted); margin-bottom: 1.25rem;">You haven't created any Python exercises yet. Get started by creating your first coding problem!</p>
                    <a href="${pageContext.request.contextPath}/instructor/exercises?action=new" class="btn btn-primary btn-sm">
                        + Create Your First Exercise
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
