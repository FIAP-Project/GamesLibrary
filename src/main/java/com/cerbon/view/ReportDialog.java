package com.cerbon.view;

import com.cerbon.controller.GameController;
import com.cerbon.model.type.Gender;
import com.cerbon.model.type.Platform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Map;

/**
 * Modal dialog for displaying comprehensive reports about concluded games.
 * Provides tabbed views showing game completion statistics grouped by platform and genre
 * with visual charts and percentage breakdowns for better data analysis.
 */
public class ReportDialog extends JDialog {
    private final GameController controller;
    
    // Components
    private JTabbedPane tabbedPane;
    private JTextArea platformReportArea;
    private JTextArea genderReportArea;
    private JButton refreshButton;
    private JButton closeButton;
    
    public ReportDialog(Frame parent, GameController controller) {
        super(parent, "Relatório de Jogos Concluídos", true);
        this.controller = controller;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        generateReports();
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        setSize(500, 400);
        
        // Create tabbed pane for different report types
        tabbedPane = new JTabbedPane();
        
        // Text areas for reports (non-editable)
        platformReportArea = new JTextArea(15, 40);
        platformReportArea.setEditable(false);
        platformReportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        platformReportArea.setMargin(new Insets(10, 10, 10, 10));
        
        genderReportArea = new JTextArea(15, 40);
        genderReportArea.setEditable(false);
        genderReportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        genderReportArea.setMargin(new Insets(10, 10, 10, 10));

        refreshButton = new JButton("Atualizar Relatórios");
        closeButton = new JButton("Fechar");

        getRootPane().setDefaultButton(closeButton);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Add text areas to scroll panes and then to tabs
        JScrollPane platformScrollPane = new JScrollPane(platformReportArea);
        platformScrollPane.setBorder(BorderFactory.createTitledBorder("Jogos Concluídos por Plataforma"));
        
        JScrollPane genderScrollPane = new JScrollPane(genderReportArea);
        genderScrollPane.setBorder(BorderFactory.createTitledBorder("Jogos Concluídos por Gênero"));
        
        tabbedPane.addTab("Por Plataforma", platformScrollPane);
        tabbedPane.addTab("Por Gênero", genderScrollPane);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        add(tabbedPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        refreshButton.addActionListener(e -> generateReports());
        
        closeButton.addActionListener(e -> dispose());
        
        // Handle ESC key to close
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void generateReports() {
        try {
            generatePlatformReport();
            generateGenderReport();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao gerar relatórios: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generatePlatformReport() {
        Map<Platform, Long> platformData = controller.getConcludedGamesByPlatform();
        
        StringBuilder report = new StringBuilder();
        report.append("RELATÓRIO DE JOGOS CONCLUÍDOS POR PLATAFORMA\n");
        report.append("=" .repeat(50)).append("\n\n");
        
        if (platformData.isEmpty()) {
            report.append("Nenhum jogo concluído encontrado.\n");
        } else {
            long totalGames = platformData.values().stream().mapToLong(Long::longValue).sum();
            
            report.append(String.format("Total de jogos concluídos: %d\n\n", totalGames));
            
            // Sort by count (descending) for better visualization
            platformData.entrySet().stream()
                .sorted(Map.Entry.<Platform, Long>comparingByValue().reversed())
                .forEach(entry -> {
                    Platform platform = entry.getKey();
                    Long count = entry.getValue();
                    double percentage = (count * 100.0) / totalGames;
                    
                    report.append(String.format("%-15s: %3d jogos (%.1f%%)\n", 
                        platform.name(), count, percentage));
                });
            
            // Add visual representation
            report.append("\n").append("-".repeat(35)).append("\n");
            report.append("Representação Visual:\n\n");
            
            platformData.entrySet().stream()
                .sorted(Map.Entry.<Platform, Long>comparingByValue().reversed())
                .forEach(entry -> {
                    Platform platform = entry.getKey();
                    Long count = entry.getValue();
                    String bar = "█".repeat(Math.max(1, (int)(count * 20 / totalGames)));
                    
                    report.append(String.format("%-10s |%s %d\n", 
                        platform.name(), bar, count));
                });
        }
        
        platformReportArea.setText(report.toString());
        platformReportArea.setCaretPosition(0); // Scroll to top
    }
    
    private void generateGenderReport() {
        Map<Gender, Long> genderData = controller.getConcludedGamesByGender();
        
        StringBuilder report = new StringBuilder();
        report.append("RELATÓRIO DE JOGOS CONCLUÍDOS POR GÊNERO\n");
        report.append("=" .repeat(50)).append("\n\n");
        
        if (genderData.isEmpty()) {
            report.append("Nenhum jogo concluído encontrado.\n");
        } else {
            long totalGames = genderData.values().stream().mapToLong(Long::longValue).sum();
            
            report.append(String.format("Total de jogos concluídos: %d\n\n", totalGames));
            
            // Sort by count (descending) for better visualization
            genderData.entrySet().stream()
                .sorted(Map.Entry.<Gender, Long>comparingByValue().reversed())
                .forEach(entry -> {
                    Gender gender = entry.getKey();
                    Long count = entry.getValue();
                    double percentage = (count * 100.0) / totalGames;
                    
                    report.append(String.format("%-15s: %3d jogos (%.1f%%)\n", 
                        gender.name(), count, percentage));
                });
            
            // Add visual representation
            report.append("\n").append("-".repeat(35)).append("\n");
            report.append("Representação Visual:\n\n");
            
            genderData.entrySet().stream()
                .sorted(Map.Entry.<Gender, Long>comparingByValue().reversed())
                .forEach(entry -> {
                    Gender gender = entry.getKey();
                    Long count = entry.getValue();
                    String bar = "█".repeat(Math.max(1, (int)(count * 20 / totalGames)));
                    
                    report.append(String.format("%-10s |%s %d\n", 
                        gender.name(), bar, count));
                });
        }
        
        genderReportArea.setText(report.toString());
        genderReportArea.setCaretPosition(0); // Scroll to top
    }
}