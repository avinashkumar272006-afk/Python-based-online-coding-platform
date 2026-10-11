package com.codingplatform.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility for cryptographically secure CSRF token generation, storage, and constant-time validation.
 */
public final class CsrfUtil {

    public static final String CSRF_TOKEN_SESSION_ATTR = "csrfToken";
    public static final String CSRF_PARAM_NAME = "csrfToken";
    public static final String CSRF_HEADER_NAME = "X-CSRF-Token";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int TOKEN_BYTE_LENGTH = 32;

    private CsrfUtil() {
        // Utility class
    }

    /**
     * Generates a cryptographically strong, URL-safe random token.
     */
    public static String generateToken() {
        byte[] randomBytes = new byte[TOKEN_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    /**
     * Retrieves the CSRF token from the session, generating and storing a new one if absent.
     * Returns null if session is null.
     */
    public static String getToken(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object tokenObj = session.getAttribute(CSRF_TOKEN_SESSION_ATTR);
        if (tokenObj instanceof String && !((String) tokenObj).trim().isEmpty()) {
            return (String) tokenObj;
        }
        String newToken = generateToken();
        session.setAttribute(CSRF_TOKEN_SESSION_ATTR, newToken);
        return newToken;
    }

    /**
     * Forces generation and storage of a fresh CSRF token in the session (e.g. after login).
     */
    public static String refreshToken(HttpSession session) {
        if (session == null) {
            return null;
        }
        String newToken = generateToken();
        session.setAttribute(CSRF_TOKEN_SESSION_ATTR, newToken);
        return newToken;
    }

    /**
     * Removes the CSRF token from the session.
     */
    public static void clearToken(HttpSession session) {
        if (session != null) {
            session.removeAttribute(CSRF_TOKEN_SESSION_ATTR);
        }
    }

    /**
     * Checks whether the request supplies a CSRF token inside the URL query string.
     * Tokens passed via query string are strictly rejected to avoid leakage in URLs, server logs, and Referer headers.
     */
    public static boolean isTokenSuppliedInQueryString(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        String queryString = request.getQueryString();
        if (queryString == null || queryString.trim().isEmpty()) {
            return false;
        }
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            String key = (idx >= 0 ? pair.substring(0, idx) : pair).trim();
            if (CSRF_PARAM_NAME.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates whether the supplied request has a valid CSRF token matching the session token.
     * Uses constant-time comparison to protect against timing attacks.
     */
    public static boolean isValidToken(HttpServletRequest request) {
        if (request == null) {
            return false;
        }

        // Never accept a token supplied only through a URL query parameter
        if (isTokenSuppliedInQueryString(request)) {
            return false;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        Object sessionTokenObj = session.getAttribute(CSRF_TOKEN_SESSION_ATTR);
        if (!(sessionTokenObj instanceof String)) {
            return false;
        }
        String expectedToken = ((String) sessionTokenObj).trim();
        if (expectedToken.isEmpty()) {
            return false;
        }

        String suppliedToken = request.getParameter(CSRF_PARAM_NAME);
        if (suppliedToken == null || suppliedToken.trim().isEmpty()) {
            suppliedToken = request.getHeader(CSRF_HEADER_NAME);
        }
        if (suppliedToken == null || suppliedToken.trim().isEmpty()) {
            return false;
        }

        byte[] expectedBytes = expectedToken.getBytes(StandardCharsets.UTF_8);
        byte[] suppliedBytes = suppliedToken.trim().getBytes(StandardCharsets.UTF_8);

        return MessageDigest.isEqual(expectedBytes, suppliedBytes);
    }
}
