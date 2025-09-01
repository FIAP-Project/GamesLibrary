package com.cerbon.view;

import com.cerbon.controller.GameController;
import com.cerbon.model.GameModel;
import com.cerbon.model.type.Gender;
import com.cerbon.model.type.Platform;
import com.cerbon.model.type.Status;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * Main application window for the Games Library application.
 * Provides a comprehensive interface with a sortable table for displaying games,
 * filtering capabilities, and action buttons for managing game data and generating reports.
 */
public class GamesLibraryMainView extends JFrame {
    private final GameController controller;
    
    // GUI Components
    private JTable gamesTable;
    private DefaultTableModel tableModel;

    // Filter components
    private JComboBox<Gender> genderFilter;
    private JComboBox<Platform> platformFilter;
    private JComboBox<Status> statusFilter;
    
    // Buttons
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton reportButton;
    private JButton applyFiltersButton;
    private JButton clearFiltersButton;
    private JButton refreshButton;
    
    public GamesLibraryMainView(GameController controller) {
        this.controller = controller;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadGamesData();
    }
    
    private void initializeComponents() {
        // Main window setup
        setTitle("Biblioteca de Jogos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Table setup with sortable columns
        String[] columnNames = {"ID", "Título", "Gênero", "Plataforma", "Ano", "Status", "Avaliação"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        gamesTable = new JTable(tableModel);
        gamesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Enable sorting
        TableRowSorter<TableModel> tableSorter = new TableRowSorter<>(tableModel);
        gamesTable.setRowSorter(tableSorter);
        
        // Set column widths
        gamesTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        gamesTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Title
        gamesTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Gender
        gamesTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Platform
        gamesTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Year
        gamesTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Status
        gamesTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Rate
        
        // Filter components
        genderFilter = new JComboBox<>();
        genderFilter.addItem(null); // "All" option
        for (Gender gender : Gender.values()) {
            genderFilter.addItem(gender);
        }
        
        platformFilter = new JComboBox<>();
        platformFilter.addItem(null); // "All" option
        for (Platform platform : Platform.values()) {
            platformFilter.addItem(platform);
        }
        
        statusFilter = new JComboBox<>();
        statusFilter.addItem(null); // "All" option
        for (Status status : Status.values()) {
            statusFilter.addItem(status);
        }
        
        // Buttons
        addButton = new JButton("Adicionar Jogo");
        editButton = new JButton("Editar Jogo");
        deleteButton = new JButton("Remover Jogo");
        reportButton = new JButton("Gerar Relatório");
        applyFiltersButton = new JButton("Aplicar Filtros");
        clearFiltersButton = new JButton("Limpar Filtros");
        refreshButton = new JButton("Atualizar");
        
        // Initially disable edit/delete buttons
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Top panel - Filters
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtros"));
        
        filterPanel.add(new JLabel("Gênero:"));
        filterPanel.add(genderFilter);
        filterPanel.add(new JLabel("Plataforma:"));
        filterPanel.add(platformFilter);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);
        filterPanel.add(applyFiltersButton);
        filterPanel.add(clearFiltersButton);
        filterPanel.add(refreshButton);
        
        // Center panel - Table
        JScrollPane scrollPane = new JScrollPane(gamesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Jogos"));
        
        // Bottom panel - Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(reportButton);
        
        // Add panels to main frame
        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        gamesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = gamesTable.getSelectedRow() != -1;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
            }
        });

        addButton.addActionListener(e -> showAddGameDialog());
        editButton.addActionListener(e -> showEditGameDialog());
        deleteButton.addActionListener(e -> deleteSelectedGame());
        reportButton.addActionListener(e -> showReportDialog());
        applyFiltersButton.addActionListener(e -> applyFilters());
        clearFiltersButton.addActionListener(e -> clearFilters());
        refreshButton.addActionListener(e -> loadGamesData());
    }
    
    private void loadGamesData() {
        loadGamesData(controller.getAllGames());
    }
    
    private void loadGamesData(List<GameModel> games) {
        tableModel.setRowCount(0); // Clear existing data
        
        for (GameModel game : games) {
            Object[] row = {
                game.id(),
                game.title(),
                game.gender().name(),
                game.platform().name(),
                game.year(),
                game.status().name(),
                game.rate()
            };
            tableModel.addRow(row);
        }
    }
    
    private void showAddGameDialog() {
        GameFormDialog dialog = new GameFormDialog(this, "Adicionar Jogo", null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            GameFormDialog.GameData data = dialog.getGameData();
            GameController.OperationResult<GameModel> result = controller.addGame(
                data.title(), data.gender(), data.platform(), data.year(), data.status(), data.rate()
            );
            
            if (result.success()) {
                JOptionPane.showMessageDialog(this, result.message(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                loadGamesData();
            } else
                JOptionPane.showMessageDialog(this, result.message(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showEditGameDialog() {
        int selectedRow = gamesTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        // Convert view row to model row (important when sorting is applied)
        int modelRow = gamesTable.convertRowIndexToModel(selectedRow);
        
        int gameId = (Integer) tableModel.getValueAt(modelRow, 0);
        String title = (String) tableModel.getValueAt(modelRow, 1);
        Gender gender = Gender.fromString((String) tableModel.getValueAt(modelRow, 2));
        Platform platform = Platform.fromString((String) tableModel.getValueAt(modelRow, 3));
        int year = (Integer) tableModel.getValueAt(modelRow, 4);
        Status status = Status.fromString((String) tableModel.getValueAt(modelRow, 5));
        int rate = (Integer) tableModel.getValueAt(modelRow, 6);
        
        GameModel currentGame = new GameModel(gameId, title, gender, platform, year, status, rate);
        
        GameFormDialog dialog = new GameFormDialog(this, "Editar Jogo", currentGame);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            GameFormDialog.GameData data = dialog.getGameData();
            GameController.OperationResult<GameModel> result = controller.updateGame(
                gameId, data.title(), data.gender(), data.platform(), data.year(), data.status(), data.rate()
            );
            
            if (result.success()) {
                JOptionPane.showMessageDialog(this, result.message(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                loadGamesData();
            } else
                JOptionPane.showMessageDialog(this, result.message(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteSelectedGame() {
        int selectedRow = gamesTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        // Convert view row to model row
        int modelRow = gamesTable.convertRowIndexToModel(selectedRow);
        int gameId = (Integer) tableModel.getValueAt(modelRow, 0);
        String gameTitle = (String) tableModel.getValueAt(modelRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente remover o jogo \"" + gameTitle + "\"?",
            "Confirmar Remoção",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            GameController.OperationResult<Void> result = controller.deleteGame(gameId);
            
            if (result.success()) {
                JOptionPane.showMessageDialog(this, result.message(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                loadGamesData();
            } else
                JOptionPane.showMessageDialog(this, result.message(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showReportDialog() {
        ReportDialog dialog = new ReportDialog(this, controller);
        dialog.setVisible(true);
    }
    
    private void applyFilters() {
        Gender selectedGender = (Gender) genderFilter.getSelectedItem();
        Platform selectedPlatform = (Platform) platformFilter.getSelectedItem();
        Status selectedStatus = (Status) statusFilter.getSelectedItem();
        
        List<GameModel> filteredGames = controller.filterGames(selectedGender, selectedPlatform, selectedStatus);
        loadGamesData(filteredGames);
    }
    
    private void clearFilters() {
        genderFilter.setSelectedIndex(0);
        platformFilter.setSelectedIndex(0);
        statusFilter.setSelectedIndex(0);
        loadGamesData();
    }
}