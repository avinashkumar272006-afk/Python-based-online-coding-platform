package com.codingplatform.model;

public class AdminDashboardStats {
    private int totalUsers;
    private int totalStudents;
    private int totalInstructors;
    private int totalAdmins;
    private int totalExercises;
    private int totalSubmissions;
    private int pendingSubmissions;
    private int reviewedSubmissions;
    private int easyExercises;
    private int mediumExercises;
    private int hardExercises;
    private java.util.List<Submission> recentSubmissions;
    private java.util.List<User> recentUsers;

    public AdminDashboardStats() {}

    public AdminDashboardStats(int totalUsers, int totalStudents, int totalInstructors, int totalAdmins, int totalExercises, int totalSubmissions, int pendingSubmissions, int reviewedSubmissions, int easyExercises, int mediumExercises, int hardExercises, java.util.List<Submission> recentSubmissions, java.util.List<User> recentUsers) {
        this.totalUsers = totalUsers;
        this.totalStudents = totalStudents;
        this.totalInstructors = totalInstructors;
        this.totalAdmins = totalAdmins;
        this.totalExercises = totalExercises;
        this.totalSubmissions = totalSubmissions;
        this.pendingSubmissions = pendingSubmissions;
        this.reviewedSubmissions = reviewedSubmissions;
        this.easyExercises = easyExercises;
        this.mediumExercises = mediumExercises;
        this.hardExercises = hardExercises;
        this.recentSubmissions = recentSubmissions;
        this.recentUsers = recentUsers;
    }

    public int getTotalUsers() { return totalUsers; }
    public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }

    public int getTotalStudents() { return totalStudents; }
    public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }

    public int getTotalInstructors() { return totalInstructors; }
    public void setTotalInstructors(int totalInstructors) { this.totalInstructors = totalInstructors; }

    public int getTotalAdmins() { return totalAdmins; }
    public void setTotalAdmins(int totalAdmins) { this.totalAdmins = totalAdmins; }

    public int getTotalExercises() { return totalExercises; }
    public void setTotalExercises(int totalExercises) { this.totalExercises = totalExercises; }

    public int getTotalSubmissions() { return totalSubmissions; }
    public void setTotalSubmissions(int totalSubmissions) { this.totalSubmissions = totalSubmissions; }

    public int getPendingSubmissions() { return pendingSubmissions; }
    public void setPendingSubmissions(int pendingSubmissions) { this.pendingSubmissions = pendingSubmissions; }

    public int getReviewedSubmissions() { return reviewedSubmissions; }
    public void setReviewedSubmissions(int reviewedSubmissions) { this.reviewedSubmissions = reviewedSubmissions; }

    public int getEasyExercises() { return easyExercises; }
    public void setEasyExercises(int easyExercises) { this.easyExercises = easyExercises; }

    public int getMediumExercises() { return mediumExercises; }
    public void setMediumExercises(int mediumExercises) { this.mediumExercises = mediumExercises; }

    public int getHardExercises() { return hardExercises; }
    public void setHardExercises(int hardExercises) { this.hardExercises = hardExercises; }

    public java.util.List<Submission> getRecentSubmissions() { return recentSubmissions; }
    public void setRecentSubmissions(java.util.List<Submission> recentSubmissions) { this.recentSubmissions = recentSubmissions; }

    public java.util.List<User> getRecentUsers() { return recentUsers; }
    public void setRecentUsers(java.util.List<User> recentUsers) { this.recentUsers = recentUsers; }
}
