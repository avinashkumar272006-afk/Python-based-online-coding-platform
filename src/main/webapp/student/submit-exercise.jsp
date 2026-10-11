<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    if (request.getAttribute("exercise") == null) {
        response.sendRedirect(request.getContextPath() + "/student/exercises");
        return;
    }
%>
<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="Submit Solution - ${exercise.title}" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activeNav" value="exercises" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageCategory" value="Exercises" />
    <jsp:param name="pageTitle" value="Submit Solution" />
</jsp:include>

<div class="page-header">
    <div>
        <div style="display: flex; align-items: center; flex-wrap: wrap; gap: 0.75rem; margin-bottom: 0.5rem;">
            <h1 class="page-title">Submit Code: <c:out value="${exercise.title}" /></h1>
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
                    <c:if test="${not empty exercise.difficulty}">
                        <span class="badge"><c:out value="${exercise.difficulty}" /></span>
                    </c:if>
                </c:otherwise>
            </c:choose>
            <c:if test="${not empty exercise.topic}">
                <span class="badge badge-info"><c:out value="${exercise.topic}" /></span>
            </c:if>
        </div>
        <div class="page-subtitle">Write or paste your Python solution below and submit for instructor grading.</div>
    </div>
    <div style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
        <a href="${pageContext.request.contextPath}/student/exercises?action=view&id=${exercise.id}" class="btn btn-secondary btn-sm">
            &larr; View Details
        </a>
        <a href="${pageContext.request.contextPath}/student/exercises" class="btn btn-secondary btn-sm">
            Browse Exercises
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

<c:if test="${not empty exercise.starterCode}">
    <div class="card">
        <div class="card-header" style="display: flex; justify-content: space-between; align-items: center;">
            <h3 class="card-title">Starter Code Reference</h3>
            <span class="badge" style="font-family: var(--font-mono);">Python 3</span>
        </div>
        <div class="card-body" style="padding: 0;">
            <pre class="code-block" style="margin: 0; border: none; border-radius: 0;"><code><c:out value="${exercise.starterCode}" /></code></pre>
        </div>
    </div>
</c:if>

<div class="card">
    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center;">
        <h2 class="card-title">Your Solution</h2>
        <span class="badge badge-info" style="font-family: var(--font-mono);">Python 3 Code</span>
    </div>
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/student/submit" method="post">
            <input type="hidden" name="csrfToken" value="${not empty csrfToken ? csrfToken : sessionScope.csrfToken}">
            <input type="hidden" name="exerciseId" value="${exercise.id}">

            <div class="form-group" style="margin-bottom: 1.25rem;">
                <label for="submittedCode" class="form-label form-label-required">Solution Code</label>
                <textarea id="submittedCode" name="submittedCode" class="form-control code-editor" rows="16" required spellcheck="false" placeholder="# Type or paste your Python solution here..."><c:out value="${not empty param.submittedCode ? param.submittedCode : exercise.starterCode}" /></textarea>
                <div class="form-helper">Ensure your solution complies with the problem requirements and executes cleanly in Python 3.</div>
            </div>

            <div class="form-actions" style="display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1.5rem;">
                <a href="${pageContext.request.contextPath}/student/exercises?action=view&id=${exercise.id}" class="btn btn-secondary">
                    Cancel
                </a>
                <button type="submit" class="btn btn-primary">
                    Submit Solution
                </button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
