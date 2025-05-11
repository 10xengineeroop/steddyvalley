package utama;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        GamePanel gamePanel = new GamePanel();

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("2D Game");
        window.add(gamePanel);
        window.pack(); // Adjusts the window size to fit the game panel
        window.setLocationRelativeTo(null); // Center the window on the screen
        window.setVisible(true);
        gamePanel.setupGame(); // Set up the game
        gamePanel.startGameThread(); // Start the game loop
    }    
}
