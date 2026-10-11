package com.codingplatform.service;

import com.codingplatform.dao.ExerciseDAO;
import com.codingplatform.dao.SubmissionDAO;
import com.codingplatform.dao.UserDAO;
import com.codingplatform.model.Exercise;
import com.codingplatform.model.Submission;
import com.codingplatform.model.User;
import com.codingplatform.model.Student;
import com.codingplatform.exception.DatabaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SubmissionServiceTest {

    private SubmissionService submissionService;
    private MockSubmissionDAO mockSubmissionDAO;
    private MockUserDAO2 mockUserDAO;
    private MockExerciseDAO2 mockExerciseDAO;

    @BeforeEach
    public void setup() {
        mockSubmissionDAO = new MockSubmissionDAO();
        mockUserDAO = new MockUserDAO2();
        mockExerciseDAO = new MockExerciseDAO2();
        submissionService = new SubmissionServiceImpl(mockSubmissionDAO, mockUserDAO, mockExerciseDAO);

        // Setup User
        User student = new Student();
        student.setId(1);
        student.setRole("STUDENT");
        mockUserDAO.addUser(student);

        User instructor1 = new Student();
        instructor1.setId(2);
        instructor1.setRole("INSTRUCTOR");
        mockUserDAO.addUser(instructor1);

        User instructor2 = new Student();
        instructor2.setId(3);
        instructor2.setRole("INSTRUCTOR");
        mockUserDAO.addUser(instructor2);

        // Setup Exercise
        Exercise ex1 = new Exercise();
        ex1.setId(100L);
        ex1.setCreatedBy(2);
        ex1.setStatus("APPROVED");
        mockExerciseDAO.addExercise(ex1);
        
        Exercise ex2 = new Exercise();
        ex2.setId(200L);
        ex2.setCreatedBy(3);
        ex2.setStatus("APPROVED");
        mockExerciseDAO.addExercise(ex2);
    }

    @Test
    public void testSuccessfulSubmission() throws DatabaseException {
        Submission sub = new Submission();
        sub.setExerciseId(100L);
        sub.setStudentId(1);
        sub.setSubmittedCode("print('Hello World')");
        
        boolean result = submissionService.createSubmission(sub);
        assertTrue(result);
        assertEquals("PENDING", sub.getStatus());
        assertEquals(1, mockSubmissionDAO.submissionsCreated);
    }

    @Test
    public void testEmptyCodeRejection() throws DatabaseException {
        Submission sub = new Submission();
        sub.setExerciseId(100L);
        sub.setStudentId(1);
        sub.setSubmittedCode("   ");
        
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> submissionService.createSubmission(sub));
        assertEquals("Submitted code cannot be empty.", ex.getMessage());
    }

    @Test
    public void testInvalidStudentRejection() throws DatabaseException {
        Submission sub = new Submission();
        sub.setExerciseId(100L);
        sub.setStudentId(999); // non-existent
        sub.setSubmittedCode("print('Hello World')");
        
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> submissionService.createSubmission(sub));
        assertEquals("Student must exist and be a student.", ex.getMessage());

        // Test existing non-student user
        sub.setStudentId(2); // this is an instructor
        ex = assertThrows(IllegalArgumentException.class, () -> submissionService.createSubmission(sub));
        assertEquals("Student must exist and be a student.", ex.getMessage());
    }

    @Test
    public void testInvalidExerciseRejection() throws DatabaseException {
        Submission sub = new Submission();
        sub.setExerciseId(999L); // non-existent
        sub.setStudentId(1);
        sub.setSubmittedCode("print('Hello World')");
        
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> submissionService.createSubmission(sub));
        assertEquals("Exercise must exist.", ex.getMessage());
    }

    @Test
    public void testPendingExerciseRejection() throws DatabaseException {
        Exercise pendingEx = new Exercise();
        pendingEx.setId(300L);
        pendingEx.setCreatedBy(2);
        pendingEx.setStatus("PENDING");
        mockExerciseDAO.addExercise(pendingEx);

        Submission sub = new Submission();
        sub.setExerciseId(300L);
        sub.setStudentId(1);
        sub.setSubmittedCode("print('Hello World')");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> submissionService.createSubmission(sub));
        assertEquals("Submissions are only allowed for approved exercises.", ex.getMessage());
    }

    @Test
    public void testRejectedExerciseRejection() throws DatabaseException {
        Exercise rejectedEx = new Exercise();
        rejectedEx.setId(400L);
        rejectedEx.setCreatedBy(2);
        rejectedEx.setStatus("REJECTED");
        mockExerciseDAO.addExercise(rejectedEx);

        Submission sub = new Submission();
        sub.setExerciseId(400L);
        sub.setStudentId(1);
        sub.setSubmittedCode("print('Hello World')");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> submissionService.createSubmission(sub));
        assertEquals("Submissions are only allowed for approved exercises.", ex.getMessage());
    }

    @Test
    public void testNullStatusExerciseRejection() throws DatabaseException {
        Exercise nullStatusEx = new Exercise();
        nullStatusEx.setId(500L);
        nullStatusEx.setCreatedBy(2);
        nullStatusEx.setStatus(null);
        mockExerciseDAO.addExercise(nullStatusEx);

        Submission sub = new Submission();
        sub.setExerciseId(500L);
        sub.setStudentId(1);
        sub.setSubmittedCode("print('Hello World')");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> submissionService.createSubmission(sub));
        assertEquals("Submissions are only allowed for approved exercises.", ex.getMessage());
    }

    @Test
    public void testStudentCanViewOwnSubmissions() throws DatabaseException {
        Submission sub = new Submission();
        sub.setExerciseId(100L);
        sub.setStudentId(1);
        sub.setSubmittedCode("code");
        submissionService.createSubmission(sub);

        List<Submission> list = submissionService.getSubmissionsByStudent(1);
        assertEquals(1, list.size());
    }

    @Test
    public void testInstructorCanViewSubmissionsForOwnExercise() throws DatabaseException {
        Submission sub = new Submission();
        sub.setExerciseId(100L); // created by 2
        sub.setStudentId(1);
        sub.setSubmittedCode("code");
        submissionService.createSubmission(sub);

        List<Submission> list = submissionService.getSubmissionsByInstructor(2);
        assertEquals(1, list.size());
    }

    @Test
    public void testInstructorCannotReviewAnotherInstructorExercise() throws DatabaseException {
        Submission sub = new Submission();
        sub.setId(10L);
        sub.setExerciseId(100L); // created by 2
        sub.setStudentId(1);
        sub.setSubmittedCode("code");
        mockSubmissionDAO.addSubmission(sub);

        // instructor 3 trying to review exercise created by 2
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            submissionService.reviewSubmission(10L, 3, "Good", "REVIEWED", null);
        });
        assertEquals("Instructor can only review submissions for exercises they created.", ex.getMessage());
    }

    @Test
    public void testSuccessfulFeedbackReview() throws DatabaseException {
        Submission sub = new Submission();
        sub.setId(10L);
        sub.setExerciseId(100L); // created by 2
        sub.setStudentId(1);
        sub.setSubmittedCode("code");
        mockSubmissionDAO.addSubmission(sub);

        boolean result = submissionService.reviewSubmission(10L, 2, "Great job", "REVIEWED", 95);
        assertTrue(result);
        
        Submission reviewedSub = submissionService.getSubmissionById(10L);
        assertEquals("Great job", reviewedSub.getFeedback());
        assertEquals("REVIEWED", reviewedSub.getStatus());
        assertEquals(2, reviewedSub.getReviewedBy());
        assertEquals(95, reviewedSub.getScore());
        assertNotNull(reviewedSub.getReviewedAt());
    }

    @Test
    public void testScoreOutOfBounds() throws DatabaseException {
        Submission sub = new Submission();
        sub.setId(10L);
        sub.setExerciseId(100L); // created by 2
        sub.setStudentId(1);
        sub.setSubmittedCode("code");
        mockSubmissionDAO.addSubmission(sub);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            submissionService.reviewSubmission(10L, 2, "Great job", "REVIEWED", 105);
        });
        assertEquals("Score must be between 0 and 100.", ex.getMessage());

        ex = assertThrows(IllegalArgumentException.class, () -> {
            submissionService.reviewSubmission(10L, 2, "Great job", "REVIEWED", -5);
        });
        assertEquals("Score must be between 0 and 100.", ex.getMessage());
    }

    @Test
    public void testInstructorCanViewSubmissionForOwnExercise() throws DatabaseException {
        Submission sub = new Submission();
        sub.setId(10L);
        sub.setExerciseId(100L); // created by instructor 2
        sub.setStudentId(1);
        sub.setSubmittedCode("code");
        mockSubmissionDAO.addSubmission(sub);

        Submission result = submissionService.getSubmissionForInstructor(10L, 2);
        assertNotNull(result);
        assertEquals(10L, result.getId());
    }

    @Test
    public void testInstructorCannotViewSubmissionForAnotherInstructorExercise() {
        Submission sub = new Submission();
        sub.setId(10L);
        sub.setExerciseId(100L); // created by instructor 2
        sub.setStudentId(1);
        sub.setSubmittedCode("code");
        mockSubmissionDAO.addSubmission(sub);

        SecurityException ex = assertThrows(SecurityException.class, () -> {
            submissionService.getSubmissionForInstructor(10L, 3);
        });
        assertEquals("Instructor is not authorized to view this submission.", ex.getMessage());
    }

    @Test
    public void testInstructorViewNonexistentSubmission_ReturnsNull() throws DatabaseException {
        Submission result = submissionService.getSubmissionForInstructor(999L, 2);
        assertNull(result);
    }

    @Test
    public void testInstructorViewSubmissionWithMissingExercise_ThrowsSecurityException() {
        Submission sub = new Submission();
        sub.setId(20L);
        sub.setExerciseId(9999L); // exercise does not exist
        sub.setStudentId(1);
        sub.setSubmittedCode("code");
        mockSubmissionDAO.addSubmission(sub);

        SecurityException ex = assertThrows(SecurityException.class, () -> {
            submissionService.getSubmissionForInstructor(20L, 2);
        });
        assertEquals("Instructor is not authorized to view this submission.", ex.getMessage());
    }

    @Test
    public void testInstructorViewSubmission_InvalidIds() {
        assertThrows(IllegalArgumentException.class, () -> {
            submissionService.getSubmissionForInstructor(0L, 2);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            submissionService.getSubmissionForInstructor(-1L, 2);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            submissionService.getSubmissionForInstructor(10L, 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            submissionService.getSubmissionForInstructor(10L, -1);
        });
    }
}

