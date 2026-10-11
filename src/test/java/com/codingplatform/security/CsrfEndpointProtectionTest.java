package com.codingplatform.security;

import com.codingplatform.model.Exercise;
import com.codingplatform.model.Submission;
import com.codingplatform.model.User;
import com.codingplatform.model.Student;
import com.codingplatform.service.*;
import com.codingplatform.servlet.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * End-to-end security integration tests verifying CSRF protection across representative workflows.
 * Verifies that:
 * 1. Missing, invalid, or mismatched tokens are rejected with 403 and do not invoke mutation services.
 * 2. Valid tokens allow legitimate requests to proceed to the servlet.
 * 3. Existing role and ownership checks continue to function as expected with a valid token.
 * 4. Legitimate validation error handling is preserved.
 */
public class CsrfEndpointProtectionTest {

    private CsrfFilter csrfFilter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private RequestDispatcher dispatcher;

    private static final String VALID_SESSION_TOKEN = "valid-session-csrf-token-abcdef123456";
    private static final String ATTACKER_TOKEN = "attacker-forged-csrf-token-999999999999";

    @BeforeEach
    public void setUp() {
        csrfFilter = new CsrfFilter();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);

        when(request.getContextPath()).thenReturn("");
        when(request.getMethod()).thenReturn("POST");
        when(request.getQueryString()).thenReturn(null);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    private FilterChain createFilterChainToServlet(jakarta.servlet.http.HttpServlet servlet) {
        return (ServletRequest req, ServletResponse res) -> {
            try {
                // invoke service method of servlet (which dispatches to doPost)
                servlet.service(req, res);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    // ==========================================
    // 1. Messaging Workflow (/messages)
    // ==========================================

    @Test
    public void testMessaging_MissingToken_RejectsAndDoesNotSendMessage() throws Exception {
        MessageService mockMessageService = mock(MessageService.class);
        MessageServlet servlet = new MessageServlet(mockMessageService);

        when(request.getRequestURI()).thenReturn("/messages");
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        // Missing token in request parameter
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(null);
        when(request.getParameter("receiverId")).thenReturn("2");
        when(request.getParameter("message")).thenReturn("Hello");

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
        verify(mockMessageService, never()).sendMessage(anyInt(), anyString(), anyInt(), anyString());
    }

    @Test
    public void testMessaging_ValidToken_SendsMessage() throws Exception {
        MessageService mockMessageService = mock(MessageService.class);
        MessageServlet servlet = new MessageServlet(mockMessageService);

        when(request.getRequestURI()).thenReturn("/messages");
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter("receiverId")).thenReturn("2");
        when(request.getParameter("message")).thenReturn("Hello instructor");

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(mockMessageService).sendMessage(1, "STUDENT", 2, "Hello instructor");
        verify(response).sendRedirect("/messages?partnerId=2");
    }

    @Test
    public void testMessaging_ValidToken_UnauthorizedRoleRejectedByServlet() throws Exception {
        MessageService mockMessageService = mock(MessageService.class);
        MessageServlet servlet = new MessageServlet(mockMessageService);

        when(request.getRequestURI()).thenReturn("/messages");
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(10);
        when(session.getAttribute("role")).thenReturn("ADMIN"); // Admin not allowed to message
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(VALID_SESSION_TOKEN);

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        verify(mockMessageService, never()).sendMessage(anyInt(), anyString(), anyInt(), anyString());
    }

    // ==========================================
    // 2. Exercise Workflow (/instructor/exercises)
    // ==========================================

    @Test
    public void testExercise_MissingToken_RejectsAndDoesNotCreateExercise() throws Exception {
        ExerciseService mockExerciseService = mock(ExerciseService.class);
        InstructorExerciseServlet servlet = new InstructorExerciseServlet(mockExerciseService);

        when(request.getRequestURI()).thenReturn("/instructor/exercises");
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(10L);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(null);
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("title")).thenReturn("New Exercise");

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
        verify(mockExerciseService, never()).createExercise(any(Exercise.class));
    }

    @Test
    public void testExercise_ValidToken_CreatesExercise() throws Exception {
        ExerciseService mockExerciseService = mock(ExerciseService.class);
        InstructorExerciseServlet servlet = new InstructorExerciseServlet(mockExerciseService);

        when(request.getRequestURI()).thenReturn("/instructor/exercises");
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(10L);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("title")).thenReturn("Valid Ex");
        when(request.getParameter("description")).thenReturn("Desc");
        when(request.getParameter("difficulty")).thenReturn("EASY");
        when(request.getParameter("topic")).thenReturn("Strings");
        when(request.getParameter("starterCode")).thenReturn("def foo(): pass");
        when(request.getParameter("expectedOutput")).thenReturn("ok");

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(mockExerciseService).createExercise(any(Exercise.class));
        verify(response).sendRedirect("/instructor/exercises");
    }

