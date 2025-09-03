package com.cerbon;

import com.cerbon.controller.GameController;
import com.cerbon.repository.IGamesRepository;
import com.cerbon.repository.postgres.PostgresGamesRepository;
import com.cerbon.view.GamesLibraryMainView;

import javax.swing.*;

/**
 * Main entry point for the Games Library application.
 * This class initializes the Swing GUI application with PostgreSQL database support,
 * sets up the system look and feel, and launches the main application window.
 */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("Warning: Could not set system look and feel: " + e.getMessage());
        }

        // Initialize application on EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            try {
                IGamesRepository repository = new PostgresGamesRepository();
                GamesLibraryMainView mainView = new GamesLibraryMainView();
                GameController controller = new GameController(repository, mainView);

                System.out.println("Games Library application started successfully!");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Erro ao inicializar a aplicação:\n" + e.getMessage() +
                                "\n\nVerifique se o banco de dados PostgreSQL está rodando" +
                                "\ne se as configurações de conexão estão corretas.",
                        "Erro de Inicialização",
                        JOptionPane.ERROR_MESSAGE
                );

                System.err.println("Failed to start Games Library application: " + e.getMessage());
                System.exit(1);
            }
        });
    }
}