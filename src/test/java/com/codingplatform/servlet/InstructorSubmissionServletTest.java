package com.codingplatform.servlet;

import com.codingplatform.model.Submission;
import com.codingplatform.service.SubmissionService;
import com.codingplatform.exception.DatabaseException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class InstructorSubmissionServletTest {

    private InstructorSubmissionServlet servlet;
    private SubmissionService mockSubmissionService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private RequestDispatcher requestDispatcher;

    @BeforeEach
    public void setUp() {
        servlet = new InstructorSubmissionServlet();
        mockSubmissionService = mock(SubmissionService.class);
        servlet.setSubmissionService(mockSubmissionService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        requestDispatcher = mock(RequestDispatcher.class);

        when(request.getSession(false)).thenReturn(session);
        when(request.getContextPath()).thenReturn("/app");
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void testDoGet_Unauthenticated_RedirectsToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendRedirect("/app/login");
        verifyNoInteractions(mockSubmissionService);
    }

    @Test
    public void testDoGet_NonInstructorRole_ReturnsForbidden() throws Exception {
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("STUDENT");

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        verifyNoInteractions(mockSubmissionService);
    }

    @Test
    public void testDoGet_ListSubmissions_Success() throws Exception {
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getServletPath()).thenReturn("/instructor/submissions");
        when(mockSubmissionService.getSubmissionsByInstructor(2)).thenReturn(Collections.emptyList());

        servlet.doGet(request, response);

        verify(mockSubmissionService).getSubmissionsByInstructor(2);
        verify(request).setAttribute(eq("submissions"), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_OwnSubmission_Success() throws Exception {
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getServletPath()).thenReturn("/instructor/submission/review");
        when(request.getParameter("id")).thenReturn("10");

        Submission submission = new Submission();
        submission.setId(10L);
        submission.setExerciseId(100L);
        when(mockSubmissionService.getSubmissionForInstructor(10L, 2)).thenReturn(submission);

        servlet.doGet(request, response);

        verify(mockSubmissionService).getSubmissionForInstructor(10L, 2);
        verify(request).setAttribute("submission", submission);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_OtherInstructorSubmission_ReturnsForbidden403() throws Exception {
        when(session.getAttribute("userId")).thenReturn(3);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getServletPath()).thenReturn("/instructor/submission/review");
        when(request.getParameter("id")).thenReturn("10");

        when(mockSubmissionService.getSubmissionForInstructor(10L, 3))
                .thenThrow(new SecurityException("Instructor is not authorized to view this submission."));

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        verify(requestDispatcher, never()).forward(request, response);
    }

    @Test
    public void testDoGet_NonexistentSubmission_ReturnsNotFound404() throws Exception {
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getServletPath()).thenReturn("/instructor/submission/review");
        when(request.getParameter("id")).thenReturn("999");

        when(mockSubmissionService.getSubmissionForInstructor(999L, 2)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Submission not found");
        verify(requestDispatcher, never()).forward(request, response);
    }

    @Test
    public void testDoGet_MissingIdParam_RedirectsToSubmissions() throws Exception {
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getServletPath()).thenReturn("/instructor/submission/review");
        when(request.getParameter("id")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendRedirect("/app/instructor/submissions");
        verifyNoInteractions(mockSubmissionService);
    }

    @Test
    public void testDoGet_BlankIdParam_RedirectsToSubmissions() throws Exception {
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getServletPath()).thenReturn("/instructor/submission/review");
        when(request.getParameter("id")).thenReturn("   ");

        servlet.doGet(request, response);

        verify(response).sendRedirect("/app/instructor/submissions");
        verifyNoInteractions(mockSubmissionService);
    }

    @Test
    public void testDoGet_MalformedIdParam_ReturnsBadRequest400() throws Exception {
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getServletPath()).thenReturn("/instructor/submission/review");
        when(request.getParameter("id")).thenReturn("abc");

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid submission ID");
        verifyNoInteractions(mockSubmissionService);
    }

    @Test
    public void testDoGet_ZeroIdParam_ReturnsBadRequest400() throws Exception {
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getServletPath()).thenReturn("/instructor/submission/review");
        when(request.getParameter("id")).thenReturn("0");

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid submission ID");
        verifyNoInteractions(mockSubmissionService);
    }

    @Test
    public void testDoGet_NegativeIdParam_ReturnsBadRequest400() throws Exception {
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getServletPath()).thenReturn("/instructor/submission/review");
        when(request.getParameter("id")).thenReturn("-5");

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid submission ID");
        verifyNoInteractions(mockSubmissionService);
    }

    @Test
    public void testDoPost_ReviewOwnSubmission_Success() throws Exception {
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getServletPath()).thenReturn("/instructor/submission/review");
        when(request.getParameter("id")).thenReturn("10");
        when(request.getParameter("feedback")).thenReturn("Good work");
        when(request.getParameter("status")).thenReturn("REVIEWED");
        when(request.getParameter("score")).thenReturn("95");

        when(mockSubmissionService.reviewSubmission(10L, 2, "Good work", "REVIEWED", 95)).thenReturn(true);

        servlet.doPost(request, response);

        verify(mockSubmissionService).reviewSubmission(10L, 2, "Good work", "REVIEWED", 95);
        verify(response).sendRedirect("/app/instructor/submissions?success=true");
    }

    @Test
    public void testDoPost_ReviewOtherInstructorSubmission_ReturnsForbidden403() throws Exception {
        when(session.getAttribute("userId")).thenReturn(3);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getServletPath()).thenReturn("/instructor/submission/review");
        when(request.getParameter("id")).thenReturn("10");
        when(request.getParameter("feedback")).thenReturn("Unauthorized review");
        when(request.getParameter("status")).thenReturn("REVIEWED");
        when(request.getParameter("score")).thenReturn("80");

        when(mockSubmissionService.reviewSubmission(10L, 3, "Unauthorized review", "REVIEWED", 80))
                .thenThrow(new IllegalArgumentException("Instructor can only review submissions for exercises they created."));

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        verify(requestDispatcher, never()).forward(request, response);
    }

    @Test
    public void testDoPost_MissingOrBlankId_ReturnsBadRequest400() throws Exception {
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getServletPath()).thenReturn("/instructor/submission/review");
        when(request.getParameter("id")).thenReturn("   ");

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid input");
        verifyNoInteractions(mockSubmissionService);
    }

    @Test
    public void testDoPost_ZeroOrNegativeId_ReturnsBadRequest400() throws Exception {
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getServletPath()).thenReturn("/instructor/submission/review");
        when(request.getParameter("id")).thenReturn("0");

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid input");
        verifyNoInteractions(mockSubmissionService);
    }

    @Test
    public void testDoPost_MalformedId_ReturnsBadRequest400() throws Exception {
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getServletPath()).thenReturn("/instructor/submission/review");
        when(request.getParameter("id")).thenReturn("invalid_id");

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid input");
        verifyNoInteractions(mockSubmissionService);
    }

    @Test
    public void testDoPost_NonexistentSubmission_ReturnsNotFound404() throws Exception {
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getServletPath()).thenReturn("/instructor/submission/review");
        when(request.getParameter("id")).thenReturn("999");
        when(request.getParameter("feedback")).thenReturn("Review");

        when(mockSubmissionService.reviewSubmission(eq(999L), eq(2), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Submission does not exist."));

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Submission not found");
        verify(requestDispatcher, never()).forward(request, response);
    }

    @Test
    public void testDoPost_ValidationError_RedisplaysForAuthorizedInstructor() throws Exception {
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getServletPath()).thenReturn("/instructor/submission/review");
        when(request.getParameter("id")).thenReturn("10");
        when(request.getParameter("score")).thenReturn("150");

        when(mockSubmissionService.reviewSubmission(eq(10L), eq(2), any(), any(), eq(150)))
                .thenThrow(new IllegalArgumentException("Score must be between 0 and 100."));

        Submission submission = new Submission();
        submission.setId(10L);
        when(mockSubmissionService.getSubmissionForInstructor(10L, 2)).thenReturn(submission);

        servlet.doPost(request, response);

        verify(request).setAttribute("error", "Score must be between 0 and 100.");
        verify(request).setAttribute("submission", submission);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPost_InvalidStatus_RedisplaysForAuthorizedInstructor() throws Exception {
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getServletPath()).thenReturn("/instructor/submission/review");
        when(request.getParameter("id")).thenReturn("10");
        when(request.getParameter("status")).thenReturn("INVALID");

        when(mockSubmissionService.reviewSubmission(eq(10L), eq(2), any(), eq("INVALID"), any()))
                .thenThrow(new IllegalArgumentException("Invalid review status. Status must be REVIEWED or PENDING."));

        Submission submission = new Submission();
        submission.setId(10L);
        when(mockSubmissionService.getSubmissionForInstructor(10L, 2)).thenReturn(submission);

        servlet.doPost(request, response);

        verify(request).setAttribute("error", "Invalid review status. Status must be REVIEWED or PENDING.");
        verify(request).setAttribute("submission", submission);
        verify(requestDispatcher).forward(request, response);
    }
}
