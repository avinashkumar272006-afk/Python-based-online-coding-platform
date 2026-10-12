<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="Sign In - Python Coding Platform" />
    <jsp:param name="standalone" value="true" />
</jsp:include>

<div class="auth-container">
    <div class="auth-card">
        <div class="auth-header">
            <div class="auth-logo">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="16 18 22 12 16 6"></polyline>
                    <polyline points="8 6 2 12 8 18"></polyline>
                </svg>
            </div>
            <h1 class="auth-title">Welcome Back</h1>
            <p class="auth-subtitle">Sign in to your account to continue learning</p>
        </div>

        <c:choose>
            <c:when test="${not empty requestScope.successMessage}">
                <div class="alert alert-success success-msg">
                    <span><c:out value="${requestScope.successMessage}" /></span>
                </div>
            </c:when>
            <c:when test="${param.logout == 'true'}">
                <div class="alert alert-success success-msg">
                    <span>You have been successfully logged out.</span>
                </div>
            </c:when>
        </c:choose>

        <c:if test="${not empty requestScope.errorMessage}">
            <div class="alert alert-error error-msg">
                <span><c:out value="${requestScope.errorMessage}" /></span>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken != null ? csrfToken : sessionScope.csrfToken}' />" />

            <div class="form-group">
                <label for="email" class="form-label form-label-required">Email Address</label>
                <input type="email" id="email" name="email" class="form-control" placeholder="name@example.com" required autocomplete="email" value="<c:out value='${param.email}' />">
            </div>

            <div class="form-group">
                <label for="password" class="form-label form-label-required">Password</label>
                <input type="password" id="password" name="password" class="form-control" placeholder="Enter your password" required autocomplete="current-password">
            </div>

            <button type="submit" class="btn btn-primary btn-block">Sign In</button>
        </form>

        <div class="auth-footer">
            <p style="margin: 0 0 0.5rem 0;">Don't have an account? <a href="${pageContext.request.contextPath}/register.jsp">Create one now</a></p>
            <div>
                <a href="${pageContext.request.contextPath}/home" class="auth-back-link">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <line x1="19" y1="12" x2="5" y2="12"></line>
                        <polyline points="12 19 5 12 12 5"></polyline>
                    </svg>
                    Back to Home
                </a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp">
    <jsp:param name="standalone" value="true" />
</jsp:include>
