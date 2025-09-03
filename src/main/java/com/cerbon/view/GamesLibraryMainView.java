package com.cerbon.view;

import com.cerbon.model.GameModel;
import com.cerbon.model.type.Gender;
import com.cerbon.model.type.Platform;
import com.cerbon.model.type.Status;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Main application window for the Games Library application.
 * Provides a comprehensive interface with a sortable table for displaying games,
 * filtering capabilities, and action buttons for managing game data and generating reports.
 */
public class GamesLibraryMainView extends JFrame {
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
    
    public GamesLibraryMainView() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
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
        
        // Add panels to the main frame
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
    }
    
    public void setGamesData(List<GameModel> games) {
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
    
    // Event registration for controller
    public void onAdd(ActionListener l) { addButton.addActionListener(l); }
    public void onEdit(ActionListener l) { editButton.addActionListener(l); }
    public void onDelete(ActionListener l) { deleteButton.addActionListener(l); }
    public void onReport(ActionListener l) { reportButton.addActionListener(l); }
    public void onApplyFilters(ActionListener l) { applyFiltersButton.addActionListener(l); }
    public void onClearFilters(ActionListener l) { clearFiltersButton.addActionListener(l); }
    public void onRefresh(ActionListener l) { refreshButton.addActionListener(l); }

    public GameFormDialog.GameData promptAddGame() {
        GameFormDialog dialog = new GameFormDialog(this, "Adicionar Jogo", null);
        dialog.setVisible(true);
        return dialog.isConfirmed() ? dialog.getGameData() : null;
    }

    public GameFormDialog.GameData promptEditGame(GameModel currentGame) {
        GameFormDialog dialog = new GameFormDialog(this, "Editar Jogo", currentGame);
        dialog.setVisible(true);
        return dialog.isConfirmed() ? dialog.getGameData() : null;
    }

    public GameModel getSelectedGameFromTable() {
        int selectedRow = gamesTable.getSelectedRow();
        if (selectedRow == -1) return null;
        int modelRow = gamesTable.convertRowIndexToModel(selectedRow);
        int gameId = (Integer) tableModel.getValueAt(modelRow, 0);
        String title = (String) tableModel.getValueAt(modelRow, 1);
        Gender gender = Gender.fromString((String) tableModel.getValueAt(modelRow, 2));
        Platform platform = Platform.fromString((String) tableModel.getValueAt(modelRow, 3));
        int year = (Integer) tableModel.getValueAt(modelRow, 4);
        Status status = Status.fromString((String) tableModel.getValueAt(modelRow, 5));
        int rate = (Integer) tableModel.getValueAt(modelRow, 6);
        return new GameModel(gameId, title, gender, platform, year, status, rate);
    }

    public Gender getSelectedGenderFilter() { return (Gender) genderFilter.getSelectedItem(); }
    public Platform getSelectedPlatformFilter() { return (Platform) platformFilter.getSelectedItem(); }
    public Status getSelectedStatusFilter() { return (Status) statusFilter.getSelectedItem(); }

    public void resetFilters() {
        genderFilter.setSelectedIndex(0);
        platformFilter.setSelectedIndex(0);
        statusFilter.setSelectedIndex(0);
    }

    public boolean confirmDeletion(String gameTitle) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente remover o jogo \"" + gameTitle + "\"?",
                "Confirmar Remoção",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return confirm == JOptionPane.YES_OPTION;
    }

    public void showReport(java.util.Map<Platform, Long> platformData, java.util.Map<Gender, Long> genderData) {
        ReportDialog dialog = new ReportDialog(this, platformData, genderData);
        dialog.setVisible(true);
    }

    public void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}