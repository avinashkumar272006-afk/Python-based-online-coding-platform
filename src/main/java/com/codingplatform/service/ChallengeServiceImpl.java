package com.codingplatform.service;

import com.codingplatform.dao.ChallengeDAO;
import com.codingplatform.dao.ChallengeParticipantDAO;
import com.codingplatform.model.Challenge;
import com.codingplatform.model.ChallengeParticipant;
import com.codingplatform.exception.DatabaseException;

import java.util.List;

public class ChallengeServiceImpl implements ChallengeService {
    
    private final ChallengeDAO challengeDAO;
    private final ChallengeParticipantDAO participantDAO;

    public ChallengeServiceImpl(ChallengeDAO challengeDAO, ChallengeParticipantDAO participantDAO) {
        this.challengeDAO = challengeDAO;
        this.participantDAO = participantDAO;
    }

    @Override
    public void createChallenge(Challenge challenge, long instructorId) throws DatabaseException, IllegalArgumentException {
        if (challenge.getTitle() == null || challenge.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (challenge.getDescription() == null || challenge.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (!"EASY".equals(challenge.getDifficulty()) && !"MEDIUM".equals(challenge.getDifficulty()) && !"HARD".equals(challenge.getDifficulty())) {
            throw new IllegalArgumentException("Invalid difficulty");
        }
        if (challenge.getStartDate() != null && challenge.getEndDate() != null) {
            if (challenge.getStartDate().after(challenge.getEndDate())) {
                throw new IllegalArgumentException("Start date must be before or equal to end date");
            }
        }
        
        challenge.setCreatedBy(instructorId);
        challenge.setStatus("PENDING");
        
        challengeDAO.createChallenge(challenge);
    }

    @Override
    public List<Challenge> getInstructorChallenges(long instructorId) throws DatabaseException {
        return challengeDAO.getChallengesByInstructor(instructorId);
    }

    @Override
    public List<Challenge> getAllChallenges() throws DatabaseException {
        return challengeDAO.getAllChallenges();
    }

    @Override
    public List<Challenge> getApprovedChallenges() throws DatabaseException {
        return challengeDAO.getApprovedChallenges();
    }

    @Override
    public void updateChallengeStatus(long challengeId, String status, long adminId) throws DatabaseException, IllegalArgumentException {
        if (!"APPROVED".equals(status) && !"REJECTED".equals(status) && !"PENDING".equals(status)) {
            throw new IllegalArgumentException("Invalid status");
        }
        Challenge challenge = challengeDAO.getChallengeById(challengeId);
        if (challenge == null) {
            throw new IllegalArgumentException("Challenge not found");
        }
        challengeDAO.updateChallengeStatus(challengeId, status);
    }

    @Override
    public void joinChallenge(long challengeId, long studentId) throws DatabaseException, IllegalArgumentException {
        Challenge challenge = challengeDAO.getChallengeById(challengeId);
        if (challenge == null) {
            throw new IllegalArgumentException("Challenge not found");
        }
        if (!"APPROVED".equals(challenge.getStatus())) {
            throw new IllegalArgumentException("Can only join approved challenges");
        }
        
        long now = System.currentTimeMillis();
        if (challenge.getStartDate() != null && now < challenge.getStartDate().getTime()) {
            throw new IllegalArgumentException("Challenge has not started yet");
        }
        if (challenge.getEndDate() != null && now > challenge.getEndDate().getTime()) {
            throw new IllegalArgumentException("Challenge has already ended");
        }
        
        ChallengeParticipant cp = new ChallengeParticipant();
        cp.setChallengeId(challengeId);
        cp.setStudentId(studentId);
        cp.setStatus("JOINED");
        
        participantDAO.createParticipant(cp);
    }

    @Override
    public List<ChallengeParticipant> getStudentChallenges(long studentId) throws DatabaseException {
        return participantDAO.getParticipantsByStudent(studentId);
    }

    @Override
    public void updateParticipantStatus(long challengeId, long studentId, String status) throws DatabaseException, IllegalArgumentException {
        if (!"COMPLETED".equals(status) && !"DROPPED".equals(status)) {
            throw new IllegalArgumentException("Invalid status");
        }
        ChallengeParticipant cp = participantDAO.getParticipant(challengeId, studentId);
        if (cp == null) {
            throw new IllegalArgumentException("Not joined this challenge");
        }
        participantDAO.updateParticipantStatus(challengeId, studentId, status);
    }
}
