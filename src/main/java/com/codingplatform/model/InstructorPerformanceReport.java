package com.codingplatform.model;

public class InstructorPerformanceReport {
    private int totalSubmissions;
    private int pendingSubmissions;
    private int reviewedSubmissions;
    private Double averageScore;
    private Integer highestScore;
    private Integer lowestScore;

    public InstructorPerformanceReport() {}

    public int getTotalSubmissions() { return totalSubmissions; }
    public void setTotalSubmissions(int totalSubmissions) { this.totalSubmissions = totalSubmissions; }

    public int getPendingSubmissions() { return pendingSubmissions; }
    public void setPendingSubmissions(int pendingSubmissions) { this.pendingSubmissions = pendingSubmissions; }

    public int getReviewedSubmissions() { return reviewedSubmissions; }
    public void setReviewedSubmissions(int reviewedSubmissions) { this.reviewedSubmissions = reviewedSubmissions; }

    public Double getAverageScore() { return averageScore; }
    public void setAverageScore(Double averageScore) { this.averageScore = averageScore; }

    public Integer getHighestScore() { return highestScore; }
    public void setHighestScore(Integer highestScore) { this.highestScore = highestScore; }

    public Integer getLowestScore() { return lowestScore; }
    public void setLowestScore(Integer lowestScore) { this.lowestScore = lowestScore; }
}
