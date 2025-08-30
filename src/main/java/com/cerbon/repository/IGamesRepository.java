package com.cerbon.repository;

import com.cerbon.model.GameModel;

import java.util.List;
import java.util.Optional;

public interface IGamesRepository {
    boolean add(GameModel game);
    boolean delete(int id);
    GameModel update(GameModel game);
    Optional<GameModel> getById(int id);
    List<GameModel> getAll();
}
