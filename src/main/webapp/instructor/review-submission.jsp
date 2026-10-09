<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>Review Submission</title>
</head>
<body>
    <h2>Review Submission for: ${submission.exerciseTitle}</h2>
    <a href="${pageContext.request.contextPath}/instructor/submissions">Back to Submissions</a>
    
    <c:if test="${not empty error}">
        <div style="color: red;">${error}</div>
    </c:if>

    <br><br>
    <p><strong>Student:</strong> ${submission.studentName}</p>
    <p><strong>Submitted At:</strong> ${submission.submittedAt}</p>
    
    <h3>Submitted Code:</h3>
    <pre>${submission.submittedCode}</pre>

    <form action="${pageContext.request.contextPath}/instructor/submission/review" method="post">
        <input type="hidden" name="id" value="${submission.id}">
        
        <div>
            <label for="status">Status:</label>
            <select id="status" name="status">
                <option value="REVIEWED" ${submission.status == 'REVIEWED' ? 'selected' : ''}>REVIEWED</option>
                <option value="PENDING" ${submission.status == 'PENDING' ? 'selected' : ''}>PENDING</option>
            </select>
        </div>
        <br>
        
        <div>
            <label for="score">Score (0-100):</label><br>
            <input type="number" id="score" name="score" min="0" max="100" step="1" value="${submission.score}">
        </div>
        <br>
        
        <div>
            <label for="feedback">Feedback:</label><br>
            <textarea id="feedback" name="feedback" rows="5" cols="80">${submission.feedback}</textarea>
        </div>
        
        <br>
        <button type="submit">Submit Review</button>
    </form>
</body>
</html>
