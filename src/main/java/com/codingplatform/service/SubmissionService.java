package com.codingplatform.service;

import com.codingplatform.model.Submission;
import com.codingplatform.exception.DatabaseException;
import java.util.List;

public interface SubmissionService {
    boolean createSubmission(Submission submission) throws DatabaseException;
    Submission getSubmissionById(long id) throws DatabaseException;
    List<Submission> getSubmissionsByStudent(int studentId) throws DatabaseException;
    List<Submission> getSubmissionsByExercise(long exerciseId) throws DatabaseException;
    List<Submission> getSubmissionsByInstructor(int instructorId) throws DatabaseException;
    List<Submission> getAllSubmissions() throws DatabaseException;
    boolean reviewSubmission(long submissionId, int instructorId, String feedback, String status, Integer score) throws DatabaseException;
}
