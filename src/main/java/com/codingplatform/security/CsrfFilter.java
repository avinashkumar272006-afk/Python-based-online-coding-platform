package com.codingplatform.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Filter that enforces CSRF token validation for all state-changing requests (POST, etc.).
 * Ensures safe methods pass through and exposes CSRF tokens to request scope for JSP rendering.
 * Supports anonymous-session tokens for public authentication and registration forms while
 * strictly enforcing authenticated-session tokens for all protected workflows.
 */
public class CsrfFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(CsrfFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No special configuration needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        String method = httpRequest.getMethod();
        boolean isSafeMethod = "GET".equalsIgnoreCase(method)
                || "HEAD".equalsIgnoreCase(method)
                || "OPTIONS".equalsIgnoreCase(method);

        boolean isPublicFormRoute = path.equals("/login")
                || path.equals("/login.jsp")
                || path.equals("/register")
                || path.equals("/register.jsp");

        if (isSafeMethod) {
            // For safe requests, ensure an anonymous session exists for public form pages so a token can be bound
            HttpSession session = httpRequest.getSession(false);
            if (session == null && isPublicFormRoute) {
                session = httpRequest.getSession(true);
            }
            if (session != null) {
                String token = CsrfUtil.getToken(session);
                httpRequest.setAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR, token);
            }
            chain.doFilter(request, response);
            return;
        }

        // State-changing requests (POST, etc.) - no routes are exempt from CSRF validation

        // Reject token if passed via URL query parameter
        if (CsrfUtil.isTokenSuppliedInQueryString(httpRequest)) {
            LOGGER.warning("CSRF validation rejected: token supplied via URL query parameter for URI: " + path);
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token must not be supplied via query parameter.");
            return;
        }

        // Validate presence of an active session
        HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            LOGGER.warning("CSRF validation rejected: no session present for state-changing endpoint: " + path);
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token");
            return;
        }

        // For non-public routes, require an authenticated user (userId present in session)
        if (!isPublicFormRoute && session.getAttribute("userId") == null) {
            LOGGER.warning("CSRF validation rejected: unauthenticated request to state-changing endpoint: " + path);
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token");
            return;
        }

        // Validate token against session token
        if (!CsrfUtil.isValidToken(httpRequest)) {
            LOGGER.warning("CSRF validation rejected: missing or mismatched token for endpoint: " + path);
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token");
            return;
        }

        // Expose token to request attribute so downstream forwards/JSPs retain it
        httpRequest.setAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR, CsrfUtil.getToken(session));

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // No resources to clean up
    }
}
