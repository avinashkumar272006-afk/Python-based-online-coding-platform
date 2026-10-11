<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<aside class="app-sidebar" id="appSidebar">
    <div class="sidebar-header">
        <c:choose>
            <c:when test="${sessionScope.role == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/admin/dashboard" style="color: inherit; text-decoration: none;">🐍 Python Platform</a>
            </c:when>
            <c:when test="${sessionScope.role == 'INSTRUCTOR'}">
                <a href="${pageContext.request.contextPath}/instructor/dashboard" style="color: inherit; text-decoration: none;">🐍 Python Platform</a>
            </c:when>
            <c:when test="${sessionScope.role == 'STUDENT'}">
                <a href="${pageContext.request.contextPath}/student/dashboard" style="color: inherit; text-decoration: none;">🐍 Python Platform</a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/home" style="color: inherit; text-decoration: none;">🐍 Python Platform</a>
            </c:otherwise>
        </c:choose>
    </div>

    <nav class="sidebar-nav">
        <%-- Role-specific links --%>
        <c:choose>
            <%-- STUDENT Navigation --%>
            <c:when test="${sessionScope.role == 'STUDENT'}">
                <div class="sidebar-nav-title">Learning</div>
                <ul class="sidebar-nav-list">
                    <li class="sidebar-nav-item">
                        <a href="${pageContext.request.contextPath}/student/dashboard" class="sidebar-nav-link ${param.activeNav == 'dashboard' ? 'active' : ''}">
                            Dashboard
                        </a>
                    </li>
                    <li class="sidebar-nav-item">
                        <a href="${pageContext.request.contextPath}/student/exercises" class="sidebar-nav-link ${param.activeNav == 'exercises' ? 'active' : ''}">
                            Browse Exercises
                        </a>
                    </li>
                    <li class="sidebar-nav-item">
                        <a href="${pageContext.request.contextPath}/student/submissions" class="sidebar-nav-link ${param.activeNav == 'submissions' ? 'active' : ''}">
                            My Submissions
                        </a>
                    </li>
                </ul>

                <div class="sidebar-nav-title">Challenges</div>
                <ul class="sidebar-nav-list">
                    <li class="sidebar-nav-item">
                        <a href="${pageContext.request.contextPath}/student/challenges" class="sidebar-nav-link ${param.activeNav == 'challenges' ? 'active' : ''}">
                            Available Challenges
                        </a>
                    </li>
                    <li class="sidebar-nav-item">
                        <a href="${pageContext.request.contextPath}/student/my-challenges" class="sidebar-nav-link ${param.activeNav == 'my-challenges' ? 'active' : ''}">
                            My Challenges
                        </a>
                    </li>
                </ul>

                <div class="sidebar-nav-title">Community</div>
                <ul class="sidebar-nav-list">
                    <li class="sidebar-nav-item">
                        <a href="${pageContext.request.contextPath}/messages" class="sidebar-nav-link ${param.activeNav == 'messages' ? 'active' : ''}">
                            Messages
                        </a>
                    </li>
                </ul>
            </c:when>

            <%-- INSTRUCTOR Navigation --%>
            <c:when test="${sessionScope.role == 'INSTRUCTOR'}">
                <div class="sidebar-nav-title">Teaching</div>
                <ul class="sidebar-nav-list">
                    <li class="sidebar-nav-item">
                        <a href="${pageContext.request.contextPath}/instructor/dashboard" class="sidebar-nav-link ${param.activeNav == 'dashboard' ? 'active' : ''}">
                            Dashboard
                        </a>
                    </li>
                    <li class="sidebar-nav-item">
                        <a href="${pageContext.request.contextPath}/instructor/exercises" class="sidebar-nav-link ${param.activeNav == 'exercises' ? 'active' : ''}">
                            Manage Exercises
                        </a>
                    </li>
                    <li class="sidebar-nav-item">
                        <a href="${pageContext.request.contextPath}/instructor/exercises?action=new" class="sidebar-nav-link ${param.activeNav == 'create-exercise' ? 'active' : ''}">
                            Create Exercise
                        </a>
                    </li>
                    <li class="sidebar-nav-item">
                        <a href="${pageContext.request.contextPath}/instructor/submissions" class="sidebar-nav-link ${param.activeNav == 'submissions' ? 'active' : ''}">
                            Review Submissions
                        </a>
                    </li>
                    <li class="sidebar-nav-item">
                        <a href="${pageContext.request.contextPath}/instructor/challenges" class="sidebar-nav-link ${param.activeNav == 'challenges' ? 'active' : ''}">
                            Challenges
                        </a>
                    </li>
                </ul>

                <div class="sidebar-nav-title">Community</div>
                <ul class="sidebar-nav-list">
                    <li class="sidebar-nav-item">
                        <a href="${pageContext.request.contextPath}/messages" class="sidebar-nav-link ${param.activeNav == 'messages' ? 'active' : ''}">
                            Messages
                        </a>
                    </li>
                </ul>
            </c:when>

            <%-- ADMIN Navigation --%>
            <c:when test="${sessionScope.role == 'ADMIN'}">
                <div class="sidebar-nav-title">Administration</div>
                <ul class="sidebar-nav-list">
                    <li class="sidebar-nav-item">
                        <a href="${pageContext.request.contextPath}/admin/dashboard" class="sidebar-nav-link ${param.activeNav == 'dashboard' ? 'active' : ''}">
                            Dashboard
                        </a>
                    </li>
                    <li class="sidebar-nav-item">
                        <a href="${pageContext.request.contextPath}/admin/users" class="sidebar-nav-link ${param.activeNav == 'users' ? 'active' : ''}">
                            Manage Users
                        </a>
                    </li>
                    <li class="sidebar-nav-item">
                        <a href="${pageContext.request.contextPath}/admin/exercises" class="sidebar-nav-link ${param.activeNav == 'exercises' ? 'active' : ''}">
                            Exercise Approvals
                        </a>
                    </li>
                    <li class="sidebar-nav-item">
                        <a href="${pageContext.request.contextPath}/admin/challenges" class="sidebar-nav-link ${param.activeNav == 'challenges' ? 'active' : ''}">
                            Manage Challenges
                        </a>
                    </li>
                    <li class="sidebar-nav-item">
                        <a href="${pageContext.request.contextPath}/admin/submissions" class="sidebar-nav-link ${param.activeNav == 'submissions' ? 'active' : ''}">
                            All Submissions
                        </a>
                    </li>
                    <li class="sidebar-nav-item">
                        <a href="${pageContext.request.contextPath}/admin/settings" class="sidebar-nav-link ${param.activeNav == 'settings' ? 'active' : ''}">
                            System Settings
                        </a>
                    </li>
                </ul>
            </c:when>
        </c:choose>

        <%-- Common Account Navigation --%>
        <div class="sidebar-nav-title">Account</div>
        <ul class="sidebar-nav-list">
            <li class="sidebar-nav-item">
                <a href="${pageContext.request.contextPath}/profile" class="sidebar-nav-link ${param.activeNav == 'profile' ? 'active' : ''}">
                    Profile
                </a>
            </li>
            <li class="sidebar-nav-item">
                <form action="${pageContext.request.contextPath}/logout" method="post" style="margin: 0; padding: 0;">
                    <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken != null ? csrfToken : sessionScope.csrfToken}' />" />
                    <button type="submit" class="sidebar-nav-link" style="background: none; border: none; width: 100%; text-align: left; cursor: pointer; font: inherit;">
                        Logout
                    </button>
                </form>
            </li>
        </ul>
    </nav>

    <div class="sidebar-footer">
        <div style="font-size: var(--font-size-xs); color: #64748b;">
            Logged in as <strong><c:out value="${sessionScope.name != null ? sessionScope.name : 'User'}" /></strong>
        </div>
    </div>
</aside>
