<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    if (request.getAttribute("myChallenges") == null) {
        response.sendRedirect(request.getContextPath() + "/student/my-challenges");
        return;
    }
%>
<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="My Challenges - Python Coding Platform" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activeNav" value="my-challenges" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageCategory" value="Challenges" />
    <jsp:param name="pageTitle" value="My Challenges" />
</jsp:include>

<div class="page-header">
    <div>
        <h1 class="page-title">My Challenges</h1>
        <div class="page-subtitle">Track your enrolled coding challenges and participation status.</div>
    </div>
    <div style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
        <a href="${pageContext.request.contextPath}/student/challenges" class="btn btn-secondary btn-sm">
            Browse Available Challenges &rarr;
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<div class="alert alert-info" style="margin-bottom: 1.5rem;">
    <span><strong>Note:</strong> Challenge participation tracking records your enrollment and status. Code evaluation is conducted separately.</span>
</div>

<c:choose>
    <c:when test="${empty myChallenges}">
        <div class="card">
            <div class="card-body" style="padding: 3.5rem 1.5rem; text-align: center; color: var(--text-secondary);">
                <div style="font-size: 2.5rem; margin-bottom: 0.75rem;">🎯</div>
                <p style="font-size: var(--font-size-base); font-weight: 600; margin-bottom: 0.25rem;">No Challenges Enrolled</p>
                <p style="font-size: var(--font-size-sm); color: var(--text-muted); margin-bottom: 1.5rem;">You have not joined any challenges yet. Explore open challenges to participate!</p>
                <a href="${pageContext.request.contextPath}/student/challenges" class="btn btn-primary btn-sm">
                    Explore Challenges &rarr;
                </a>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <c:forEach var="cp" items="${myChallenges}">
            <div class="card" style="margin-bottom: 1.25rem;">
                <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
                    <div style="display: flex; align-items: center; gap: 0.75rem; flex-wrap: wrap;">
                        <h2 class="card-title" style="font-size: var(--font-size-lg);"><c:out value="${cp.challengeTitle}"/></h2>
                        <c:choose>
                            <c:when test="${cp.status == 'COMPLETED'}">
                                <span class="badge badge-success"><c:out value="${cp.status}"/></span>
                            </c:when>
                            <c:when test="${cp.status == 'JOINED'}">
                                <span class="badge badge-warning"><c:out value="${cp.status}"/></span>
                            </c:when>
                            <c:when test="${cp.status == 'DROPPED'}">
                                <span class="badge badge-danger"><c:out value="${cp.status}"/></span>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${not empty cp.status}">
                                    <span class="badge"><c:out value="${cp.status}"/></span>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div style="font-size: var(--font-size-xs); color: var(--text-secondary); display: flex; gap: 1.25rem; align-items: center; flex-wrap: wrap;">
                        <span><strong>Joined at:</strong> <c:out value="${cp.joinedAt}"/></span>
                        <c:if test="${cp.completedAt != null}">
                            <span><strong>Completed at:</strong> <c:out value="${cp.completedAt}"/></span>
                        </c:if>
                    </div>
                </div>
                <div class="card-body" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
                    <div style="font-size: var(--font-size-sm); color: var(--text-secondary);">
                        <c:choose>
                            <c:when test="${cp.status == 'COMPLETED'}">
                                <span style="color: var(--state-success); font-weight: 500;">✓ Challenge completed.</span>
                            </c:when>
                            <c:when test="${cp.status == 'DROPPED'}">
                                <span style="color: var(--text-muted); font-style: italic;">Challenge dropped.</span>
                            </c:when>
                            <c:otherwise>
                                <span>Active enrollment. Mark as completed once you finish, or drop if no longer participating.</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    
                    <c:if test="${cp.status == 'JOINED'}">
                        <div style="display: flex; gap: 0.5rem; align-items: center; flex-wrap: wrap;">
                            <form action="${pageContext.request.contextPath}/student/challenges" method="post" style="display: inline; margin: 0;">
                                <input type="hidden" name="csrfToken" value="${not empty csrfToken ? csrfToken : sessionScope.csrfToken}">
                                <input type="hidden" name="action" value="complete">
                                <input type="hidden" name="id" value="${cp.challengeId}">
                                <button type="submit" class="btn btn-success btn-sm">
                                    Mark as Completed
                                </button>
                            </form>
                            <form action="${pageContext.request.contextPath}/student/challenges" method="post" style="display: inline; margin: 0;">
                                <input type="hidden" name="csrfToken" value="${not empty csrfToken ? csrfToken : sessionScope.csrfToken}">
                                <input type="hidden" name="action" value="drop">
                                <input type="hidden" name="id" value="${cp.challengeId}">
                                <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Drop this challenge?');">
                                    Drop Challenge
                                </button>
                            </form>
                        </div>
                    </c:if>
                </div>
            </div>
        </c:forEach>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
