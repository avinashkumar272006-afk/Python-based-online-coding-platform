<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="Create Exercise - Python Coding Platform" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activeNav" value="create-exercise" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageCategory" value="Teaching" />
    <jsp:param name="pageTitle" value="Create Exercise" />
</jsp:include>

<div class="page-header">
    <div>
        <h1 class="page-title">Create New Exercise</h1>
        <div class="page-subtitle">Design a new Python coding problem for your students with instructions, starter code, and expected outputs.</div>
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
            <input type="hidden" name="action" value="create"/>

            <div class="form-group">
                <label for="title" class="form-label form-label-required">Exercise Title</label>
                <input type="text" id="title" name="title" class="form-control" value="<c:out value="${param.title}" />" placeholder="e.g., Reverse a String, Fibonacci Sequence" required />
                <div class="form-helper">A concise and descriptive title for the exercise.</div>
            </div>

            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 1.25rem; margin-bottom: 1.25rem;">
                <div class="form-group" style="margin-bottom: 0;">
                    <label for="difficulty" class="form-label form-label-required">Difficulty Level</label>
                    <select id="difficulty" name="difficulty" class="form-select" required>
                        <option value="EASY" ${param.difficulty == 'EASY' ? 'selected' : ''}>EASY</option>
                        <option value="MEDIUM" ${param.difficulty == 'MEDIUM' ? 'selected' : ''}>MEDIUM</option>
                        <option value="HARD" ${param.difficulty == 'HARD' ? 'selected' : ''}>HARD</option>
                    </select>
                    <div class="form-helper">Select the expected challenge difficulty.</div>
                </div>

                <div class="form-group" style="margin-bottom: 0;">
                    <label for="topic" class="form-label">Topic / Category</label>
                    <input type="text" id="topic" name="topic" class="form-control" value="<c:out value="${param.topic}" />" placeholder="e.g., Strings, Loops, Recursion, OOP" />
                    <div class="form-helper">Categorize the exercise to help students browse by subject.</div>
                </div>
            </div>

            <div class="form-group">
                <label for="description" class="form-label form-label-required">Problem Description</label>
                <textarea id="description" name="description" class="form-control" rows="5" placeholder="Provide problem instructions, constraints, input/output specifications, and examples..." required><c:out value="${param.description}" /></textarea>
                <div class="form-helper">Detailed instructions and requirements for students solving this exercise.</div>
            </div>

            <div class="form-group">
                <label for="starterCode" class="form-label">Starter Code (Optional)</label>
                <textarea id="starterCode" name="starterCode" class="form-control code-editor" rows="6" spellcheck="false" placeholder="# def solution():&#10;#     pass"><c:out value="${param.starterCode}" /></textarea>
                <div class="form-helper">Initial boilerplate or function template provided to the student in Python 3.</div>
            </div>

            <div class="form-group">
                <label for="expectedOutput" class="form-label">Expected Output (Optional)</label>
                <textarea id="expectedOutput" name="expectedOutput" class="form-control code-editor" rows="4" spellcheck="false" placeholder="Expected terminal stdout output for grading or verification..."><c:out value="${param.expectedOutput}" /></textarea>
                <div class="form-helper">Expected standard output used to evaluate solutions.</div>
            </div>

            <div class="form-actions" style="display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 1.5rem; padding-top: 1.25rem; border-top: 1px solid var(--border-subtle);">
                <a href="${pageContext.request.contextPath}/instructor/exercises" class="btn btn-secondary">
                    Cancel
                </a>
                <button type="submit" class="btn btn-primary">
                    Create Exercise
                </button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
