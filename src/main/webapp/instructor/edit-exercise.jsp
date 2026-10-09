<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>Edit Exercise</title>
</head>
<body>
    <h2>Edit Exercise</h2>
    <a href="${pageContext.request.contextPath}/instructor/exercises">Back to Exercises</a>
    
    <br><br>
    <c:if test="${not empty error}">
        <p style="color:red;">${error}</p>
    </c:if>
    
    <form action="${pageContext.request.contextPath}/instructor/exercises" method="post">
        <input type="hidden" name="action" value="update"/>
        <input type="hidden" name="id" value="${exercise.id}"/>
        
        <label>Title:</label><br>
        <input type="text" name="title" value="${exercise.title}" required/><br><br>
        
        <label>Description:</label><br>
        <textarea name="description" rows="4" cols="50" required>${exercise.description}</textarea><br><br>
        
        <label>Difficulty:</label><br>
        <select name="difficulty" required>
            <option value="EASY" ${exercise.difficulty == 'EASY' ? 'selected' : ''}>EASY</option>
            <option value="MEDIUM" ${exercise.difficulty == 'MEDIUM' ? 'selected' : ''}>MEDIUM</option>
            <option value="HARD" ${exercise.difficulty == 'HARD' ? 'selected' : ''}>HARD</option>
        </select><br><br>
        
        <label>Topic:</label><br>
        <input type="text" name="topic" value="${exercise.topic}"/><br><br>
        
        <label>Starter Code:</label><br>
        <textarea name="starterCode" rows="4" cols="50">${exercise.starterCode}</textarea><br><br>
        
        <label>Expected Output:</label><br>
        <textarea name="expectedOutput" rows="4" cols="50">${exercise.expectedOutput}</textarea><br><br>
        
        <button type="submit">Update</button>
    </form>
</body>
</html>
