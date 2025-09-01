package com.cerbon.repository;

import com.cerbon.model.GameModel;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface defining the contract for game data persistence operations.
 * Provides methods for CRUD operations on GameModel entities including
 * adding, updating, deleting, and retrieving games from the data store.
 */
public interface IGamesRepository {
    boolean add(GameModel game);
    boolean delete(int id);
    GameModel update(GameModel game);
    Optional<GameModel> getById(int id);
    List<GameModel> getAll();
}
