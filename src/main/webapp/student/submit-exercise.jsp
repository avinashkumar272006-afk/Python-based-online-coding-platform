<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>Submit Exercise</title>
</head>
<body>
    <h2>Submit Code for: ${exercise.title}</h2>
    <a href="${pageContext.request.contextPath}/student/exercises">Back to Exercises</a>
    
    <c:if test="${not empty error}">
        <div style="color: red;">${error}</div>
    </c:if>

    <br><br>
    <h3>Description:</h3>
    <pre>${exercise.description}</pre>
    
    <h3>Starter Code:</h3>
    <pre>${exercise.starterCode}</pre>

    <form action="${pageContext.request.contextPath}/student/submit" method="post">
        <input type="hidden" name="exerciseId" value="${exercise.id}">
        
        <div>
            <label for="submittedCode">Your Code:</label><br>
            <textarea id="submittedCode" name="submittedCode" rows="15" cols="80" required>${exercise.starterCode}</textarea>
        </div>
        
        <br>
        <button type="submit">Submit</button>
    </form>
</body>
</html>
