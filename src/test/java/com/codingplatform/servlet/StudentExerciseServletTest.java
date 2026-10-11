package com.codingplatform.servlet;

import com.codingplatform.service.ExerciseService;
import com.codingplatform.model.Exercise;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class StudentExerciseServletTest {

    private StudentExerciseServlet servlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private ExerciseService exerciseService;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new StudentExerciseServlet(exerciseService);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    public void testStudentCanViewApprovedExercises() throws Exception {
        when(request.getParameter("action")).thenReturn("list");
        Exercise ex = new Exercise();
        ex.setTitle("Test Ex");
        ex.setStatus("APPROVED");
        when(exerciseService.getApprovedExercises()).thenReturn(Arrays.asList(ex));

        servlet.doGet(request, response);

        verify(exerciseService).getApprovedExercises();
        verify(request).setAttribute(eq("exercises"), anyList());
        verify(request).getRequestDispatcher("/student/exercises.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testStudentCanViewApprovedExerciseDetails_Success() throws Exception {
        when(request.getParameter("action")).thenReturn("view");
        when(request.getParameter("id")).thenReturn("1");

        Exercise ex = new Exercise();
        ex.setId(1L);
        ex.setTitle("Sum of Two Numbers");
        ex.setStatus("APPROVED");

        when(exerciseService.getExerciseById(1L)).thenReturn(ex);

        servlet.doGet(request, response);

        verify(exerciseService).getExerciseById(1L);
        verify(request).setAttribute("exercise", ex);
        verify(request).getRequestDispatcher("/student/exercise-detail.jsp");
        verify(dispatcher).forward(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    public void testStudentCannotViewPendingExerciseDetails() throws Exception {
        when(request.getParameter("action")).thenReturn("view");
        when(request.getParameter("id")).thenReturn("1");
        
        Exercise ex = new Exercise();
        ex.setId(1L);
        ex.setStatus("PENDING");
        
        when(exerciseService.getExerciseById(1L)).thenReturn(ex);
        
        servlet.doGet(request, response);
        
        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Exercise not found or not approved.");
        verify(request, never()).setAttribute(eq("exercise"), any());
        verify(dispatcher, never()).forward(request, response);
    }

    @Test
    public void testStudentCannotViewRejectedExerciseDetails() throws Exception {
        when(request.getParameter("action")).thenReturn("view");
        when(request.getParameter("id")).thenReturn("2");

        Exercise ex = new Exercise();
        ex.setId(2L);
        ex.setStatus("REJECTED");

        when(exerciseService.getExerciseById(2L)).thenReturn(ex);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Exercise not found or not approved.");
        verify(request, never()).setAttribute(eq("exercise"), any());
        verify(dispatcher, never()).forward(request, response);
    }

    @Test
    public void testStudentViewExercise_NonexistentExercise_ReturnsNotFound() throws Exception {
        when(request.getParameter("action")).thenReturn("view");
        when(request.getParameter("id")).thenReturn("999");

        when(exerciseService.getExerciseById(999L)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Exercise not found or not approved.");
        verify(request, never()).setAttribute(eq("exercise"), any());
        verify(dispatcher, never()).forward(request, response);
    }

    @Test
    public void testStudentViewExercise_MissingIdParam_ReturnsBadRequest400() throws Exception {
        when(request.getParameter("action")).thenReturn("view");
        when(request.getParameter("id")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid exercise ID");
        verify(exerciseService, never()).getExerciseById(anyLong());
        verify(dispatcher, never()).forward(request, response);
    }

    @Test
    public void testStudentViewExercise_BlankIdParam_ReturnsBadRequest400() throws Exception {
        when(request.getParameter("action")).thenReturn("view");
        when(request.getParameter("id")).thenReturn("   ");

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid exercise ID");
        verify(exerciseService, never()).getExerciseById(anyLong());
        verify(dispatcher, never()).forward(request, response);
    }

    @Test
    public void testStudentViewExercise_MalformedIdParam_ReturnsBadRequest400() throws Exception {
        when(request.getParameter("action")).thenReturn("view");
        when(request.getParameter("id")).thenReturn("abc");

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid exercise ID");
        verify(exerciseService, never()).getExerciseById(anyLong());
        verify(dispatcher, never()).forward(request, response);
    }

    @Test
    public void testStudentViewExercise_ZeroIdParam_ReturnsBadRequest400() throws Exception {
        when(request.getParameter("action")).thenReturn("view");
        when(request.getParameter("id")).thenReturn("0");

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid exercise ID");
        verify(exerciseService, never()).getExerciseById(anyLong());
        verify(dispatcher, never()).forward(request, response);
    }

    @Test
    public void testStudentViewExercise_NegativeIdParam_ReturnsBadRequest400() throws Exception {
        when(request.getParameter("action")).thenReturn("view");
        when(request.getParameter("id")).thenReturn("-10");

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid exercise ID");
        verify(exerciseService, never()).getExerciseById(anyLong());
        verify(dispatcher, never()).forward(request, response);
    }

    @Test
    public void testStudentViewExercise_DatabaseException_ReturnsInternalServerError500() throws Exception {
        when(request.getParameter("action")).thenReturn("view");
        when(request.getParameter("id")).thenReturn("1");

        when(exerciseService.getExerciseById(1L))
                .thenThrow(new com.codingplatform.exception.DatabaseException("Database connection failure", null));

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        verify(dispatcher, never()).forward(request, response);
    }
}
