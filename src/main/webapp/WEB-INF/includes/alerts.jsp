<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%-- Session-scoped Flash Success Message --%>
<c:if test="${not empty sessionScope.successMessage}">
    <div class="alert alert-success">
        <span><c:out value="${sessionScope.successMessage}" /></span>
    </div>
    <c:remove var="successMessage" scope="session" />
</c:if>

<%-- Session-scoped Flash Error Message --%>
<c:if test="${not empty sessionScope.errorMessage}">
    <div class="alert alert-error">
        <span><c:out value="${sessionScope.errorMessage}" /></span>
    </div>
    <c:remove var="errorMessage" scope="session" />
</c:if>

<%-- Request-scoped Success Message --%>
<c:if test="${not empty requestScope.successMessage}">
    <div class="alert alert-success">
        <span><c:out value="${requestScope.successMessage}" /></span>
    </div>
</c:if>

<%-- Request-scoped Error Message --%>
<c:if test="${not empty requestScope.errorMessage}">
    <div class="alert alert-error">
        <span><c:out value="${requestScope.errorMessage}" /></span>
    </div>
</c:if>

<%-- Request-scoped General Error Message --%>
<c:if test="${not empty requestScope.error}">
    <div class="alert alert-error">
        <span><c:out value="${requestScope.error}" /></span>
    </div>
</c:if>

<%-- Request-scoped Performance Error Message --%>
<c:if test="${not empty requestScope.performanceError}">
    <div class="alert alert-error">
        <span><c:out value="${requestScope.performanceError}" /></span>
    </div>
</c:if>

<%-- Query Parameter Success Feedback --%>
<c:if test="${param.success == 'true'}">
    <div class="alert alert-success">
        <span>Operation completed successfully!</span>
    </div>
</c:if>
<c:if test="${param.success == 'approve'}">
    <div class="alert alert-success">
        <span>Approved successfully!</span>
    </div>
</c:if>
<c:if test="${param.success == 'reject'}">
    <div class="alert alert-success">
        <span>Rejected successfully!</span>
    </div>
</c:if>
<c:if test="${param.success == 'created'}">
    <div class="alert alert-success">
        <span>Created successfully!</span>
    </div>
</c:if>
<c:if test="${param.success == 'joined'}">
    <div class="alert alert-success">
        <span>Successfully joined the challenge!</span>
    </div>
</c:if>
<c:if test="${param.success == 'completed'}">
    <div class="alert alert-success">
        <span>Challenge marked as completed!</span>
    </div>
</c:if>
<c:if test="${param.success == 'dropped'}">
    <div class="alert alert-success">
        <span>Challenge dropped.</span>
    </div>
</c:if>
