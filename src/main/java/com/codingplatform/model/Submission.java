package com.codingplatform.model;

import java.sql.Timestamp;

public class Submission {
    private long id;
    private long exerciseId;
    private int studentId;
    private String submittedCode;
    private String status;
    private String feedback;
    private Timestamp submittedAt;
    private Timestamp reviewedAt;
    private Integer reviewedBy; // Can be null if not reviewed
    private Integer score; // Can be null if not graded

    // Additional fields for convenience in UI
    private String studentName;
    private String exerciseTitle;

    public Submission() {}

    public Submission(long id, long exerciseId, int studentId, String submittedCode, String status,
                      String feedback, Timestamp submittedAt, Timestamp reviewedAt, Integer reviewedBy, Integer score) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.studentId = studentId;
        this.submittedCode = submittedCode;
        this.status = status;
        this.feedback = feedback;
        this.submittedAt = submittedAt;
        this.reviewedAt = reviewedAt;
        this.reviewedBy = reviewedBy;
        this.score = score;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getSubmittedCode() {
        return submittedCode;
    }

    public void setSubmittedCode(String submittedCode) {
        this.submittedCode = submittedCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Timestamp getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Timestamp submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Timestamp getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Timestamp reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public Integer getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Integer reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getExerciseTitle() {
        return exerciseTitle;
    }

    public void setExerciseTitle(String exerciseTitle) {
        this.exerciseTitle = exerciseTitle;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
