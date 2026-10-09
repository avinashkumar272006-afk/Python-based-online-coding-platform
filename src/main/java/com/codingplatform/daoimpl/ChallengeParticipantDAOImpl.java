package com.codingplatform.daoimpl;

import com.codingplatform.dao.ChallengeParticipantDAO;
import com.codingplatform.model.ChallengeParticipant;
import com.codingplatform.exception.DatabaseException;
import com.codingplatform.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChallengeParticipantDAOImpl implements ChallengeParticipantDAO {

    @Override
    public void createParticipant(ChallengeParticipant participant) throws DatabaseException {
        String sql = "INSERT INTO challenge_participants (challenge_id, student_id, status) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setLong(1, participant.getChallengeId());
            pstmt.setLong(2, participant.getStudentId());
            pstmt.setString(3, participant.getStatus() != null ? participant.getStatus() : "JOINED");
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Creating participant failed, no rows affected.", null);
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    participant.setId(generatedKeys.getLong(1));
                } else {
                    throw new DatabaseException("Creating participant failed, no ID obtained.", null);
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("Already joined this challenge.");
        } catch (SQLException e) {
            // Postgres unique violation is class 23 (23505)
            if ("23505".equals(e.getSQLState())) {
                throw new IllegalArgumentException("Already joined this challenge.");
            }
            throw new DatabaseException("Error creating challenge participant", e);
        }
    }

    @Override
    public ChallengeParticipant getParticipant(long challengeId, long studentId) throws DatabaseException {
        String sql = "SELECT * FROM challenge_participants WHERE challenge_id = ? AND student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, challengeId);
            pstmt.setLong(2, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToParticipant(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting participant", e);
        }
        return null;
    }

    @Override
    public List<ChallengeParticipant> getParticipantsByStudent(long studentId) throws DatabaseException {
        String sql = "SELECT cp.*, c.title as challenge_title FROM challenge_participants cp " +
                     "JOIN challenges c ON cp.challenge_id = c.id " +
                     "WHERE cp.student_id = ? ORDER BY cp.joined_at DESC";
        List<ChallengeParticipant> participants = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ChallengeParticipant cp = mapRowToParticipant(rs);
                    cp.setChallengeTitle(rs.getString("challenge_title"));
                    participants.add(cp);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting participants by student", e);
        }
        return participants;
    }

    @Override
    public void updateParticipantStatus(long challengeId, long studentId, String status) throws DatabaseException {
        String sql = "UPDATE challenge_participants SET status = ?, completed_at = CASE WHEN ? = 'COMPLETED' THEN CURRENT_TIMESTAMP ELSE completed_at END WHERE challenge_id = ? AND student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setString(2, status);
            pstmt.setLong(3, challengeId);
            pstmt.setLong(4, studentId);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating participant status", e);
        }
    }

    private ChallengeParticipant mapRowToParticipant(ResultSet rs) throws SQLException {
        ChallengeParticipant cp = new ChallengeParticipant();
        cp.setId(rs.getLong("id"));
        cp.setChallengeId(rs.getLong("challenge_id"));
        cp.setStudentId(rs.getLong("student_id"));
        cp.setJoinedAt(rs.getTimestamp("joined_at"));
        cp.setStatus(rs.getString("status"));
        cp.setCompletedAt(rs.getTimestamp("completed_at"));
        return cp;
    }
}
