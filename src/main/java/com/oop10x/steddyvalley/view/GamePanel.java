package com.oop10x.steddyvalley.view;

import com.oop10x.steddyvalley.model.FarmMap; // Import FarmMap
import com.oop10x.steddyvalley.model.GameState;
import com.oop10x.steddyvalley.model.GameStateObserver;
import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.model.PlayerObserver;
import com.oop10x.steddyvalley.controller.GameController;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
// Import Item dan Inventory jika mau menampilkan info item di inventory
// import com.oop10x.steddyvalley.model.items.Item;
// import com.oop10x.steddyvalley.model.Inventory;
// import java.util.Map;


public class GamePanel extends JPanel implements Runnable, PlayerObserver, GameStateObserver {

    public static final int TILE_SIZE = 24;
    public final int MAX_SCREEN_COL = FarmMap.MAP_WIDTH_IN_TILES; // Ambil dari FarmMap
    public final int MAX_SCREEN_ROW = FarmMap.MAP_HEIGHT_IN_TILES; // Ambil dari FarmMap
    public final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COL;
    public final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_ROW;

    private final int FPS = 60;
    private Thread gameThread;

    private Player playerModel;
    private GameState gameStateModel;
    private GameController gameController;
    private FarmMap farmMapModel; // Referensi ke FarmMap

    public GamePanel(Player player, GameState gameState, GameController controller, FarmMap farmMap) {
        this.playerModel = player;
        this.gameStateModel = gameState;
        this.gameController = controller;
        this.farmMapModel = farmMap; // Simpan FarmMap

        this.playerModel.addObserver(this);
        this.gameStateModel.addObserver(this);

        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
    }

    public void startGameThread() { if (gameThread == null) { gameThread = new Thread(this); gameThread.start(); }}
    public void stopGameThread() { if (gameThread != null) { gameThread = null; }}
    @Override public void run() { /* ... game loop tetap sama ... */
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                updateGame();
                repaint();
                delta--;
            }
        }
    }
    public void updateGame() { gameController.updateGameLogic(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int currentGameState = gameStateModel.getCurrentState();

        if (currentGameState == GameState.PLAY_STATE) {
            drawPlayState(g2);
        } else if (currentGameState == GameState.PAUSE_STATE) {
            drawPauseState(g2);
        } else if (currentGameState == GameState.INVENTORY_STATE) {
            drawInventoryState(g2);
        } else if (currentGameState == GameState.HOUSE_STATE) {
            drawHouseState(g2);
        }
        g2.dispose();
    }

    private void drawPlayState(Graphics2D g2) {
        // 1. Gambar Peta menggunakan FarmMap
        if (farmMapModel != null) {
            farmMapModel.draw(g2, TILE_SIZE); // Berikan tileSize ke FarmMap.draw
        }

        // 2. Gambar Pemain
        int playerScreenX = playerModel.getPosition().getX();
        int playerScreenY = playerModel.getPosition().getY();
        g2.setColor(Color.BLUE);
        g2.fillRect(playerScreenX, playerScreenY, TILE_SIZE, TILE_SIZE);

        // Informasi Debug
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(14F));
        g2.drawString("Player X: " + playerScreenX + " Y: " + playerScreenY, 10, 20);
        g2.drawString("STATE: PLAYING (ESC:Pause, I:Inv, Enter:Action)", 10, 40);
        if (playerModel.getEquippedItem() != null) {
            g2.drawString("Equipped: " + playerModel.getEquippedItem().getName(), 10, 60);
        }
    }

    private void drawPauseState(Graphics2D g2) {
        drawPlayState(g2); // Gambar game di belakang
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 50));
        String text = "PAUSED";
        g2.drawString(text, getXforCenteredText(text, g2), SCREEN_HEIGHT / 2);
    }

    private void drawInventoryState(Graphics2D g2) {
        drawPlayState(g2); // Gambar game di belakang
        // Gambar panel inventory (contoh sederhana)
        int pX = 50, pY = 50, pW = SCREEN_WIDTH - 100, pH = SCREEN_HEIGHT - 100;
        g2.setColor(new Color(0,0,0,200));
        g2.fillRect(pX,pY,pW,pH);
        g2.setColor(Color.WHITE);
        g2.drawRect(pX,pY,pW,pH);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Inventory", pX + 10, pY + 30);
        // Tampilkan item dari playerModel.getInventory()
        // ...
    }
    private void drawHouseState(Graphics2D g2) { 
        // Gambar rumah (contoh sederhana)
        g2.setColor(Color.YELLOW);
        g2.fillRect(100, 100, 200, 200);
        g2.setColor(Color.BLACK);
        g2.drawRect(100, 100, 200, 200);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        String text = "House";
        g2.drawString(text, getXforCenteredText(text, g2), 150);
    }
    private int getXforCenteredText(String text, Graphics2D g2) {
        return SCREEN_WIDTH / 2 - (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth() / 2;
    }

    @Override public void onPlayerUpdated(Player player) { /* ... */ }
    @Override public void onGameStateChanged(int newState, int oldState) { /* ... */ }
}