class MockSubmissionDAO implements SubmissionDAO {
    int submissionsCreated = 0;
    private Map<Long, Submission> db = new HashMap<>();

    public void addSubmission(Submission s) {
        db.put(s.getId(), s);
    }

    @Override
    public boolean createSubmission(Submission submission) {
        submissionsCreated++;
        submission.setId(submissionsCreated);
        db.put(submission.getId(), submission);
        return true;
    }

    @Override
    public Submission getSubmissionById(long id) {
        return db.get(id);
    }

    @Override
    public List<Submission> getSubmissionsByStudent(int studentId) {
        List<Submission> res = new ArrayList<>();
        for (Submission s : db.values()) {
            if (s.getStudentId() == studentId) res.add(s);
        }
        return res;
    }

    @Override
    public List<Submission> getSubmissionsByExercise(long exerciseId) {
        List<Submission> res = new ArrayList<>();
        for (Submission s : db.values()) {
            if (s.getExerciseId() == exerciseId) res.add(s);
        }
        return res;
    }

    @Override
    public List<Submission> getSubmissionsByInstructor(int instructorId) {
        // We cannot fully simulate this without exercise info in the DAO but we can mock enough for test
        // Let's assume exercise 100 is instructor 2
        List<Submission> res = new ArrayList<>();
        for (Submission s : db.values()) {
            if (instructorId == 2 && s.getExerciseId() == 100L) res.add(s);
            if (instructorId == 3 && s.getExerciseId() == 200L) res.add(s);
        }
        return res;
    }