    // ==========================================
    // 3. Submission Review Workflow (/instructor/submission/review)
    // ==========================================

    @Test
    public void testReviewSubmission_InvalidToken_RejectsAndDoesNotReview() throws Exception {
        SubmissionService mockSubmissionService = mock(SubmissionService.class);
        InstructorSubmissionServlet servlet = new InstructorSubmissionServlet(mockSubmissionService);

        when(request.getRequestURI()).thenReturn("/instructor/submission/review");
        when(request.getServletPath()).thenReturn("/instructor/submission/review");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(5);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(ATTACKER_TOKEN);

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
        verify(mockSubmissionService, never()).reviewSubmission(anyLong(), anyInt(), anyString(), anyString(), any());
    }

    @Test
    public void testReviewSubmission_ValidToken_ReviewsSubmission() throws Exception {
        SubmissionService mockSubmissionService = mock(SubmissionService.class);
        InstructorSubmissionServlet servlet = new InstructorSubmissionServlet(mockSubmissionService);

        when(request.getRequestURI()).thenReturn("/instructor/submission/review");
        when(request.getServletPath()).thenReturn("/instructor/submission/review");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(5);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter("id")).thenReturn("100");
        when(request.getParameter("feedback")).thenReturn("Good job");
        when(request.getParameter("status")).thenReturn("REVIEWED");
        when(request.getParameter("score")).thenReturn("95");

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(mockSubmissionService).reviewSubmission(100L, 5, "Good job", "REVIEWED", 95);
        verify(response).sendRedirect("/instructor/submissions?success=true");
    }

    // ==========================================
    // 4. Student Submission Workflow (/student/submit)
    // ==========================================

    @Test
    public void testStudentSubmit_MissingToken_RejectsAndDoesNotCreateSubmission() throws Exception {
        SubmissionService mockSubmissionService = mock(SubmissionService.class);
        StudentSubmissionServlet servlet = new StudentSubmissionServlet(mockSubmissionService, mock(ExerciseService.class));

        when(request.getRequestURI()).thenReturn("/student/submit");
        when(request.getServletPath()).thenReturn("/student/submit");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(null);

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
        verify(mockSubmissionService, never()).createSubmission(any(Submission.class));
    }

    @Test
    public void testStudentSubmit_ValidToken_CreatesSubmission() throws Exception {
        SubmissionService mockSubmissionService = mock(SubmissionService.class);
        StudentSubmissionServlet servlet = new StudentSubmissionServlet(mockSubmissionService, mock(ExerciseService.class));

        when(request.getRequestURI()).thenReturn("/student/submit");
        when(request.getServletPath()).thenReturn("/student/submit");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter("exerciseId")).thenReturn("50");
        when(request.getParameter("submittedCode")).thenReturn("print('hello')");

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(mockSubmissionService).createSubmission(any(Submission.class));
        verify(response).sendRedirect("/student/submissions?success=true");
    }

    // ==========================================
    // 5. Challenge Workflow (/student/challenges)
    // ==========================================

    @Test
    public void testChallenge_MissingToken_RejectsAndDoesNotJoin() throws Exception {
        ChallengeService mockChallengeService = mock(ChallengeService.class);
        StudentChallengeServlet servlet = new StudentChallengeServlet();
        servlet.setChallengeService(mockChallengeService);

        when(request.getRequestURI()).thenReturn("/student/challenges");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(20L);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(null);
        when(request.getParameter("action")).thenReturn("join");
        when(request.getParameter("id")).thenReturn("5");

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
        verify(mockChallengeService, never()).joinChallenge(anyLong(), anyLong());
    }

    @Test
    public void testChallenge_ValidToken_JoinsChallenge() throws Exception {
        ChallengeService mockChallengeService = mock(ChallengeService.class);
        StudentChallengeServlet servlet = new StudentChallengeServlet();
        servlet.setChallengeService(mockChallengeService);

        when(request.getRequestURI()).thenReturn("/student/challenges");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(20L);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter("action")).thenReturn("join");
        when(request.getParameter("id")).thenReturn("5");

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(mockChallengeService).joinChallenge(5L, 20L);
        verify(response).sendRedirect("/student/my-challenges?success=joined");
    }

    // ==========================================
    // 6. Admin User Workflow (/admin/users)
    // ==========================================

    @Test
    public void testAdminUser_MissingToken_RejectsAndDoesNotUpdateRole() throws Exception {
        AdminService mockAdminService = mock(AdminService.class);
        AdminUserServlet servlet = new AdminUserServlet();
        servlet.setAdminService(mockAdminService);

        when(request.getRequestURI()).thenReturn("/admin/users");
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(99);
        when(session.getAttribute("role")).thenReturn("ADMIN");
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(null);

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
        verify(mockAdminService, never()).updateUserRole(anyInt(), anyInt(), anyString());
    }

    @Test
    public void testAdminUser_ValidToken_UpdatesRole() throws Exception {
        AdminService mockAdminService = mock(AdminService.class);
        AdminUserServlet servlet = new AdminUserServlet();
        servlet.setAdminService(mockAdminService);

        when(request.getRequestURI()).thenReturn("/admin/users");
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(99);
        when(session.getAttribute("role")).thenReturn("ADMIN");
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter("action")).thenReturn("updateRole");
        when(request.getParameter("userId")).thenReturn("3");
        when(request.getParameter("role")).thenReturn("INSTRUCTOR");

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(mockAdminService).updateUserRole(99, 3, "INSTRUCTOR");
        verify(response).sendRedirect("/admin/users");
    }

    // ==========================================
    // 7. Admin Settings Workflow (/admin/settings)
    // ==========================================

    @Test
    public void testAdminSettings_MissingToken_RejectsAndDoesNotUpdateSetting() throws Exception {
        SystemSettingsService mockSettingsService = mock(SystemSettingsService.class);
        SystemSettingsServlet servlet = new SystemSettingsServlet(mockSettingsService);

        when(request.getRequestURI()).thenReturn("/admin/settings");
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(99);
        when(session.getAttribute("role")).thenReturn("ADMIN");
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(null);

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
        verify(mockSettingsService, never()).updateSetting(anyInt(), anyString(), anyString(), anyString());
    }

    @Test
    public void testAdminSettings_ValidToken_UpdatesSetting() throws Exception {
        SystemSettingsService mockSettingsService = mock(SystemSettingsService.class);
        SystemSettingsServlet servlet = new SystemSettingsServlet(mockSettingsService);

        when(request.getRequestURI()).thenReturn("/admin/settings");
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(99);
        when(session.getAttribute("role")).thenReturn("ADMIN");
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter("settingKey")).thenReturn("max_attempts");
        when(request.getParameter("settingValue")).thenReturn("5");

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(mockSettingsService).updateSetting(99, "ADMIN", "max_attempts", "5");
        verify(response).sendRedirect("/admin/settings");
    }

    // ==========================================
    // 8. Profile Workflow (/profile)
    // ==========================================

    @Test
    public void testProfile_MissingToken_RejectsAndDoesNotUpdateProfile() throws Exception {
        UserService mockUserService = mock(UserService.class);
        ProfileServlet servlet = new ProfileServlet();
        servlet.setUserService(mockUserService);

        when(request.getRequestURI()).thenReturn("/profile");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(null);

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
        verify(mockUserService, never()).updateUserProfile(anyInt(), anyString(), anyString());
    }

    @Test
    public void testProfile_ValidToken_UpdatesProfile() throws Exception {
        UserService mockUserService = mock(UserService.class);
        ProfileServlet servlet = new ProfileServlet();
        servlet.setUserService(mockUserService);

        when(request.getRequestURI()).thenReturn("/profile");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter("name")).thenReturn("New Name");
        when(request.getParameter("email")).thenReturn("newemail@test.com");
        when(mockUserService.updateUserProfile(1, "New Name", "newemail@test.com")).thenReturn(true);

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(mockUserService).updateUserProfile(1, "New Name", "newemail@test.com");
        verify(dispatcher).forward(request, response);
    }

    // ==========================================
    // 9. Logout Workflow (/logout)
    // ==========================================

    @Test
    public void testLogout_MissingToken_RejectsAndDoesNotInvalidateSession() throws Exception {
        LogoutServlet servlet = new LogoutServlet();

        when(request.getRequestURI()).thenReturn("/logout");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(null);

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
        verify(session, never()).invalidate();
    }

    @Test
    public void testLogout_InvalidToken_RejectsAndDoesNotInvalidateSession() throws Exception {
        LogoutServlet servlet = new LogoutServlet();

        when(request.getRequestURI()).thenReturn("/logout");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(ATTACKER_TOKEN);

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
        verify(session, never()).invalidate();
    }

    @Test
    public void testLogout_ValidToken_InvalidatesSessionAndRedirects() throws Exception {
        LogoutServlet servlet = new LogoutServlet();

        when(request.getRequestURI()).thenReturn("/logout");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(VALID_SESSION_TOKEN);

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(session).invalidate();
        verify(response).sendRedirect("/login.jsp?logout=true");
    }

    @Test
    public void testLogout_GetRequest_DoesNotInvalidateSession() throws Exception {
        LogoutServlet servlet = new LogoutServlet();

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/logout");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(session, never()).invalidate();
        verify(response).sendRedirect("/student/dashboard");
    }

    // ==========================================
    // 10. Public Login Workflow (/login)
    // ==========================================

    @Test
    public void testLogin_MissingToken_RejectsAndDoesNotAuthenticate() throws Exception {
        UserService mockUserService = mock(UserService.class);
        LoginServlet servlet = new LoginServlet();
        servlet.setUserService(mockUserService);

        when(request.getRequestURI()).thenReturn("/login");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(null); // Anonymous session
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(null);
        when(request.getParameter("email")).thenReturn("student@test.com");
        when(request.getParameter("password")).thenReturn("password");

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
        verify(mockUserService, never()).authenticateUser(anyString(), anyString());
    }

    @Test
    public void testLogin_InvalidToken_RejectsAndDoesNotAuthenticate() throws Exception {
        UserService mockUserService = mock(UserService.class);
        LoginServlet servlet = new LoginServlet();
        servlet.setUserService(mockUserService);

        when(request.getRequestURI()).thenReturn("/login");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(null); // Anonymous session
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(ATTACKER_TOKEN);
        when(request.getParameter("email")).thenReturn("student@test.com");
        when(request.getParameter("password")).thenReturn("password");

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
        verify(mockUserService, never()).authenticateUser(anyString(), anyString());
    }

    @Test
    public void testLogin_ValidToken_AuthenticatesAndRotatesSession() throws Exception {
        UserService mockUserService = mock(UserService.class);
        LoginServlet servlet = new LoginServlet();
        servlet.setUserService(mockUserService);

        User authenticatedUser = new Student();
        authenticatedUser.setId(1);
        authenticatedUser.setName("Test Student");
        authenticatedUser.setEmail("student@test.com");
        authenticatedUser.setRole("STUDENT");

        HttpSession newAuthenticatedSession = mock(HttpSession.class);

        when(request.getRequestURI()).thenReturn("/login");
        when(request.getSession(false)).thenReturn(session);
        when(request.getSession(true)).thenReturn(newAuthenticatedSession);
        when(session.getAttribute("userId")).thenReturn(null); // Anonymous session
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter("email")).thenReturn("student@test.com");
        when(request.getParameter("password")).thenReturn("correctPassword");
        when(mockUserService.authenticateUser("student@test.com", "correctPassword")).thenReturn(authenticatedUser);

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        // Pre-existing session invalidated
        verify(session).invalidate();
        // New authenticated session populated
        verify(newAuthenticatedSession).setAttribute("userId", 1);
        verify(newAuthenticatedSession).setAttribute("role", "STUDENT");
        // Fresh token refreshed in new session
        verify(newAuthenticatedSession).setAttribute(eq(CsrfUtil.CSRF_TOKEN_SESSION_ATTR), anyString());
        verify(response).sendRedirect("/student/dashboard");
    }

    @Test
    public void testLogin_ValidToken_FailedCredentials_PreservesSessionAndForwardsError() throws Exception {
        UserService mockUserService = mock(UserService.class);
        LoginServlet servlet = new LoginServlet();
        servlet.setUserService(mockUserService);

        when(request.getRequestURI()).thenReturn("/login");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(null);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter("email")).thenReturn("student@test.com");
        when(request.getParameter("password")).thenReturn("wrongPassword");
        when(mockUserService.authenticateUser("student@test.com", "wrongPassword")).thenReturn(null);

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(session, never()).invalidate();
        verify(request).setAttribute("errorMessage", "Invalid email or password.");
        verify(dispatcher).forward(request, response);
    }

    // ==========================================
    // 11. Public Registration Workflow (/register)
    // ==========================================

    @Test
    public void testRegister_MissingToken_RejectsAndDoesNotRegisterUser() throws Exception {
        UserService mockUserService = mock(UserService.class);
        RegisterServlet servlet = new RegisterServlet();
        servlet.setUserService(mockUserService);

        when(request.getRequestURI()).thenReturn("/register");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(null);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(null);
        when(request.getParameter("name")).thenReturn("New User");
        when(request.getParameter("email")).thenReturn("newuser@test.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("confirmPassword")).thenReturn("password123");
        when(request.getParameter("role")).thenReturn("STUDENT");

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
        verify(mockUserService, never()).registerUser(any(User.class));
    }

    @Test
    public void testRegister_InvalidToken_RejectsAndDoesNotRegisterUser() throws Exception {
        UserService mockUserService = mock(UserService.class);
        RegisterServlet servlet = new RegisterServlet();
        servlet.setUserService(mockUserService);

        when(request.getRequestURI()).thenReturn("/register");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(null);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(ATTACKER_TOKEN);
        when(request.getParameter("name")).thenReturn("New User");
        when(request.getParameter("email")).thenReturn("newuser@test.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("confirmPassword")).thenReturn("password123");
        when(request.getParameter("role")).thenReturn("STUDENT");

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
        verify(mockUserService, never()).registerUser(any(User.class));
    }

    @Test
    public void testRegister_ValidToken_RegistersUser() throws Exception {
        UserService mockUserService = mock(UserService.class);
        RegisterServlet servlet = new RegisterServlet();
        servlet.setUserService(mockUserService);

        when(request.getRequestURI()).thenReturn("/register");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(null);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter("name")).thenReturn("New User");
        when(request.getParameter("email")).thenReturn("newuser@test.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("confirmPassword")).thenReturn("password123");
        when(request.getParameter("role")).thenReturn("STUDENT");
        when(mockUserService.registerUser(any(User.class))).thenReturn(true);

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(mockUserService).registerUser(any(User.class));
        verify(request).setAttribute(eq("successMessage"), contains("Registration successful"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testRegister_ValidToken_AdminRoleRejectedByServlet() throws Exception {
        UserService mockUserService = mock(UserService.class);
        RegisterServlet servlet = new RegisterServlet();
        servlet.setUserService(mockUserService);

        when(request.getRequestURI()).thenReturn("/register");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(null);
        when(session.getAttribute(CsrfUtil.CSRF_TOKEN_SESSION_ATTR)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter(CsrfUtil.CSRF_PARAM_NAME)).thenReturn(VALID_SESSION_TOKEN);
        when(request.getParameter("name")).thenReturn("Admin Candidate");
        when(request.getParameter("email")).thenReturn("admin@test.com");
        when(request.getParameter("password")).thenReturn("password123");
        when(request.getParameter("confirmPassword")).thenReturn("password123");
        when(request.getParameter("role")).thenReturn("ADMIN");

        FilterChain chain = createFilterChainToServlet(servlet);
        csrfFilter.doFilter(request, response, chain);

        verify(mockUserService, never()).registerUser(any(User.class));
        verify(request).setAttribute("errorMessage", "Registration with ADMIN role is not permitted.");
        verify(dispatcher).forward(request, response);
    }
}
