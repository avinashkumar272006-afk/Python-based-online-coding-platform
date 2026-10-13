package com.codingplatform.servlet;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());
        
        boolean isStudentPath = path.startsWith("/student/");
        boolean isInstructorPath = path.startsWith("/instructor/");
        boolean isAdminPath = path.startsWith("/admin/");
        boolean isMessagesPath = path.equals("/messages") || path.startsWith("/messages/");
        boolean isProfilePath = path.equals("/profile") || path.equals("/profile.jsp");
        
        boolean isProtectedPath = isStudentPath || isInstructorPath || isAdminPath || isMessagesPath || isProfilePath;
        
        if (isProtectedPath) {
            HttpSession session = httpRequest.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                httpResponse.sendRedirect(contextPath + "/login.jsp");
                return;
            }
            
            String role = (String) session.getAttribute("role");
            if (isStudentPath && !"STUDENT".equals(role)) {
                httpResponse.sendRedirect(contextPath + "/login.jsp");
                return;
            }
            if (isInstructorPath && !"INSTRUCTOR".equals(role)) {
                httpResponse.sendRedirect(contextPath + "/login.jsp");
                return;
            }
            if (isAdminPath && !"ADMIN".equals(role)) {
                httpResponse.sendRedirect(contextPath + "/login.jsp");
                return;
            }
            if (isMessagesPath && !"STUDENT".equals(role) && !"INSTRUCTOR".equals(role)) {
                httpResponse.sendRedirect(contextPath + "/login.jsp");
                return;
            }
        }
        
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
