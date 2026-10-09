<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.codingplatform.model.SystemSetting" %>
<html>
<head>
    <title>System Settings - Python Coding Platform</title>
</head>
<body>
    <h2>System Settings</h2>
    
    <% String successMsg = (String) session.getAttribute("successMessage");
       if (successMsg != null) { %>
        <p style="color: green;"><%= successMsg %></p>
    <%     session.removeAttribute("successMessage");
       } %>
       
    <% String errorMsg = (String) session.getAttribute("errorMessage");
       if (errorMsg != null) { %>
        <p style="color: red;"><%= errorMsg %></p>
    <%     session.removeAttribute("errorMessage");
       } %>

    <table border="1" cellpadding="10">
        <tr>
            <th>Setting Key</th>
            <th>Description</th>
            <th>Current Value</th>
            <th>Last Updated By</th>
            <th>Action</th>
        </tr>
        <% 
            List<SystemSetting> settings = (List<SystemSetting>) request.getAttribute("settings");
            if (settings != null && !settings.isEmpty()) {
                int index = 0;
                for (SystemSetting setting : settings) {
                    index++;
        %>
        <tr>
            <td>
                <%= setting.getSettingKey() %>
            </td>
            <td><%= setting.getDescription() %></td>
            <td>
                <form id="form_<%= index %>" action="<%= request.getContextPath() %>/admin/settings" method="post" style="margin: 0;">
                    <input type="hidden" name="settingKey" value="<%= setting.getSettingKey() %>">
                    <input type="text" name="settingValue" value="<%= setting.getSettingValue() != null ? setting.getSettingValue() : "" %>">
                </form>
            </td>
            <td><%= setting.getUpdatedByName() != null ? setting.getUpdatedByName() : "System" %></td>
            <td><button type="submit" form="form_<%= index %>">Save</button></td>
        </tr>
        <%
                }
            } else {
        %>
        <tr>
            <td colspan="5">No settings found.</td>
        </tr>
        <% } %>
    </table>
    
    <br/>
    <a href="<%= request.getContextPath() %>/admin/dashboard">Back to Dashboard</a>
</body>
</html>
