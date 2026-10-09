package com.codingplatform.model;

import java.sql.Timestamp;

public class Exercise {
    private long id;
    private String title;
    private String description;
    private String difficulty;
    private String topic;
    private String starterCode;
    private String expectedOutput;
    private long createdBy;
    private Timestamp createdAt;
    private String status;

    public Exercise() {}

    public Exercise(long id, String title, String description, String difficulty, String topic,
                    String starterCode, String expectedOutput, long createdBy, Timestamp createdAt, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.topic = topic;
        this.starterCode = starterCode;
        this.expectedOutput = expectedOutput;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getStarterCode() {
        return starterCode;
    }

    public void setStarterCode(String starterCode) {
        this.starterCode = starterCode;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
