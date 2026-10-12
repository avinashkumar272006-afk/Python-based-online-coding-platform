<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="Create Account - Python Coding Platform" />
    <jsp:param name="standalone" value="true" />
</jsp:include>

<div class="auth-container">
    <div class="auth-card">
        <div class="auth-header">
            <div class="auth-logo">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                    <circle cx="8.5" cy="7" r="4"></circle>
                    <line x1="20" y1="8" x2="20" y2="14"></line>
                    <line x1="23" y1="11" x2="17" y2="11"></line>
                </svg>
            </div>
            <h1 class="auth-title">Create an Account</h1>
            <p class="auth-subtitle">Join the interactive Python learning platform</p>
        </div>

        <c:if test="${not empty requestScope.errorMessage}">
            <div class="alert alert-error error-msg">
                <span><c:out value="${requestScope.errorMessage}" /></span>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/register" method="post">
            <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken != null ? csrfToken : sessionScope.csrfToken}' />" />

            <div class="form-group">
                <label for="name" class="form-label form-label-required">Full Name</label>
                <input type="text" id="name" name="name" class="form-control" placeholder="Enter your full name" required value="<c:out value='${param.name}' />">
            </div>

            <div class="form-group">
                <label for="email" class="form-label form-label-required">Email Address</label>
                <input type="email" id="email" name="email" class="form-control" placeholder="name@example.com" required autocomplete="email" value="<c:out value='${param.email}' />">
            </div>

            <div class="form-group">
                <label for="password" class="form-label form-label-required">Password</label>
                <input type="password" id="password" name="password" class="form-control" placeholder="Minimum 6 characters" required minlength="6" autocomplete="new-password">
            </div>

            <div class="form-group">
                <label for="confirmPassword" class="form-label form-label-required">Confirm Password</label>
                <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" placeholder="Re-enter your password" required minlength="6" autocomplete="new-password">
            </div>

            <div class="form-group">
                <label for="role" class="form-label form-label-required">Account Role</label>
                <select id="role" name="role" class="form-control form-select" required>
                    <option value="STUDENT" ${param.role == 'INSTRUCTOR' ? '' : 'selected'}>Student</option>
                    <option value="INSTRUCTOR" ${param.role == 'INSTRUCTOR' ? 'selected' : ''}>Instructor</option>
                </select>
                <div class="form-helper">Select Student to practice coding or Instructor to manage assignments.</div>
            </div>

            <button type="submit" class="btn btn-primary btn-block">Create Account</button>
        </form>

        <div class="auth-footer">
            <p style="margin: 0 0 0.5rem 0;">Already have an account? <a href="${pageContext.request.contextPath}/login.jsp">Sign In</a></p>
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
