package com.codingplatform.model;

public class StudentProgress {
    private int totalExercises;
    private int submittedExercises;
    private int reviewedSubmissions;
    private int pendingSubmissions;
    private Double averageScore;

    public StudentProgress() {}

    public StudentProgress(int totalExercises, int submittedExercises, int reviewedSubmissions, int pendingSubmissions, Double averageScore) {
        this.totalExercises = totalExercises;
        this.submittedExercises = submittedExercises;
        this.reviewedSubmissions = reviewedSubmissions;
        this.pendingSubmissions = pendingSubmissions;
        this.averageScore = averageScore;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public int getTotalExercises() {
        return totalExercises;
    }

    public void setTotalExercises(int totalExercises) {
        this.totalExercises = totalExercises;
    }

    public int getSubmittedExercises() {
        return submittedExercises;
    }

    public void setSubmittedExercises(int submittedExercises) {
        this.submittedExercises = submittedExercises;
    }

    public int getReviewedSubmissions() {
        return reviewedSubmissions;
    }

    public void setReviewedSubmissions(int reviewedSubmissions) {
        this.reviewedSubmissions = reviewedSubmissions;
    }

    public int getPendingSubmissions() {
        return pendingSubmissions;
    }

    public void setPendingSubmissions(int pendingSubmissions) {
        this.pendingSubmissions = pendingSubmissions;
    }

    public int getProgressPercentage() {
        if (totalExercises == 0) {
            return 0;
        }
        return (int) (((double) submittedExercises / totalExercises) * 100);
    }
}
