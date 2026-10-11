<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    if (request.getAttribute("exercises") == null) {
        response.sendRedirect(request.getContextPath() + "/student/exercises");
        return;
    }
%>
<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="Available Exercises - Python Coding Platform" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activeNav" value="exercises" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageCategory" value="Learning" />
    <jsp:param name="pageTitle" value="Exercises" />
</jsp:include>

<div class="page-header">
    <div>
        <h1 class="page-title">Available Exercises</h1>
        <div class="page-subtitle">Practice Python coding problems, sharpen your skills, and submit solutions for review.</div>
    </div>
    <div>
        <a href="${pageContext.request.contextPath}/student/dashboard" class="btn btn-secondary btn-sm">
            &larr; Back to Dashboard
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<div class="card">
    <div class="card-header">
        <h2 class="card-title">Practice Curriculum</h2>
        <span class="badge badge-info"><c:out value="${exercises != null ? exercises.size() : 0}" /> Exercises</span>
    </div>
    <div class="card-body" style="padding: 0;">
        <c:choose>
            <c:when test="${not empty exercises}">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Title</th>
                                <th>Difficulty</th>
                                <th>Topic</th>
                                <th style="text-align: right;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="exercise" items="${exercises}">
                                <tr>
                                    <td>
                                        <div style="font-weight: 600; color: var(--text-primary);"><c:out value="${exercise.title}" /></div>
                                        <c:if test="${not empty exercise.description}">
                                            <div style="font-size: var(--font-size-xs); color: var(--text-muted); margin-top: 0.125rem; max-width: 400px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
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
                                        <span class="badge badge-info" style="font-weight: 500;"><c:out value="${exercise.topic}" /></span>
                                    </td>
                                    <td style="text-align: right;">
                                        <a href="${pageContext.request.contextPath}/student/exercises?action=view&id=${exercise.id}" class="btn btn-primary btn-sm">
                                            View Details
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div style="padding: 3rem 1.5rem; text-align: center; color: var(--text-secondary);">
                    <div style="font-size: 2rem; margin-bottom: 0.75rem;">📚</div>
                    <p style="font-size: var(--font-size-base); font-weight: 600; margin-bottom: 0.25rem;">No Exercises Available</p>
                    <p style="font-size: var(--font-size-sm); color: var(--text-muted); margin-bottom: 1.25rem;">There are currently no approved exercises available in the curriculum. Please check back later!</p>
                    <a href="${pageContext.request.contextPath}/student/dashboard" class="btn btn-secondary btn-sm">Return to Dashboard</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
