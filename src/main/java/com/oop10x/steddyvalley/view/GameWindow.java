package com.oop10x.steddyvalley.view; 

import com.oop10x.steddyvalley.controller.KeyHandler; 

import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.SwingUtilities;

public class GameWindow extends JFrame {

    private GamePanel gamePanel; 

    public GameWindow(String title, GamePanel gamePanel, KeyHandler keyHandler) {
        super(title); 

        if (gamePanel == null) {
            throw new IllegalArgumentException("GamePanel cannot be null");
        }
        if (keyHandler == null) {
            throw new IllegalArgumentException("KeyHandler cannot be null");
        }

        this.gamePanel = gamePanel;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        this.setResizable(false); 

        this.add(gamePanel);

        gamePanel.addKeyListener(keyHandler);
        this.pack();

        this.setLocationRelativeTo(null);
    }

    public void displayAndFocus() {
        this.setVisible(true);

        SwingUtilities.invokeLater(() -> {
            gamePanel.requestFocusInWindow();
            if (!gamePanel.hasFocus()) {
                System.err.println("GamePanel could not gain focus. Keyboard input might not work.");
            }
        });
    }

    public void addCleanShutdownHook() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (gamePanel != null) {
                    gamePanel.stopGameThread(); 
                }
                System.out.println("Game window closing, game thread stopped.");
            }
        });
    }
}
