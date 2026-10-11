<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    if (request.getAttribute("challenges") == null) {
        response.sendRedirect(request.getContextPath() + "/student/challenges");
        return;
    }
%>
<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="Available Challenges - Python Coding Platform" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activeNav" value="challenges" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageCategory" value="Challenges" />
    <jsp:param name="pageTitle" value="Available Challenges" />
</jsp:include>

<div class="page-header">
    <div>
        <h1 class="page-title">Available Challenges</h1>
        <div class="page-subtitle">Participate in coding challenges and track your competitive learning journey.</div>
    </div>
    <div style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
        <a href="${pageContext.request.contextPath}/student/my-challenges" class="btn btn-secondary btn-sm">
            My Enrolled Challenges &rarr;
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<div class="alert alert-info" style="margin-bottom: 1.5rem;">
    <span><strong>Note:</strong> Challenge participation tracking records your enrollment and completion. Code evaluation is conducted separately.</span>
</div>

<c:choose>
    <c:when test="${empty challenges}">
        <div class="card">
            <div class="card-body" style="padding: 3.5rem 1.5rem; text-align: center; color: var(--text-secondary);">
                <div style="font-size: 2.5rem; margin-bottom: 0.75rem;">🏆</div>
                <p style="font-size: var(--font-size-base); font-weight: 600; margin-bottom: 0.25rem;">No Challenges Available</p>
                <p style="font-size: var(--font-size-sm); color: var(--text-muted); margin-bottom: 1.5rem;">There are currently no open challenges to join. Please check back later or review your active challenges!</p>
                <a href="${pageContext.request.contextPath}/student/my-challenges" class="btn btn-secondary btn-sm">
                    View My Challenges
                </a>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <c:forEach var="challenge" items="${challenges}">
            <div class="card" style="margin-bottom: 1.25rem;">
                <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
                    <div style="display: flex; align-items: center; gap: 0.75rem; flex-wrap: wrap;">
                        <h2 class="card-title" style="font-size: var(--font-size-lg);"><c:out value="${challenge.title}"/></h2>
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
                        <span><strong>Start:</strong> <c:out value="${challenge.startDate != null ? challenge.startDate : 'N/A'}"/></span>
                        <span><strong>End:</strong> <c:out value="${challenge.endDate != null ? challenge.endDate : 'N/A'}"/></span>
                    </div>
                </div>
                <div class="card-body">
                    <p style="color: var(--text-primary); margin: 0 0 1.25rem 0; line-height: 1.6; white-space: pre-wrap;"><c:out value="${challenge.description}"/></p>
                    
                    <form action="${pageContext.request.contextPath}/student/challenges" method="post" style="display: inline;">
                        <input type="hidden" name="csrfToken" value="${not empty csrfToken ? csrfToken : sessionScope.csrfToken}">
                        <input type="hidden" name="action" value="join">
                        <input type="hidden" name="id" value="${challenge.id}">
                        <button type="submit" class="btn btn-primary btn-sm">
                            Join Challenge &rarr;
                        </button>
                    </form>
                </div>
            </div>
        </c:forEach>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
