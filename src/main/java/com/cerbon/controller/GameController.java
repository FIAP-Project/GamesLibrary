package com.cerbon.controller;

import com.cerbon.model.GameModel;
import com.cerbon.model.type.Gender;
import com.cerbon.model.type.Platform;
import com.cerbon.model.type.Status;
import com.cerbon.repository.IGamesRepository;
import com.cerbon.view.GamesLibraryMainView;
import com.cerbon.view.GameFormDialog;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller class that handles all business logic for game management operations.
 * Provides methods for adding, updating, deleting, and querying games with validation,
 * filtering capabilities, and reporting functionality for concluded games by platform and gender.
 */
public class GameController {
    private final IGamesRepository repository;
    private final GamesLibraryMainView view;
    
    public GameController(IGamesRepository repository, GamesLibraryMainView view) {
        this.repository = repository;
        this.view = view;
        if (this.view != null) {
            attachViewListeners();
            // Initial load of data into the view
            this.view.setGamesData(getAllGames());
            this.view.setVisible(true);
        }
    }

    private void attachViewListeners() {
        view.onAdd(e -> {
            GameFormDialog.GameData data = view.promptAddGame();
            if (data != null) {
                OperationResult<GameModel> result = addGame(
                        data.title(), data.gender(), data.platform(), data.year(), data.status(), data.rate()
                );
                if (result.success()) {
                    view.showInfoMessage(result.message());
                    view.setGamesData(getAllGames());
                } else {
                    view.showErrorMessage(result.message());
                }
            }
        });

        view.onEdit(e -> {
            GameModel current = view.getSelectedGameFromTable();
            if (current == null) {
                view.showErrorMessage("Selecione um jogo para editar.");
                return;
            }
            GameFormDialog.GameData data = view.promptEditGame(current);
            if (data != null) {
                OperationResult<GameModel> result = updateGame(
                        current.id(), data.title(), data.gender(), data.platform(), data.year(), data.status(), data.rate()
                );
                if (result.success()) {
                    view.showInfoMessage(result.message());
                    view.setGamesData(getAllGames());
                } else {
                    view.showErrorMessage(result.message());
                }
            }
        });

        view.onDelete(e -> {
            GameModel current = view.getSelectedGameFromTable();
            if (current == null) {
                view.showErrorMessage("Selecione um jogo para remover.");
                return;
            }
            boolean confirmed = view.confirmDeletion(current.title());
            if (confirmed) {
                OperationResult<Void> result = deleteGame(current.id());
                if (result.success()) {
                    view.showInfoMessage(result.message());
                    view.setGamesData(getAllGames());
                } else {
                    view.showErrorMessage(result.message());
                }
            }
        });

        view.onReport(e -> {
            Map<Platform, Long> platformData = getConcludedGamesByPlatform();
            Map<Gender, Long> genderData = getConcludedGamesByGender();
            view.showReport(platformData, genderData);
        });

        view.onApplyFilters(e -> {
            Gender selectedGender = view.getSelectedGenderFilter();
            Platform selectedPlatform = view.getSelectedPlatformFilter();
            Status selectedStatus = view.getSelectedStatusFilter();
            List<GameModel> filtered = filterGames(selectedGender, selectedPlatform, selectedStatus);
            view.setGamesData(filtered);
        });

        view.onClearFilters(e -> {
            view.resetFilters();
            view.setGamesData(getAllGames());
        });

        view.onRefresh(e -> view.setGamesData(getAllGames()));
    }

    public ValidationResult validateGame(GameModel game) {
        return validateGame(game, false);
    }
    
    public ValidationResult validateGameForUpdate(GameModel game) {
        return validateGame(game, true);
    }
    
