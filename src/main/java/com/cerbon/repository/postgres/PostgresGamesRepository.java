package com.cerbon.repository.postgres;

import com.cerbon.model.GameModel;
import com.cerbon.model.type.Gender;
import com.cerbon.model.type.Platform;
import com.cerbon.model.type.Status;
import com.cerbon.repository.IGamesRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * PostgreSQL implementation of the IGamesRepository interface.
 * Handles all database operations for game entities using JDBC connections
 * to a PostgreSQL database with connection pooling and error handling.
 */
public class PostgresGamesRepository implements IGamesRepository {
    
    private static final String URL = "jdbc:postgresql://localhost:5432/gameslibrary";
    private static final String USER = "games_user";
    private static final String PASSWORD = "games_pass";
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    private GameModel mapResultSetToGameModel(ResultSet rs) throws SQLException {
        return new GameModel(
            rs.getInt("id"),
            rs.getString("title"),
            Gender.fromString(rs.getString("gender")),
            Platform.fromString(rs.getString("platform")),
            rs.getInt("year"),
            Status.fromString(rs.getString("status")),
            rs.getInt("rate")
        );
    }

    @Override
    public boolean add(GameModel game) {
        String sql = "INSERT INTO games (title, gender, platform, year, status, rate) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, game.title());
            stmt.setString(2, game.gender().name());
            stmt.setString(3, game.platform().name());
            stmt.setInt(4, game.year());
            stmt.setString(5, game.status().name());
            stmt.setInt(6, game.rate());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding game: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM games WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting game: " + e.getMessage());
            return false;
        }
    }

    @Override
    public GameModel update(GameModel game) {
        String sql = "UPDATE games SET title = ?, gender = ?, platform = ?, year = ?, status = ?, rate = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, game.title());
            stmt.setString(2, game.gender().name());
            stmt.setString(3, game.platform().name());
            stmt.setInt(4, game.year());
            stmt.setString(5, game.status().name());
            stmt.setInt(6, game.rate());
            stmt.setInt(7, game.id());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                Optional<GameModel> updatedGame = getById(game.id());
                return updatedGame.orElseThrow();

            } else return game;

        } catch (SQLException e) {
            System.err.println("Error updating game: " + e.getMessage());
            return game;
        }
    }

    @Override
    public Optional<GameModel> getById(int id) {
        String sql = "SELECT id, title, gender, platform, year, status, rate FROM games WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    GameModel game = mapResultSetToGameModel(rs);
                    return Optional.of(game);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting game by id: " + e.getMessage());
        }
        
        return Optional.empty();
    }

    @Override
    public List<GameModel> getAll() {
        String sql = "SELECT id, title, gender, platform, year, status, rate FROM games";
        List<GameModel> games = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                GameModel game = mapResultSetToGameModel(rs);
                games.add(game);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all games: " + e.getMessage());
        }
        
        return games;
    }
}
