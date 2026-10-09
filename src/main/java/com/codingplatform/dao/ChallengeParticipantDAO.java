package com.codingplatform.dao;

import com.codingplatform.model.ChallengeParticipant;
import com.codingplatform.exception.DatabaseException;
import java.util.List;

public interface ChallengeParticipantDAO {
    void createParticipant(ChallengeParticipant participant) throws DatabaseException;
    ChallengeParticipant getParticipant(long challengeId, long studentId) throws DatabaseException;
    List<ChallengeParticipant> getParticipantsByStudent(long studentId) throws DatabaseException;
    void updateParticipantStatus(long challengeId, long studentId, String status) throws DatabaseException;
}
