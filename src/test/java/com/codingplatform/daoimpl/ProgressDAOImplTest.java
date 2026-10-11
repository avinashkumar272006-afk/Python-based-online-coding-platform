package com.codingplatform.daoimpl;

import com.codingplatform.model.StudentProgress;
import com.codingplatform.exception.DatabaseException;
import com.codingplatform.util.DBConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ProgressDAOImplTest {

    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    public void setUp() {
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
    }

    @Test
    public void testProgressQueryOnlyCountsApprovedExercisesAndDeduplicatesSubmissions() throws Exception {
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        when(mockConnection.prepareStatement(sqlCaptor.capture())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("total_exercises")).thenReturn(10);
        when(mockResultSet.getInt("submitted_exercises")).thenReturn(4);
        when(mockResultSet.getInt("reviewed_submissions")).thenReturn(3);
        when(mockResultSet.getInt("pending_submissions")).thenReturn(1);
        when(mockResultSet.getDouble("average_score")).thenReturn(88.5);
        when(mockResultSet.wasNull()).thenReturn(false);

        ProgressDAOImpl dao = new ProgressDAOImpl(mockConnection);
        StudentProgress progress = dao.getStudentProgress(42);

        assertNotNull(progress);
        assertEquals(10, progress.getTotalExercises());
        assertEquals(4, progress.getSubmittedExercises());
        assertEquals(3, progress.getReviewedSubmissions());
        assertEquals(1, progress.getPendingSubmissions());
        assertEquals(88.5, progress.getAverageScore());
        assertEquals(40, progress.getProgressPercentage());

        // Verify the exact SQL query guarantees
        String executedSql = sqlCaptor.getValue();
        assertTrue(executedSql.contains("(SELECT COUNT(*) FROM exercises WHERE status = 'APPROVED') AS total_exercises"),
                "total_exercises query must filter exclusively for APPROVED status");
        assertTrue(executedSql.contains("COUNT(DISTINCT s.exercise_id)"),
                "submitted_exercises must use COUNT(DISTINCT ...) so duplicate submissions do not increase distinct exercise count");
        assertTrue(executedSql.contains("JOIN exercises e ON s.exercise_id = e.id") && executedSql.contains("e.status = 'APPROVED'"),
                "submitted_exercises must join exercises table and require status = 'APPROVED'");

        // Verify parameter bindings
        verify(mockStatement).setInt(1, 42);
        verify(mockStatement).setInt(2, 42);
        verify(mockStatement).setInt(3, 42);
        verify(mockStatement).setInt(4, 42);
    }

    @Test
    public void testProgressQueryZeroApprovedExercisesHandlesSafely() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("total_exercises")).thenReturn(0);
        when(mockResultSet.getInt("submitted_exercises")).thenReturn(0);
        when(mockResultSet.getInt("reviewed_submissions")).thenReturn(0);
        when(mockResultSet.getInt("pending_submissions")).thenReturn(0);
        when(mockResultSet.getDouble("average_score")).thenReturn(0.0);
        when(mockResultSet.wasNull()).thenReturn(true);

        ProgressDAOImpl dao = new ProgressDAOImpl(mockConnection);
        StudentProgress progress = dao.getStudentProgress(42);

        assertNotNull(progress);
        assertEquals(0, progress.getTotalExercises());
        assertEquals(0, progress.getSubmittedExercises());
        assertNull(progress.getAverageScore());
        assertEquals(0, progress.getProgressPercentage(), "Zero approved exercises must yield 0% progress without division-by-zero error");
    }

    @Test
    public void testProgressQueryWhenNoRowReturned() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        ProgressDAOImpl dao = new ProgressDAOImpl(mockConnection);
        StudentProgress progress = dao.getStudentProgress(42);

        assertNotNull(progress);
        assertEquals(0, progress.getTotalExercises());
        assertEquals(0, progress.getSubmittedExercises());
        assertEquals(0, progress.getProgressPercentage());
    }

    @Test
    public void testProgressQueryWrapsSQLExceptionInDatabaseException() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB connection lost"));

        ProgressDAOImpl dao = new ProgressDAOImpl(mockConnection);
        DatabaseException ex = assertThrows(DatabaseException.class, () -> dao.getStudentProgress(42));
        assertEquals("Error retrieving student progress", ex.getMessage());
        assertTrue(ex.getCause() instanceof SQLException);
    }

    @Test
    public void testLiveDatabaseQueryIfConfigured() {
        assumeTrue(Boolean.getBoolean("live.db.test"),
                "Skipping live database progress query: opt-in flag -Dlive.db.test=true or -Plive-db not enabled");

        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        if (url == null || user == null || password == null) {
            System.out.println("Skipping live database test: DB credentials not configured in environment.");
            return;
        }

        try {
            ProgressDAOImpl liveDao = new ProgressDAOImpl();
            StudentProgress progress = liveDao.getStudentProgress(1);
            assertNotNull(progress);
            assertTrue(progress.getTotalExercises() >= 0, "Total exercises must be non-negative");
            assertTrue(progress.getSubmittedExercises() >= 0, "Submitted exercises must be non-negative");
            assertTrue(progress.getSubmittedExercises() <= progress.getTotalExercises(),
                    "Submitted approved exercises cannot exceed total approved exercises");
            assertTrue(progress.getProgressPercentage() >= 0 && progress.getProgressPercentage() <= 100,
                    "Progress percentage must be between 0 and 100");
        } catch (DatabaseException e) {
            fail("Live database progress query failed: " + e.getMessage());
        }
    }
}
