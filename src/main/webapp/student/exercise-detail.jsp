<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    if (request.getAttribute("exercise") == null) {
        response.sendRedirect(request.getContextPath() + "/student/exercises");
        return;
    }
%>
<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="${exercise.title} - Exercise Details" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activeNav" value="exercises" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageCategory" value="Exercises" />
    <jsp:param name="pageTitle" value="Problem Details" />
</jsp:include>

<div class="page-header">
    <div>
        <div style="display: flex; align-items: center; flex-wrap: wrap; gap: 0.75rem; margin-bottom: 0.5rem;">
            <h1 class="page-title"><c:out value="${exercise.title}" /></h1>
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
            <span class="badge badge-info"><c:out value="${exercise.topic}" /></span>
        </div>
        <div class="page-subtitle">Review problem requirements, analyze sample code, and submit your Python solution.</div>
    </div>
    <div style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
        <a href="${pageContext.request.contextPath}/student/exercises" class="btn btn-secondary btn-sm">
            &larr; Back to Exercises
        </a>
        <a href="${pageContext.request.contextPath}/student/submit?exerciseId=${exercise.id}" class="btn btn-primary btn-sm">
            Submit Solution &rarr;
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<div class="card">
    <div class="card-header">
        <h2 class="card-title">Problem Description</h2>
    </div>
    <div class="card-body">
        <div style="font-size: var(--font-size-base); line-height: 1.7; color: var(--text-primary); white-space: pre-wrap; font-family: inherit;"><c:out value="${exercise.description}" /></div>
    </div>
</div>

<div class="card">
    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center;">
        <h2 class="card-title">Starter Code</h2>
        <span class="badge" style="font-family: var(--font-mono);">Python 3</span>
    </div>
    <div class="card-body" style="padding: 0;">
        <pre class="code-block" style="margin: 0; border: none; border-radius: 0;"><code><c:out value="${exercise.starterCode != null ? exercise.starterCode : '# Write your solution here'}" /></code></pre>
    </div>
</div>

<div class="card">
    <div class="card-header">
        <h2 class="card-title">Expected Output</h2>
    </div>
    <div class="card-body" style="padding: 0;">
        <pre class="code-block" style="margin: 0; border: none; border-radius: 0; background-color: #0b1329;"><code><c:out value="${exercise.expectedOutput != null ? exercise.expectedOutput : 'N/A'}" /></code></pre>
    </div>
</div>

<div class="card" style="background-color: var(--brand-accent-light); border-color: var(--brand-accent-subtle); margin-top: 2rem;">
    <div class="card-body" style="display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 1rem;">
        <div>
            <div style="font-weight: 600; color: var(--brand-primary); font-size: var(--font-size-base);">Ready to submit your code?</div>
            <div style="font-size: var(--font-size-sm); color: var(--text-secondary);">Submit your Python solution to have it evaluated and reviewed by instructors.</div>
        </div>
        <a href="${pageContext.request.contextPath}/student/submit?exerciseId=${exercise.id}" class="btn btn-primary">
            Submit Solution
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