    private ValidationResult validateGame(GameModel game, boolean isUpdate) {
        if (game.title() == null || game.title().trim().isEmpty())
            return new ValidationResult(false, "Título não pode estar vazio");


        if (game.rate() < 0 || game.rate() > 10)
            return new ValidationResult(false, "Avaliação deve ser entre 0 e 10");


        int currentYear = LocalDate.now().getYear();
        if (game.year() > currentYear)
            return new ValidationResult(false, "Ano de lançamento não pode ser futuro");
        
        // Check for duplicate title + platform (only for new games or when updating these fields)
        if (!isUpdate || isDuplicateForUpdate(game)) {
            List<GameModel> existingGames = repository.getAll();
            boolean duplicate = existingGames.stream()
                .anyMatch(existing -> 
                    (!isUpdate || existing.id() != game.id()) &&
                    existing.title().equalsIgnoreCase(game.title().trim()) &&
                    existing.platform() == game.platform()
                );
            
            if (duplicate)
                return new ValidationResult(false, "Já existe um jogo com este título e plataforma");
        }
        
        return new ValidationResult(true, "");
    }
    
    private boolean isDuplicateForUpdate(GameModel game) {
        Optional<GameModel> existingOpt = repository.getById(game.id());
        if (existingOpt.isEmpty()) return true;
        
        GameModel existing = existingOpt.get();
        return !existing.title().equals(game.title()) || existing.platform() != game.platform();
    }

    public OperationResult<GameModel> addGame(String title, Gender gender, Platform platform, int year, Status status, int rate) {
        try {
            GameModel game = new GameModel(0, title.trim(), gender, platform, year, status, rate);
            ValidationResult validation = validateGame(game);
            
            if (!validation.valid())
                return new OperationResult<>(false, validation.message(), null);

            
            boolean success = repository.add(game);
            if (success)
                return new OperationResult<>(true, "Jogo adicionado com sucesso", null);
            else
                return new OperationResult<>(false, "Erro ao adicionar jogo no banco de dados", null);

        } catch (Exception e) {
            return new OperationResult<>(false, "Erro inesperado: " + e.getMessage(), null);
        }
    }
    
    public OperationResult<GameModel> updateGame(int id, String title, Gender gender, Platform platform, int year, Status status, int rate) {
        try {
            GameModel game = new GameModel(id, title.trim(), gender, platform, year, status, rate);
            ValidationResult validation = validateGameForUpdate(game);
            
            if (!validation.valid()) {
                return new OperationResult<>(false, validation.message(), null);
            }
            
            GameModel updated = repository.update(game);
            return new OperationResult<>(true, "Jogo atualizado com sucesso", updated);
        } catch (Exception e) {
            return new OperationResult<>(false, "Erro inesperado: " + e.getMessage(), null);
        }
    }
    
    public OperationResult<Void> deleteGame(int id) {
        try {
            boolean success = repository.delete(id);
            if (success)
                return new OperationResult<>(true, "Jogo removido com sucesso", null);
            else
                return new OperationResult<>(false, "Erro ao remover jogo - jogo não encontrado", null);

        } catch (Exception e) {
            return new OperationResult<>(false, "Erro inesperado: " + e.getMessage(), null);
        }
    }

    public List<GameModel> getAllGames() {
        return repository.getAll();
    }
    
    public List<GameModel> filterGames(Gender gender, Platform platform, Status status) {
        List<GameModel> allGames = repository.getAll();
        
        return allGames.stream()
            .filter(game -> gender == null || game.gender() == gender)
            .filter(game -> platform == null || game.platform() == platform)
            .filter(game -> status == null || game.status() == status)
            .collect(Collectors.toList());
    }

    public Map<Platform, Long> getConcludedGamesByPlatform() {
        List<GameModel> concludedGames = repository.getAll().stream()
            .filter(game -> game.status() == Status.CONCLUDED)
            .toList();
            
        Map<Platform, Long> report = new HashMap<>();
        for (Platform platform : Platform.values()) {
            long count = concludedGames.stream()
                .filter(game -> game.platform() == platform)
                .count();
            if (count > 0)
                report.put(platform, count);
        }
        
        return report;
    }
    
    public Map<Gender, Long> getConcludedGamesByGender() {
        List<GameModel> concludedGames = repository.getAll().stream()
            .filter(game -> game.status() == Status.CONCLUDED)
            .toList();
            
        Map<Gender, Long> report = new HashMap<>();
        for (Gender gender : Gender.values()) {
            long count = concludedGames.stream()
                .filter(game -> game.gender() == gender)
                .count();
            if (count > 0)
                report.put(gender, count);
        }
        
        return report;
    }

    public record ValidationResult(boolean valid, String message) {}
    public record OperationResult<T>(boolean success, String message, T data) {}
}