package com.codingplatform.servlet;

import com.codingplatform.model.Exercise;
import com.codingplatform.service.ExerciseService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class InstructorExerciseServletTest {

    private InstructorExerciseServlet servlet;

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

    private static final long INSTRUCTOR_ID = 10L;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new InstructorExerciseServlet(exerciseService);

        when(request.getSession()).thenReturn(session);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(INSTRUCTOR_ID);
        when(session.getAttribute("role")).thenReturn("INSTRUCTOR");
        when(request.getContextPath()).thenReturn("");
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    public void testShowEditForm_AuthorizedInstructorCanAccessOwnExercise() throws Exception {
        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("id")).thenReturn("1");

        Exercise ownExercise = new Exercise();
        ownExercise.setId(1L);
        ownExercise.setTitle("Binary Search");
        ownExercise.setDescription("Implement binary search");
        ownExercise.setStarterCode("def binary_search(): pass");
        ownExercise.setExpectedOutput("[1, 2, 3]");
        ownExercise.setCreatedBy(INSTRUCTOR_ID);

        when(exerciseService.getExerciseById(1L)).thenReturn(ownExercise);

        servlet.doGet(request, response);

        verify(exerciseService).getExerciseById(1L);
        verify(request).setAttribute("exercise", ownExercise);
        verify(request).getRequestDispatcher("/instructor/edit-exercise.jsp");
        verify(dispatcher).forward(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void testShowEditForm_InstructorCannotAccessAnotherInstructorExercise() throws Exception {
        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("id")).thenReturn("2");

        Exercise otherInstructorExercise = new Exercise();
        otherInstructorExercise.setId(2L);
        otherInstructorExercise.setTitle("Secret Exercise");
        otherInstructorExercise.setDescription("Confidential description");
        otherInstructorExercise.setStarterCode("secret starter code");
        otherInstructorExercise.setExpectedOutput("secret output");
        otherInstructorExercise.setCreatedBy(99L); // Different instructor

        when(exerciseService.getExerciseById(2L)).thenReturn(otherInstructorExercise);

        servlet.doGet(request, response);

        verify(exerciseService).getExerciseById(2L);
        // Sensitive details must NOT be set as request attribute or forwarded
        verify(request, never()).setAttribute(eq("exercise"), any());
        verify(dispatcher, never()).forward(request, response);
        verify(response).sendRedirect("/instructor/exercises");
    }

    @Test
    public void testShowEditForm_NonexistentExerciseHandledGracefully() throws Exception {
        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("id")).thenReturn("999");

        when(exerciseService.getExerciseById(999L)).thenReturn(null);

        servlet.doGet(request, response);

        verify(exerciseService).getExerciseById(999L);
        verify(request, never()).setAttribute(eq("exercise"), any());
        verify(dispatcher, never()).forward(request, response);
        verify(response).sendRedirect("/instructor/exercises");
    }

    @Test
    public void testShowEditForm_MissingIdHandledGracefully() throws Exception {
        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("id")).thenReturn(null);

        servlet.doGet(request, response);

        verify(exerciseService, never()).getExerciseById(anyLong());
        verify(request, never()).setAttribute(eq("exercise"), any());
        verify(dispatcher, never()).forward(request, response);
        verify(response).sendRedirect("/instructor/exercises");
    }

    @Test
    public void testShowEditForm_EmptyIdHandledGracefully() throws Exception {
        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("id")).thenReturn("   ");

        servlet.doGet(request, response);

        verify(exerciseService, never()).getExerciseById(anyLong());
        verify(request, never()).setAttribute(eq("exercise"), any());
        verify(dispatcher, never()).forward(request, response);
        verify(response).sendRedirect("/instructor/exercises");
    }

    @Test
    public void testShowEditForm_MalformedIdHandledGracefully() throws Exception {
        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("id")).thenReturn("abc");

        servlet.doGet(request, response);

        verify(exerciseService, never()).getExerciseById(anyLong());
        verify(request, never()).setAttribute(eq("exercise"), any());
        verify(dispatcher, never()).forward(request, response);
        verify(response).sendRedirect("/instructor/exercises");
    }

    @Test
    public void testShowEditForm_NegativeOrZeroIdHandledGracefully() throws Exception {
        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("id")).thenReturn("0");

        servlet.doGet(request, response);

        verify(exerciseService, never()).getExerciseById(anyLong());
        verify(request, never()).setAttribute(eq("exercise"), any());
        verify(dispatcher, never()).forward(request, response);
        verify(response).sendRedirect("/instructor/exercises");
    }

    @Test
    public void testShowEditForm_UnauthenticatedRedirectsToLogin() throws Exception {
        when(session.getAttribute("userId")).thenReturn(null);
        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("id")).thenReturn("1");

        servlet.doGet(request, response);

        verify(exerciseService, never()).getExerciseById(anyLong());
        verify(request, never()).setAttribute(eq("exercise"), any());
        verify(dispatcher, never()).forward(request, response);
        verify(response).sendRedirect("/login.jsp");
    }

    @Test
    public void testListExercises() throws Exception {
        when(request.getParameter("action")).thenReturn("list");
        List<Exercise> exercises = Collections.singletonList(new Exercise());
        when(exerciseService.getExercisesByInstructor(INSTRUCTOR_ID)).thenReturn(exercises);

        servlet.doGet(request, response);

        verify(exerciseService).getExercisesByInstructor(INSTRUCTOR_ID);
        verify(request).setAttribute("exercises", exercises);
        verify(request).getRequestDispatcher("/instructor/exercises.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testShowNewForm() throws Exception {
        when(request.getParameter("action")).thenReturn("new");

        servlet.doGet(request, response);

        verify(request).getRequestDispatcher("/instructor/create-exercise.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testCreateExercise_Success() throws Exception {
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("title")).thenReturn("New Ex");
        when(request.getParameter("description")).thenReturn("Desc");
        when(request.getParameter("difficulty")).thenReturn("EASY");
        when(request.getParameter("topic")).thenReturn("Loops");
        when(request.getParameter("starterCode")).thenReturn("code");
        when(request.getParameter("expectedOutput")).thenReturn("out");

        servlet.doPost(request, response);

        verify(exerciseService).createExercise(any(Exercise.class));
        verify(response).sendRedirect("/instructor/exercises");
    }

    @Test
    public void testUpdateExercise_Success() throws Exception {
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("title")).thenReturn("Updated Ex");
        when(request.getParameter("description")).thenReturn("Updated Desc");
        when(request.getParameter("difficulty")).thenReturn("MEDIUM");
        when(request.getParameter("topic")).thenReturn("Loops");
        when(request.getParameter("starterCode")).thenReturn("updated code");
        when(request.getParameter("expectedOutput")).thenReturn("updated out");

        servlet.doPost(request, response);

        verify(exerciseService).updateExercise(any(Exercise.class), eq(INSTRUCTOR_ID));
        verify(response).sendRedirect("/instructor/exercises");
    }

    @Test
    public void testDeleteExercise_Success() throws Exception {
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("id")).thenReturn("1");

        servlet.doPost(request, response);

        verify(exerciseService).deleteExercise(1L, INSTRUCTOR_ID);
        verify(response).sendRedirect("/instructor/exercises");
    }
}
