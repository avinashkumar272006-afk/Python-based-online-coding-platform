package com.codingplatform.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CsrfUtilTest {

    private HttpSession session;
    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        session = mock(HttpSession.class);
        request = mock(HttpServletRequest.class);
    }

    @Test
    public void testGenerateToken_UnpredictableAndSecure() {
        Set<String> tokens = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            String token = CsrfUtil.generateToken();
            assertNotNull(token);
            assertTrue(token.length() >= 40, "Token length should be at least 40 characters for 32 bytes base64");
            assertFalse(tokens.contains(token), "Token should be unique and unpredictable");
            tokens.add(token);
        }
    }

    @Test
    public void testGetToken_GeneratesWhenAbsent() {
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(null);

        String token = CsrfUtil.getToken(session);

        assertNotNull(token);
        verify(session).setAttribute(eq(CsrfUtil.CSRF_TOKEN_SESSION_ATTR), eq(token));
    }

    @Test
    public void testGetToken_ReturnsExistingWhenPresent() {
        String existingToken = "existing-valid-csrf-token";
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(existingToken);

        String token = CsrfUtil.getToken(session);

        assertEquals(existingToken, token);
        verify(session, never()).setAttribute(anyString(), anyString());
    }

    @Test
    public void testGetToken_NullSession_ReturnsNull() {
        assertNull(CsrfUtil.getToken(null));
    }

    @Test
    public void testRefreshToken_ForcesNewToken() {
        String token = CsrfUtil.refreshToken(session);

        assertNotNull(token);
        verify(session).setAttribute(eq(CsrfUtil.CSRF_TOKEN_SESSION_ATTR), eq(token));
    }

    @Test
    public void testClearToken_RemovesSessionAttribute() {
        CsrfUtil.clearToken(session);

        verify(session).removeAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR);
    }

    @Test
    public void testIsTokenSuppliedInQueryString_DetectsQueryParam() {
        when(request.getQueryString()).thenReturn("action=submit&csrfToken=xyz123");
        assertTrue(CsrfUtil.isTokenSuppliedInQueryString(request));

        when(request.getQueryString()).thenReturn("csrfToken=xyz123");
        assertTrue(CsrfUtil.isTokenSuppliedInQueryString(request));

        when(request.getQueryString()).thenReturn("CSRFTOKEN=xyz123");
        assertTrue(CsrfUtil.isTokenSuppliedInQueryString(request));
    }

    @Test
    public void testIsTokenSuppliedInQueryString_ReturnsFalseWhenNotPresent() {
        when(request.getQueryString()).thenReturn("action=submit&id=5");
        assertFalse(CsrfUtil.isTokenSuppliedInQueryString(request));

        when(request.getQueryString()).thenReturn(null);
        assertFalse(CsrfUtil.isTokenSuppliedInQueryString(request));

        when(request.getQueryString()).thenReturn("");
        assertFalse(CsrfUtil.isTokenSuppliedInQueryString(request));
    }

    @Test
    public void testIsValidToken_ValidTokenInBody_ReturnsTrue() {
        String token = "valid-token-12345678901234567890";
        when(request.getQueryString()).thenReturn(null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(token);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(token);

        assertTrue(CsrfUtil.isValidToken(request));
    }

    @Test
    public void testIsValidToken_ValidTokenInHeader_ReturnsTrue() {
        String token = "valid-token-12345678901234567890";
        when(request.getQueryString()).thenReturn(null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(token);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(null);
        when(request.getHeader(CsrfUtil.CSRF_HEADER_NAME)).thenReturn(token);

        assertTrue(CsrfUtil.isValidToken(request));
    }

    @Test
    public void testIsValidToken_TokenInQueryParam_Rejected() {
        String token = "valid-token-12345678901234567890";
        when(request.getQueryString()).thenReturn("csrfToken=" + token);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(token);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(token);

        assertFalse(CsrfUtil.isValidToken(request), "Token in query string must be rejected");
    }

    @Test
    public void testIsValidToken_MissingToken_ReturnsFalse() {
        when(request.getQueryString()).thenReturn(null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn("expected-token");
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(null);
        when(request.getHeader(CsrfUtil.CSRF_HEADER_NAME)).thenReturn(null);

        assertFalse(CsrfUtil.isValidToken(request));
    }

    @Test
    public void testIsValidToken_MismatchedToken_ReturnsFalse() {
        when(request.getQueryString()).thenReturn(null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn("expected-token");
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn("wrong-attacker-token");

        assertFalse(CsrfUtil.isValidToken(request));
    }

    @Test
    public void testIsValidToken_NoSession_ReturnsFalse() {
        when(request.getQueryString()).thenReturn(null);
        when(request.getSession(false)).thenReturn(null);

        assertFalse(CsrfUtil.isValidToken(request));
    }

    @Test
    public void testIsValidToken_NoSessionToken_ReturnsFalse() {
        when(request.getQueryString()).thenReturn(null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(null);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn("some-token");

        assertFalse(CsrfUtil.isValidToken(request));
    }
}
