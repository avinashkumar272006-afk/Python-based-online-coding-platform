<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>Create Exercise</title>
</head>
<body>
    <h2>Create New Exercise</h2>
    <a href="${pageContext.request.contextPath}/instructor/exercises">Back to Exercises</a>
    
    <br><br>
    <c:if test="${not empty error}">
        <p style="color:red;">${error}</p>
    </c:if>
    
    <form action="${pageContext.request.contextPath}/instructor/exercises" method="post">
        <input type="hidden" name="action" value="create"/>
        
        <label>Title:</label><br>
        <input type="text" name="title" required/><br><br>
        
        <label>Description:</label><br>
        <textarea name="description" rows="4" cols="50" required></textarea><br><br>
        
        <label>Difficulty:</label><br>
        <select name="difficulty" required>
            <option value="EASY">EASY</option>
            <option value="MEDIUM">MEDIUM</option>
            <option value="HARD">HARD</option>
        </select><br><br>
        
        <label>Topic:</label><br>
        <input type="text" name="topic"/><br><br>
        
        <label>Starter Code:</label><br>
        <textarea name="starterCode" rows="4" cols="50"></textarea><br><br>
        
        <label>Expected Output:</label><br>
        <textarea name="expectedOutput" rows="4" cols="50"></textarea><br><br>
        
        <button type="submit">Create</button>
    </form>
</body>
</html>
