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
        servlet = new StudentExerciseServlet() {
            @Override
            public void init() {
                // Ignore real init to avoid hitting DB
            }
        };
        
        // Use reflection or package-private to inject mock service if possible. 
        // Since exerciseService is private, let's inject via reflection.
        java.lang.reflect.Field field = servlet.getClass().getSuperclass().getDeclaredField("exerciseService");
        field.setAccessible(true);
        field.set(servlet, exerciseService);
        
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
    public void testStudentCannotViewPendingExerciseDetails() throws Exception {
        when(request.getParameter("action")).thenReturn("view");
        when(request.getParameter("id")).thenReturn("1");
        
        Exercise ex = new Exercise();
        ex.setId(1);
        ex.setStatus("PENDING");
        
        when(exerciseService.getExerciseById(1)).thenReturn(ex);
        
        servlet.doGet(request, response);
        
        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Exercise not found or not approved.");
    }
}
