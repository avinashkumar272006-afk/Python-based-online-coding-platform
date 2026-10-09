package com.codingplatform.service;

import com.codingplatform.dao.ChallengeDAO;
import com.codingplatform.dao.ChallengeParticipantDAO;
import com.codingplatform.model.Challenge;
import com.codingplatform.model.ChallengeParticipant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ChallengeServiceTest {

    private ChallengeDAO challengeDAO;
    private ChallengeParticipantDAO participantDAO;
    private ChallengeService challengeService;

    @BeforeEach
    public void setUp() {
        challengeDAO = mock(ChallengeDAO.class);
        participantDAO = mock(ChallengeParticipantDAO.class);
        challengeService = new ChallengeServiceImpl(challengeDAO, participantDAO);
    }

    @Test
    public void testCreateChallenge_Valid() throws Exception {
        Challenge c = new Challenge();
        c.setTitle("Test");
        c.setDescription("Desc");
        c.setDifficulty("EASY");
        
        challengeService.createChallenge(c, 1L);
        
        verify(challengeDAO).createChallenge(c);
        assertEquals(1L, c.getCreatedBy());
        assertEquals("PENDING", c.getStatus());
    }

    @Test
    public void testCreateChallenge_InvalidDifficulty() {
        Challenge c = new Challenge();
        c.setTitle("Test");
        c.setDescription("Desc");
        c.setDifficulty("INVALID");
        
        assertThrows(IllegalArgumentException.class, () -> challengeService.createChallenge(c, 1L));
    }

    @Test
    public void testCreateChallenge_InvalidDates() {
        Challenge c = new Challenge();
        c.setTitle("Test");
        c.setDescription("Desc");
        c.setDifficulty("EASY");
        c.setStartDate(new Timestamp(System.currentTimeMillis() + 10000));
        c.setEndDate(new Timestamp(System.currentTimeMillis())); // End before start
        
        assertThrows(IllegalArgumentException.class, () -> challengeService.createChallenge(c, 1L));
    }

    @Test
    public void testUpdateChallengeStatus_AdminCanApproveReject() throws Exception {
        Challenge c = new Challenge();
        c.setId(1L);
        when(challengeDAO.getChallengeById(1L)).thenReturn(c);
        
        challengeService.updateChallengeStatus(1L, "APPROVED", 100L);
        verify(challengeDAO).updateChallengeStatus(1L, "APPROVED");
    }

    @Test
    public void testUpdateChallengeStatus_InvalidStatus() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> challengeService.updateChallengeStatus(1L, "INVALID", 100L));
    }

    @Test
    public void testJoinChallenge_Success() throws Exception {
        Challenge c = new Challenge();
        c.setId(1L);
        c.setStatus("APPROVED");
        when(challengeDAO.getChallengeById(1L)).thenReturn(c);
        
        challengeService.joinChallenge(1L, 2L);
        verify(participantDAO).createParticipant(any(ChallengeParticipant.class));
    }

    @Test
    public void testJoinChallenge_Unapproved() throws Exception {
        Challenge c = new Challenge();
        c.setId(1L);
        c.setStatus("PENDING");
        when(challengeDAO.getChallengeById(1L)).thenReturn(c);
        
        assertThrows(IllegalArgumentException.class, () -> challengeService.joinChallenge(1L, 2L));
    }

    @Test
    public void testJoinChallenge_BeforeStartDate() throws Exception {
        Challenge c = new Challenge();
        c.setId(1L);
        c.setStatus("APPROVED");
        c.setStartDate(new Timestamp(System.currentTimeMillis() + 100000));
        when(challengeDAO.getChallengeById(1L)).thenReturn(c);
        
        assertThrows(IllegalArgumentException.class, () -> challengeService.joinChallenge(1L, 2L));
    }

    @Test
    public void testUpdateParticipantStatus_Complete() throws Exception {
        ChallengeParticipant cp = new ChallengeParticipant();
        when(participantDAO.getParticipant(1L, 2L)).thenReturn(cp);
        
        challengeService.updateParticipantStatus(1L, 2L, "COMPLETED");
        verify(participantDAO).updateParticipantStatus(1L, 2L, "COMPLETED");
    }
}
