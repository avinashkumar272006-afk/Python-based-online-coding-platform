<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Exercise Details</title>
</head>
<body>
    <h2>${exercise.title}</h2>
    <a href="${pageContext.request.contextPath}/student/exercises">Back to Exercises</a>
    
    <br><br>
    <p><strong>Difficulty:</strong> ${exercise.difficulty}</p>
    <p><strong>Topic:</strong> ${exercise.topic}</p>
    
    <h3>Description:</h3>
    <pre>${exercise.description}</pre>
    
    <h3>Starter Code:</h3>
    <pre>${exercise.starterCode}</pre>
    
    <h3>Expected Output:</h3>
    <pre>${exercise.expectedOutput}</pre>

    <br>
    <a href="${pageContext.request.contextPath}/student/submit?exerciseId=${exercise.id}">
        <button>Submit Solution</button>
    </a>
</body>
</html>
