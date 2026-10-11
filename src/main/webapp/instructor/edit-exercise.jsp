<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    if (request.getAttribute("exercise") == null) {
        response.sendRedirect(request.getContextPath() + "/instructor/exercises");
        return;
    }
%>
<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="Edit Exercise - Python Coding Platform" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activeNav" value="exercises" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageCategory" value="Teaching" />
    <jsp:param name="pageTitle" value="Edit Exercise" />
</jsp:include>

<div class="page-header">
    <div>
        <div style="display: flex; align-items: center; gap: 0.75rem; flex-wrap: wrap; margin-bottom: 0.25rem;">
            <h1 class="page-title">Edit Exercise #<c:out value="${exercise.id}" /></h1>
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
                    <c:if test="${not empty exercise.status}">
                        <span class="badge"><c:out value="${exercise.status}" /></span>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="page-subtitle">Update problem details, difficulty, starter code, and test outputs for this exercise.</div>
    </div>
    <div style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
        <a href="${pageContext.request.contextPath}/instructor/exercises" class="btn btn-secondary btn-sm">
            &larr; Back to Exercises
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<div class="card">
    <div class="card-header">
        <h2 class="card-title">Exercise Specification</h2>
    </div>
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/instructor/exercises" method="post">
            <input type="hidden" name="csrfToken" value="${not empty csrfToken ? csrfToken : sessionScope.csrfToken}">
            <input type="hidden" name="action" value="update"/>
            <input type="hidden" name="id" value="${exercise.id}"/>

            <div class="form-group">
                <label for="title" class="form-label form-label-required">Exercise Title</label>
                <input type="text" id="title" name="title" class="form-control" value="<c:out value="${exercise.title}" />" placeholder="e.g., Reverse a String, Fibonacci Sequence" required />
                <div class="form-helper">A concise and descriptive title for the exercise.</div>
            </div>

            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 1.25rem; margin-bottom: 1.25rem;">
                <div class="form-group" style="margin-bottom: 0;">
                    <label for="difficulty" class="form-label form-label-required">Difficulty Level</label>
                    <select id="difficulty" name="difficulty" class="form-select" required>
                        <option value="EASY" ${exercise.difficulty == 'EASY' ? 'selected' : ''}>EASY</option>
                        <option value="MEDIUM" ${exercise.difficulty == 'MEDIUM' ? 'selected' : ''}>MEDIUM</option>
                        <option value="HARD" ${exercise.difficulty == 'HARD' ? 'selected' : ''}>HARD</option>
                    </select>
                    <div class="form-helper">Select the expected challenge difficulty.</div>
                </div>

                <div class="form-group" style="margin-bottom: 0;">
                    <label for="topic" class="form-label">Topic / Category</label>
                    <input type="text" id="topic" name="topic" class="form-control" value="<c:out value="${exercise.topic}" />" placeholder="e.g., Strings, Loops, Recursion, OOP" />
                    <div class="form-helper">Categorize the exercise to help students browse by subject.</div>
                </div>
            </div>

            <div class="form-group">
                <label for="description" class="form-label form-label-required">Problem Description</label>
                <textarea id="description" name="description" class="form-control" rows="5" placeholder="Provide problem instructions, constraints, input/output specifications, and examples..." required><c:out value="${exercise.description}" /></textarea>
                <div class="form-helper">Detailed instructions and requirements for students solving this exercise.</div>
            </div>

            <div class="form-group">
                <label for="starterCode" class="form-label">Starter Code (Optional)</label>
                <textarea id="starterCode" name="starterCode" class="form-control code-editor" rows="6" spellcheck="false" placeholder="# def solution():&#10;#     pass"><c:out value="${exercise.starterCode}" /></textarea>
                <div class="form-helper">Initial boilerplate or function template provided to the student in Python 3.</div>
            </div>

            <div class="form-group">
                <label for="expectedOutput" class="form-label">Expected Output (Optional)</label>
                <textarea id="expectedOutput" name="expectedOutput" class="form-control code-editor" rows="4" spellcheck="false" placeholder="Expected terminal stdout output for grading or verification..."><c:out value="${exercise.expectedOutput}" /></textarea>
                <div class="form-helper">Expected standard output used to evaluate solutions.</div>
            </div>

            <div class="form-actions" style="display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1.5rem; padding-top: 1.25rem; border-top: 1px solid var(--border-subtle);">
                <a href="${pageContext.request.contextPath}/instructor/exercises" class="btn btn-secondary">
                    Cancel
                </a>
                <button type="submit" class="btn btn-primary">
                    Save Changes
                </button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