    @Override
    public List<Submission> getAllSubmissions() {
        return new ArrayList<>(db.values());
    }

    @Override
    public List<Submission> getRecentSubmissions(int limit) {
        return new ArrayList<>(db.values());
    }

    @Override
    public boolean updateSubmissionReview(Submission submission) {
        db.put(submission.getId(), submission);
        return true;
    }
}

class MockUserDAO2 implements UserDAO {
    private Map<Integer, User> db = new HashMap<>();
    public void addUser(User u) { db.put(u.getId(), u); }
    @Override public User getUserById(int id) { return db.get(id); }
    @Override public User getUserByEmail(String email) { return null; }
    @Override public boolean createUser(User user) { return false; }
    @Override public List<User> getAllUsers() { return null; }
    @Override public List<User> getRecentUsers(int limit) { return null; }
    @Override public boolean updateUserRole(int userId, String newRole) { return false; }
    @Override public int countAdmins() { return 0; }
    @Override public boolean updateUserProfile(int userId, String name, String email) { return false; }
}

class MockExerciseDAO2 implements ExerciseDAO {
    private Map<Long, Exercise> db = new HashMap<>();
    public void addExercise(Exercise e) { db.put(e.getId(), e); }
    @Override public void createExercise(Exercise exercise) {}
    @Override public Exercise getExerciseById(long id) { return db.get(id); }
    @Override public List<Exercise> getAllExercises() { return null; }
    @Override public List<Exercise> getExercisesByInstructor(long instructorId) { return null; }
    @Override public void updateExercise(Exercise exercise) {}
    @Override public void deleteExercise(long id) {}
    @Override public List<Exercise> getApprovedExercises() throws DatabaseException { return null; }
    @Override public List<Exercise> getPendingExercises() throws DatabaseException { return null; }
    @Override public void updateExerciseStatus(long id, String status) throws DatabaseException {}
}
