<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    if (request.getAttribute("settings") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/settings");
        return;
    }
%>
<jsp:include page="/WEB-INF/includes/header.jsp">
    <jsp:param name="pageTitle" value="System Settings - Python Coding Platform" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/sidebar.jsp">
    <jsp:param name="activeNav" value="settings" />
</jsp:include>

<jsp:include page="/WEB-INF/includes/topbar.jsp">
    <jsp:param name="pageCategory" value="Administration" />
    <jsp:param name="pageTitle" value="System Settings" />
</jsp:include>

<%-- Metric derivation from existing model objects --%>
<c:set var="totalCount" value="${settings != null ? settings.size() : 0}" />
<c:set var="adminModifiedCount" value="0" />
<c:set var="systemDefaultsCount" value="0" />
<c:set var="booleanCount" value="0" />

<c:if test="${not empty settings}">
    <c:forEach var="setting" items="${settings}">
        <c:choose>
            <c:when test="${setting.updatedBy != null}">
                <c:set var="adminModifiedCount" value="${adminModifiedCount + 1}" />
            </c:when>
            <c:otherwise>
                <c:set var="systemDefaultsCount" value="${systemDefaultsCount + 1}" />
            </c:otherwise>
        </c:choose>
        <c:set var="val" value="${setting.settingValue}" />
        <c:if test="${val == 'true' || val == 'false' || val == 'TRUE' || val == 'FALSE' || val == 'True' || val == 'False'}">
            <c:set var="booleanCount" value="${booleanCount + 1}" />
        </c:if>
    </c:forEach>
</c:if>

<div class="page-header">
    <div>
        <h1 class="page-title">System Settings</h1>
        <div class="page-subtitle">Configure global platform behavior, registration policies, maintenance modes, and curriculum defaults.</div>
    </div>
    <div style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-secondary btn-sm">
            &larr; Back to Dashboard
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<%-- Summary KPI Metrics --%>
<div class="metrics-grid" style="margin-bottom: 1.75rem;">
    <div class="metric-card">
        <div class="metric-label">Configured Keys</div>
        <div class="metric-value"><c:out value="${totalCount}" /></div>
        <div class="metric-sub">Total platform configuration keys</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Admin Modified</div>
        <div class="metric-value" style="color: var(--brand-accent);"><c:out value="${adminModifiedCount}" /></div>
        <div class="metric-sub">Customized by platform admins</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">System Defaults</div>
        <div class="metric-value" style="color: var(--text-secondary);"><c:out value="${systemDefaultsCount}" /></div>
        <div class="metric-sub">Initial seed default settings</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Boolean Toggles</div>
        <div class="metric-value" style="color: var(--state-success);"><c:out value="${booleanCount}" /></div>
        <div class="metric-sub">Active feature flags and switches</div>
    </div>
</div>

<%-- Settings Management Table --%>
<div class="card">
    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
        <div>
            <h2 class="card-title">System Settings Configuration</h2>
            <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">
                Adjust platform properties and runtime parameters. Changes take effect immediately.
            </div>
        </div>
        <span class="badge badge-info"><c:out value="${totalCount}" /> Settings</span>
    </div>
    <div class="card-body" style="padding: 0;">
        <c:choose>
            <c:when test="${not empty settings}">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th style="min-width: 180px;">Setting Key</th>
                                <th style="min-width: 220px;">Description</th>
                                <th style="min-width: 260px;">Configuration Value</th>
                                <th style="min-width: 160px;">Last Updated</th>
                                <th style="width: 100px; text-align: right;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="setting" items="${settings}" varStatus="status">
                                <tr>
                                    <td>
                                        <span style="font-family: var(--font-mono); font-size: var(--font-size-xs); font-weight: 600; color: var(--brand-primary); background-color: var(--brand-accent-light); padding: 0.25rem 0.5rem; border-radius: var(--radius-sm); border: 1px solid var(--brand-accent-subtle); display: inline-block;">
                                            <c:out value="${setting.settingKey}" />
                                        </span>
                                    </td>
                                    <td>
                                        <div style="color: var(--text-secondary); font-size: var(--font-size-sm); line-height: 1.4;">
                                            <c:out value="${setting.description}" />
                                        </div>
                                    </td>
                                    <td>
                                        <form id="form_${status.index}" action="${pageContext.request.contextPath}/admin/settings" method="post" style="margin: 0;">
                                            <input type="hidden" name="csrfToken" value="${not empty csrfToken ? csrfToken : sessionScope.csrfToken}">
                                            <input type="hidden" name="settingKey" value="<c:out value="${setting.settingKey}" />">
                                            <input type="text" id="setting_${setting.settingKey}" name="settingValue" value="<c:out value="${setting.settingValue}" />" class="form-control" autocomplete="off" style="width: 100%; max-width: 320px; padding: 0.375rem 0.625rem; font-size: var(--font-size-sm); font-family: var(--font-mono);">
                                        </form>
                                    </td>
                                    <td>
                                        <div style="font-size: var(--font-size-xs); line-height: 1.4;">
                                            <div style="font-weight: 600; color: var(--text-primary);">
                                                <c:choose>
                                                    <c:when test="${not empty setting.updatedByName}">
                                                        <c:out value="${setting.updatedByName}" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span style="color: var(--text-muted);">System</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div style="color: var(--text-muted); font-size: 0.7rem; margin-top: 2px;">
                                                <c:choose>
                                                    <c:when test="${not empty setting.updatedAt}">
                                                        <c:out value="${setting.updatedAt}" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span>Not available</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </td>
                                    <td style="text-align: right;">
                                        <button type="submit" form="form_${status.index}" class="btn btn-primary btn-sm">
                                            Save
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div style="padding: 3.5rem 1.5rem; text-align: center; color: var(--text-secondary);">
                    <div style="font-size: 2.5rem; margin-bottom: 0.75rem;">⚙️</div>
                    <p style="font-size: var(--font-size-base); font-weight: 600; margin-bottom: 0.25rem;">No System Settings Found</p>
                    <p style="font-size: var(--font-size-sm); color: var(--text-muted); margin-bottom: 0;">There are currently no platform configuration settings registered in the database.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
