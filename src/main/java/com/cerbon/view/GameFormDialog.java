package com.cerbon.view;

import com.cerbon.model.GameModel;
import com.cerbon.model.type.Gender;
import com.cerbon.model.type.Platform;
import com.cerbon.model.type.Status;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;

public class GameFormDialog extends JDialog {
    private boolean confirmed = false;
    
    // Form components
    private JTextField titleField;
    private JComboBox<Gender> genderCombo;
    private JComboBox<Platform> platformCombo;
    private JSpinner yearSpinner;
    private JComboBox<Status> statusCombo;
    private JSpinner rateSpinner;
    
    // Buttons
    private JButton confirmButton;
    private JButton cancelButton;
    
    public GameFormDialog(Frame parent, String title, GameModel existingGame) {
        super(parent, title, true);
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        if (existingGame != null)
            populateFields(existingGame);
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        titleField = new JTextField(20);

        genderCombo = new JComboBox<>(Gender.values());
        platformCombo = new JComboBox<>(Platform.values());
        statusCombo = new JComboBox<>(Status.values());

        int currentYear = java.time.LocalDate.now().getYear();
        yearSpinner = new JSpinner(new SpinnerNumberModel(currentYear, 1970, currentYear + 5, 1));

        rateSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));

        confirmButton = new JButton("Confirmar");
        cancelButton = new JButton("Cancelar");

        getRootPane().setDefaultButton(confirmButton);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Título:"), gbc);
        gbc.gridx = 1;
        formPanel.add(titleField, gbc);
        
        // Gender
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Gênero:"), gbc);
        gbc.gridx = 1;
        formPanel.add(genderCombo, gbc);
        
        // Platform
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Plataforma:"), gbc);
        gbc.gridx = 1;
        formPanel.add(platformCombo, gbc);
        
        // Year
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Ano:"), gbc);
        gbc.gridx = 1;
        formPanel.add(yearSpinner, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        formPanel.add(statusCombo, gbc);
        
        // Rate
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Avaliação (0-10):"), gbc);
        gbc.gridx = 1;
        formPanel.add(rateSpinner, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        
        // Add panels to dialog
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        confirmButton.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                dispose();
            }
        });
        
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        
        // Handle ESC key to cancel
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmed = false;
                dispose();
            }
        });
    }
    
    private boolean validateInput() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Título não pode estar vazio", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            titleField.requestFocus();
            return false;
        }

        int year = (Integer) yearSpinner.getValue();
        int currentYear = LocalDate.now().getYear();
        if (year > currentYear) {
            JOptionPane.showMessageDialog(this, "Ano não pode ser futuro", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            yearSpinner.requestFocus();
            return false;
        }

        int rate = (Integer) rateSpinner.getValue();
        if (rate < 0 || rate > 10) {
            JOptionPane.showMessageDialog(this, "Avaliação deve ser entre 0 e 10", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            rateSpinner.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void populateFields(GameModel game) {
        titleField.setText(game.title());
        genderCombo.setSelectedItem(game.gender());
        platformCombo.setSelectedItem(game.platform());
        yearSpinner.setValue(game.year());
        statusCombo.setSelectedItem(game.status());
        rateSpinner.setValue(game.rate());
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public GameData getGameData() {
        return new GameData(
            titleField.getText().trim(),
            (Gender) genderCombo.getSelectedItem(),
            (Platform) platformCombo.getSelectedItem(),
            (Integer) yearSpinner.getValue(),
            (Status) statusCombo.getSelectedItem(),
            (Integer) rateSpinner.getValue()
        );
    }

    public record GameData(
        String title,
        Gender gender,
        Platform platform,
        int year,
        Status status,
        int rate
    ) {}
}