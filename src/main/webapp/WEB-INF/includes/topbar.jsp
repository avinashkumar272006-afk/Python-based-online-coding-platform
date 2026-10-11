<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<div class="app-main">
    <header class="app-topbar">
        <div style="display: flex; align-items: center; gap: 1rem;">
            <button type="button" class="sidebar-toggle btn btn-secondary btn-sm" aria-label="Toggle navigation" aria-expanded="false" style="display: inline-flex;">
                ☰
            </button>
            <div class="breadcrumb" style="margin-bottom: 0;">
                <c:choose>
                    <c:when test="${sessionScope.role == 'ADMIN'}">
                        <a href="${pageContext.request.contextPath}/admin/dashboard">Home</a>
                    </c:when>
                    <c:when test="${sessionScope.role == 'INSTRUCTOR'}">
                        <a href="${pageContext.request.contextPath}/instructor/dashboard">Home</a>
                    </c:when>
                    <c:when test="${sessionScope.role == 'STUDENT'}">
                        <a href="${pageContext.request.contextPath}/student/dashboard">Home</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/home">Home</a>
                    </c:otherwise>
                </c:choose>
                <c:if test="${not empty param.pageCategory}">
                    <span>/</span>
                    <span><c:out value="${param.pageCategory}" /></span>
                </c:if>
                <c:if test="${not empty param.pageTitle}">
                    <span>/</span>
                    <span style="color: var(--text-primary); font-weight: 600;"><c:out value="${param.pageTitle}" /></span>
                </c:if>
            </div>
        </div>

        <div style="display: flex; align-items: center; gap: 0.75rem;">
            <span style="font-size: var(--font-size-sm); color: var(--text-secondary);">
                <c:out value="${sessionScope.name != null ? sessionScope.name : ''}" />
            </span>
            <c:choose>
                <c:when test="${sessionScope.role == 'ADMIN'}">
                    <span class="badge badge-danger">ADMIN</span>
                </c:when>
                <c:when test="${sessionScope.role == 'INSTRUCTOR'}">
                    <span class="badge badge-info">INSTRUCTOR</span>
                </c:when>
                <c:when test="${sessionScope.role == 'STUDENT'}">
                    <span class="badge badge-success">STUDENT</span>
                </c:when>
                <c:otherwise>
                    <span class="badge">${sessionScope.role}</span>
                </c:otherwise>
            </c:choose>
        </div>
    </header>
    <main class="app-content">
