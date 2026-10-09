<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Challenges - Admin</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }
        header { background-color: #333; color: #fff; padding: 1em 0; text-align: center; }
        nav { text-align: center; margin: 1em 0; }
        nav a { margin: 0 1em; text-decoration: none; color: #333; font-weight: bold; }
        .container { padding: 2em; max-width: 800px; margin: auto; }
        .challenge-card { background: #fff; padding: 1em; margin-bottom: 1em; border-radius: 5px; box-shadow: 0 0 5px rgba(0,0,0,0.1); }
        .success { color: green; }
        .error { color: red; }
        .btn { background: #333; color: #fff; padding: 5px 10px; border: none; cursor: pointer; }
        .btn-approve { background: #5cb85c; }
        .btn-reject { background: #d9534f; }
    </style>
</head>
<body>
    <header>
        <h1>Challenge Participation Tracking (Admin)</h1>
    </header>
    <nav>
        <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
        <a href="${pageContext.request.contextPath}/admin/users">Users</a>
        <a href="${pageContext.request.contextPath}/admin/exercises">Exercises</a>
        <a href="${pageContext.request.contextPath}/admin/challenges">Challenges</a>
        <a href="${pageContext.request.contextPath}/system/settings">Settings</a>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </nav>
    <div class="container">
        <h2>All Challenges</h2>
        <c:if test="${not empty error}">
            <p class="error">${error}</p>
        </c:if>
        <c:if test="${param.success == 'approve'}">
            <p class="success">Challenge approved.</p>
        </c:if>
        <c:if test="${param.success == 'reject'}">
            <p class="success">Challenge rejected.</p>
        </c:if>

        <c:choose>
            <c:when test="${empty challenges}">
                <p>No challenges found.</p>
            </c:when>
            <c:otherwise>
                <c:forEach var="challenge" items="${challenges}">
                    <div class="challenge-card">
                        <h3><c:out value="${challenge.title}"/> (ID: ${challenge.id})</h3>
                        <p>Status: <strong><c:out value="${challenge.status}"/></strong> | Created By ID: ${challenge.createdBy}</p>
                        <p><c:out value="${challenge.description}"/></p>
                        
                        <c:if test="${challenge.status == 'PENDING'}">
                            <form action="${pageContext.request.contextPath}/admin/challenges" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="approve">
                                <input type="hidden" name="id" value="${challenge.id}">
                                <button type="submit" class="btn btn-approve">Approve</button>
                            </form>
                            <form action="${pageContext.request.contextPath}/admin/challenges" method="post" style="display:inline;">
                                <input type="hidden" name="action" value="reject">
                                <input type="hidden" name="id" value="${challenge.id}">
                                <button type="submit" class="btn btn-reject">Reject</button>
                            </form>
                        </c:if>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>
