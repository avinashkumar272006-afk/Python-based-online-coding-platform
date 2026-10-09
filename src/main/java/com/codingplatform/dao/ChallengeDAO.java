package com.codingplatform.dao;

import com.codingplatform.model.Challenge;
import com.codingplatform.exception.DatabaseException;
import java.util.List;

public interface ChallengeDAO {
    void createChallenge(Challenge challenge) throws DatabaseException;
    Challenge getChallengeById(long id) throws DatabaseException;
    List<Challenge> getAllChallenges() throws DatabaseException;
    List<Challenge> getApprovedChallenges() throws DatabaseException;
    List<Challenge> getPendingChallenges() throws DatabaseException;
    List<Challenge> getChallengesByInstructor(long instructorId) throws DatabaseException;
    void updateChallengeStatus(long id, String status) throws DatabaseException;
}
