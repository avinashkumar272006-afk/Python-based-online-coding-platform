package com.codingplatform.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CsrfFilterTest {

    private CsrfFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;
    private HttpSession session;

    @BeforeEach
    public void setUp() {
        filter = new CsrfFilter();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
        session = mock(HttpSession.class);

        when(request.getContextPath()).thenReturn("");
    }

    @Test
    public void testSafeGetRequest_PassesThrough_AndPopulatesTokenAttribute() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/student/exercises");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn("existing-session-token");

        filter.doFilter(request, response, chain);

        verify(request).setAttribute(eq(CsrfUtil.CSRF_TOKEN_SESSION_ATTR), eq("existing-session-token"));
        verify(chain).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    public void testSafeHeadAndOptionsRequests_PassThrough() throws Exception {
        when(request.getMethod()).thenReturn("HEAD");
        when(request.getRequestURI()).thenReturn("/home");
        when(request.getSession(false)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);

        reset(chain);
        when(request.getMethod()).thenReturn("OPTIONS");
        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    public void testPostLogin_MissingToken_RejectedWith403() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/login");
        when(request.getQueryString()).thenReturn(null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(null); // Anonymous session
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn("anonymous-token-123");
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(null);
        when(request.getHeader(CsrfUtil.CSRF_HEADER_NAME)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), contains("Invalid or missing CSRF token"));
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testPostLogin_InvalidToken_RejectedWith403() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/login");
        when(request.getQueryString()).thenReturn(null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(null); // Anonymous session
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn("anonymous-token-123");
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn("attacker-token-999");

        filter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), contains("Invalid or missing CSRF token"));
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testPostLogin_NoSession_RejectedWith403() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/login");
        when(request.getQueryString()).thenReturn(null);
        when(request.getSession(false)).thenReturn(null); // No session

        filter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), contains("Invalid or missing CSRF token"));
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testPostLogin_ValidAnonymousToken_Allowed() throws Exception {
        String token = "valid-anon-token-xyz";
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/login");
        when(request.getQueryString()).thenReturn(null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(null); // Anonymous session!
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(token);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(token);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    public void testPostRegister_MissingToken_RejectedWith403() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/register");
        when(request.getQueryString()).thenReturn(null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(null); // Anonymous session
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn("anonymous-token-456");
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(null);
        when(request.getHeader(CsrfUtil.CSRF_HEADER_NAME)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), contains("Invalid or missing CSRF token"));
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testPostRegister_InvalidToken_RejectedWith403() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/register");
        when(request.getQueryString()).thenReturn(null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(null); // Anonymous session
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn("anonymous-token-456");
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn("attacker-token-999");

        filter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), contains("Invalid or missing CSRF token"));
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testPostRegister_NoSession_RejectedWith403() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/register");
        when(request.getQueryString()).thenReturn(null);
        when(request.getSession(false)).thenReturn(null); // No session

        filter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), contains("Invalid or missing CSRF token"));
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testPostRegister_ValidAnonymousToken_Allowed() throws Exception {
        String token = "valid-anon-register-token";
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/register");
        when(request.getQueryString()).thenReturn(null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(null); // Anonymous session!
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(token);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(token);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    public void testPublicGetLogin_CreatesSessionAndExposesToken() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/login");
        when(request.getSession(false)).thenReturn(null);
        when(request.getSession(true)).thenReturn(session);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn("new-anon-token");

        filter.doFilter(request, response, chain);

        verify(request).getSession(true);
        verify(request).setAttribute(eq(CsrfUtil.CSRF_TOKEN_SESSION_ATTR), eq("new-anon-token"));
        verify(chain).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    public void testPublicGetRegister_CreatesSessionAndExposesToken() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/register.jsp");
        when(request.getSession(false)).thenReturn(null);
        when(request.getSession(true)).thenReturn(session);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn("new-anon-reg-token");

        filter.doFilter(request, response, chain);

        verify(request).getSession(true);
        verify(request).setAttribute(eq(CsrfUtil.CSRF_TOKEN_SESSION_ATTR), eq("new-anon-reg-token"));
        verify(chain).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    public void testPostLogout_MissingToken_RejectedWith403() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/logout");
        when(request.getQueryString()).thenReturn(null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn("valid-session-token");
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(null);
        when(request.getHeader(CsrfUtil.CSRF_HEADER_NAME)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), contains("Invalid or missing CSRF token"));
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testPostLogout_InvalidToken_RejectedWith403() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/logout");
        when(request.getQueryString()).thenReturn(null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn("valid-session-token");
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn("attacker-forged-token");

        filter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), contains("Invalid or missing CSRF token"));
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testPostLogout_ValidToken_Allowed() throws Exception {
        String token = "valid-logout-token";
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/logout");
        when(request.getQueryString()).thenReturn(null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(token);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(token);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    public void testGetLogout_PassesThroughAsSafeMethod() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/logout");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn("session-token");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request).setAttribute(eq(CsrfUtil.CSRF_TOKEN_SESSION_ATTR), eq("session-token"));
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    public void testAuthenticatedPost_ValidToken_Allowed() throws Exception {
        String token = "valid-session-token-xyz123";
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/student/submit");
        when(request.getQueryString()).thenReturn(null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(token);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(token);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    public void testAuthenticatedPost_ValidTokenViaHeader_Allowed() throws Exception {
        String token = "valid-session-token-xyz123";
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/messages");
        when(request.getQueryString()).thenReturn(null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(token);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(null);
        when(request.getHeader(CsrfUtil.CSRF_HEADER_NAME)).thenReturn(token);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    public void testAuthenticatedPost_MissingToken_RejectedWith403() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/instructor/exercises");
        when(request.getQueryString()).thenReturn(null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(10);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn("session-expected-token");
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(null);
        when(request.getHeader(CsrfUtil.CSRF_HEADER_NAME)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), contains("Invalid or missing CSRF token"));
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testAuthenticatedPost_IncorrectToken_RejectedWith403() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/instructor/exercises");
        when(request.getQueryString()).thenReturn(null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(10);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn("session-expected-token");
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn("attacker-forged-token");

        filter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), contains("Invalid or missing CSRF token"));
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testAuthenticatedPost_TokenFromAnotherSession_RejectedWith403() throws Exception {
        String sessionAToken = "session-a-token-11111111111111";
        String sessionBToken = "session-b-token-22222222222222";

        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/admin/users");
        when(request.getQueryString()).thenReturn(null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(100);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(sessionAToken);
        // Attacker provides valid token from session B instead of session A
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(sessionBToken);

        filter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), contains("Invalid or missing CSRF token"));
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testAuthenticatedPost_TokenSuppliedInQueryString_RejectedWith403() throws Exception {
        String token = "valid-token-33333333333333";

        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/student/submit");
        when(request.getQueryString()).thenReturn("csrfToken=" + token);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(token);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(token);

        filter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), contains("CSRF token must not be supplied via query parameter"));
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testUnauthenticatedPost_ProtectedEndpoint_RejectedWith403() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/profile");
        when(request.getSession(false)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), contains("Invalid or missing CSRF token"));
        verify(chain, never()).doFilter(request, response);
    }
}
