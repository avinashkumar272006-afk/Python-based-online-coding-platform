<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login - Python Coding Platform</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
        .login-box { background: white; padding: 2em; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1); width: 300px; }
        .login-box h2 { margin-top: 0; }
        .login-box input { width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        .login-box button { width: 100%; padding: 10px; background-color: #333; color: white; border: none; border-radius: 4px; cursor: pointer; }
        .login-box button:hover { background-color: #555; }
        .success-msg { color: green; font-size: 0.9em; margin-bottom: 10px; }
        .error-msg { color: red; font-size: 0.9em; margin-bottom: 10px; }
    </style>
</head>
<body>
    <div class="login-box">
        <h2>Login</h2>
        <% 
            String success = (String) request.getAttribute("successMessage");
            if (success != null) {
        %>
            <div class="success-msg"><%= success %></div>
        <% } %>
        <% 
            String error = (String) request.getAttribute("errorMessage");
            if (error != null) {
        %>
            <div class="error-msg"><%= error %></div>
        <% } %>
        <form action="login" method="post">
            <input type="email" name="email" placeholder="Email" required>
            <input type="password" name="password" placeholder="Password" required>
            <button type="submit">Login</button>
        </form>
        <p style="text-align: center; font-size: 0.9em;"><a href="home">Back to Home</a></p>
    </div>
</body>
</html>
