package com.codingplatform.service;

import com.codingplatform.dao.ExerciseDAO;
import com.codingplatform.dao.SubmissionDAO;
import com.codingplatform.dao.UserDAO;
import com.codingplatform.model.Exercise;
import com.codingplatform.model.Submission;
import com.codingplatform.model.User;
import com.codingplatform.exception.DatabaseException;

import java.sql.Timestamp;
import java.util.List;

public class SubmissionServiceImpl implements SubmissionService {

    private SubmissionDAO submissionDAO;
    private UserDAO userDAO;
    private ExerciseDAO exerciseDAO;

    public SubmissionServiceImpl(SubmissionDAO submissionDAO, UserDAO userDAO, ExerciseDAO exerciseDAO) {
        this.submissionDAO = submissionDAO;
        this.userDAO = userDAO;
        this.exerciseDAO = exerciseDAO;
    }

    @Override
    public boolean createSubmission(Submission submission) throws DatabaseException {
        if (submission.getSubmittedCode() == null || submission.getSubmittedCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Submitted code cannot be empty.");
        }

        User student = userDAO.getUserById(submission.getStudentId());
        if (student == null || !"STUDENT".equals(student.getRole())) {
            throw new IllegalArgumentException("Student must exist and be a student.");
        }

        Exercise exercise = exerciseDAO.getExerciseById(submission.getExerciseId());
        if (exercise == null) {
            throw new IllegalArgumentException("Exercise must exist.");
        }

        submission.setStatus("PENDING");
        return submissionDAO.createSubmission(submission);
    }

    @Override
    public Submission getSubmissionById(long id) throws DatabaseException {
        return submissionDAO.getSubmissionById(id);
    }

    @Override
    public List<Submission> getSubmissionsByStudent(int studentId) throws DatabaseException {
        return submissionDAO.getSubmissionsByStudent(studentId);
    }

    @Override
    public List<Submission> getSubmissionsByExercise(long exerciseId) throws DatabaseException {
        return submissionDAO.getSubmissionsByExercise(exerciseId);
    }

    @Override
    public List<Submission> getSubmissionsByInstructor(int instructorId) throws DatabaseException {
        return submissionDAO.getSubmissionsByInstructor(instructorId);
    }

    @Override
    public List<Submission> getAllSubmissions() throws DatabaseException {
        return submissionDAO.getAllSubmissions();
    }

    @Override
    public boolean reviewSubmission(long submissionId, int instructorId, String feedback, String status, Integer score) throws DatabaseException {
        Submission submission = submissionDAO.getSubmissionById(submissionId);
        if (submission == null) {
            throw new IllegalArgumentException("Submission does not exist.");
        }

        Exercise exercise = exerciseDAO.getExerciseById(submission.getExerciseId());
        if (exercise == null || exercise.getCreatedBy() != instructorId) {
            throw new IllegalArgumentException("Instructor can only review submissions for exercises they created.");
        }

        if (status == null || status.trim().isEmpty()) {
            status = "REVIEWED";
        }
        
        if (score != null && (score < 0 || score > 100)) {
            throw new IllegalArgumentException("Score must be between 0 and 100.");
        }

        submission.setFeedback(feedback);
        submission.setStatus(status);
        submission.setScore(score);
        submission.setReviewedBy(instructorId);
        submission.setReviewedAt(new Timestamp(System.currentTimeMillis()));

        return submissionDAO.updateSubmissionReview(submission);
    }
}
