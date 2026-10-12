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
    public void testUpdateChallengeStatus_PendingToApproved() throws Exception {
        Challenge c = new Challenge();
        c.setId(1L);
        c.setStatus("PENDING");
        when(challengeDAO.getChallengeById(1L)).thenReturn(c);
        when(challengeDAO.updateChallengeStatusIfPending(1L, "APPROVED")).thenReturn(true);

        challengeService.updateChallengeStatus(1L, "APPROVED", 100L);
        verify(challengeDAO).updateChallengeStatusIfPending(1L, "APPROVED");
    }

    @Test
    public void testUpdateChallengeStatus_PendingToRejected() throws Exception {
        Challenge c = new Challenge();
        c.setId(1L);
        c.setStatus("PENDING");
        when(challengeDAO.getChallengeById(1L)).thenReturn(c);
        when(challengeDAO.updateChallengeStatusIfPending(1L, "REJECTED")).thenReturn(true);

        challengeService.updateChallengeStatus(1L, "REJECTED", 100L);
        verify(challengeDAO).updateChallengeStatusIfPending(1L, "REJECTED");
    }

    @Test
    public void testUpdateChallengeStatus_AlreadyApproved_ThrowsException() throws Exception {
        Challenge c = new Challenge();
        c.setId(1L);
        c.setStatus("APPROVED");
        when(challengeDAO.getChallengeById(1L)).thenReturn(c);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
            challengeService.updateChallengeStatus(1L, "APPROVED", 100L)
        );
        assertTrue(ex.getMessage().contains("Only PENDING challenges can be moderated"));
        verify(challengeDAO, never()).updateChallengeStatusIfPending(anyLong(), anyString());
    }

    @Test
    public void testUpdateChallengeStatus_AlreadyRejected_ThrowsException() throws Exception {
        Challenge c = new Challenge();
        c.setId(1L);
        c.setStatus("REJECTED");
        when(challengeDAO.getChallengeById(1L)).thenReturn(c);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
            challengeService.updateChallengeStatus(1L, "REJECTED", 100L)
        );
        assertTrue(ex.getMessage().contains("Only PENDING challenges can be moderated"));
        verify(challengeDAO, never()).updateChallengeStatusIfPending(anyLong(), anyString());
    }

    @Test
    public void testUpdateChallengeStatus_NotFound_ThrowsException() throws Exception {
        when(challengeDAO.getChallengeById(1L)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            challengeService.updateChallengeStatus(1L, "APPROVED", 100L)
        );
        assertEquals("Challenge not found", ex.getMessage());
        verify(challengeDAO, never()).updateChallengeStatusIfPending(anyLong(), anyString());
    }

    @Test
    public void testUpdateChallengeStatus_ConcurrentUpdate_ThrowsException() throws Exception {
        Challenge c = new Challenge();
        c.setId(1L);
        c.setStatus("PENDING");

        Challenge updated = new Challenge();
        updated.setId(1L);
        updated.setStatus("APPROVED");

        when(challengeDAO.getChallengeById(1L)).thenReturn(c, updated);
        when(challengeDAO.updateChallengeStatusIfPending(1L, "APPROVED")).thenReturn(false);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
            challengeService.updateChallengeStatus(1L, "APPROVED", 100L)
        );
        assertTrue(ex.getMessage().contains("no longer PENDING"));
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
        Challenge c = new Challenge();
        c.setId(1L);
        c.setStatus("APPROVED");
        when(challengeDAO.getChallengeById(1L)).thenReturn(c);

        ChallengeParticipant cp = new ChallengeParticipant();
        cp.setStatus("JOINED");
        when(participantDAO.getParticipant(1L, 2L)).thenReturn(cp);
        
        challengeService.updateParticipantStatus(1L, 2L, "COMPLETED");
        verify(participantDAO).updateParticipantStatus(1L, 2L, "COMPLETED");
    }

    @Test
    public void testUpdateParticipantStatus_Drop() throws Exception {
        Challenge c = new Challenge();
        c.setId(1L);
        c.setStatus("APPROVED");
        when(challengeDAO.getChallengeById(1L)).thenReturn(c);

        ChallengeParticipant cp = new ChallengeParticipant();
        cp.setStatus("JOINED");
        when(participantDAO.getParticipant(1L, 2L)).thenReturn(cp);
        
        challengeService.updateParticipantStatus(1L, 2L, "DROPPED");
        verify(participantDAO).updateParticipantStatus(1L, 2L, "DROPPED");
    }

    @Test
    public void testUpdateParticipantStatus_ChallengeNotFound() throws Exception {
        when(challengeDAO.getChallengeById(1L)).thenReturn(null);
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> 
            challengeService.updateParticipantStatus(1L, 2L, "COMPLETED")
        );
        assertEquals("Challenge not found", ex.getMessage());
    }

    @Test
    public void testUpdateParticipantStatus_ChallengeNotApproved() throws Exception {
        Challenge c = new Challenge();
        c.setId(1L);
        c.setStatus("PENDING");
        when(challengeDAO.getChallengeById(1L)).thenReturn(c);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> 
            challengeService.updateParticipantStatus(1L, 2L, "COMPLETED")
        );
        assertEquals("Challenge is not active", ex.getMessage());
    }

    @Test
    public void testUpdateParticipantStatus_BeforeStartDate() throws Exception {
        Challenge c = new Challenge();
        c.setId(1L);
        c.setStatus("APPROVED");
        c.setStartDate(new Timestamp(System.currentTimeMillis() + 100000));
        when(challengeDAO.getChallengeById(1L)).thenReturn(c);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> 
            challengeService.updateParticipantStatus(1L, 2L, "COMPLETED")
        );
        assertEquals("Challenge has not started yet", ex.getMessage());
    }

    @Test
    public void testUpdateParticipantStatus_Complete_AfterEndDate() throws Exception {
        Challenge c = new Challenge();
        c.setId(1L);
        c.setStatus("APPROVED");
        c.setEndDate(new Timestamp(System.currentTimeMillis() - 100000));
        when(challengeDAO.getChallengeById(1L)).thenReturn(c);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> 
            challengeService.updateParticipantStatus(1L, 2L, "COMPLETED")
        );
        assertEquals("Challenge has already ended", ex.getMessage());
    }

    @Test
    public void testUpdateParticipantStatus_NotJoined() throws Exception {
        Challenge c = new Challenge();
        c.setId(1L);
        c.setStatus("APPROVED");
        when(challengeDAO.getChallengeById(1L)).thenReturn(c);
        when(participantDAO.getParticipant(1L, 2L)).thenReturn(null);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> 
            challengeService.updateParticipantStatus(1L, 2L, "COMPLETED")
        );
        assertEquals("Not joined this challenge", ex.getMessage());
    }

    @Test
    public void testUpdateParticipantStatus_AlreadyCompleted_PreventsDuplicate() throws Exception {
        Challenge c = new Challenge();
        c.setId(1L);
        c.setStatus("APPROVED");
        when(challengeDAO.getChallengeById(1L)).thenReturn(c);

        ChallengeParticipant cp = new ChallengeParticipant();
        cp.setStatus("COMPLETED");
        when(participantDAO.getParticipant(1L, 2L)).thenReturn(cp);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> 
            challengeService.updateParticipantStatus(1L, 2L, "COMPLETED")
        );
        assertEquals("Challenge is already completed", ex.getMessage());
    }

    @Test
    public void testUpdateParticipantStatus_AlreadyDropped_PreventsCompletion() throws Exception {
        Challenge c = new Challenge();
        c.setId(1L);
        c.setStatus("APPROVED");
        when(challengeDAO.getChallengeById(1L)).thenReturn(c);

        ChallengeParticipant cp = new ChallengeParticipant();
        cp.setStatus("DROPPED");
        when(participantDAO.getParticipant(1L, 2L)).thenReturn(cp);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> 
            challengeService.updateParticipantStatus(1L, 2L, "COMPLETED")
        );
        assertEquals("Cannot modify dropped challenge", ex.getMessage());
    }

    @Test
    public void testUpdateParticipantStatus_InvalidStatus() {
        assertThrows(IllegalArgumentException.class, () -> 
            challengeService.updateParticipantStatus(1L, 2L, "UNKNOWN")
        );
    }

    @Test
    public void testUpdateParticipantStatus_InvalidIds() {
        assertThrows(IllegalArgumentException.class, () -> 
            challengeService.updateParticipantStatus(-1L, 2L, "COMPLETED")
        );
        assertThrows(IllegalArgumentException.class, () -> 
            challengeService.updateParticipantStatus(1L, 0L, "COMPLETED")
        );
    }
}
