<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    if (request.getAttribute("stats") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard - Python Coding Platform</title>
    <style>
        .dashboard-container { display: flex; flex-wrap: wrap; gap: 20px; }
        .dashboard-card { border: 1px solid #ccc; padding: 15px; min-width: 250px; border-radius: 5px; }
        table { border-collapse: collapse; width: 100%; margin-top: 10px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
    </style>
</head>
<body>
    <h1>Python Coding Platform</h1>
    <h2>Admin Dashboard</h2>
    <p>Welcome, <%= session.getAttribute("name") %>!</p>
    
    <div class="quick-links" style="margin-bottom: 20px;">
        <h3>Quick Links</h3>
        <ul>
            <li><a href="${pageContext.request.contextPath}/admin/users">Manage Users</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/exercises">Content Approval / View Exercises</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/challenges">Manage Challenges</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/submissions">View Submissions</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/settings">System Settings</a></li>
            <li><a href="${pageContext.request.contextPath}/profile">Profile</a></li>
        </ul>
    </div>

    <div class="dashboard-container">
        <div class="dashboard-card">
            <h3>Platform Overview</h3>
            <ul>
                <li>Total Users: ${stats.totalUsers}</li>
                <li>Total Students: ${stats.totalStudents}</li>
                <li>Total Instructors: ${stats.totalInstructors}</li>
                <li>Total Admins: ${stats.totalAdmins}</li>
            </ul>
        </div>

        <div class="dashboard-card">
            <h3>Exercise Overview</h3>
            <ul>
                <li>Total Exercises: ${stats.totalExercises}</li>
                <li>Easy Exercises: ${stats.easyExercises}</li>
                <li>Medium Exercises: ${stats.mediumExercises}</li>
                <li>Hard Exercises: ${stats.hardExercises}</li>
            </ul>
        </div>

        <div class="dashboard-card">
            <h3>Submissions Overview</h3>
            <ul>
                <li>Total Submissions: ${stats.totalSubmissions}</li>
                <li>Pending Submissions: ${stats.pendingSubmissions}</li>
                <li>Reviewed Submissions: ${stats.reviewedSubmissions}</li>
            </ul>
        </div>
    </div>

    <h3>Recent Submissions</h3>
    <c:choose>
        <c:when test="${empty stats.recentSubmissions}">
            <p>No submissions found.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Student</th>
                        <th>Exercise</th>
                        <th>Status</th>
                        <th>Submitted At</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="sub" items="${stats.recentSubmissions}">
                        <tr>
                            <td>${sub.id}</td>
                            <td>${sub.studentName}</td>
                            <td>${sub.exerciseTitle}</td>
                            <td>${sub.status}</td>
                            <td>${sub.submittedAt}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>

    <h3>Recent Users</h3>
    <c:choose>
        <c:when test="${empty stats.recentUsers}">
            <p>No users found.</p>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Role</th>
                        <th>Joined At</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="usr" items="${stats.recentUsers}">
                        <tr>
                            <td>${usr.id}</td>
                            <td>${usr.name}</td>
                            <td>${usr.email}</td>
                            <td>${usr.role}</td>
                            <td>${usr.createdAt}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>

    <br>
    <a href="${pageContext.request.contextPath}/logout">Logout</a>
</body>
</html>
