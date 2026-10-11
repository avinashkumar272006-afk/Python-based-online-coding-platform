<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="Messages - Python Coding Platform" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activeNav" value="messages" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageCategory" value="Community" />
    <jsp:param name="pageTitle" value="Messages" />
</jsp:include>

<div class="page-header">
    <div>
        <h1 class="page-title">
            <c:choose>
                <c:when test="${sessionScope.role == 'STUDENT'}">Interact with Instructors</c:when>
                <c:when test="${sessionScope.role == 'INSTRUCTOR'}">Student Interaction</c:when>
                <c:otherwise>Messages</c:otherwise>
            </c:choose>
        </h1>
        <div class="page-subtitle">
            <c:choose>
                <c:when test="${sessionScope.role == 'STUDENT'}">Connect with your instructors for guidance, questions, and course discussions.</c:when>
                <c:when test="${sessionScope.role == 'INSTRUCTOR'}">Communicate with students about their coursework, progress, and submissions.</c:when>
                <c:otherwise>Direct messaging and communication.</c:otherwise>
            </c:choose>
        </div>
    </div>
    <div>
        <c:choose>
            <c:when test="${sessionScope.role == 'STUDENT'}">
                <a href="${pageContext.request.contextPath}/student/dashboard" class="btn btn-secondary btn-sm">
                    &larr; Back to Dashboard
                </a>
            </c:when>
            <c:when test="${sessionScope.role == 'INSTRUCTOR'}">
                <a href="${pageContext.request.contextPath}/instructor/dashboard" class="btn btn-secondary btn-sm">
                    &larr; Back to Dashboard
                </a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-secondary btn-sm">
                    &larr; Back to Dashboard
                </a>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<div class="card">
    <div class="card-header">
        <h2 class="card-title">
            <c:choose>
                <c:when test="${sessionScope.role == 'STUDENT'}">Available Instructors</c:when>
                <c:when test="${sessionScope.role == 'INSTRUCTOR'}">Available Students</c:when>
                <c:otherwise>Contacts</c:otherwise>
            </c:choose>
        </h2>
        <span class="badge badge-info"><c:out value="${partners != null ? partners.size() : 0}" /> Contacts</span>
    </div>
    <div class="card-body" style="padding: 0;">
        <c:choose>
            <c:when test="${not empty partners}">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Contact Name</th>
                                <th>Role</th>
                                <th style="text-align: right;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="partner" items="${partners}">
                                <tr>
                                    <td>
                                        <div style="font-weight: 600; color: var(--text-primary);"><c:out value="${partner.name}" /></div>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${partner.role == 'INSTRUCTOR'}">
                                                <span class="badge badge-info"><c:out value="${partner.role}" /></span>
                                            </c:when>
                                            <c:when test="${partner.role == 'STUDENT'}">
                                                <span class="badge badge-success"><c:out value="${partner.role}" /></span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge"><c:out value="${partner.role}" /></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="text-align: right;">
                                        <a href="${pageContext.request.contextPath}/messages?partnerId=${partner.id}" class="btn btn-primary btn-sm">
                                            Open Conversation
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div style="padding: 3rem 1.5rem; text-align: center; color: var(--text-secondary);">
                    <div style="font-size: 2rem; margin-bottom: 0.75rem;">💬</div>
                    <p style="font-size: var(--font-size-base); font-weight: 600; margin-bottom: 0.25rem;">No contacts available.</p>
                    <p style="font-size: var(--font-size-sm); color: var(--text-muted); margin-bottom: 1.25rem;">
                        <c:choose>
                            <c:when test="${sessionScope.role == 'STUDENT'}">There are currently no instructors available to message.</c:when>
                            <c:when test="${sessionScope.role == 'INSTRUCTOR'}">There are currently no students available to message.</c:when>
                            <c:otherwise>No messaging contacts found.</c:otherwise>
                        </c:choose>
                    </p>
                    <c:choose>
                        <c:when test="${sessionScope.role == 'STUDENT'}">
                            <a href="${pageContext.request.contextPath}/student/dashboard" class="btn btn-secondary btn-sm">Return to Dashboard</a>
                        </c:when>
                        <c:when test="${sessionScope.role == 'INSTRUCTOR'}">
                            <a href="${pageContext.request.contextPath}/instructor/dashboard" class="btn btn-secondary btn-sm">Return to Dashboard</a>
                        </c:when>
                    </c:choose>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
