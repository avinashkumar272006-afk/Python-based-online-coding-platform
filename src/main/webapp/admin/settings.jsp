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

<%-- Metric and category derivation from existing model objects --%>
<c:set var="totalCount" value="${settings != null ? settings.size() : 0}" />
<c:set var="adminModifiedCount" value="0" />
<c:set var="systemDefaultsCount" value="0" />
<c:set var="booleanCount" value="0" />
<c:set var="platformCount" value="0" />
<c:set var="accessCount" value="0" />
<c:set var="opsCount" value="0" />
<c:set var="curriculumCount" value="0" />
<c:set var="otherCount" value="0" />

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

        <c:set var="k" value="${setting.settingKey}" />
        <c:choose>
            <c:when test="${k == 'platform_name' || k.startsWith('platform_') || k.startsWith('site_') || k.startsWith('app_')}">
                <c:set var="platformCount" value="${platformCount + 1}" />
            </c:when>
            <c:when test="${k == 'allow_registration' || k.startsWith('allow_') || k.startsWith('auth_') || k.startsWith('reg_') || k.startsWith('login_') || k.startsWith('user_')}">
                <c:set var="accessCount" value="${accessCount + 1}" />
            </c:when>
            <c:when test="${k == 'maintenance_mode' || k.startsWith('maintenance_') || k.startsWith('system_') || k.startsWith('cache_') || k.startsWith('debug_')}">
                <c:set var="opsCount" value="${opsCount + 1}" />
            </c:when>
            <c:when test="${k == 'default_exercise_difficulty' || k.startsWith('default_') || k.startsWith('exercise_') || k.startsWith('difficulty') || k.startsWith('challenge_') || k.startsWith('submission_')}">
                <c:set var="curriculumCount" value="${curriculumCount + 1}" />
            </c:when>
            <c:otherwise>
                <c:set var="otherCount" value="${otherCount + 1}" />
            </c:otherwise>
        </c:choose>
    </c:forEach>
</c:if>

<div class="page-header">
    <div>
        <div style="display: flex; align-items: center; gap: 0.625rem; margin-bottom: 0.25rem;">
            <span style="font-size: 1.5rem;">⚙️</span>
            <h1 class="page-title" style="margin: 0;">System Settings &amp; Configuration</h1>
            <span class="badge badge-info" style="font-size: 0.7rem;">Platform Config</span>
        </div>
        <div class="page-subtitle" style="color: var(--text-secondary); font-size: var(--font-size-sm); max-width: 720px;">
            Configure global platform behavior, user registration policies, operational maintenance modes, and curriculum defaults.
        </div>
    </div>
    <div style="display: flex; gap: 0.75rem; align-items: center; flex-wrap: wrap;">
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-secondary btn-sm">
            &larr; Back to Dashboard
        </a>
        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary btn-sm">
            👥 Manage Users
        </a>
        <a href="${pageContext.request.contextPath}/admin/exercises" class="btn btn-secondary btn-sm">
            📚 Manage Exercises
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/alerts.jsp" />

<%-- Summary KPI Metrics --%>
<div class="metrics-grid" style="margin-bottom: 1.75rem;">
    <div class="metric-card">
        <div class="metric-label">Configured Keys</div>
        <div class="metric-value"><c:out value="${totalCount}" /></div>
        <div class="metric-sub">Active platform configuration keys</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Admin Modified</div>
        <div class="metric-value stat-accent"><c:out value="${adminModifiedCount}" /></div>
        <div class="metric-sub">Customized by platform administrators</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">System Defaults</div>
        <div class="metric-value" style="color: var(--text-secondary);"><c:out value="${systemDefaultsCount}" /></div>
        <div class="metric-sub">Initial deployment seed values</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Boolean Toggles</div>
        <div class="metric-value stat-success"><c:out value="${booleanCount}" /></div>
        <div class="metric-sub">Active feature switches and flags</div>
    </div>
</div>

<%-- Administrative Security & Policy Banner --%>
<div style="display: flex; align-items: center; justify-content: space-between; padding: 0.875rem 1.125rem; background-color: var(--state-info-bg); border: 1px solid var(--state-info-border); border-radius: var(--radius-md); margin-bottom: 1.5rem; gap: 0.75rem; flex-wrap: wrap;">
    <div style="display: flex; align-items: flex-start; gap: 0.625rem;">
        <span style="font-size: 1.125rem; line-height: 1.2;">ℹ️</span>
        <div>
            <div style="font-weight: 600; font-size: var(--font-size-sm); color: var(--state-info-text);">
                Immediate Persistence &amp; Audit Governance
            </div>
            <div style="font-size: var(--font-size-xs); color: var(--state-info-text); opacity: 0.9; margin-top: 0.125rem;">
                Changes take effect across the runtime immediately upon saving. Every configuration update is secured with CSRF protection and audit-logged with the administrator's account identifier and timestamp.
            </div>
        </div>
    </div>
    <span class="badge badge-info" style="font-size: 0.7rem;">Live Runtime</span>
</div>

