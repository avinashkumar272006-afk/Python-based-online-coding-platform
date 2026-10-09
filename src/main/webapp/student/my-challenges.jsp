<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Challenges - Student</title>
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
        .btn-drop { background: #d9534f; }
        .btn-complete { background: #5cb85c; }
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
        <h2>My Challenges</h2>
        <p><i>Note: This feature is for participation tracking and does not execute Python code.</i></p>
        
        <c:if test="${not empty error}">
            <p class="error">${error}</p>
        </c:if>
        <c:if test="${param.success == 'joined'}">
            <p class="success">Successfully joined the challenge!</p>
        </c:if>
        <c:if test="${param.success == 'completed'}">
            <p class="success">Challenge marked as completed!</p>
        </c:if>
        <c:if test="${param.success == 'dropped'}">
            <p class="success">Challenge dropped.</p>
        </c:if>
        
        <c:choose>
            <c:when test="${empty myChallenges}">
                <p>You have not joined any challenges yet.</p>
            </c:when>
            <c:otherwise>
                <c:forEach var="cp" items="${myChallenges}">
                    <div class="challenge-card">
                        <h3><c:out value="${cp.challengeTitle}"/></h3>
                        <p class="challenge-meta">
                            Joined at: <c:out value="${cp.joinedAt}"/> | 
                            Status: <strong><c:out value="${cp.status}"/></strong>
                            <c:if test="${cp.completedAt != null}">
                                | Completed at: <c:out value="${cp.completedAt}"/>
                            </c:if>
                        </p>
                        
                        <c:if test="${cp.status == 'JOINED'}">
                            <form action="${pageContext.request.contextPath}/student/challenges" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="complete">
                                <input type="hidden" name="id" value="${cp.challengeId}">
                                <button type="submit" class="btn btn-complete">Mark as Completed</button>
                            </form>
                            <form action="${pageContext.request.contextPath}/student/challenges" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="drop">
                                <input type="hidden" name="id" value="${cp.challengeId}">
                                <button type="submit" class="btn btn-drop" onclick="return confirm('Drop this challenge?');">Drop Challenge</button>
                            </form>
                        </c:if>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>
