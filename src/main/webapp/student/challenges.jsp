<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Available Challenges - Student</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }
        header { background-color: #333; color: #fff; padding: 1em 0; text-align: center; }
        nav { text-align: center; margin: 1em 0; }
        nav a { margin: 0 1em; text-decoration: none; color: #333; font-weight: bold; }
        .container { padding: 2em; max-width: 800px; margin: auto; }
        .challenge-card { background: #fff; padding: 1em; margin-bottom: 1em; border-radius: 5px; box-shadow: 0 0 5px rgba(0,0,0,0.1); }
        .error { color: red; text-align: center; }
        .success { color: green; text-align: center; }
        .challenge-meta { font-size: 0.9em; color: #666; margin-bottom: 10px; }
        .btn { background: #333; color: #fff; padding: 5px 10px; text-decoration: none; border-radius: 3px; cursor: pointer; border: none; }
    </style>
</head>
<body>
    <header>
        <h1>Challenge Participation Tracking</h1>
    </header>
    <nav>
        <a href="${pageContext.request.contextPath}/student/dashboard">Dashboard</a>
        <a href="${pageContext.request.contextPath}/student/exercises">Exercises</a>
        <a href="${pageContext.request.contextPath}/student/challenges">Available Challenges</a>
        <a href="${pageContext.request.contextPath}/student/my-challenges">My Challenges</a>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </nav>
    <div class="container">
        <h2>Available Challenges</h2>
        <p><i>Note: This feature is for participation tracking and does not execute Python code.</i></p>
        
        <c:if test="${not empty error}">
            <p class="error">${error}</p>
        </c:if>
        
        <c:choose>
            <c:when test="${empty challenges}">
                <p>No available challenges right now.</p>
            </c:when>
            <c:otherwise>
                <c:forEach var="challenge" items="${challenges}">
                    <div class="challenge-card">
                        <h3><c:out value="${challenge.title}"/></h3>
                        <p class="challenge-meta">
                            Difficulty: <c:out value="${challenge.difficulty}"/> | 
                            Start: <c:out value="${challenge.startDate != null ? challenge.startDate : 'N/A'}"/> | 
                            End: <c:out value="${challenge.endDate != null ? challenge.endDate : 'N/A'}"/>
                        </p>
                        <p><c:out value="${challenge.description}"/></p>
                        
                        <form action="${pageContext.request.contextPath}/student/challenges" method="post" style="display:inline;">
                            <input type="hidden" name="action" value="join">
                            <input type="hidden" name="id" value="${challenge.id}">
                            <button type="submit" class="btn">Join Challenge</button>
                        </form>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>
