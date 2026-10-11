<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${not empty param.pageTitle ? param.pageTitle : 'Python Coding Platform'}" /></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<c:if test="${param.standalone != 'true'}">
    <div class="sidebar-overlay" id="sidebarOverlay"></div>
    <div class="app-wrapper">
</c:if>
