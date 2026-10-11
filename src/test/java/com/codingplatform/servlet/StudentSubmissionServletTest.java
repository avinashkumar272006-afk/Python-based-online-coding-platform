package com.codingplatform.servlet;

import com.codingplatform.model.Exercise;
import com.codingplatform.model.Submission;
import com.codingplatform.service.ExerciseService;
import com.codingplatform.service.SubmissionService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class StudentSubmissionServletTest {

    private StudentSubmissionServlet servlet;

    @Mock
    private SubmissionService submissionService;

    @Mock
    private ExerciseService exerciseService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    private static final int STUDENT_ID = 1;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new StudentSubmissionServlet(submissionService, exerciseService);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(STUDENT_ID);
        when(session.getAttribute("role")).thenReturn("STUDENT");
        when(request.getContextPath()).thenReturn("");
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    public void testDoGet_SubmitForm_ApprovedExercise_Success() throws Exception {
        when(request.getServletPath()).thenReturn("/student/submit");
        when(request.getParameter("exerciseId")).thenReturn("10");

        Exercise approvedExercise = new Exercise();
        approvedExercise.setId(10L);
        approvedExercise.setTitle("Sum of Two Numbers");
        approvedExercise.setStatus("APPROVED");

        when(exerciseService.getExerciseById(10L)).thenReturn(approvedExercise);

        servlet.doGet(request, response);

        verify(exerciseService).getExerciseById(10L);
        verify(request).setAttribute("exercise", approvedExercise);
        verify(request).getRequestDispatcher("/student/submit-exercise.jsp");
        verify(dispatcher).forward(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    public void testDoGet_SubmitForm_PendingExercise_SendsNotFound() throws Exception {
        when(request.getServletPath()).thenReturn("/student/submit");
        when(request.getParameter("exerciseId")).thenReturn("11");

        Exercise pendingExercise = new Exercise();
        pendingExercise.setId(11L);
        pendingExercise.setStatus("PENDING");

        when(exerciseService.getExerciseById(11L)).thenReturn(pendingExercise);

        servlet.doGet(request, response);

        verify(exerciseService).getExerciseById(11L);
        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Exercise not found or not approved.");
        verify(request, never()).setAttribute(eq("exercise"), any());
        verify(dispatcher, never()).forward(request, response);
    }

    @Test
    public void testDoGet_SubmitForm_RejectedExercise_SendsNotFound() throws Exception {
        when(request.getServletPath()).thenReturn("/student/submit");
        when(request.getParameter("exerciseId")).thenReturn("12");

        Exercise rejectedExercise = new Exercise();
        rejectedExercise.setId(12L);
        rejectedExercise.setStatus("REJECTED");

        when(exerciseService.getExerciseById(12L)).thenReturn(rejectedExercise);

        servlet.doGet(request, response);

        verify(exerciseService).getExerciseById(12L);
        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Exercise not found or not approved.");
        verify(request, never()).setAttribute(eq("exercise"), any());
        verify(dispatcher, never()).forward(request, response);
    }

    @Test
    public void testDoGet_SubmitForm_NonexistentExercise_SendsNotFound() throws Exception {
        when(request.getServletPath()).thenReturn("/student/submit");
        when(request.getParameter("exerciseId")).thenReturn("999");

        when(exerciseService.getExerciseById(999L)).thenReturn(null);

        servlet.doGet(request, response);

        verify(exerciseService).getExerciseById(999L);
        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Exercise not found or not approved.");
        verify(request, never()).setAttribute(eq("exercise"), any());
        verify(dispatcher, never()).forward(request, response);
    }

    @Test
    public void testDoGet_SubmitForm_MissingExerciseId_RedirectsToExercises() throws Exception {
        when(request.getServletPath()).thenReturn("/student/submit");
        when(request.getParameter("exerciseId")).thenReturn(null);

        servlet.doGet(request, response);

        verify(exerciseService, never()).getExerciseById(anyLong());
        verify(response).sendRedirect("/student/exercises");
        verify(dispatcher, never()).forward(request, response);
    }

    @Test
    public void testDoGet_SubmitForm_InvalidExerciseId_SendsBadRequest() throws Exception {
        when(request.getServletPath()).thenReturn("/student/submit");
        when(request.getParameter("exerciseId")).thenReturn("abc");

        servlet.doGet(request, response);

        verify(exerciseService, never()).getExerciseById(anyLong());
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid exercise ID");
        verify(dispatcher, never()).forward(request, response);
    }

    @Test
    public void testDoGet_ListSubmissions_Success() throws Exception {
        when(request.getServletPath()).thenReturn("/student/submissions");
        when(submissionService.getSubmissionsByStudent(STUDENT_ID)).thenReturn(Collections.emptyList());

        servlet.doGet(request, response);

        verify(submissionService).getSubmissionsByStudent(STUDENT_ID);
        verify(request).setAttribute(eq("submissions"), anyList());
        verify(request).getRequestDispatcher("/student/submissions.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testDoPost_Submit_ApprovedExercise_Success() throws Exception {
        when(request.getServletPath()).thenReturn("/student/submit");
        when(request.getParameter("exerciseId")).thenReturn("10");
        when(request.getParameter("submittedCode")).thenReturn("print('Hello World')");

        when(submissionService.createSubmission(any(Submission.class))).thenReturn(true);

        servlet.doPost(request, response);

        verify(submissionService).createSubmission(argThat(sub ->
                sub.getExerciseId() == 10L &&
                sub.getStudentId() == STUDENT_ID &&
                "print('Hello World')".equals(sub.getSubmittedCode())
        ));
        verify(response).sendRedirect("/student/submissions?success=true");
    }

    @Test
    public void testDoPost_Submit_UnapprovedExercise_SendsBadRequest() throws Exception {
        when(request.getServletPath()).thenReturn("/student/submit");
        when(request.getParameter("exerciseId")).thenReturn("11");
        when(request.getParameter("submittedCode")).thenReturn("print('Hello World')");

        doThrow(new IllegalArgumentException("Submissions are only allowed for approved exercises."))
                .when(submissionService).createSubmission(any(Submission.class));

        Exercise pendingExercise = new Exercise();
        pendingExercise.setId(11L);
        pendingExercise.setStatus("PENDING");
        when(exerciseService.getExerciseById(11L)).thenReturn(pendingExercise);

        servlet.doPost(request, response);

        verify(submissionService).createSubmission(any(Submission.class));
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Submissions are only allowed for approved exercises.");
        verify(dispatcher, never()).forward(request, response);
    }

    @Test
    public void testDoPost_Submit_NonexistentExercise_SendsBadRequest() throws Exception {
        when(request.getServletPath()).thenReturn("/student/submit");
        when(request.getParameter("exerciseId")).thenReturn("999");
        when(request.getParameter("submittedCode")).thenReturn("print('Hello World')");

        doThrow(new IllegalArgumentException("Exercise must exist."))
                .when(submissionService).createSubmission(any(Submission.class));

        when(exerciseService.getExerciseById(999L)).thenReturn(null);

        servlet.doPost(request, response);

        verify(submissionService).createSubmission(any(Submission.class));
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Exercise must exist.");
        verify(dispatcher, never()).forward(request, response);
    }

    @Test
    public void testDoPost_Submit_EmptyCode_ApprovedExercise_ForwardsWithValidationMessage() throws Exception {
        when(request.getServletPath()).thenReturn("/student/submit");
        when(request.getParameter("exerciseId")).thenReturn("10");
        when(request.getParameter("submittedCode")).thenReturn("   ");

        doThrow(new IllegalArgumentException("Submitted code cannot be empty."))
                .when(submissionService).createSubmission(any(Submission.class));

        Exercise approvedExercise = new Exercise();
        approvedExercise.setId(10L);
        approvedExercise.setStatus("APPROVED");
        when(exerciseService.getExerciseById(10L)).thenReturn(approvedExercise);

        servlet.doPost(request, response);

        verify(submissionService).createSubmission(any(Submission.class));
        verify(request).setAttribute("error", "Submitted code cannot be empty.");
        verify(request).setAttribute("exercise", approvedExercise);
        verify(request).getRequestDispatcher("/student/submit-exercise.jsp");
        verify(dispatcher).forward(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    public void testDoPost_Submit_OversizedCode_ApprovedExercise_ForwardsWithValidationMessage() throws Exception {
        when(request.getServletPath()).thenReturn("/student/submit");
        when(request.getParameter("exerciseId")).thenReturn("10");
        String longCode = "a".repeat(50_001);
        when(request.getParameter("submittedCode")).thenReturn(longCode);

        doThrow(new IllegalArgumentException("Submitted code exceeds the maximum allowed length of 50000 characters."))
                .when(submissionService).createSubmission(any(Submission.class));

        Exercise approvedExercise = new Exercise();
        approvedExercise.setId(10L);
        approvedExercise.setStatus("APPROVED");
        when(exerciseService.getExerciseById(10L)).thenReturn(approvedExercise);

        servlet.doPost(request, response);

        verify(submissionService).createSubmission(any(Submission.class));
        verify(request).setAttribute("error", "Submitted code exceeds the maximum allowed length of 50000 characters.");
        verify(request).setAttribute("exercise", approvedExercise);
        verify(request).getRequestDispatcher("/student/submit-exercise.jsp");
        verify(dispatcher).forward(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    public void testDoPost_Unauthenticated_RedirectsToLogin() throws Exception {
        when(session.getAttribute("userId")).thenReturn(null);
        when(request.getServletPath()).thenReturn("/student/submit");

        servlet.doPost(request, response);

        verify(response).sendRedirect("/login");
        verify(submissionService, never()).createSubmission(any(Submission.class));
    }

    @Test
    public void testDoPost_NonStudent_SendsForbidden() throws Exception {
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getServletPath()).thenReturn("/student/submit");

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        verify(submissionService, never()).createSubmission(any(Submission.class));
    }
}
