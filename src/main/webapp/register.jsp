<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register - Python Coding Platform</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
        .register-box { background: white; padding: 2em; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1); width: 300px; }
        .register-box h2 { margin-top: 0; }
        .register-box input, .register-box select { width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        .register-box button { width: 100%; padding: 10px; background-color: #333; color: white; border: none; border-radius: 4px; cursor: pointer; }
        .register-box button:hover { background-color: #555; }
        .error-msg { color: red; font-size: 0.9em; margin-bottom: 10px; }
    </style>
</head>
<body>
    <div class="register-box">
        <h2>Register</h2>
        <% 
            String error = (String) request.getAttribute("errorMessage");
            if (error != null) {
        %>
            <div class="error-msg"><%= error %></div>
        <% } %>
        <form action="register" method="post">
            <input type="text" name="name" placeholder="Full Name" required>
            <input type="email" name="email" placeholder="Email" required>
            <input type="password" name="password" placeholder="Password" required minlength="6">
            <input type="password" name="confirmPassword" placeholder="Confirm Password" required minlength="6">
            <select name="role">
                <option value="STUDENT">Student</option>
                <option value="INSTRUCTOR">Instructor</option>
                <option value="ADMIN">Admin</option>
            </select>
            <button type="submit">Register</button>
        </form>
        <p style="text-align: center; font-size: 0.9em;">Already have an account? <a href="login.jsp">Login</a></p>
        <p style="text-align: center; font-size: 0.9em;"><a href="home">Back to Home</a></p>
    </div>
</body>
</html>