<c:choose>
    <c:when test="${not empty settings}">
        <%-- Quick Jump Links between Logical Sections --%>
        <div style="display: flex; gap: 0.5rem; align-items: center; flex-wrap: wrap; margin-bottom: 1.5rem; padding: 0.625rem 0.875rem; background: var(--bg-surface); border: 1px solid var(--border-subtle); border-radius: var(--radius-md);">
            <span style="font-size: var(--font-size-xs); font-weight: 600; color: var(--text-secondary); text-transform: uppercase; letter-spacing: 0.05em; margin-right: 0.25rem;">
                Jump to Section:
            </span>
            <c:if test="${platformCount > 0}">
                <a href="#section-platform" class="btn btn-secondary btn-sm" style="font-size: var(--font-size-xs); padding: 0.25rem 0.625rem;">
                    🌐 Platform Identity (<c:out value="${platformCount}" />)
                </a>
            </c:if>
            <c:if test="${accessCount > 0}">
                <a href="#section-access" class="btn btn-secondary btn-sm" style="font-size: var(--font-size-xs); padding: 0.25rem 0.625rem;">
                    🔐 User Access (<c:out value="${accessCount}" />)
                </a>
            </c:if>
            <c:if test="${opsCount > 0}">
                <a href="#section-ops" class="btn btn-secondary btn-sm" style="font-size: var(--font-size-xs); padding: 0.25rem 0.625rem;">
                    🛠️ System Operations (<c:out value="${opsCount}" />)
                </a>
            </c:if>
            <c:if test="${curriculumCount > 0}">
                <a href="#section-curriculum" class="btn btn-secondary btn-sm" style="font-size: var(--font-size-xs); padding: 0.25rem 0.625rem;">
                    📚 Curriculum Defaults (<c:out value="${curriculumCount}" />)
                </a>
            </c:if>
            <c:if test="${otherCount > 0}">
                <a href="#section-other" class="btn btn-secondary btn-sm" style="font-size: var(--font-size-xs); padding: 0.25rem 0.625rem;">
                    ⚙️ Advanced Properties (<c:out value="${otherCount}" />)
                </a>
            </c:if>
        </div>

        <%-- Section 1: Platform Identity & Branding --%>
        <c:if test="${platformCount > 0}">
            <div class="card" id="section-platform" style="margin-bottom: 1.75rem;">
                <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
                    <div style="display: flex; align-items: center; gap: 0.75rem;">
                        <div style="width: 36px; height: 36px; border-radius: var(--radius-md); background-color: var(--brand-accent-light); color: var(--brand-accent); display: flex; align-items: center; justify-content: center; font-size: 1.125rem;">
                            🌐
                        </div>
                        <div>
                            <h2 class="card-title" style="font-size: var(--font-size-base);">Platform Identity &amp; Branding</h2>
                            <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">
                                Platform display title, brand attributes, and general visual identity
                            </div>
                        </div>
                    </div>
                    <span class="badge badge-info"><c:out value="${platformCount}" /> Setting<c:if test="${platformCount != 1}">s</c:if></span>
                </div>
                <div class="card-body" style="padding: 0;">
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th style="min-width: 220px; width: 32%;">Configuration Key &amp; Description</th>
                                    <th style="min-width: 320px; width: 44%;">Runtime Value &amp; Controls</th>
                                    <th style="min-width: 180px; width: 24%;">Audit History</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="setting" items="${settings}">
                                    <c:set var="k" value="${setting.settingKey}" />
                                    <c:if test="${k == 'platform_name' || k.startsWith('platform_') || k.startsWith('site_') || k.startsWith('app_')}">
                                        <tr>
                                            <td>
                                                <div>
                                                    <span style="font-family: var(--font-mono); font-size: var(--font-size-xs); font-weight: 600; color: var(--brand-primary); background-color: var(--brand-accent-light); padding: 0.25rem 0.5rem; border-radius: var(--radius-sm); border: 1px solid var(--brand-accent-subtle); display: inline-block;">
                                                        <c:out value="${setting.settingKey}" />
                                                    </span>
                                                </div>
                                                <div style="color: var(--text-secondary); font-size: var(--font-size-sm); line-height: 1.45; margin-top: 0.375rem;">
                                                    <c:out value="${not empty setting.description ? setting.description : 'Platform identity configuration property.'}" />
                                                </div>
                                                <div style="margin-top: 0.375rem; display: flex; gap: 0.35rem; align-items: center; flex-wrap: wrap;">
                                                    <span class="badge" style="font-size: 0.65rem;">Text String</span>
                                                </div>
                                            </td>
                                            <td>
                                                <form id="form_${setting.settingKey}" action="${pageContext.request.contextPath}/admin/settings" method="post" style="margin: 0;">
                                                    <input type="hidden" name="csrfToken" value="<c:out value='${not empty csrfToken ? csrfToken : sessionScope.csrfToken}' />">
                                                    <input type="hidden" name="settingKey" value="<c:out value='${setting.settingKey}' />">
                                                    <div style="display: flex; gap: 0.5rem; align-items: center; flex-wrap: wrap;">
                                                        <input type="text" id="setting_${setting.settingKey}" name="settingValue" value="<c:out value='${setting.settingValue}' />" class="form-control" autocomplete="off" style="font-family: var(--font-mono); font-size: var(--font-size-sm); min-width: 180px; max-width: 280px; padding: 0.4375rem 0.625rem;">
                                                        <button type="submit" class="btn btn-primary btn-sm" style="display: inline-flex; align-items: center; gap: 0.375rem;">
                                                            <span>💾</span> Save
                                                        </button>
                                                    </div>
                                                </form>
                                            </td>
                                            <td>
                                                <div style="font-size: var(--font-size-xs); line-height: 1.45;">
                                                    <div style="display: flex; align-items: center; gap: 0.375rem; flex-wrap: wrap;">
                                                        <span style="color: var(--text-secondary); font-weight: 500;">Modified by:</span>
                                                        <c:choose>
                                                            <c:when test="${not empty setting.updatedByName}">
                                                                <span style="font-weight: 600; color: var(--text-primary);"><c:out value="${setting.updatedByName}" /></span>
                                                                <c:if test="${not empty setting.updatedBy}">
                                                                    <span style="color: var(--text-muted); font-size: 0.7rem;">(#<c:out value="${setting.updatedBy}" />)</span>
                                                                </c:if>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge" style="font-size: 0.65rem; color: var(--text-muted);">System Seed</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                    <div style="display: flex; align-items: center; gap: 0.375rem; margin-top: 0.25rem; flex-wrap: wrap;">
                                                        <span style="color: var(--text-secondary); font-weight: 500;">Timestamp:</span>
                                                        <c:choose>
                                                            <c:when test="${not empty setting.updatedAt}">
                                                                <span style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--text-secondary);"><c:out value="${setting.updatedAt}" /></span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span style="color: var(--text-muted); font-style: italic;">Initial migration</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:if>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </c:if>

        <%-- Section 2: User Registration & Access Control --%>
        <c:if test="${accessCount > 0}">
            <div class="card" id="section-access" style="margin-bottom: 1.75rem;">
                <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
                    <div style="display: flex; align-items: center; gap: 0.75rem;">
                        <div style="width: 36px; height: 36px; border-radius: var(--radius-md); background-color: var(--brand-accent-light); color: var(--brand-accent); display: flex; align-items: center; justify-content: center; font-size: 1.125rem;">
                            🔐
                        </div>
                        <div>
                            <h2 class="card-title" style="font-size: var(--font-size-base);">User Registration &amp; Access Control</h2>
                            <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">
                                Public account registration toggles, onboarding authorization, and authentication switches
                            </div>
                        </div>
                    </div>
                    <span class="badge badge-info"><c:out value="${accessCount}" /> Setting<c:if test="${accessCount != 1}">s</c:if></span>
                </div>
                <div class="card-body" style="padding: 0;">
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th style="min-width: 220px; width: 32%;">Configuration Key &amp; Description</th>
                                    <th style="min-width: 320px; width: 44%;">Runtime Value &amp; Controls</th>
                                    <th style="min-width: 180px; width: 24%;">Audit History</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="setting" items="${settings}">
                                    <c:set var="k" value="${setting.settingKey}" />
                                    <c:if test="${k == 'allow_registration' || k.startsWith('allow_') || k.startsWith('auth_') || k.startsWith('reg_') || k.startsWith('login_') || k.startsWith('user_')}">
                                        <tr>
                                            <td>
                                                <div>
                                                    <span style="font-family: var(--font-mono); font-size: var(--font-size-xs); font-weight: 600; color: var(--brand-primary); background-color: var(--brand-accent-light); padding: 0.25rem 0.5rem; border-radius: var(--radius-sm); border: 1px solid var(--brand-accent-subtle); display: inline-block;">
                                                        <c:out value="${setting.settingKey}" />
                                                    </span>
                                                </div>
                                                <div style="color: var(--text-secondary); font-size: var(--font-size-sm); line-height: 1.45; margin-top: 0.375rem;">
                                                    <c:out value="${not empty setting.description ? setting.description : 'User registration and access policy parameter.'}" />
                                                </div>
                                                <div style="margin-top: 0.375rem; display: flex; gap: 0.35rem; align-items: center; flex-wrap: wrap;">
                                                    <c:set var="curVal" value="${setting.settingValue}" />
                                                    <c:choose>
                                                        <c:when test="${curVal == 'true' || curVal == 'TRUE' || curVal == 'True'}">
                                                            <span class="badge badge-success" style="font-size: 0.65rem;">Active (true)</span>
                                                        </c:when>
                                                        <c:when test="${curVal == 'false' || curVal == 'FALSE' || curVal == 'False'}">
                                                            <span class="badge badge-warning" style="font-size: 0.65rem;">Disabled (false)</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge" style="font-size: 0.65rem;">Custom Flag</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </td>
                                            <td>
                                                <form id="form_${setting.settingKey}" action="${pageContext.request.contextPath}/admin/settings" method="post" style="margin: 0;">
                                                    <input type="hidden" name="csrfToken" value="<c:out value='${not empty csrfToken ? csrfToken : sessionScope.csrfToken}' />">
                                                    <input type="hidden" name="settingKey" value="<c:out value='${setting.settingKey}' />">
                                                    <div style="display: flex; gap: 0.5rem; align-items: center; flex-wrap: wrap;">
                                                        <input type="text" id="setting_${setting.settingKey}" name="settingValue" value="<c:out value='${setting.settingValue}' />" class="form-control" autocomplete="off" style="font-family: var(--font-mono); font-size: var(--font-size-sm); min-width: 180px; max-width: 280px; padding: 0.4375rem 0.625rem;">
                                                        <button type="submit" class="btn btn-primary btn-sm" style="display: inline-flex; align-items: center; gap: 0.375rem;">
                                                            <span>💾</span> Save
                                                        </button>
                                                    </div>
                                                    <div style="display: flex; gap: 0.375rem; align-items: center; font-size: var(--font-size-xs); color: var(--text-muted); margin-top: 0.375rem; flex-wrap: wrap;">
                                                        <span style="font-weight: 500;">Quick presets:</span>
                                                        <button type="button" class="btn btn-secondary btn-sm" style="padding: 0.125rem 0.4rem; font-size: 0.7rem; line-height: 1.2;" onclick="document.getElementById('setting_${setting.settingKey}').value='true'">true</button>
                                                        <button type="button" class="btn btn-secondary btn-sm" style="padding: 0.125rem 0.4rem; font-size: 0.7rem; line-height: 1.2;" onclick="document.getElementById('setting_${setting.settingKey}').value='false'">false</button>
                                                        <span style="color: var(--text-muted); font-size: 0.7rem;">(Allowed: <code>true</code> / <code>false</code>)</span>
                                                    </div>
                                                </form>
                                            </td>
                                            <td>
                                                <div style="font-size: var(--font-size-xs); line-height: 1.45;">
                                                    <div style="display: flex; align-items: center; gap: 0.375rem; flex-wrap: wrap;">
                                                        <span style="color: var(--text-secondary); font-weight: 500;">Modified by:</span>
                                                        <c:choose>
                                                            <c:when test="${not empty setting.updatedByName}">
                                                                <span style="font-weight: 600; color: var(--text-primary);"><c:out value="${setting.updatedByName}" /></span>
                                                                <c:if test="${not empty setting.updatedBy}">
                                                                    <span style="color: var(--text-muted); font-size: 0.7rem;">(#<c:out value="${setting.updatedBy}" />)</span>
                                                                </c:if>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge" style="font-size: 0.65rem; color: var(--text-muted);">System Seed</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                    <div style="display: flex; align-items: center; gap: 0.375rem; margin-top: 0.25rem; flex-wrap: wrap;">
                                                        <span style="color: var(--text-secondary); font-weight: 500;">Timestamp:</span>
                                                        <c:choose>
                                                            <c:when test="${not empty setting.updatedAt}">
                                                                <span style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--text-secondary);"><c:out value="${setting.updatedAt}" /></span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span style="color: var(--text-muted); font-style: italic;">Initial migration</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:if>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </c:if>

        <%-- Section 3: System Operations & Maintenance --%>
        <c:if test="${opsCount > 0}">
            <div class="card" id="section-ops" style="margin-bottom: 1.75rem;">
                <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
                    <div style="display: flex; align-items: center; gap: 0.75rem;">
                        <div style="width: 36px; height: 36px; border-radius: var(--radius-md); background-color: var(--brand-accent-light); color: var(--brand-accent); display: flex; align-items: center; justify-content: center; font-size: 1.125rem;">
                            🛠️
                        </div>
                        <div>
                            <h2 class="card-title" style="font-size: var(--font-size-base);">System Operations &amp; Maintenance Mode</h2>
                            <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">
                                Platform availability controls, scheduled maintenance mode, and operational downtime toggles
                            </div>
                        </div>
                    </div>
                    <span class="badge badge-info"><c:out value="${opsCount}" /> Setting<c:if test="${opsCount != 1}">s</c:if></span>
                </div>
                <div class="card-body" style="padding: 0;">
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th style="min-width: 220px; width: 32%;">Configuration Key &amp; Description</th>
                                    <th style="min-width: 320px; width: 44%;">Runtime Value &amp; Controls</th>
                                    <th style="min-width: 180px; width: 24%;">Audit History</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="setting" items="${settings}">
                                    <c:set var="k" value="${setting.settingKey}" />
                                    <c:if test="${k == 'maintenance_mode' || k.startsWith('maintenance_') || k.startsWith('system_') || k.startsWith('cache_') || k.startsWith('debug_')}">
                                        <tr>
                                            <td>
                                                <div>
                                                    <span style="font-family: var(--font-mono); font-size: var(--font-size-xs); font-weight: 600; color: var(--brand-primary); background-color: var(--brand-accent-light); padding: 0.25rem 0.5rem; border-radius: var(--radius-sm); border: 1px solid var(--brand-accent-subtle); display: inline-block;">
                                                        <c:out value="${setting.settingKey}" />
                                                    </span>
                                                </div>
                                                <div style="color: var(--text-secondary); font-size: var(--font-size-sm); line-height: 1.45; margin-top: 0.375rem;">
                                                    <c:out value="${not empty setting.description ? setting.description : 'System operations and availability parameter.'}" />
                                                </div>
                                                <div style="margin-top: 0.375rem; display: flex; gap: 0.35rem; align-items: center; flex-wrap: wrap;">
                                                    <c:set var="curVal" value="${setting.settingValue}" />
                                                    <c:choose>
                                                        <c:when test="${curVal == 'true' || curVal == 'TRUE' || curVal == 'True'}">
                                                            <span class="badge badge-danger" style="font-size: 0.65rem;">Maintenance Active (true)</span>
                                                        </c:when>
                                                        <c:when test="${curVal == 'false' || curVal == 'FALSE' || curVal == 'False'}">
                                                            <span class="badge badge-success" style="font-size: 0.65rem;">Normal Operation (false)</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge" style="font-size: 0.65rem;">Custom State</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </td>
                                            <td>
                                                <form id="form_${setting.settingKey}" action="${pageContext.request.contextPath}/admin/settings" method="post" style="margin: 0;">
                                                    <input type="hidden" name="csrfToken" value="<c:out value='${not empty csrfToken ? csrfToken : sessionScope.csrfToken}' />">
                                                    <input type="hidden" name="settingKey" value="<c:out value='${setting.settingKey}' />">
                                                    <div style="display: flex; gap: 0.5rem; align-items: center; flex-wrap: wrap;">
                                                        <input type="text" id="setting_${setting.settingKey}" name="settingValue" value="<c:out value='${setting.settingValue}' />" class="form-control" autocomplete="off" style="font-family: var(--font-mono); font-size: var(--font-size-sm); min-width: 180px; max-width: 280px; padding: 0.4375rem 0.625rem;">
                                                        <button type="submit" class="btn btn-primary btn-sm" style="display: inline-flex; align-items: center; gap: 0.375rem;">
                                                            <span>💾</span> Save
                                                        </button>
                                                    </div>
                                                    <div style="display: flex; gap: 0.375rem; align-items: center; font-size: var(--font-size-xs); color: var(--text-muted); margin-top: 0.375rem; flex-wrap: wrap;">
                                                        <span style="font-weight: 500;">Quick presets:</span>
                                                        <button type="button" class="btn btn-secondary btn-sm" style="padding: 0.125rem 0.4rem; font-size: 0.7rem; line-height: 1.2;" onclick="document.getElementById('setting_${setting.settingKey}').value='true'">true</button>
                                                        <button type="button" class="btn btn-secondary btn-sm" style="padding: 0.125rem 0.4rem; font-size: 0.7rem; line-height: 1.2;" onclick="document.getElementById('setting_${setting.settingKey}').value='false'">false</button>
                                                        <span style="color: var(--text-muted); font-size: 0.7rem;">(Allowed: <code>true</code> / <code>false</code>)</span>
                                                    </div>
                                                </form>
                                            </td>
                                            <td>
                                                <div style="font-size: var(--font-size-xs); line-height: 1.45;">
                                                    <div style="display: flex; align-items: center; gap: 0.375rem; flex-wrap: wrap;">
                                                        <span style="color: var(--text-secondary); font-weight: 500;">Modified by:</span>
                                                        <c:choose>
                                                            <c:when test="${not empty setting.updatedByName}">
                                                                <span style="font-weight: 600; color: var(--text-primary);"><c:out value="${setting.updatedByName}" /></span>
                                                                <c:if test="${not empty setting.updatedBy}">
                                                                    <span style="color: var(--text-muted); font-size: 0.7rem;">(#<c:out value="${setting.updatedBy}" />)</span>
                                                                </c:if>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge" style="font-size: 0.65rem; color: var(--text-muted);">System Seed</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                    <div style="display: flex; align-items: center; gap: 0.375rem; margin-top: 0.25rem; flex-wrap: wrap;">
                                                        <span style="color: var(--text-secondary); font-weight: 500;">Timestamp:</span>
                                                        <c:choose>
                                                            <c:when test="${not empty setting.updatedAt}">
                                                                <span style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--text-secondary);"><c:out value="${setting.updatedAt}" /></span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span style="color: var(--text-muted); font-style: italic;">Initial migration</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:if>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </c:if>

        <%-- Section 4: Curriculum & Learning Defaults --%>
        <c:if test="${curriculumCount > 0}">
            <div class="card" id="section-curriculum" style="margin-bottom: 1.75rem;">
                <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
                    <div style="display: flex; align-items: center; gap: 0.75rem;">
                        <div style="width: 36px; height: 36px; border-radius: var(--radius-md); background-color: var(--brand-accent-light); color: var(--brand-accent); display: flex; align-items: center; justify-content: center; font-size: 1.125rem;">
                            📚
                        </div>
                        <div>
                            <h2 class="card-title" style="font-size: var(--font-size-base);">Curriculum &amp; Exercise Defaults</h2>
                            <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">
                                Default coding problem difficulty, curriculum parameters, and student evaluation baselines
                            </div>
                        </div>
                    </div>
                    <span class="badge badge-info"><c:out value="${curriculumCount}" /> Setting<c:if test="${curriculumCount != 1}">s</c:if></span>
                </div>
                <div class="card-body" style="padding: 0;">
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th style="min-width: 220px; width: 32%;">Configuration Key &amp; Description</th>
                                    <th style="min-width: 320px; width: 44%;">Runtime Value &amp; Controls</th>
                                    <th style="min-width: 180px; width: 24%;">Audit History</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="setting" items="${settings}">
                                    <c:set var="k" value="${setting.settingKey}" />
                                    <c:if test="${k == 'default_exercise_difficulty' || k.startsWith('default_') || k.startsWith('exercise_') || k.startsWith('difficulty') || k.startsWith('challenge_') || k.startsWith('submission_')}">
                                        <tr>
                                            <td>
                                                <div>
                                                    <span style="font-family: var(--font-mono); font-size: var(--font-size-xs); font-weight: 600; color: var(--brand-primary); background-color: var(--brand-accent-light); padding: 0.25rem 0.5rem; border-radius: var(--radius-sm); border: 1px solid var(--brand-accent-subtle); display: inline-block;">
                                                        <c:out value="${setting.settingKey}" />
                                                    </span>
                                                </div>
                                                <div style="color: var(--text-secondary); font-size: var(--font-size-sm); line-height: 1.45; margin-top: 0.375rem;">
                                                    <c:out value="${not empty setting.description ? setting.description : 'Curriculum default parameter.'}" />
                                                </div>
                                                <div style="margin-top: 0.375rem; display: flex; gap: 0.35rem; align-items: center; flex-wrap: wrap;">
                                                    <c:set var="curVal" value="${setting.settingValue}" />
                                                    <c:choose>
                                                        <c:when test="${curVal == 'EASY' || curVal == 'Easy'}">
                                                            <span class="badge badge-success" style="font-size: 0.65rem;">EASY</span>
                                                        </c:when>
                                                        <c:when test="${curVal == 'MEDIUM' || curVal == 'Medium'}">
                                                            <span class="badge badge-warning" style="font-size: 0.65rem;">MEDIUM</span>
                                                        </c:when>
                                                        <c:when test="${curVal == 'HARD' || curVal == 'Hard'}">
                                                            <span class="badge badge-danger" style="font-size: 0.65rem;">HARD</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge" style="font-size: 0.65rem;"><c:out value="${curVal}" /></span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </td>
                                            <td>
                                                <form id="form_${setting.settingKey}" action="${pageContext.request.contextPath}/admin/settings" method="post" style="margin: 0;">
                                                    <input type="hidden" name="csrfToken" value="<c:out value='${not empty csrfToken ? csrfToken : sessionScope.csrfToken}' />">
                                                    <input type="hidden" name="settingKey" value="<c:out value='${setting.settingKey}' />">
                                                    <div style="display: flex; gap: 0.5rem; align-items: center; flex-wrap: wrap;">
                                                        <input type="text" id="setting_${setting.settingKey}" name="settingValue" value="<c:out value='${setting.settingValue}' />" class="form-control" autocomplete="off" style="font-family: var(--font-mono); font-size: var(--font-size-sm); min-width: 180px; max-width: 280px; padding: 0.4375rem 0.625rem;">
                                                        <button type="submit" class="btn btn-primary btn-sm" style="display: inline-flex; align-items: center; gap: 0.375rem;">
                                                            <span>💾</span> Save
                                                        </button>
                                                    </div>
                                                    <div style="display: flex; gap: 0.375rem; align-items: center; font-size: var(--font-size-xs); color: var(--text-muted); margin-top: 0.375rem; flex-wrap: wrap;">
                                                        <span style="font-weight: 500;">Quick presets:</span>
                                                        <button type="button" class="btn btn-secondary btn-sm" style="padding: 0.125rem 0.4rem; font-size: 0.7rem; line-height: 1.2;" onclick="document.getElementById('setting_${setting.settingKey}').value='EASY'">EASY</button>
                                                        <button type="button" class="btn btn-secondary btn-sm" style="padding: 0.125rem 0.4rem; font-size: 0.7rem; line-height: 1.2;" onclick="document.getElementById('setting_${setting.settingKey}').value='MEDIUM'">MEDIUM</button>
                                                        <button type="button" class="btn btn-secondary btn-sm" style="padding: 0.125rem 0.4rem; font-size: 0.7rem; line-height: 1.2;" onclick="document.getElementById('setting_${setting.settingKey}').value='HARD'">HARD</button>
                                                        <span style="color: var(--text-muted); font-size: 0.7rem;">(Allowed: <code>EASY</code>, <code>MEDIUM</code>, <code>HARD</code>)</span>
                                                    </div>
                                                </form>
                                            </td>
                                            <td>
                                                <div style="font-size: var(--font-size-xs); line-height: 1.45;">
                                                    <div style="display: flex; align-items: center; gap: 0.375rem; flex-wrap: wrap;">
                                                        <span style="color: var(--text-secondary); font-weight: 500;">Modified by:</span>
                                                        <c:choose>
                                                            <c:when test="${not empty setting.updatedByName}">
                                                                <span style="font-weight: 600; color: var(--text-primary);"><c:out value="${setting.updatedByName}" /></span>
                                                                <c:if test="${not empty setting.updatedBy}">
                                                                    <span style="color: var(--text-muted); font-size: 0.7rem;">(#<c:out value="${setting.updatedBy}" />)</span>
                                                                </c:if>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge" style="font-size: 0.65rem; color: var(--text-muted);">System Seed</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                    <div style="display: flex; align-items: center; gap: 0.375rem; margin-top: 0.25rem; flex-wrap: wrap;">
                                                        <span style="color: var(--text-secondary); font-weight: 500;">Timestamp:</span>
                                                        <c:choose>
                                                            <c:when test="${not empty setting.updatedAt}">
                                                                <span style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--text-secondary);"><c:out value="${setting.updatedAt}" /></span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span style="color: var(--text-muted); font-style: italic;">Initial migration</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:if>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </c:if>

        <%-- Section 5: Additional / Custom Platform Properties --%>
        <c:if test="${otherCount > 0}">
            <div class="card" id="section-other" style="margin-bottom: 1.75rem;">
                <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
                    <div style="display: flex; align-items: center; gap: 0.75rem;">
                        <div style="width: 36px; height: 36px; border-radius: var(--radius-md); background-color: var(--brand-accent-light); color: var(--brand-accent); display: flex; align-items: center; justify-content: center; font-size: 1.125rem;">
                            ⚙️
                        </div>
                        <div>
                            <h2 class="card-title" style="font-size: var(--font-size-base);">Additional Platform Properties</h2>
                            <div style="font-size: var(--font-size-xs); color: var(--text-secondary); margin-top: 0.125rem;">
                                Custom runtime parameters, miscellaneous attributes, and advanced configuration keys
                            </div>
                        </div>
                    </div>
                    <span class="badge badge-info"><c:out value="${otherCount}" /> Setting<c:if test="${otherCount != 1}">s</c:if></span>
                </div>
                <div class="card-body" style="padding: 0;">
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th style="min-width: 220px; width: 32%;">Configuration Key &amp; Description</th>
                                    <th style="min-width: 320px; width: 44%;">Runtime Value &amp; Controls</th>
                                    <th style="min-width: 180px; width: 24%;">Audit History</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="setting" items="${settings}">
                                    <c:set var="k" value="${setting.settingKey}" />
                                    <%-- Check that it does not belong to any of the 4 standard categories --%>
                                    <c:if test="${k != 'platform_name' && !k.startsWith('platform_') && !k.startsWith('site_') && !k.startsWith('app_')
                                                  && k != 'allow_registration' && !k.startsWith('allow_') && !k.startsWith('auth_') && !k.startsWith('reg_') && !k.startsWith('login_') && !k.startsWith('user_')
                                                  && k != 'maintenance_mode' && !k.startsWith('maintenance_') && !k.startsWith('system_') && !k.startsWith('cache_') && !k.startsWith('debug_')
                                                  && k != 'default_exercise_difficulty' && !k.startsWith('default_') && !k.startsWith('exercise_') && !k.startsWith('difficulty') && !k.startsWith('challenge_') && !k.startsWith('submission_')}">
                                        <tr>
                                            <td>
                                                <div>
                                                    <span style="font-family: var(--font-mono); font-size: var(--font-size-xs); font-weight: 600; color: var(--brand-primary); background-color: var(--brand-accent-light); padding: 0.25rem 0.5rem; border-radius: var(--radius-sm); border: 1px solid var(--brand-accent-subtle); display: inline-block;">
                                                        <c:out value="${setting.settingKey}" />
                                                    </span>
                                                </div>
                                                <div style="color: var(--text-secondary); font-size: var(--font-size-sm); line-height: 1.45; margin-top: 0.375rem;">
                                                    <c:out value="${not empty setting.description ? setting.description : 'Custom configuration property.'}" />
                                                </div>
                                                <div style="margin-top: 0.375rem; display: flex; gap: 0.35rem; align-items: center; flex-wrap: wrap;">
                                                    <span class="badge" style="font-size: 0.65rem;">Custom</span>
                                                </div>
                                            </td>
                                            <td>
                                                <form id="form_${setting.settingKey}" action="${pageContext.request.contextPath}/admin/settings" method="post" style="margin: 0;">
                                                    <input type="hidden" name="csrfToken" value="<c:out value='${not empty csrfToken ? csrfToken : sessionScope.csrfToken}' />">
                                                    <input type="hidden" name="settingKey" value="<c:out value='${setting.settingKey}' />">
                                                    <div style="display: flex; gap: 0.5rem; align-items: center; flex-wrap: wrap;">
                                                        <input type="text" id="setting_${setting.settingKey}" name="settingValue" value="<c:out value='${setting.settingValue}' />" class="form-control" autocomplete="off" style="font-family: var(--font-mono); font-size: var(--font-size-sm); min-width: 180px; max-width: 280px; padding: 0.4375rem 0.625rem;">
                                                        <button type="submit" class="btn btn-primary btn-sm" style="display: inline-flex; align-items: center; gap: 0.375rem;">
                                                            <span>💾</span> Save
                                                        </button>
                                                    </div>
                                                </form>
                                            </td>
                                            <td>
                                                <div style="font-size: var(--font-size-xs); line-height: 1.45;">
                                                    <div style="display: flex; align-items: center; gap: 0.375rem; flex-wrap: wrap;">
                                                        <span style="color: var(--text-secondary); font-weight: 500;">Modified by:</span>
                                                        <c:choose>
                                                            <c:when test="${not empty setting.updatedByName}">
                                                                <span style="font-weight: 600; color: var(--text-primary);"><c:out value="${setting.updatedByName}" /></span>
                                                                <c:if test="${not empty setting.updatedBy}">
                                                                    <span style="color: var(--text-muted); font-size: 0.7rem;">(#<c:out value="${setting.updatedBy}" />)</span>
                                                                </c:if>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge" style="font-size: 0.65rem; color: var(--text-muted);">System Seed</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                    <div style="display: flex; align-items: center; gap: 0.375rem; margin-top: 0.25rem; flex-wrap: wrap;">
                                                        <span style="color: var(--text-secondary); font-weight: 500;">Timestamp:</span>
                                                        <c:choose>
                                                            <c:when test="${not empty setting.updatedAt}">
                                                                <span style="font-family: var(--font-mono); font-size: 0.75rem; color: var(--text-secondary);"><c:out value="${setting.updatedAt}" /></span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span style="color: var(--text-muted); font-style: italic;">Initial migration</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:if>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </c:if>
    </c:when>
    <c:otherwise>
        <div class="card">
            <div class="card-body" style="padding: 3.5rem 1.5rem; text-align: center; color: var(--text-secondary);">
                <div style="font-size: 2.5rem; margin-bottom: 0.75rem;">⚙️</div>
                <h3 style="font-size: var(--font-size-base); font-weight: 600; margin-bottom: 0.25rem; color: var(--text-primary);">No System Settings Found</h3>
                <p style="font-size: var(--font-size-sm); color: var(--text-muted); margin-bottom: 0;">There are currently no platform configuration parameters registered in the database.</p>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<%-- Platform Settings Reference & Guidelines Card --%>
<div class="card" style="margin-top: 2rem; margin-bottom: 2rem; background-color: var(--bg-surface);">
    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem;">
        <div style="display: flex; align-items: center; gap: 0.625rem;">
            <span style="font-size: 1.125rem;">📖</span>
            <h2 class="card-title" style="font-size: var(--font-size-base);">Standard Configuration Parameters Reference</h2>
        </div>
        <span class="badge badge-info" style="font-size: 0.7rem;">Documentation</span>
    </div>
    <div class="card-body">
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 1rem;">
            <div style="padding: 1rem; border-radius: var(--radius-md); background: var(--bg-subtle); border: 1px solid var(--border-subtle);">
                <div style="display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.375rem;">
                    <span style="font-size: 1rem;">🌐</span>
                    <strong style="font-family: var(--font-mono); font-size: var(--font-size-xs); color: var(--brand-primary);">platform_name</strong>
                </div>
                <div style="font-size: var(--font-size-xs); color: var(--text-secondary); line-height: 1.45;">
                    The global platform display name rendered across headers, navigation, and page metadata.
                </div>
                <div style="font-size: 0.7rem; color: var(--text-muted); margin-top: 0.5rem;">Type: <code>Text String</code></div>
            </div>
            <div style="padding: 1rem; border-radius: var(--radius-md); background: var(--bg-subtle); border: 1px solid var(--border-subtle);">
                <div style="display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.375rem;">
                    <span style="font-size: 1rem;">🔐</span>
                    <strong style="font-family: var(--font-mono); font-size: var(--font-size-xs); color: var(--brand-primary);">allow_registration</strong>
                </div>
                <div style="font-size: var(--font-size-xs); color: var(--text-secondary); line-height: 1.45;">
                    Governs whether public visitors can register new learner accounts. When false, registration is disabled.
                </div>
                <div style="font-size: 0.7rem; color: var(--text-muted); margin-top: 0.5rem;">Allowed: <code>true</code> | <code>false</code></div>
            </div>
            <div style="padding: 1rem; border-radius: var(--radius-md); background: var(--bg-subtle); border: 1px solid var(--border-subtle);">
                <div style="display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.375rem;">
                    <span style="font-size: 1rem;">🛠️</span>
                    <strong style="font-family: var(--font-mono); font-size: var(--font-size-xs); color: var(--brand-primary);">maintenance_mode</strong>
                </div>
                <div style="font-size: var(--font-size-xs); color: var(--text-secondary); line-height: 1.45;">
                    Controls the global system maintenance flag for scheduled maintenance windows and updates.
                </div>
                <div style="font-size: 0.7rem; color: var(--text-muted); margin-top: 0.5rem;">Allowed: <code>true</code> | <code>false</code></div>
            </div>
            <div style="padding: 1rem; border-radius: var(--radius-md); background: var(--bg-subtle); border: 1px solid var(--border-subtle);">
                <div style="display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.375rem;">
                    <span style="font-size: 1rem;">📚</span>
                    <strong style="font-family: var(--font-mono); font-size: var(--font-size-xs); color: var(--brand-primary);">default_exercise_difficulty</strong>
                </div>
                <div style="font-size: var(--font-size-xs); color: var(--text-secondary); line-height: 1.45;">
                    Default difficulty level pre-selected for instructors when creating new curriculum exercises.
                </div>
                <div style="font-size: 0.7rem; color: var(--text-muted); margin-top: 0.5rem;">Allowed: <code>EASY</code> | <code>MEDIUM</code> | <code>HARD</code></div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/includes/footer.jsp" />
