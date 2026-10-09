package com.codingplatform.model;

import java.sql.Timestamp;

public class ChallengeParticipant {
    private long id;
    private long challengeId;
    private long studentId;
    private Timestamp joinedAt;
    private String status;
    private Timestamp completedAt;
    
    // For convenience in UI, we might want to store the challenge title
    private String challengeTitle; 

    public ChallengeParticipant() {}

    public ChallengeParticipant(long id, long challengeId, long studentId, Timestamp joinedAt, String status, Timestamp completedAt) {
        this.id = id;
        this.challengeId = challengeId;
        this.studentId = studentId;
        this.joinedAt = joinedAt;
        this.status = status;
        this.completedAt = completedAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getChallengeId() { return challengeId; }
    public void setChallengeId(long challengeId) { this.challengeId = challengeId; }
    public long getStudentId() { return studentId; }
    public void setStudentId(long studentId) { this.studentId = studentId; }
    public Timestamp getJoinedAt() { return joinedAt; }
    public void setJoinedAt(Timestamp joinedAt) { this.joinedAt = joinedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCompletedAt() { return completedAt; }
    public void setCompletedAt(Timestamp completedAt) { this.completedAt = completedAt; }
    public String getChallengeTitle() { return challengeTitle; }
    public void setChallengeTitle(String challengeTitle) { this.challengeTitle = challengeTitle; }
}
