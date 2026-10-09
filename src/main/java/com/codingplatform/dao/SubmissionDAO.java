package com.codingplatform.dao;

import com.codingplatform.model.Submission;
import java.util.List;

public interface SubmissionDAO {
    boolean createSubmission(Submission submission);
    Submission getSubmissionById(long id);
    List<Submission> getSubmissionsByStudent(int studentId);
    List<Submission> getSubmissionsByExercise(long exerciseId);
    List<Submission> getSubmissionsByInstructor(int instructorId);
    List<Submission> getAllSubmissions();
    List<Submission> getRecentSubmissions(int limit);
    boolean updateSubmissionReview(Submission submission);
}
