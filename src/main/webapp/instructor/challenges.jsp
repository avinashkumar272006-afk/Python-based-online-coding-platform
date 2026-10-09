<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Challenges - Instructor</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }
        header { background-color: #333; color: #fff; padding: 1em 0; text-align: center; }
        nav { text-align: center; margin: 1em 0; }
        nav a { margin: 0 1em; text-decoration: none; color: #333; font-weight: bold; }
        .container { padding: 2em; max-width: 800px; margin: auto; }
        .challenge-card { background: #fff; padding: 1em; margin-bottom: 1em; border-radius: 5px; box-shadow: 0 0 5px rgba(0,0,0,0.1); }
        .form-group { margin-bottom: 1em; }
        .form-group label { display: block; margin-bottom: 0.5em; }
        .form-group input, .form-group select, .form-group textarea { width: 100%; padding: 0.5em; }
        .error { color: red; }
        .success { color: green; }
        .btn { background: #333; color: #fff; padding: 5px 10px; border: none; cursor: pointer; }
    </style>
</head>
<body>
    <header>
        <h1>Challenge Participation Tracking (Instructor)</h1>
    </header>
    <nav>
        <a href="${pageContext.request.contextPath}/instructor/dashboard">Dashboard</a>
        <a href="${pageContext.request.contextPath}/instructor/exercises">Exercises</a>
        <a href="${pageContext.request.contextPath}/instructor/challenges">Challenges</a>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </nav>
    <div class="container">
        <h2>Create New Challenge</h2>
        <c:if test="${not empty error}">
            <p class="error">${error}</p>
        </c:if>
        <c:if test="${param.success == 'created'}">
            <p class="success">Challenge created successfully! Pending admin approval.</p>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/instructor/challenges" method="post" class="challenge-card">
            <input type="hidden" name="action" value="create">
            <div class="form-group">
                <label>Title</label>
                <input type="text" name="title" required>
            </div>
            <div class="form-group">
                <label>Description</label>
                <textarea name="description" required rows="4"></textarea>
            </div>
            <div class="form-group">
                <label>Difficulty</label>
                <select name="difficulty">
                    <option value="EASY">EASY</option>
                    <option value="MEDIUM">MEDIUM</option>
                    <option value="HARD">HARD</option>
                </select>
            </div>
            <div class="form-group">
                <label>Start Date (Optional)</label>
                <input type="datetime-local" name="startDate">
            </div>
            <div class="form-group">
                <label>End Date (Optional)</label>
                <input type="datetime-local" name="endDate">
            </div>
            <button type="submit" class="btn">Create Challenge</button>
        </form>

        <h2>My Created Challenges</h2>
        <c:choose>
            <c:when test="${empty challenges}">
                <p>You haven't created any challenges yet.</p>
            </c:when>
            <c:otherwise>
                <c:forEach var="challenge" items="${challenges}">
                    <div class="challenge-card">
                        <h3><c:out value="${challenge.title}"/></h3>
                        <p>Status: <strong><c:out value="${challenge.status}"/></strong> | Difficulty: <c:out value="${challenge.difficulty}"/></p>
                        <p><c:out value="${challenge.description}"/></p>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>
