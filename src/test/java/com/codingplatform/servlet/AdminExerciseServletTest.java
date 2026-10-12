package com.codingplatform.servlet;

import com.codingplatform.model.Exercise;
import com.codingplatform.service.ExerciseService;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class AdminExerciseServletTest {

    @Mock
    private ExerciseService exerciseService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher requestDispatcher;

    @InjectMocks
    private AdminExerciseServlet adminExerciseServlet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        // We inject the mock service via reflection because it's private and has no setter.
        java.lang.reflect.Field serviceField = AdminExerciseServlet.class.getDeclaredField("exerciseService");
        serviceField.setAccessible(true);
        serviceField.set(adminExerciseServlet, exerciseService);
    }

    @Test
    void doGet_ShouldSetExercisesAndForwardToJsp() throws Exception {
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(new Exercise());
        
        List<Exercise> pending = new ArrayList<>();
        pending.add(new Exercise());
        
        when(exerciseService.getAllExercises()).thenReturn(exercises);
        when(exerciseService.getPendingExercises()).thenReturn(pending);
        when(request.getRequestDispatcher("/admin/exercises.jsp")).thenReturn(requestDispatcher);

        adminExerciseServlet.doGet(request, response);

        verify(exerciseService, times(1)).getAllExercises();
        verify(exerciseService, times(1)).getPendingExercises();
        verify(request, times(1)).setAttribute("exercises", exercises);
        verify(request, times(1)).setAttribute("pendingExercises", pending);
        verify(requestDispatcher, times(1)).forward(request, response);
    }

    @Test
    void doPost_ApproveExercise() throws Exception {
        jakarta.servlet.http.HttpSession session = mock(jakarta.servlet.http.HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("action")).thenReturn("approve");
        when(request.getParameter("id")).thenReturn("1");
        
        when(session.getAttribute("userId")).thenReturn(10);
        
        when(request.getContextPath()).thenReturn("");
        
        adminExerciseServlet.doPost(request, response);
        
        verify(exerciseService).approveExercise(1L, 10L);
        verify(session).setAttribute("successMessage", "Exercise approved successfully.");
        verify(response).sendRedirect("/admin/exercises");
    }

    @Test
    void doPost_RejectExercise() throws Exception {
        jakarta.servlet.http.HttpSession session = mock(jakarta.servlet.http.HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("action")).thenReturn("reject");
        when(request.getParameter("id")).thenReturn("2");
        
        when(session.getAttribute("userId")).thenReturn(10);
        
        when(request.getContextPath()).thenReturn("");
        
        adminExerciseServlet.doPost(request, response);
        
        verify(exerciseService).rejectExercise(2L, 10L);
        verify(session).setAttribute("successMessage", "Exercise rejected successfully.");
        verify(response).sendRedirect("/admin/exercises");
    }

    @Test
    void doPost_MissingUserId() throws Exception {
        jakarta.servlet.http.HttpSession session = mock(jakarta.servlet.http.HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("action")).thenReturn("approve");
        when(request.getParameter("id")).thenReturn("1");
        
        when(session.getAttribute("userId")).thenReturn(null);
        
        when(request.getContextPath()).thenReturn("");
        
        adminExerciseServlet.doPost(request, response);
        
        verify(response).sendRedirect("/login.jsp");
        verify(exerciseService, never()).approveExercise(anyLong(), anyLong());
    }

    @Test
    void doPost_AlreadyApproved_SetsErrorMessageAndRedirects() throws Exception {
        jakarta.servlet.http.HttpSession session = mock(jakarta.servlet.http.HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("action")).thenReturn("approve");
        when(request.getParameter("id")).thenReturn("1");
        when(session.getAttribute("userId")).thenReturn(10);
        when(request.getContextPath()).thenReturn("");

        doThrow(new IllegalStateException("Only PENDING exercises can be moderated. Current status: APPROVED"))
                .when(exerciseService).approveExercise(1L, 10L);

        adminExerciseServlet.doPost(request, response);

        verify(session).setAttribute("errorMessage", "Only PENDING exercises can be moderated. Current status: APPROVED");
        verify(response).sendRedirect("/admin/exercises");
    }

    @Test
    void doPost_AlreadyRejected_SetsErrorMessageAndRedirects() throws Exception {
        jakarta.servlet.http.HttpSession session = mock(jakarta.servlet.http.HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("action")).thenReturn("reject");
        when(request.getParameter("id")).thenReturn("2");
        when(session.getAttribute("userId")).thenReturn(10);
        when(request.getContextPath()).thenReturn("");

        doThrow(new IllegalStateException("Only PENDING exercises can be moderated. Current status: REJECTED"))
                .when(exerciseService).rejectExercise(2L, 10L);

        adminExerciseServlet.doPost(request, response);

        verify(session).setAttribute("errorMessage", "Only PENDING exercises can be moderated. Current status: REJECTED");
        verify(response).sendRedirect("/admin/exercises");
    }

    @Test
    void doPost_NonexistentExercise_SetsErrorMessageAndRedirects() throws Exception {
        jakarta.servlet.http.HttpSession session = mock(jakarta.servlet.http.HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("action")).thenReturn("approve");
        when(request.getParameter("id")).thenReturn("999");
        when(session.getAttribute("userId")).thenReturn(10);
        when(request.getContextPath()).thenReturn("");

        doThrow(new IllegalArgumentException("Exercise not found."))
                .when(exerciseService).approveExercise(999L, 10L);

        adminExerciseServlet.doPost(request, response);

        verify(session).setAttribute("errorMessage", "Exercise not found.");
        verify(response).sendRedirect("/admin/exercises");
    }
}
