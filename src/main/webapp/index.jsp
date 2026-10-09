<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Python Coding Platform - Home</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }
        header { background-color: #333; color: #fff; padding: 1em 0; text-align: center; }
        nav { text-align: center; margin: 1em 0; }
        nav a { margin: 0 1em; text-decoration: none; color: #333; font-weight: bold; }
        .container { padding: 2em; text-align: center; }
    </style>
</head>
<body>
    <header>
        <h1>Python Online Coding Platform</h1>
    </header>
    <nav>
        <a href="home">Home</a>
        <a href="login.jsp">Login</a>
        <a href="register.jsp">Register</a>
    </nav>
    <div class="container">
        <h2>${welcomeMessage != null ? welcomeMessage : "Welcome to the Python Coding Platform"}</h2>
        <p>Learn Python by solving interactive coding challenges.</p>
    </div>
</body>
</html>
