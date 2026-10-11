<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="Python Online Coding Platform - Home" />
    <jsp:param name="standalone" value="true" />
</jsp:include>

<header style="background-color: #0f172a; color: #fff; padding: 1.5rem 1rem; text-align: center; border-bottom: 1px solid #1e293b;">
    <h1 style="margin: 0; font-size: var(--font-size-2xl); font-weight: 700; letter-spacing: -0.02em;">Python Online Coding Platform</h1>
</header>

<nav style="text-align: center; margin: 1.5rem 0; display: flex; justify-content: center; gap: 0.75rem;">
    <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary">Home</a>
    <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-primary">Login</a>
    <a href="${pageContext.request.contextPath}/register.jsp" class="btn btn-secondary">Register</a>
</nav>

<div class="app-container" style="max-width: 800px; margin: 2rem auto;">
    <div class="card" style="text-align: center;">
        <div class="card-body" style="padding: 2.5rem 2rem;">
            <h2 class="card-title" style="font-size: var(--font-size-xl); margin-bottom: 0.75rem;">
                <c:out value="${welcomeMessage != null ? welcomeMessage : 'Welcome to the Python-Based Online Coding Platform!'}" />
            </h2>
            <p style="color: var(--text-secondary); margin: 0; font-size: var(--font-size-base);">
                Learn Python by solving interactive coding challenges.
            </p>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp">
    <jsp:param name="standalone" value="true" />
</jsp:include>

