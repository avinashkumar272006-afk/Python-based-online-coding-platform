package com.codingplatform.service;

import com.codingplatform.model.Challenge;
import com.codingplatform.model.ChallengeParticipant;
import com.codingplatform.exception.DatabaseException;

import java.util.List;

public interface ChallengeService {
    void createChallenge(Challenge challenge, long instructorId) throws DatabaseException, IllegalArgumentException;
    List<Challenge> getInstructorChallenges(long instructorId) throws DatabaseException;
    List<Challenge> getAllChallenges() throws DatabaseException;
    List<Challenge> getApprovedChallenges() throws DatabaseException;
    void updateChallengeStatus(long challengeId, String status, long adminId) throws DatabaseException, IllegalArgumentException, IllegalStateException;
    
    void joinChallenge(long challengeId, long studentId) throws DatabaseException, IllegalArgumentException;
    List<ChallengeParticipant> getStudentChallenges(long studentId) throws DatabaseException;
    void updateParticipantStatus(long challengeId, long studentId, String status) throws DatabaseException, IllegalArgumentException;
}
