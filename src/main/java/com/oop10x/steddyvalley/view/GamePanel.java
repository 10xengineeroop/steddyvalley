package com.oop10x.steddyvalley.view;

import com.oop10x.steddyvalley.model.FarmMap; // Import FarmMap
import com.oop10x.steddyvalley.model.GameState;
import com.oop10x.steddyvalley.model.GameStateObserver;
import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.model.PlayerObserver;
import com.oop10x.steddyvalley.model.items.Item;
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
    private boolean isFishingSliderUIVisible = false;
    private String fishingUIMessage = "";
    private int fishingSliderDisplayValue = 0;
    private int fishingSliderMinDisplay = 0;
    private int fishingSliderMaxDisplay = 0;  

    public GamePanel(Player player, GameState gameState, GameController controller, FarmMap farmMap) {
        this.playerModel = player;
        this.gameStateModel = gameState;
        this.gameController = controller;
        this.farmMapModel = farmMap;

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
            drawFishGuessState(g2);
        } else if (currentGameState == GameState.MESSAGE_TV) {
            drawMessageDisplayState(g2);
        }
        /* else if (currentGameState == GameState.SHOP_STATE) {
            drawShopState(g2);
        } */

        g2.dispose();
    }

    private void drawShopState(Graphics2D g2) {
        drawPlayState(g2); 

        int panelX = SCREEN_WIDTH / 8;
        int panelY = SCREEN_HEIGHT / 8;
        int panelWidth = SCREEN_WIDTH * 3 / 4;
        int panelHeight = SCREEN_HEIGHT * 3 / 4;

        g2.setColor(new Color(50, 30, 10, 230)); 
        g2.fillRect(panelX, panelY, panelWidth, panelHeight);
        g2.setColor(Color.ORANGE); 
        g2.setStroke(new java.awt.BasicStroke(2));
        g2.drawRect(panelX, panelY, panelWidth, panelHeight);

        g2.setFont(new Font("Arial", Font.BOLD, 28));
        g2.setColor(Color.YELLOW);
        String title = "Toko Serba Ada Emily";
        int titleX = getXforCenteredText(title, g2, panelX, panelWidth);
        g2.drawString(title, titleX, panelY + 40);

        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.setColor(Color.WHITE);
        g2.drawString("Gold: " + playerModel.getGold() + "g", panelX + panelWidth - 150, panelY + 40);


        if (gameController.isShowingShopFeedback()) {
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.setColor(Color.CYAN);
            String feedback = gameController.getShopFeedbackMessage();
            int feedbackX = getXforCenteredText(feedback, g2, panelX, panelWidth);
            g2.drawString(feedback, feedbackX, panelY + panelHeight / 2 - 10);

            g2.setFont(new Font("Arial", Font.PLAIN, 16));
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString("Tekan Esc untuk kembali", getXforCenteredText("Tekan Esc untuk kembali", g2, panelX, panelWidth), panelY + panelHeight / 2 + 30);
        } else {
            List<Item> shopItems = gameController.getCurrentShopItems();
            int selectedIdx = gameController.getSelectedShopItemIndex();

            g2.setFont(new Font("Arial", Font.PLAIN, 18));
            int itemDrawY = panelY + 80; 
            int itemDrawX = panelX + 30;
            int itemLineHeight = 28;

            int maxVisibleItems = (panelHeight - 120) / itemLineHeight; 
            int scrollOffset = 0;
            if (shopItems != null && shopItems.size() > maxVisibleItems) {
                if (selectedIdx >= maxVisibleItems -1) { 
                    scrollOffset = selectedIdx - (maxVisibleItems -1) +1;
                }
                scrollOffset = Math.max(0, Math.min(scrollOffset, shopItems.size() - maxVisibleItems));
            }


            if (shopItems == null || shopItems.isEmpty()) {
                g2.setColor(Color.GRAY);
                g2.drawString("Maaf, tidak ada item yang dijual saat ini.", itemDrawX, itemDrawY);
            } else {
                for (int i = 0; i < shopItems.size(); i++) {
                    if (i < scrollOffset) continue;
                    if (i - scrollOffset >= maxVisibleItems) break;

                    Item currentItem = shopItems.get(i);
                    Integer price = currentItem.getBuyPrice();
                    String priceString = (price != null) ? price + "g" : "N/A";
                    String itemText = currentItem.getName() + " - " + priceString;
                    
                    int currentItemY = itemDrawY + ((i - scrollOffset) * itemLineHeight);

                    if (i == selectedIdx) {
                        g2.setColor(Color.YELLOW);
                        g2.drawString("> " + itemText, itemDrawX - 10, currentItemY); // Pointer sedikit ke kiri
                    } else {
                        g2.setColor(Color.WHITE);
                        g2.drawString("  " + itemText, itemDrawX, currentItemY);
                    }
                }
            }
            // Petunjuk
            g2.setFont(new Font("Arial", Font.ITALIC, 14));
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString("W/S: Pilih | E: Beli 1 | Esc: Keluar Toko", panelX + 20, panelY + panelHeight - 25);
        }
    }

    private void drawMessageDisplayState(Graphics2D g2) {
        drawPlayState(g2);
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    
        String message = gameController.getMessageTV();
        if (message == null || message.isEmpty()) {
            message = "Displaying message...";
        }
    
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        int yPos = SCREEN_HEIGHT / 2 - 30;
        for (String line : message.split("\n")) {
            int stringWidth = g2.getFontMetrics().stringWidth(line);
            int xPos = (SCREEN_WIDTH - stringWidth) / 2;
            g2.drawString(line, xPos, yPos);
            yPos += 30;
        }
    
        g2.setFont(new Font("Arial", Font.ITALIC, 18));
        String continueMessage = "Press Escape to continue...";
        int continueStringWidth = g2.getFontMetrics().stringWidth(continueMessage);
        int continueXPos = (SCREEN_WIDTH - continueStringWidth) / 2;
        g2.drawString(continueMessage, continueXPos, yPos + 20);
    }

    public void setManagers(com.oop10x.steddyvalley.model.TimeManager tm, com.oop10x.steddyvalley.model.SeasonManager sm, com.oop10x.steddyvalley.model.WeatherManager wm) {
        this.timeManager = tm;
        this.seasonManager = sm;
        this.weatherManager = wm;
    }

    private void drawPlayState(Graphics2D g2) {
        if (farmMapModel != null) {
            farmMapModel.draw(g2, TILE_SIZE); // Berikan tileSize ke FarmMap.draw
        }

        int playerScreenX = playerModel.getPosition().getX();
        int playerScreenY = playerModel.getPosition().getY();
        g2.setColor(Color.BLUE);
        g2.fillRect(playerScreenX, playerScreenY, TILE_SIZE, TILE_SIZE);

        // --- HUD: Time, Weather, Season ---
        int hudPadding = 10;
        Font hudFont = new Font("Arial", Font.BOLD, 16);
        g2.setFont(hudFont);
        int textHeight = g2.getFontMetrics().getHeight();
        int singleLineHudHeight = textHeight + (hudPadding * 2);

        String timeStr = "Time: --:--";
        String seasonStr = "Season: -";
        String weatherStr = "Weather: -";
        String dayStr = "Day ";

        if (timeManager != null) {
            int mins = timeManager.getMinutes();
            int hour = (mins / 60) % 24;
            int min = mins % 60;
            String ampm = (hour < 12 || hour == 24) ? "AM" : "PM";
            if (hour == 0) {
                hour = 12;
            } else if (hour > 12) {
                hour -= 12;
            }
            timeStr = String.format("Time: %02d:%02d %s", hour, min, ampm);
            dayStr = String.format("Day %d", (mins / 1440) + 1);
        }
        if (seasonManager != null) {
            seasonStr = "Season: " + seasonManager.getCurrentSeason();
        }
        if (weatherManager != null) {
            weatherStr = "Weather: " + weatherManager.getCurrentWeather();
        }

        int timeStrWidth = g2.getFontMetrics().stringWidth(timeStr);
        int seasonStrWidth = g2.getFontMetrics().stringWidth(seasonStr);
        int weatherStrWidth = g2.getFontMetrics().stringWidth(weatherStr);
        int dayStrWidth = g2.getFontMetrics().stringWidth(dayStr);

        int totalTextWidth = timeStrWidth + seasonStrWidth + weatherStrWidth + dayStrWidth;
        int interTextPadding = hudPadding * 2;
        int hudWidth = totalTextWidth + (interTextPadding * 2) + (hudPadding * 2);

        g2.setColor(new Color(0,0,0,180));
        g2.fillRoundRect(5, 5, hudWidth, singleLineHudHeight, 15, 15);

        g2.setColor(Color.WHITE);
        int textY = 5 + hudPadding + g2.getFontMetrics().getAscent();

        int currentX = 5 + hudPadding;
        g2.drawString(timeStr, currentX, textY);

        currentX += timeStrWidth + interTextPadding;
        g2.drawString(seasonStr, currentX, textY);

        currentX += seasonStrWidth + interTextPadding;
        g2.drawString(weatherStr, currentX, textY);

        currentX += weatherStrWidth + interTextPadding;
        g2.drawString(dayStr, currentX, textY);
        // Ghazy

        // Informasi Debug
        int debugStartX = 10;
        int debugStartY = 70;
        int debugLineHeight = 20;
        int debugBgPadding = 5;
        int cornerRadius = 10;

        String line1 = "Player X: " + playerScreenX + " Y: " + playerScreenY;
        String line2 = "STATE: PLAYING (ESC:Pause, I:Inventory, E:Action)";
        String line3 = "Gold: " + playerModel.getGold() + " Energy: " + playerModel.getEnergy();
        String line4 = playerModel.getEquippedItem() != null ? "Equipped: " + playerModel.getEquippedItem().getName() : "";

        g2.setFont(g2.getFont().deriveFont(13F));
        int ascent = g2.getFontMetrics().getAscent();
        int maxWidth = g2.getFontMetrics().stringWidth(line1);
        maxWidth = Math.max(maxWidth, g2.getFontMetrics().stringWidth(line2));
        maxWidth = Math.max(maxWidth, g2.getFontMetrics().stringWidth(line3));
        if (!line4.isEmpty()) {
            maxWidth = Math.max(maxWidth, g2.getFontMetrics().stringWidth(line4));
        }
        int debugBgHeight = (line4.isEmpty() ? 3 : 4) * debugLineHeight - (debugLineHeight - ascent) + (debugBgPadding *2);

        g2.setColor(new Color(255, 255, 255, 180));
        g2.fillRoundRect(
                debugStartX - debugBgPadding,
                debugStartY - ascent - debugBgPadding,
                maxWidth + (debugBgPadding * 2),
                debugBgHeight,
                cornerRadius,
                cornerRadius
        );

        g2.setColor(Color.BLACK);
        g2.drawString(line1, debugStartX, debugStartY);
        g2.drawString(line2, debugStartX, debugStartY + debugLineHeight);
        g2.drawString(line3, debugStartX, debugStartY + (debugLineHeight * 2));
        if (!line4.isEmpty()) {
            g2.drawString(line4, debugStartX, debugStartY + (debugLineHeight * 3));
        }
    }

    private void drawPauseState(Graphics2D g2) {
        drawPlayState(g2);
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 50));
        String text = "PAUSED";
        g2.drawString(text, getXforCenteredText(text, g2), SCREEN_HEIGHT / 2);
    }

    int selectedInventoryIndex = 0;
    private void drawInventoryState(Graphics2D g2) {
        drawPlayState(g2);
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
        if (playerModel.getEquippedItem() != null) {
            g2.drawString("Equipped: " + playerModel.getEquippedItem().getName(), pX + pW - 200, pY + 30);
        }
    }

    private void drawHouseState(Graphics2D g2) { 
        drawPlayState(g2);

        int panelX = SCREEN_WIDTH / 4;
        int panelY = SCREEN_HEIGHT / 4;
        int panelWidth = SCREEN_WIDTH / 2;
        int panelHeight = SCREEN_HEIGHT / 2;

        // Latar belakang panel menu rumah
        g2.setColor(new Color(30, 30, 70, 220));
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

        String controllerMessage = gameController.getMessageTV();
        if (controllerMessage != null && !controllerMessage.isEmpty()) {
            g2.setFont(new Font("Arial", Font.ITALIC, 16));
            g2.setColor(Color.CYAN);
            int messageX = panelX + 20;
            int messageY = panelY + panelHeight - 50;
            g2.drawString(controllerMessage, messageX, messageY);
        }

        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.drawString("W/S: Navigate | E: Select | Esc: Exit", panelX + 20, panelY + panelHeight - 20);

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
        String message = gameController.getTransitionMessage();
        if (message == null || message.isEmpty()) {
            message = "Processing...";
        }

        int x = getXforCenteredText(message, g2);
        int y = SCREEN_HEIGHT / 2 - 30;
        g2.drawString(message, x, y);

        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        String continueMessage = "Press Esc to continue...";
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
        String message = gameController.getTransitionMessage();
        if (message == null || message.isEmpty()) {
            message = "Processing...";
        }

        int x = getXforCenteredText(message, g2);
        int y = SCREEN_HEIGHT / 2 - 30;
        g2.drawString(message, x, y);

        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        String continueMessage = "Press Esc to continue...";
        x = getXforCenteredText(continueMessage, g2);
        g2.drawString(continueMessage, x, y + 50);
    }
    private void drawRecipeState(Graphics2D g2) {
        drawPlayState(g2);

        int panelX = SCREEN_WIDTH / 4;
        int panelY = SCREEN_HEIGHT / 4;
        int panelWidth = SCREEN_WIDTH / 2;
        int panelHeight = SCREEN_HEIGHT / 2  + 50;

        g2.setColor(new Color(30, 30, 70, 220));
        g2.fillRect(panelX, panelY, panelWidth, panelHeight);
        g2.setColor(Color.WHITE);
        g2.drawRect(panelX, panelY, panelWidth, panelHeight);

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
        g2.drawString("W/S: Navigate | E: Select | Esc: Exit", panelX + 20, panelY + panelHeight - 20);        
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
        if (fishingUIMessage != null && !fishingUIMessage.isEmpty()) {
            int lineY = y + 40;
            for (String line : fishingUIMessage.split("\n")) {
                g2.drawString(line, getXforCenteredText(line, g2), lineY);
                lineY += 25;
            }
        } else {
            String defaultPrompt = "Press E to cast your line! Esc to exit.";
            g2.drawString(defaultPrompt, getXforCenteredText(defaultPrompt, g2), y + 40);
        }
    }

    private void drawFishGuessState(Graphics2D g2) {
        drawPlayState(g2);

        g2.setColor(new Color(0, 0, 60, 210));
        g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 28));
        String title = "GUESS THE NUMBER!";
        int titleX = getXforCenteredText(title, g2);
        int currentY = SCREEN_HEIGHT / 5;
        g2.drawString(title, titleX, currentY);
        currentY += 40;

        g2.setFont(new Font("Arial", Font.PLAIN, 18));

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

        int knobWidth = TILE_SIZE;
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
        g2.drawString("Use A/D or Left/Right. Click 'E' to confirm.", getXforCenteredText("Use A/D or Left/Right. Click 'E' to confirm.", g2), currentY);
    }
     public void setFishingMessage(String message) {
        this.fishingUIMessage = message;
        repaint();
    }
    public void setFishingMessage2(String message) {
        this.fishingMessage = message;
    }

    public void startFishingSliderUI(String initialMessage, int min, int max, int currentValue, int triesLeft) {
        this.isFishingSliderUIVisible = true;
        this.fishingUIMessage = initialMessage;
        this.fishingSliderMinDisplay = min;
        this.fishingSliderMaxDisplay = max;
        this.fishingSliderDisplayValue = currentValue;
        // this.fishingTriesLeftForDisplay = triesLeft;
        System.out.println("[View] Starting fishing SLIDER UI. Value: " + currentValue + " Message: " + initialMessage);
        repaint();
    }

    public void updateFishingSliderDisplay(int value, String message, int triesLeft) {
        this.fishingSliderDisplayValue = value;
        this.fishingUIMessage = message;
        // this.fishingTriesLeftForDisplay = triesLeft;
        System.out.println("[View] Updating fishing SLIDER UI. Value: " + value + " Message: " + message);
        repaint();
    }

    public void endFishingSliderUI(boolean success) {
        this.isFishingSliderUIVisible = false;
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

    public void signalFishingInteractionComplete() {
        System.out.println("[View] Signaling GameController to end fishing session.");
        if (gameController != null) {
            gameController.endFishingSession();
        }
    }
    public boolean isFishingSliderActive() {
        return gameStateModel.isGuessingFish();
    }

    private int getXforCenteredText(String text, Graphics2D g2, int areaX, int areaWidth) {
        int stringWidth = g2.getFontMetrics().stringWidth(text);
        return areaX + (areaWidth - stringWidth) / 2;
    }


    @Override public void onPlayerUpdated(Player player) { /* ... */ }
    @Override public void onGameStateChanged(int newState, int oldState) { /* ... */ }
}