<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    String role = (String) session.getAttribute("role");
    String dashboardUrl = request.getContextPath() + "/student/dashboard";
    if ("ADMIN".equals(role)) {
        dashboardUrl = request.getContextPath() + "/admin/dashboard";
    } else if ("INSTRUCTOR".equals(role)) {
        dashboardUrl = request.getContextPath() + "/instructor/dashboard";
    }
%>

<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="Python Online Coding Platform" />
    <jsp:param name="standalone" value="true" />
</jsp:include>

<nav class="landing-nav" aria-label="Main Navigation">
    <div class="landing-nav-inner">
        <a href="${pageContext.request.contextPath}/home" class="landing-brand">
            <span class="landing-brand-icon">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="16 18 22 12 16 6"></polyline>
                    <polyline points="8 6 2 12 8 18"></polyline>
                </svg>
            </span>
            <span>Python Coding Platform</span>
        </a>

        <div class="landing-nav-actions">
            <c:choose>
                <c:when test="${not empty sessionScope.userId}">
                    <span style="color: #cbd5e1; font-size: var(--font-size-sm); display: inline-flex; align-items: center; gap: 0.5rem;">
                        <span>Hello, <c:out value="${sessionScope.name != null ? sessionScope.name : 'User'}" /></span>
                    </span>
                    <a href="<%= dashboardUrl %>" class="btn btn-primary btn-sm">My Dashboard</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-secondary btn-sm">Sign In</a>
                    <a href="${pageContext.request.contextPath}/register.jsp" class="btn btn-primary btn-sm">Get Started</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</nav>

<header class="landing-hero">
    <div class="landing-hero-content">
        <div class="landing-badge">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"></circle>
                <polyline points="12 6 12 12 14 14"></polyline>
            </svg>
            <span>Interactive Python Learning</span>
        </div>

        <h1 class="landing-title">
            Master Python Programming <span class="text-gradient">Through Practice</span>
        </h1>

        <p class="landing-subtitle">
            <c:out value="${welcomeMessage != null ? welcomeMessage : 'Welcome to the Python-Based Online Coding Platform!'}" />
            Solve hands-on exercises, participate in timed challenges, receive instructor feedback, and track your progress all in one place.
        </p>

        <div class="landing-hero-btns">
            <c:choose>
                <c:when test="${not empty sessionScope.userId}">
                    <a href="<%= dashboardUrl %>" class="btn btn-primary btn-lg">Go to Your Dashboard</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/register.jsp" class="btn btn-primary btn-lg">Start Learning Free</a>
                    <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-secondary btn-lg">Sign In</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</header>

<section class="landing-section landing-section-alt" aria-labelledby="features-heading">
    <div class="landing-section-inner">
        <div class="landing-section-header">
            <div class="landing-section-tag">Platform Features</div>
            <h2 id="features-heading" class="landing-section-title">Everything You Need to Excel in Python</h2>
            <p class="landing-section-desc">A structured learning environment built to bridge the gap between reading syntax and writing production-ready code.</p>
        </div>

        <div class="features-grid-landing">
            <div class="feature-box">
                <div class="feature-box-icon">
                    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="16 18 22 12 16 6"></polyline>
                        <polyline points="8 6 2 12 8 18"></polyline>
                    </svg>
                </div>
                <h3 class="feature-box-title">Interactive Exercises</h3>
                <p class="feature-box-desc">Browse structured coding exercises covering core concepts, data structures, and practical algorithms with clear requirements.</p>
            </div>

            <div class="feature-box">
                <div class="feature-box-icon">
                    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                        <polyline points="22 4 12 14.01 9 11.01"></polyline>
                    </svg>
                </div>
                <h3 class="feature-box-title">Score-Based Evaluation</h3>
                <p class="feature-box-desc">Submit your Python solutions for instructor review with quantitative 0–100 scoring, structured feedback, and submission tracking.</p>
            </div>

            <div class="feature-box">
                <div class="feature-box-icon">
                    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                    </svg>
                </div>
                <h3 class="feature-box-title">Instructor Code Reviews</h3>
                <p class="feature-box-desc">Receive qualitative feedback, grades, and personalized recommendations directly from instructors to improve code style.</p>
            </div>

            <div class="feature-box">
                <div class="feature-box-icon">
                    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <line x1="18" y1="20" x2="18" y2="10"></line>
                        <line x1="12" y1="20" x2="12" y2="4"></line>
                        <line x1="6" y1="20" x2="6" y2="14"></line>
                    </svg>
                </div>
                <h3 class="feature-box-title">Progress Tracking</h3>
                <p class="feature-box-desc">Monitor your completion rate, track submitted assignments, and see your mastery grow across every curriculum module.</p>
            </div>
        </div>
    </div>
</section>

<section class="landing-section" aria-labelledby="workflow-heading">
    <div class="landing-section-inner">
        <div class="landing-section-header">
            <div class="landing-section-tag">Learning Workflow</div>
            <h2 id="workflow-heading" class="landing-section-title">How Learning Works</h2>
            <p class="landing-section-desc">A straightforward, feedback-driven path from problem selection to verified mastery.</p>
        </div>

        <div class="workflow-steps-grid">
            <div class="workflow-step-card">
                <div class="workflow-step-num">1</div>
                <h3 class="workflow-step-title">Choose an Exercise</h3>
                <p class="workflow-step-desc">Pick an exercise from your student curriculum tailored to your current learning objectives and skill level.</p>
            </div>

            <div class="workflow-step-card">
                <div class="workflow-step-num">2</div>
                <h3 class="workflow-step-title">Code and Submit</h3>
                <p class="workflow-step-desc">Write your solution in Python using starter templates, test your ideas, and submit your code for instructor evaluation.</p>
            </div>

            <div class="workflow-step-card">
                <div class="workflow-step-num">3</div>
                <h3 class="workflow-step-title">Review & Level Up</h3>
                <p class="workflow-step-desc">Inspect instructor grades and feedback notes, tackle timed challenges, and track your milestone achievements.</p>
            </div>
        </div>

        <div style="margin-top: 4rem;">
            <div class="landing-cta-banner">
                <h2 class="landing-cta-title">Ready to Start Learning Python?</h2>
                <p class="landing-cta-desc">
                    Join students and instructors already mastering Python on the platform. Create an account or sign in to begin solving exercises.
                </p>
                <div>
                    <c:choose>
                        <c:when test="${not empty sessionScope.userId}">
                            <a href="<%= dashboardUrl %>" class="btn btn-secondary btn-lg" style="color: var(--brand-primary); font-weight: 600;">Go to Dashboard</a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/register.jsp" class="btn btn-secondary btn-lg" style="color: var(--brand-primary); font-weight: 600;">Create Free Account</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</section>

<footer class="landing-footer">
    <div class="landing-footer-inner">
        <div class="landing-footer-links">
            <a href="${pageContext.request.contextPath}/home">Home</a>
            <a href="${pageContext.request.contextPath}/login.jsp">Sign In</a>
            <a href="${pageContext.request.contextPath}/register.jsp">Register</a>
        </div>
        <p style="margin: 0;">&copy; Python-Based Online Coding Platform. All rights reserved.</p>
    </div>
</footer>

<jsp:include page="/WEB-INF/includes/footer.jsp">
    <jsp:param name="standalone" value="true" />
</jsp:include>
