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
import java.util.List;


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

    // --- HUD fields ---
    private com.oop10x.steddyvalley.model.TimeManager timeManager;
    private com.oop10x.steddyvalley.model.SeasonManager seasonManager;
    private com.oop10x.steddyvalley.model.WeatherManager weatherManager;

    // --- FISHING GUESSING GAME STATE ---
    private boolean fishingGuessActive = false;
    private String fishingMessage = "";
    private boolean isFishingSliderUIVisible = false; // Status apakah UI slider aktif
    private String fishingUIMessage = "";             // Pesan yang ditampilkan di UI fishing
    private int fishingSliderDisplayValue = 0;        // Nilai yang ditunjuk slider saat ini
    private int fishingSliderMinDisplay = 0;          // Batas bawah nilai slider untuk ditampilkan
    private int fishingSliderMaxDisplay = 0;  

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
        else if (currentGameState == GameState.SLEEP_STATE) {
            drawSleepState(g2);
        }
        else if (currentGameState == GameState.COOK_STATE) {
            drawCookState(g2);
        }
        else if (currentGameState == GameState.RECIPE_STATE) {
            drawRecipeState(g2);
        }
        else if (currentGameState == GameState.FISHING_STATE) {
            drawFishingState(g2);
        }
        else if (currentGameState == GameState.FISH_GUESS_STATE) {
            // Jika ada state khusus untuk fishing guessing game
            drawFishGuessState(g2);
        }
        g2.dispose();
    }

    public void setManagers(com.oop10x.steddyvalley.model.TimeManager tm, com.oop10x.steddyvalley.model.SeasonManager sm, com.oop10x.steddyvalley.model.WeatherManager wm) {
        this.timeManager = tm;
        this.seasonManager = sm;
        this.weatherManager = wm;
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

        // --- HUD: Time, Weather, Season ---
        g2.setColor(new Color(0,0,0,180));
        g2.fillRoundRect(5, 5, 340, 38, 12, 12);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        String timeStr = "Time: --:--";
        String seasonStr = "Season: -";
        String weatherStr = "Weather: -";
        if (timeManager != null) {
            int mins = timeManager.getMinutes();
            int hour = (mins / 60) % 24;
            int min = mins % 60;
            String ampm = hour < 12 ? "AM" : "PM";
            int hour12 = hour % 12;
            if (hour12 == 0) hour12 = 12;
            timeStr = String.format("Time: %02d:%02d %s", hour12, min, ampm);
        }
        if (seasonManager != null) {
            seasonStr = "Season: " + seasonManager.getCurrentSeason();
        }
        if (weatherManager != null) {
            weatherStr = "Weather: " + weatherManager.getCurrentWeather();
        }
        g2.drawString(timeStr, 15, 28);
        g2.drawString(seasonStr, 130, 28);
        g2.drawString(weatherStr, 250, 28);

        // Informasi Debug
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(14F));
        g2.drawString("Player X: " + playerScreenX + " Y: " + playerScreenY, 10, 60);
        g2.drawString("STATE: PLAYING (ESC:Pause, I:Inv, E:Action)", 10, 80);
        g2.drawString("Gold: " + playerModel.getGold() + " Energy: " + playerModel.getEnergy(), 10, 100);
        if (playerModel.getEquippedItem() != null) {
            g2.drawString("Equipped: " + playerModel.getEquippedItem().getName(), 10, 120);
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

    private int selectedInventoryIndex = 0;
    private void drawInventoryState(Graphics2D g2) {
        drawPlayState(g2); // Gambar game di belakang
        int pX = 50, pY = 50, pW = SCREEN_WIDTH - 100, pH = SCREEN_HEIGHT - 100;
        g2.setColor(new Color(0,0,0,200));
        g2.fillRect(pX,pY,pW,pH);
        g2.setColor(Color.WHITE);
        g2.drawRect(pX,pY,pW,pH);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Inventory", pX + 10, pY + 30);

        // Ambil semua item dan jumlahnya
        var itemsMap = playerModel.getInventory().getAllItems();
        java.util.List<com.oop10x.steddyvalley.model.items.Item> items = new java.util.ArrayList<>(itemsMap.keySet());
        int lineHeight = 28;
        int startY = pY + 60;
        int maxVisible = (pH - 80) / lineHeight;
        int selectedIdx = gameController.getSelectedInventoryIndex();
        int scroll = 0;
        if (selectedIdx >= maxVisible) scroll = selectedIdx - maxVisible + 1;
        for (int i = 0; i < items.size(); i++) {
            if (i < scroll) continue;
            if (i - scroll >= maxVisible) break;
            com.oop10x.steddyvalley.model.items.Item item = items.get(i);
            int amount = itemsMap.get(item);
            int y = startY + (i - scroll) * lineHeight;
            if (i == selectedIdx) {
                g2.setColor(Color.YELLOW);
                g2.drawString("> " + item.getName() + " x" + amount, pX + 20, y);
                g2.setColor(Color.WHITE);
            } else {
                g2.drawString("  " + item.getName() + " x" + amount, pX + 20, y);
            }
        }
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.drawString("W/S: Scroll  E: Equip  Esc: Exit", pX + 20, pY + pH - 20);
        // Show equipped item
        if (playerModel.getEquippedItem() != null) {
            g2.drawString("Equipped: " + playerModel.getEquippedItem().getName(), pX + pW - 200, pY + 30);
        }
    }
    private void drawHouseState(Graphics2D g2) { 
        drawPlayState(g2); // Gambar game di belakang

        int panelX = SCREEN_WIDTH / 4;
        int panelY = SCREEN_HEIGHT / 4;
        int panelWidth = SCREEN_WIDTH / 2;
        int panelHeight = SCREEN_HEIGHT / 2;

        // Latar belakang panel menu rumah
        g2.setColor(new Color(30, 30, 70, 220)); // Biru tua semi-transparan
        g2.fillRect(panelX, panelY, panelWidth, panelHeight);
        g2.setColor(Color.WHITE);
        g2.drawRect(panelX, panelY, panelWidth, panelHeight);

        // Judul
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        String title = "House Actions";
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, panelX + (panelWidth - titleWidth) / 2, panelY + 40);

        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        int actionY = panelY + 80;
        int actionX = panelX + 30;
        int lineHeight = 30;

        List<String> actions = gameController.getHouseActions();
        int selectedIndex = gameController.getSelectedHouseActionIndex();

        if (actions.isEmpty()) {
            g2.drawString("No actions available.", actionX, actionY);
        } else {
            for (int i = 0; i < actions.size(); i++) {
                String actionText = actions.get(i);
                if (i == selectedIndex) {
                    g2.setColor(Color.YELLOW);
                    g2.drawString("> " + actionText, actionX, actionY + (i * lineHeight));
                    g2.setColor(Color.WHITE);
                } else {
                    g2.drawString("  " + actionText, actionX, actionY + (i * lineHeight));
                }
            }
        }
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.drawString("W/S: Navigate | Enter: Select | Esc: Exit", panelX + 20, panelY + panelHeight - 20);

    }
    private int getXforCenteredText(String text, Graphics2D g2) {
        return SCREEN_WIDTH / 2 - (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth() / 2;
    }

    private void drawSleepState(Graphics2D g2) {
        drawPlayState(g2); // Atau g2.setColor(Color.BLACK); g2.fillRect(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);

        // Overlay semi-transparan
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // Tampilkan pesan transisi
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 28));
        String message = gameController.getTransitionMessage(); // Ambil pesan dari controller
        if (message == null || message.isEmpty()) {
            message = "Processing..."; // Pesan default jika tidak ada
        }

        int x = getXforCenteredText(message, g2);
        int y = SCREEN_HEIGHT / 2 - 30;
        g2.drawString(message, x, y);

        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        String continueMessage = "Press Enter or Esc to continue...";
        x = getXforCenteredText(continueMessage, g2);
        g2.drawString(continueMessage, x, y + 50);
    }

    private void drawCookState(Graphics2D g2) {
        drawPlayState(g2);
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // Tampilkan pesan transisi
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 28));
        String message = gameController.getTransitionMessage(); // Ambil pesan dari controller
        if (message == null || message.isEmpty()) {
            message = "Processing..."; // Pesan default jika tidak ada
        }

        int x = getXforCenteredText(message, g2);
        int y = SCREEN_HEIGHT / 2 - 30;
        g2.drawString(message, x, y);

        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        String continueMessage = "Press Enter or Esc to continue...";
        x = getXforCenteredText(continueMessage, g2);
        g2.drawString(continueMessage, x, y + 50);
    }
    private void drawRecipeState(Graphics2D g2) {
        drawPlayState(g2); // Gambar game di belakang

        int panelX = SCREEN_WIDTH / 4;
        int panelY = SCREEN_HEIGHT / 4;
        int panelWidth = SCREEN_WIDTH / 2;
        int panelHeight = SCREEN_HEIGHT / 2  + 50;

        // Latar belakang panel menu rumah
        g2.setColor(new Color(30, 30, 70, 220)); // Biru tua semi-transparan
        g2.fillRect(panelX, panelY, panelWidth, panelHeight);
        g2.setColor(Color.WHITE);
        g2.drawRect(panelX, panelY, panelWidth, panelHeight);

        // Judul
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        String title = "Recipe Book";
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, panelX + (panelWidth - titleWidth) / 2, panelY + 40);

        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        int actionY = panelY + 80;
        int actionX = panelX + 30;
        int lineHeight = 30;

        List<String> actions = gameController.getRecipes();
        int selectedIndex = gameController.getSelectedRecipeIndex();

        if (actions.isEmpty()) {
            g2.drawString("No recipe available.", actionX, actionY);
        } else {
            for (int i = 0; i < actions.size(); i++) {
                String actionText = actions.get(i);
                if (i == selectedIndex) {
                    g2.setColor(Color.YELLOW);
                    g2.drawString("> " + actionText, actionX, actionY + (i * lineHeight));
                    g2.setColor(Color.WHITE);
                } else {
                    g2.drawString("  " + actionText, actionX, actionY + (i * lineHeight));
                }
            }
        }
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.drawString("W/S: Navigate | Enter: Select | Esc: Exit", panelX + 20, panelY + panelHeight - 20);        
    }
    private void drawFishingState(Graphics2D g2) {
        drawPlayState(g2);
        g2.setColor(new Color(0, 0, 80, 180));
        g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 32));
        String title = "Fishing";
        int x = getXforCenteredText(title, g2);
        int y = 100;
        g2.drawString(title, x, y);
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        if (fishingGuessActive) {
            g2.drawString(fishingMessage, x, y + 40);
            g2.drawString("Type your guess and press Enter.", x, y + 70);
        } else {
            g2.drawString(fishingMessage, x, y + 70);
            g2.drawString("Press E to cast your line! Esc to exit.", x, y + 40);
        }
    }

    // --- FISHING GUESSING GAME METHODS ---
    // Call this to start the guessing game from GameController
    private void drawFishGuessState(Graphics2D g2) {
        drawPlayState(g2);

        g2.setColor(new Color(0, 0, 60, 210)); // Overlay
        g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 28));
        String title = "GUESS THE NUMBER!"; // Judul lebih spesifik
        int titleX = getXforCenteredText(title, g2);
        int currentY = SCREEN_HEIGHT / 5;
        g2.drawString(title, titleX, currentY);
        currentY += 40;

        g2.setFont(new Font("Arial", Font.PLAIN, 18));

        // Tampilkan pesan (instruksi, sisa percobaan)
        if (fishingUIMessage != null && !fishingUIMessage.isEmpty()) {
            for (String line : fishingUIMessage.split("\\n")) {
                g2.drawString(line, getXforCenteredText(line, g2), currentY);
                currentY += 25;
            }
        }
        currentY += 20;

        int sliderWidth = SCREEN_WIDTH / 2;
        int sliderX = SCREEN_WIDTH / 2 - sliderWidth / 2;
        int sliderHeight = 20;
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(sliderX, currentY, sliderWidth, sliderHeight);
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawRect(sliderX, currentY, sliderWidth, sliderHeight);

        double percentage = 0;
        if (fishingSliderMaxDisplay > fishingSliderMinDisplay) {
            percentage = (double) (fishingSliderDisplayValue - fishingSliderMinDisplay) / (fishingSliderMaxDisplay - fishingSliderMinDisplay);
        }
        percentage = Math.max(0.0, Math.min(1.0, percentage)) ;

        int knobWidth = TILE_SIZE > 0 ? TILE_SIZE : 20; // Pastikan knobWidth > 0
        knobWidth = Math.max(10, knobWidth/2) ;
        int knobX = sliderX + (int) (percentage * (sliderWidth - knobWidth)) ;
        g2.setColor(Color.CYAN);
        g2.fillRect(knobX, currentY - 5, knobWidth, sliderHeight + 10);

        String sliderValueText = "Selected: " + fishingSliderDisplayValue;
        g2.setColor(Color.YELLOW);
        g2.drawString(sliderValueText, getXforCenteredText(sliderValueText, g2), currentY + sliderHeight + 30);
        currentY += sliderHeight + 30 + 25;

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.ITALIC, 16));
        g2.drawString("Use A/D or Left/Right. 'E' or Enter to confirm.", getXforCenteredText("Use A/D or Left/Right. 'E' or Enter to confirm.", g2), currentY);
    }
     public void setFishingMessage(String message) {
        this.fishingUIMessage = message;
        repaint();
    }
    public void setFishingMessage2(String message) {
        this.fishingMessage = message;
    }

    // Dipanggil oleh GameController untuk memulai UI tebakan slider
    public void startFishingSliderUI(String initialMessage, int min, int max, int currentValue, int triesLeft) {
        this.isFishingSliderUIVisible = true; // Aktifkan UI slider
        this.fishingUIMessage = initialMessage;
        this.fishingSliderMinDisplay = min;
        this.fishingSliderMaxDisplay = max;
        this.fishingSliderDisplayValue = currentValue;
        // this.fishingTriesLeftForDisplay = triesLeft; // Biasanya info ini ada di initialMessage
        System.out.println("[View] Starting fishing SLIDER UI. Value: " + currentValue + " Message: " + initialMessage);
        repaint();
    }

    public void updateFishingSliderDisplay(int value, String message, int triesLeft) {
        this.fishingSliderDisplayValue = value;
        this.fishingUIMessage = message; // Pesan mungkin berisi sisa percobaan
        // this.fishingTriesLeftForDisplay = triesLeft;
        System.out.println("[View] Updating fishing SLIDER UI. Value: " + value + " Message: " + message);
        repaint();
    }

    // Dipanggil oleh GameController setelah tebakan (salah satu tebakan) diproses
    // atau semua percobaan habis. Ini menandakan mode input slider tidak aktif lagi.
    public void endFishingSliderUI(boolean success) {
        this.isFishingSliderUIVisible = false; // Mode adjusment slider selesai
        // Pesan hasil (sukses/gagal) akan di-set oleh GameController via setFishingMessage.
        System.out.println("[View] Ending fishing SLIDER input UI. Success: " + success);
        repaint();
    }
    public void clearFishingUIState() {
        this.isFishingSliderUIVisible = false;
        this.fishingUIMessage = "";
        this.fishingSliderDisplayValue = 0;
        this.fishingSliderMinDisplay = 0;
        this.fishingSliderMaxDisplay = 0;
        System.out.println("[View] All Fishing UI state (slider) cleared.");
        repaint();
    }

    // Dipanggil oleh KeyHandler
    public void signalFishingInteractionComplete() {
        System.out.println("[View] Signaling GameController to end fishing session.");
        if (gameController != null) {
            gameController.endFishingSession();
        }
    }
    public boolean isFishingSliderActive() {
        return gameStateModel.isGuessingFish();
    }



    @Override public void onPlayerUpdated(Player player) { /* ... */ }
    @Override public void onGameStateChanged(int newState, int oldState) { /* ... */ }
}
