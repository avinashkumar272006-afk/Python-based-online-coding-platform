package com.codingplatform.servlet;

import com.codingplatform.model.Submission;
import com.codingplatform.service.SubmissionService;

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

public class AdminSubmissionServletTest {

    @Mock
    private SubmissionService submissionService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher requestDispatcher;

    @InjectMocks
    private AdminSubmissionServlet adminSubmissionServlet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        java.lang.reflect.Field serviceField = AdminSubmissionServlet.class.getDeclaredField("submissionService");
        serviceField.setAccessible(true);
        serviceField.set(adminSubmissionServlet, submissionService);
    }

    @Test
    void doGet_ShouldSetSubmissionsAndForwardToJsp() throws Exception {
        List<Submission> submissions = new ArrayList<>();
        submissions.add(new Submission());
        
        when(submissionService.getAllSubmissions()).thenReturn(submissions);
        when(request.getRequestDispatcher("/admin/submissions.jsp")).thenReturn(requestDispatcher);

        adminSubmissionServlet.doGet(request, response);

        verify(submissionService, times(1)).getAllSubmissions();
        verify(request, times(1)).setAttribute("submissions", submissions);
        verify(requestDispatcher, times(1)).forward(request, response);
    }
}
