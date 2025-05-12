package utama;

import javax.swing.*;
import Tiles.*;
import Object.SuperObject;
import Entities.*;
import java.awt.*;


public class GamePanel extends JPanel implements Runnable {
    public final int originalTileSize = 32; 
    public final int scale = 1; // Scale the tile size

    public final int tileSize = originalTileSize * scale; 
    public final int maxScreenCol = 32; 
    public final int maxScreenRow = 32; 
    public final int screenWidth = tileSize * maxScreenCol;  
    public final int screenHeight = tileSize * maxScreenRow; 

    public TileManager tileM = new TileManager(this); // Create a TileManager object
    Thread gameThread; // Thread for the game loop
    public KeyHandler keyHandler = new KeyHandler(this); 
    public Player player = new Player(this, keyHandler); // Create a player object
    public int FPS = 60; // Frames per second
    public CollisionChecker cChecker = new CollisionChecker(this); // Create a CollisionChecker object
    public AssetSetter aSetter = new AssetSetter(this); // Create an AssetSetter object
    public SuperObject[] obj = new SuperObject[10]; // Array to hold game objects
    public UI ui = new UI(this); // Create a UI object

    public int gameState; // Variable to hold the current game state
    public final int playState = 0; // Title screen state
    public final int houseState = 1;


    public GamePanel() {
        this.setPreferredSize(new java.awt.Dimension(screenWidth, screenHeight));
        this.setBackground(java.awt.Color.BLACK);
        this.setDoubleBuffered(true); // Enable double buffering for smoother rendering
        this.addKeyListener(keyHandler); // Add key listener for user input
        this.setFocusable(true); // Make the panel focusable to receive key events
    } 
    public void setupGame() {
        aSetter.setObject(); // Set up game objects
        gameState = playState; // Set the initial game state
        
    }
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start(); // Start the game loop
    }
    public void run () {
        // Game loop
        double drawInterval = 1000000000 / FPS; // Calculate the time interval for each frame
        double nextDrawTime = System.nanoTime() + drawInterval; // Calculate the next draw time

        while (gameThread != null) {
            
            Update();
            repaint(); 

            try {
                double remainingTime = nextDrawTime - System.nanoTime(); // Calculate the remaining time
                remainingTime /= 1000000; // Convert to milliseconds
                if (remainingTime < 0) {
                    remainingTime = 0; // Ensure remaining time is not negative
                }
                Thread.sleep((long) remainingTime); // Sleep for the remaining time
                nextDrawTime += drawInterval; // Update the next draw time
            }
            catch (InterruptedException e) {
                e.printStackTrace(); // Handle any exceptions
            }
            
        }
    }

    public void Update() {
        // Update game state
        if (gameState == playState) {
            player.update(); // Update the player
        }
        if (gameState == houseState) {
            // Update game state for house
             // Draw the UI
            ui.draw((Graphics2D) getGraphics()); // Draw the UI
        }
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw game graphics
        // Example: g.drawImage(image, x, y, tileSize, tileSize, null);
        Graphics2D g2 = (Graphics2D) g; // Cast to Graphics2D for advanced rendering
        tileM.draw(g2);

        for (int i = 0; i < obj.length; i++) {
            if (obj[i] != null) {
                obj[i].draw(g2, this); // Draw each object
            }
        }
        player.draw(g2); 
        g2.dispose(); // Dispose of the graphics context
        
    }
}