package com.codingplatform.daoimpl;

import com.codingplatform.dao.ChallengeDAO;
import com.codingplatform.model.Challenge;
import com.codingplatform.exception.DatabaseException;
import com.codingplatform.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChallengeDAOImpl implements ChallengeDAO {

    @Override
    public void createChallenge(Challenge challenge) throws DatabaseException {
        String sql = "INSERT INTO challenges (title, description, difficulty, created_by, status, start_date, end_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, challenge.getTitle());
            pstmt.setString(2, challenge.getDescription());
            pstmt.setString(3, challenge.getDifficulty());
            pstmt.setLong(4, challenge.getCreatedBy());
            pstmt.setString(5, challenge.getStatus());
            pstmt.setTimestamp(6, challenge.getStartDate());
            pstmt.setTimestamp(7, challenge.getEndDate());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Creating challenge failed, no rows affected.", null);
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    challenge.setId(generatedKeys.getLong(1));
                } else {
                    throw new DatabaseException("Creating challenge failed, no ID obtained.", null);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error creating challenge", e);
        }
    }

    @Override
    public Challenge getChallengeById(long id) throws DatabaseException {
        String sql = "SELECT * FROM challenges WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToChallenge(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting challenge by id", e);
        }
        return null;
    }

    @Override
    public List<Challenge> getAllChallenges() throws DatabaseException {
        String sql = "SELECT * FROM challenges ORDER BY created_at DESC";
        return getChallengesList(sql);
    }

    @Override
    public List<Challenge> getApprovedChallenges() throws DatabaseException {
        String sql = "SELECT * FROM challenges WHERE status = 'APPROVED' ORDER BY created_at DESC";
        return getChallengesList(sql);
    }

    @Override
    public List<Challenge> getPendingChallenges() throws DatabaseException {
        String sql = "SELECT * FROM challenges WHERE status = 'PENDING' ORDER BY created_at DESC";
        return getChallengesList(sql);
    }

    @Override
    public List<Challenge> getChallengesByInstructor(long instructorId) throws DatabaseException {
        String sql = "SELECT * FROM challenges WHERE created_by = ? ORDER BY created_at DESC";
        List<Challenge> challenges = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, instructorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    challenges.add(mapRowToChallenge(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting challenges by instructor", e);
        }
        return challenges;
    }

    @Override
    public void updateChallengeStatus(long id, String status) throws DatabaseException {
        String sql = "UPDATE challenges SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setLong(2, id);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating challenge status", e);
        }
    }

    @Override
    public boolean updateChallengeStatusIfPending(long id, String status) throws DatabaseException {
        String sql = "UPDATE challenges SET status = ? WHERE id = ? AND status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setLong(2, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating challenge status", e);
        }
    }

    private List<Challenge> getChallengesList(String sql) throws DatabaseException {
        List<Challenge> challenges = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                challenges.add(mapRowToChallenge(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting challenge list", e);
        }
        return challenges;
    }

    private Challenge mapRowToChallenge(ResultSet rs) throws SQLException {
        Challenge challenge = new Challenge();
        challenge.setId(rs.getLong("id"));
        challenge.setTitle(rs.getString("title"));
        challenge.setDescription(rs.getString("description"));
        challenge.setDifficulty(rs.getString("difficulty"));
        challenge.setCreatedBy(rs.getLong("created_by"));
        challenge.setStatus(rs.getString("status"));
        challenge.setStartDate(rs.getTimestamp("start_date"));
        challenge.setEndDate(rs.getTimestamp("end_date"));
        challenge.setCreatedAt(rs.getTimestamp("created_at"));
        return challenge;
    }
}
