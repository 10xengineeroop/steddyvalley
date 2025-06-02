package com.oop10x.steddyvalley.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import com.oop10x.steddyvalley.controller.GameController;
import com.oop10x.steddyvalley.model.FarmMap;
import com.oop10x.steddyvalley.model.GameState;
import com.oop10x.steddyvalley.model.GameStateObserver;
import com.oop10x.steddyvalley.model.NPC;
import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.model.PlayerObserver;
import com.oop10x.steddyvalley.model.SeasonManager;
import com.oop10x.steddyvalley.model.TimeManager;
import com.oop10x.steddyvalley.model.WeatherManager;
import com.oop10x.steddyvalley.model.items.Item;
import com.oop10x.steddyvalley.utils.FishRarity;
import com.oop10x.steddyvalley.utils.RelStatus;
import java.util.List;
import java.util.Map;


public class GamePanel extends JPanel implements Runnable, PlayerObserver, GameStateObserver {

    public static final int TILE_SIZE = 24;
    public final int MAX_SCREEN_COL = FarmMap.MAP_WIDTH_IN_TILES;
    public final int MAX_SCREEN_ROW = FarmMap.MAP_HEIGHT_IN_TILES;
    public final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COL;
    public final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_ROW;

    private final int FPS = 60;
    private Thread gameThread;

    private Player playerModel;
    private GameState gameStateModel;
    private GameController gameController;
    private FarmMap farmMapModel;

    private int endGameStatsScrollY = 0;
    private int endGameStatsTotalContentHeight = 0;
    private int statAreaVisibleHeight = 0;

    // --- HUD fields ---
    private TimeManager timeManager;
    private SeasonManager seasonManager;
    private WeatherManager weatherManager;

    // --- FISHING GUESSING GAME STATE ---
    private boolean isFishingSliderUIVisible = false;
    private String fishingUIMessage = "";
    private int fishingSliderDisplayValue = 0;
    private int fishingSliderMinDisplay = 0;
    private int fishingSliderMaxDisplay = 0;  

    public GamePanel(Player playerModel, GameState gameStateModel, GameController gameController, FarmMap farmMapModel) {
        this.playerModel = playerModel;
        this.gameStateModel = gameStateModel;
        this.gameController = gameController;
        this.farmMapModel = farmMapModel;

        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);

        if (playerModel != null) playerModel.addObserver(this);
        if (gameStateModel != null) gameStateModel.addObserver(this);
    }

    public void startGameThread() { 
        if (gameThread == null) { 
            gameThread = new Thread(this); 
            gameThread.start(); 
        }
    }
    public void stopGameThread() { 
        if (gameThread != null) { 
            gameThread = null; 
        }
    }
    @Override public void run() {
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
    public void updateGame() {
        gameController.updateGameLogic();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int currentGameState = gameStateModel.getCurrentState();

        if (currentGameState == GameState.MAIN_MENU_STATE) {
            drawMainMenuState(g2);
        } else if (currentGameState == GameState.PLAY_STATE) {
            drawPlayState(g2);
        } 
        else if (currentGameState == GameState.PAUSE_MENU_STATE) {
            drawPauseState(g2);
        } 
        else if (currentGameState == GameState.INVENTORY_STATE) {
            drawInventoryState(g2);
        } 
        else if (currentGameState == GameState.HOUSE_STATE) {
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
        } 
        else if (currentGameState == GameState.MESSAGE_TV) {
            drawMessageDisplayState(g2);
        }
        else if (currentGameState == GameState.SHOP_STATE) {
            drawShopState(g2);
        }
        else if (currentGameState == GameState.VISIT_STATE) {
            drawVisitState(g2);
        }
        else if (currentGameState == GameState.NPCVISIT_STATE) {
            drawNPCVisitState(g2);
        }
        else if (currentGameState == GameState.STOREOPT_STATE) {
            drawStoreOptState(g2);
        }
        else if (currentGameState == GameState.GIFT_STATE) {
            drawGiftingState(g2);
        }
        else if (currentGameState == GameState.GIFTED_STATE) {
            drawGiftedState(g2);
        }
        else if (currentGameState == GameState.ENDGAME_STATE) {
            drawEndGameState(g2);
        }
        else if (currentGameState == GameState.SHIPPING_STATE) {
            drawShippingState(g2);
        }
        else if (currentGameState == GameState.PLAYER_NAME_INPUT_STATE) {
            drawPlayerNameInputState(g2);
        }
        else if (currentGameState == GameState.HELP_STATE) {
            drawHelpState(g2);
        }
        else if (currentGameState == GameState.CREDITS_STATE) {
            drawCreditsState(g2);
        }
        else if (currentGameState == GameState.PLAYER_GENDER_INPUT_STATE) {
            drawPlayerGenderInputState(g2);
        } 
        else if (currentGameState == GameState.PLAYER_FAV_ITEM_INPUT_STATE) {
            drawPlayerFavItemInputState(g2);
        }

        if (currentGameState == GameState.PLAY_STATE || currentGameState == GameState.INVENTORY_STATE || currentGameState == GameState.HOUSE_STATE) {
        drawHUD(g2);
        }

        g2.dispose();
    }

    private void drawHUD(Graphics2D g2) {
        if (timeManager == null || seasonManagerInternal == null || playerModel == null || gameController == null) {
             System.err.println("[GamePanel] HUD draw skipped: One or more managers/models are null.");
             return;
        }

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Font hudFont = new Font("Arial", Font.BOLD, 15);
        g2.setFont(hudFont);
        FontMetrics fm = g2.getFontMetrics();
        int padding = 8;
        int lineHeight = fm.getHeight();
        int lineSpacing = 4;

        List<String> hudLines = new ArrayList<>();
        hudLines.add(String.format("Time: %02d:%02d %s", (timeManager.getMinutes() / 60) % 24 == 0 ? 12 : (timeManager.getMinutes() / 60) % 24 > 12 ? ((timeManager.getMinutes() / 60) % 24) - 12 : (timeManager.getMinutes() / 60) % 24, timeManager.getMinutes() % 60, ((timeManager.getMinutes() / 60) % 24 < 12 || (timeManager.getMinutes() / 60) % 24 == 0) ? "AM" : "PM"));
        hudLines.add("Day: " + timeManager.getCurrentDay());
        hudLines.add("Season: " + seasonManagerInternal.getCurrentSeason().toString());
        if (weatherManagerInternal != null) {
             hudLines.add("Weather: " + weatherManagerInternal.getCurrentWeather().toString());
        } else {
             hudLines.add("Weather: N/A");
        }
        hudLines.add("Gold: " + playerModel.getGold() + "g");
        hudLines.add("Energy: " + playerModel.getEnergy());

        String playerName = gameController.getTempPlayerName();
        String playerGender = gameController.getTempPlayerGender();
        String playerFavItem = gameController.getTempPlayerFavItem();

        if (gameStateModel.isPlaying() || gameStateModel.isInHouse()) {
            playerName = playerModel.getName();
            playerGender = playerModel.getGender();
            playerFavItem = playerModel.getFavoriteItem();
        }

        hudLines.add("Name: " + (playerName != null ? playerName : "N/A"));
        hudLines.add("Gender: " + (playerGender != null ? playerGender : "N/A"));
        hudLines.add("Fav Item: " + (playerFavItem != null ? playerFavItem : "N/A"));
        hudLines.add("Partner: " + playerModel.getPartnerStatus());

        int maxWidth = 0;
        for (String line : hudLines) {
            if (line != null) maxWidth = Math.max(maxWidth, fm.stringWidth(line));
        }

        int hudPanelWidth = maxWidth + padding * 2;
        int hudPanelHeight = (lineHeight * hudLines.size()) + (lineSpacing * (hudLines.size() -1)) + (padding * 2) ;
        int hudX = 5;
        int hudY = 5;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(hudX, hudY, hudPanelWidth, hudPanelHeight, 10, 10);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(hudX, hudY, hudPanelWidth, hudPanelHeight, 10, 10);

        int textY = hudY + padding + fm.getAscent();
        for (String line : hudLines) {
            if (line != null) g2.drawString(line, hudX + padding, textY);
            textY += lineHeight + lineSpacing;
        }
    }

    private void drawMainMenuState(Graphics2D g2) {
        try {
            g2.setColor(new Color(20, 20, 50));
            g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

            g2.setFont(new Font("Arial", Font.BOLD, 50));
            g2.setColor(Color.YELLOW);
            String gameTitle = "Spakbor Hills";
            int titleX = getXforCenteredText(gameTitle, g2);
            g2.drawString(gameTitle, titleX, SCREEN_HEIGHT / 4);

            g2.setFont(new Font("Arial", Font.PLAIN, 28));
            List<String> options = gameController.getMainMenuOptions();
            int selectedIndex = gameController.getSelectedMainMenuIndex();
            int optionY = SCREEN_HEIGHT / 4 + 100;
            int lineHeight = 40;

            if (options != null) {
                for (int i = 0; i < options.size(); i++) {
                    if (i == selectedIndex) {
                        g2.setColor(Color.ORANGE);
                        g2.drawString("> " + options.get(i), getXforCenteredText("> " + options.get(i), g2) - 15, optionY + (i * lineHeight));
                    } else {
                        g2.setColor(Color.WHITE);
                        g2.drawString(options.get(i), getXforCenteredText(options.get(i), g2), optionY + (i * lineHeight));
                    }
                }
            }
            
            String feedbackMsg = gameController.getTransitionMessage();
            if (feedbackMsg != null && !feedbackMsg.isEmpty() && !feedbackMsg.startsWith("Welcome")) {
                g2.setColor(Color.CYAN);
                g2.setFont(new Font("Arial", Font.ITALIC, 18));
                g2.drawString(feedbackMsg, getXforCenteredText(feedbackMsg, g2), SCREEN_HEIGHT - 60);
            }


        } catch (Exception e) {
            System.err.println("Error in drawMainMenuState: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void drawPlayerNameInputState(Graphics2D g2) {
        try {
            g2.setColor(new Color(20, 20, 50));
            g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

            g2.setFont(new Font("Arial", Font.BOLD, 28));
            g2.setColor(Color.WHITE);
            String prompt = "Enter Your Character's Name:";
            g2.drawString(prompt, getXforCenteredText(prompt, g2), SCREEN_HEIGHT / 3);

            String currentName = gameController.getPlayerNameInputBuffer();
            g2.setFont(new Font("Monospaced", Font.PLAIN, 32));
            g2.setColor(Color.YELLOW);
            int nameFieldWidth = 300;
            int nameFieldX = (SCREEN_WIDTH - nameFieldWidth) / 2;
            int nameFieldY = SCREEN_HEIGHT / 3 + 60;
            g2.drawRect(nameFieldX, nameFieldY - 30, nameFieldWidth, 40);
            g2.drawString(currentName + "_", nameFieldX + 10, nameFieldY);

            g2.setFont(new Font("Arial", Font.PLAIN, 16));
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString("Press E/Enter to confirm, Backspace to delete.", getXforCenteredText("Press E/Enter to confirm, Backspace to delete.", g2), nameFieldY + 60);

            String feedbackMsg = gameController.getTransitionMessage();
            if (feedbackMsg != null && !feedbackMsg.isEmpty() && feedbackMsg.contains("cannot be empty")) {
                 g2.setColor(Color.RED);
                 g2.drawString(feedbackMsg, getXforCenteredText(feedbackMsg, g2), nameFieldY + 90);
            }

        } catch (Exception e) {
            System.err.println("Error in drawPlayerNameInputState: " + e.getMessage());
            e.printStackTrace();
        }
   }

   private void drawHelpState(Graphics2D g2) {
       drawPlayState(g2);
       g2.setColor(new Color(0, 0, 0, 200));
       g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

       g2.setColor(Color.WHITE);
       g2.setFont(new Font("Arial", Font.BOLD, 24));
       String title = "How to Play Spakbor Hills";
       g2.drawString(title, getXforCenteredText(title, g2), SCREEN_HEIGHT / 4);

       g2.setFont(new Font("Arial", Font.PLAIN, 16));
       int helpY = SCREEN_HEIGHT / 4 + 50;
       String[] helpText = {
           "Welcome to Spakbor Hills! Your mission is to become a successful farmer.",
           "Controls:",
           "  W/A/S/D or Arrow Keys: Move player",
           "  E / Enter: Interact / Select Menu / Confirm",
           "  I: Open/Close Inventory",
           "  Esc: Pause Game / Go Back / Exit Menus",
           "Game Basics:",
           "  - Manage your Energy and Gold.",
           "  - Till land, plant seeds, water crops, and harvest them.",
           "  - Sell items via the Shipping Bin (next to your house).",
           "  - Cook food to restore energy.",
           "  - Fish at various locations.",
           "  - Interact with NPCs: Chat, Gift, Propose, Marry.",
           "  - Watch TV for tomorrow's weather.",
           "  - Sleep to save game (implicitly) and restore energy.",
           "  - Reach game milestones (Gold or Marriage) to see your progress!"
       };
       for (String line : helpText) {
           g2.drawString(line, 50, helpY);
           helpY += 25;
       }
       g2.setFont(new Font("Arial", Font.ITALIC, 18));
       g2.drawString("Press Esc to return to Main Menu.", getXforCenteredText("Press Esc to return to Main Menu.", g2), SCREEN_HEIGHT - 50);
   }

   private void drawCreditsState(Graphics2D g2) {
       drawPlayState(g2);
       g2.setColor(new Color(0, 0, 0, 200));
       g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

       g2.setColor(Color.WHITE);
       g2.setFont(new Font("Arial", Font.BOLD, 24));
       String title = "Game Credits";
       g2.drawString(title, getXforCenteredText(title, g2), SCREEN_HEIGHT / 4);

       g2.setFont(new Font("Arial", Font.PLAIN, 18));
       int creditY = SCREEN_HEIGHT / 4 + 60;
       String[] creditsText = {
           "Spakbor Hills - A Farming Adventure",
           "",
           "Developed By:",
           "18222011 Serenada Cinta Sunindyo\n",
           "18223041 Luckman Fakhmanidris Arvasirri : Controller, view\n",
           "18223093 Ghazy Achmed Movlech Urbayani : Controller, view\n",
           "18223009 Muhammad Faiz Alfikrona : Model\n",
           "18223087 Bryan Adi Priasmoro\n",
           "",
           "Special Thanks to Dr. Asep Spakbor & Agen Purry!"
       };
       for (String line : creditsText) {
           g2.drawString(line, getXforCenteredText(line, g2), creditY);
           creditY += 28;
       }
       g2.setFont(new Font("Arial", Font.ITALIC, 18));
       g2.drawString("Press Esc to return to Main Menu.", getXforCenteredText("Press Esc to return to Main Menu.", g2), SCREEN_HEIGHT - 50);
   }

    private void drawShippingState(Graphics2D g2) {
        try {
            drawPlayState(g2);
            // -- Panel utama untuk shipping --
            int pX = 30, pY = 30, pW = SCREEN_WIDTH - 60, pH = SCREEN_HEIGHT - 60;
            g2.setColor(new Color(30, 30, 70, 220));
            g2.fillRoundRect(pX, pY, pW, pH, 20, 20);
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(pX, pY, pW, pH, 20, 20);

            g2.setFont(new Font("Arial", Font.BOLD, 26));
            g2.drawString("Shipping Bin\n", pX + 20, pY + 25);

            int lineHeight = 25;
            int itemStartY = pY + 80;
            int itemStartXPlayer = pX + 30;
            int itemStartXBin = pX + pW / 2 + 20;

            // --- Tampilkan Inventory Pemain ---
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.setColor(Color.WHITE);
            g2.drawString("\nYour Inventory (W/S, E to Ship):", itemStartXPlayer, itemStartY - 30);
            
            Map<Item, Integer> playerItemsMap = playerModel.getInventory().getAllItems();
            List<Item> playerItemList = new ArrayList<>(playerItemsMap.keySet());
            int selectedPlayerItemIdx = gameController.getSelectedShippingInventoryIndex();

            g2.setFont(new Font("Arial", Font.PLAIN, 16));
            int maxVisibleItems = (pH - 180) / lineHeight;
            int scrollOffsetPlayer = 0;
            if (playerItemList.size() > 0 && selectedPlayerItemIdx >= maxVisibleItems) {
                scrollOffsetPlayer = selectedPlayerItemIdx - maxVisibleItems + 1;
            }

            for (int i = 0; i < playerItemList.size(); i++) {
                if (i < scrollOffsetPlayer) continue;
                if ((i - scrollOffsetPlayer) >= maxVisibleItems) break;

                Item item = playerItemList.get(i);
                int amount = playerItemsMap.get(item);
                String itemText = item.getName() + " x" + amount;
                
                Integer sellPrice = item.getSellPrice();
                if (sellPrice != null && sellPrice > 0) {
                    itemText += " (" + sellPrice + "g each)";
                } else {
                    itemText += " (Unsellable)";
                }

                if (i == selectedPlayerItemIdx) {
                    g2.setColor(Color.YELLOW);
                    g2.drawString("> " + itemText, itemStartXPlayer, itemStartY + ((i - scrollOffsetPlayer) * lineHeight));
                } else {
                    g2.setColor(Color.WHITE);
                    g2.drawString("  " + itemText, itemStartXPlayer, itemStartY + ((i - scrollOffsetPlayer) * lineHeight));
                }
            }
            if (playerItemList.isEmpty()) {
                g2.setColor(Color.GRAY);
                g2.drawString("Inventory is empty.", itemStartXPlayer, itemStartY + 10);
            }

            // --- Tampilkan Isi Shipping Bin (Untuk Hari Ini) ---
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.drawString("In Bin (Today):", itemStartXBin, itemStartY - 20);
            com.oop10x.steddyvalley.model.ShippingBin currentBin = gameController.getActiveShippingBin();
            g2.setFont(new Font("Arial", Font.PLAIN, 16));
            if (currentBin != null) {
                Map<Item, Integer> binItemsMap = currentBin.getItemsInBin();
                List<Item> binItemList = new ArrayList<>(binItemsMap.keySet());
                int displayCountBin = 0;
                int currentBinY = itemStartY;
                for (Item item : binItemList) {
                    if (displayCountBin >= maxVisibleItems) {
                        g2.setColor(Color.LIGHT_GRAY);
                        g2.drawString("...and " + (binItemList.size() - maxVisibleItems) + " more...", itemStartXBin, currentBinY);
                        break;
                    }
                    int amount = binItemsMap.get(item);
                    String itemText = item.getName() + " x" + amount;
                    Integer itemSellPrice = item.getSellPrice();
                    if (itemSellPrice != null) {
                         itemText += " (Value: " + (itemSellPrice * amount) + "g)";
                    }
                    g2.setColor(Color.CYAN);
                    g2.drawString(itemText, itemStartXBin, currentBinY);
                    currentBinY += lineHeight;
                    displayCountBin++;
                }
                if (binItemList.isEmpty()) {
                    g2.setColor(Color.GRAY);
                    g2.drawString("Bin is empty for today.", itemStartXBin, itemStartY);
                }
            }

            // --- Pesan Transisi/Info dari Controller ---
            String currentTransitionMessage = gameController.getTransitionMessage();
            if (currentTransitionMessage != null && !currentTransitionMessage.isEmpty()) {
                g2.setColor(Color.ORANGE);
                g2.setFont(new Font("Arial", Font.ITALIC, 16));
                int msgY = pY + pH - 50;
                g2.drawString(currentTransitionMessage, pX + 20, msgY);
            }

            // --- Instruksi Umum ---
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, 16));
            g2.drawString("W/S: Select Item | E: Add to Bin | Esc: Finish Shipping", pX + 20, pY + pH - 20);

        } catch (Exception e) {
            System.err.println("RUNTIME ERROR in drawShippingState: " + e.getMessage());
            e.printStackTrace();
            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.drawString("Error rendering Shipping UI. Check console.", 50, SCREEN_HEIGHT / 2);
        }
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
            g2.drawString("Tekan Esc atau E untuk kembali", getXforCenteredText("Tekan Esc atau E untuk kembali", g2, panelX, panelWidth), panelY + panelHeight / 2 + 30);
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
            farmMapModel.draw(g2, TILE_SIZE);
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
        String dayStr = "Day xxx";

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
            dayStr = String.format("Day %d ", (mins / 1440) + 1);
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

        int interTextPadding = 15;

        int totalTextWidthWithPaddings = timeStrWidth + interTextPadding + seasonStrWidth + interTextPadding + weatherStrWidth + interTextPadding + dayStrWidth;
        int hudWidth = totalTextWidthWithPaddings + (hudPadding * 2);

        g2.setColor(new Color(0,0,0,180));
        g2.fillRoundRect(5, 5, hudWidth, singleLineHudHeight, 15,   15);

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

        int debugStartX = 10;
        int debugStartY = 70;
        int debugLineHeight = 20;
        int debugBgPadding = 5;
        int cornerRadius = 10;

        String line1 = "Player X: " + playerScreenX + " Y: " + playerScreenY;
        String line2 = "STATE: PLAYING (ESC:Pause, I:Inventory, E:Action, V: Visit)";
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
        g2.setColor(new Color(0, 0, 0, 190));
        g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        g2.setFont(new Font("Arial", Font.BOLD, 42));
        g2.setColor(Color.ORANGE);
        String pauseTitle = "GAME PAUSED";
        g2.drawString(pauseTitle, getXforCenteredText(pauseTitle, g2), SCREEN_HEIGHT / 4);

        g2.setFont(new Font("Arial", Font.PLAIN, 26));
        List<String> options = gameController.getPauseMenuOptions();
        int selectedIndex = gameController.getSelectedPauseMenuIndex();
        int optionStartY = SCREEN_HEIGHT / 4 + 100;
        int lineHeight = 38;

        if (options != null) {
            for (int i = 0; i < options.size(); i++) {
                if (i == selectedIndex) {
                    g2.setColor(Color.YELLOW);
                    g2.drawString("> " + options.get(i), getXforCenteredText("> " + options.get(i), g2) - 15, optionStartY + (i * lineHeight));
                } else {
                    g2.setColor(Color.WHITE);
                    g2.drawString(options.get(i), getXforCenteredText(options.get(i), g2), optionStartY + (i * lineHeight));
                }
            }
        }
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


        var itemsMap = playerModel.getInventory().getAllItems();
        List<Item> items = new ArrayList<>(itemsMap.keySet());
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

        g2.setColor(new Color(30, 30, 70, 220));
        g2.fillRect(panelX, panelY, panelWidth, panelHeight);
        g2.setColor(Color.WHITE);
        g2.drawRect(panelX, panelY, panelWidth, panelHeight);

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
        drawPlayState(g2);

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

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
        repaint();
    }

    public void updateFishingSliderDisplay(int value, String message, int triesLeft) {
        this.fishingSliderDisplayValue = value;
        this.fishingUIMessage = message;
        repaint();
    }

    public void endFishingSliderUI(boolean success) {
        this.isFishingSliderUIVisible = false;
        repaint();
    }
    public void clearFishingUIState() {
        this.isFishingSliderUIVisible = false;
        this.fishingUIMessage = "";
        this.fishingSliderDisplayValue = 0;
        this.fishingSliderMinDisplay = 0;
        this.fishingSliderMaxDisplay = 0;
        repaint();
    }

    public void signalFishingInteractionComplete() {
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

    private void drawVisitState(Graphics2D g2) { 
        drawPlayState(g2);

        int panelX = SCREEN_WIDTH / 4;
        int panelY = SCREEN_HEIGHT / 4;
        int panelWidth = SCREEN_WIDTH / 2;
        int panelHeight = SCREEN_HEIGHT / 2 + 50;

        g2.setColor(new Color(30, 30, 70, 220));
        g2.fillRect(panelX, panelY, panelWidth, panelHeight);
        g2.setColor(Color.WHITE);
        g2.drawRect(panelX, panelY, panelWidth, panelHeight);

        g2.setFont(new Font("Arial", Font.BOLD, 24));
        String title = "Where To Visit?";
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, panelX + (panelWidth - titleWidth) / 2, panelY + 40);

        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        int actionY = panelY + 80;
        int actionX = panelX + 30;
        int lineHeight = 30;

        List<String> actions = gameController.getViewVisitActions();
        int selectedIndex = gameController.getSelectedVisitActionIndex();

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
        g2.drawString("W/S: Navigate | E: Select | Esc: Exit", panelX + 20, panelY + panelHeight - 20);

    }

    private void drawNPCVisitState(Graphics2D g2) { 
        drawPlayState(g2);

        int panelX = SCREEN_WIDTH / 4;
        int panelY = SCREEN_HEIGHT / 4;
        int panelWidth = SCREEN_WIDTH / 2;
        int panelHeight = SCREEN_HEIGHT / 2;

        g2.setColor(new Color(30, 30, 70, 220));
        g2.fillRect(panelX, panelY, panelWidth, panelHeight);
        g2.setColor(Color.WHITE);
        g2.drawRect(panelX, panelY, panelWidth, panelHeight);

        g2.setFont(new Font("Arial", Font.BOLD, 24));
        String title = "Deepen Your Bond";
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, panelX + (panelWidth - titleWidth) / 2, panelY + 40);

        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        int actionY = panelY + 80;
        int actionX = panelX + 30;
        int lineHeight = 30;

        if (gameController.isShowingNPCFeedback()) {
            String feedback = gameController.getNPCFeedbackMessage();
            int feedbackX = getXforCenteredText(feedback, g2, panelX, panelWidth);
            g2.drawString(feedback, feedbackX, panelY + panelHeight / 2 - 10);

            g2.setFont(new Font("Arial", Font.PLAIN, 16));
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString("Tekan Esc atau E untuk kembali", getXforCenteredText("Tekan Esc atau E untuk kembali", g2, panelX, panelWidth), panelY + panelHeight / 2 + 30);
        }
        else{
            g2.setFont(new Font("Arial", Font.PLAIN, 18));
            actionY = panelY + 80;
            actionX = panelX + 30;
            lineHeight = 30;
            List<String> actions = gameController.getNpcVisitActions();
            if (gameController.getNpcRelation().equals(RelStatus.FIANCE)){
                actions = List.of("Chat", "Gift", "Marry");
            }
            int selectedIndex = gameController.getSelectedNPCVisitActionIndex();

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
        }
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.drawString("W/S: Navigate | E: Select | Esc: Exit", panelX + 20, panelY + panelHeight - 20);

        String locationText = "Location: " + gameController.getVisiting();
        String currentlyWithText = "Currently With: " + gameController.getNpcNow();
        String heartPointsText = "HeartPoints: " + gameController.getNpcHeartPoints();
        String npcStatusText = "Status: " + gameController.getNpcRelStatus();

        Font npcInfoFont = new Font("Arial", Font.PLAIN, 12);
        g2.setFont(npcInfoFont);
        FontMetrics fm = g2.getFontMetrics();
        int infoX = panelX + panelWidth - 10;
        int infoY = panelY + 60;
        int infoLineHeight = fm.getHeight() + 5;
        int locationWidth = fm.stringWidth(locationText);
        g2.drawString(locationText, infoX - locationWidth, infoY);
        int currentlyWidth = fm.stringWidth(currentlyWithText);
        g2.drawString(currentlyWithText, infoX - currentlyWidth, infoY + infoLineHeight);
        int heartPointsWidth = fm.stringWidth(heartPointsText);
        g2.drawString(heartPointsText, infoX - heartPointsWidth, infoY + 2 * infoLineHeight);
        int npcStatusWidth = fm.stringWidth(npcStatusText);
        g2.drawString(npcStatusText, infoX - npcStatusWidth, infoY + 3 * infoLineHeight);
    }

    private void drawGiftingState(Graphics2D g2) {
        drawPlayState(g2);

        int panelX = SCREEN_WIDTH / 4;
        int panelY = SCREEN_HEIGHT / 4;
        int panelWidth = SCREEN_WIDTH / 2;
        int panelHeight = SCREEN_HEIGHT / 2 + 50;

        g2.setColor(new Color(30, 30, 70, 220));
        g2.fillRect(panelX, panelY, panelWidth, panelHeight);
        g2.setColor(Color.WHITE);
        g2.drawRect(panelX, panelY, panelWidth, panelHeight);

        g2.setFont(new Font("Arial", Font.BOLD, 24));
        String title = "What To Give?";
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, panelX + (panelWidth - titleWidth) / 2, panelY + 40);

        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        int actionY = panelY + 80;
        int actionX = panelX + 30;
        int lineHeight = 30;

        List<Item> actions = gameController.getGiftOption();
        int selectedIndex = gameController.getSelectedGiftIndex();

        if (actions.isEmpty()) {
            g2.drawString("No actions available.", actionX, actionY);
        } else {
            for (int i = 0; i < actions.size(); i++) {
                String actionText = actions.get(i).getName();
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
    
    private void drawGiftedState(Graphics2D g2) {
        drawPlayState(g2);
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    
        String message = gameController.getHeartMessage();
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

    private void drawStoreOptState(Graphics2D g2) {
        drawPlayState(g2);

        int panelX = SCREEN_WIDTH / 4;
        int panelY = SCREEN_HEIGHT / 4;
        int panelWidth = SCREEN_WIDTH / 2;
        int panelHeight = SCREEN_HEIGHT / 2;

        g2.setColor(new Color(30, 30, 70, 220));
        g2.fillRect(panelX, panelY, panelWidth, panelHeight);
        g2.setColor(Color.WHITE);
        g2.drawRect(panelX, panelY, panelWidth, panelHeight);

        g2.setFont(new Font("Arial", Font.BOLD, 24));
        String title = "Where To Do Here?";
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, panelX + (panelWidth - titleWidth) / 2, panelY + 40);

        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        int actionY = panelY + 80;
        int actionX = panelX + 30;
        int lineHeight = 30;

        List<String> actions = List.of("Meet Emily", "Shopping");
        int selectedIndex = gameController.getSelectedStoreOptionIndex();

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
        g2.drawString("W/S: Navigate | E: Select | Esc: Exit", panelX + 20, panelY + panelHeight - 20);
    }

    public void adjustEndGameStatsScroll(int amount) {
        if (gameStateModel.isEndGame()) { 
            int newScrollY = endGameStatsScrollY + amount;

            if (this.statAreaVisibleHeight == 0) { 
                 this.statAreaVisibleHeight = SCREEN_HEIGHT - (SCREEN_HEIGHT / 10 + 90) - 80; 
            }
            int maxScroll = Math.max(0, endGameStatsTotalContentHeight - this.statAreaVisibleHeight);
            this.endGameStatsScrollY = Math.max(0, Math.min(newScrollY, maxScroll));
            repaint();
        }
    }
    public void resetEndGameStatsScroll() {
        this.endGameStatsScrollY = 0;
        this.endGameStatsTotalContentHeight = 0;
        System.out.println("[View Panel] EndGameScrollY Reset.");
    }

    private void drawPlayerGenderInputState(Graphics2D g2) {
        try {
            g2.setColor(new Color(20, 20, 50));
            g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

            g2.setFont(new Font("Arial", Font.BOLD, 28));
            g2.setColor(Color.WHITE);
            String prompt = "Select Your Gender:";
            g2.drawString(prompt, getXforCenteredText(prompt, g2), SCREEN_HEIGHT / 3 - 40);

            List<String> options = gameController.getGenderOptions();
            int selectedIndex = gameController.getSelectedGenderIndex();
            int optionStartY = SCREEN_HEIGHT / 3 + 20;
            int lineHeight = 40;

            g2.setFont(new Font("Arial", Font.PLAIN, 24));
            for (int i = 0; i < options.size(); i++) {
                String optionText = options.get(i);
                int currentOptionY = optionStartY + (i * lineHeight);
                if (i == selectedIndex) {
                    g2.setColor(Color.ORANGE);
                    g2.fillRoundRect(getXforCenteredText(optionText, g2) - 20, currentOptionY - lineHeight/2 - 5, g2.getFontMetrics().stringWidth(optionText) + 40, lineHeight + 5, 10,10);
                    g2.setColor(Color.BLACK);
                    g2.drawString(optionText, getXforCenteredText(optionText, g2), currentOptionY);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.drawString(optionText, getXforCenteredText(optionText, g2), currentOptionY);
                }
            }
            g2.setFont(new Font("Arial", Font.PLAIN, 16));
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString("W/S to select, E/Enter to confirm.", getXforCenteredText("W/S to select, E/Enter to confirm.", g2), optionStartY + (options.size() * lineHeight) + 30);
            
            String feedbackMsg = gameController.getTransitionMessage();
             if (feedbackMsg != null && !feedbackMsg.isEmpty() && feedbackMsg.contains("Select Your Gender")) {
                 g2.setColor(Color.CYAN);
                 g2.drawString(feedbackMsg, getXforCenteredText(feedbackMsg, g2), SCREEN_HEIGHT - 60);
             }
        } catch (Exception e) {
            System.err.println("Error in drawPlayerGenderInputState: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void drawPlayerFavItemInputState(Graphics2D g2) {
        try {
            g2.setColor(new Color(20, 20, 50));
            g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

            g2.setFont(new Font("Arial", Font.BOLD, 28));
            g2.setColor(Color.WHITE);
            String prompt = "Enter Your Favorite Item:";
            g2.drawString(prompt, getXforCenteredText(prompt, g2), SCREEN_HEIGHT / 3 - 40);

            String currentFavItem = gameController.getFavItemInputBuffer();
            g2.setFont(new Font("Monospaced", Font.PLAIN, 32));
            g2.setColor(Color.YELLOW);
            int fieldWidth = 380;
            int fieldX = (SCREEN_WIDTH - fieldWidth) / 2;
            int fieldY = SCREEN_HEIGHT / 3 + 20;
            g2.drawRect(fieldX - 5, fieldY - 30, fieldWidth, 40);
            g2.drawString(currentFavItem + ( (System.currentTimeMillis()/500) % 2 == 0 ? "_" : " "), fieldX + 10, fieldY);

            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString("Max 20 chars. Press E/Enter to confirm, Backspace to delete.", getXforCenteredText("Max 20 chars. Press E/Enter to confirm, Backspace to delete.", g2), fieldY + 50);

            g2.setFont(new Font("Arial", Font.ITALIC, 12));
            g2.setColor(Color.GRAY);
            int suggestionY = fieldY + 80;
            g2.drawString("Examples: Diamond, Spakbor Kanan, Parsnip, Carp, Fish n' Chips...", getXforCenteredText("Examples: Diamond, Spakbor Kanan, Parsnip, Carp, Fish n' Chips...", g2), suggestionY);

            String feedbackMsg = gameController.getTransitionMessage();
             if (feedbackMsg != null && !feedbackMsg.isEmpty() && (feedbackMsg.contains("Enter Your Favorite Item") || feedbackMsg.contains("cannot be empty"))) {
                 g2.setColor(feedbackMsg.contains("cannot be empty") ? Color.RED : Color.CYAN);
                 g2.drawString(feedbackMsg, getXforCenteredText(feedbackMsg, g2), SCREEN_HEIGHT - 60);
             }
        } catch (Exception e) {
            System.err.println("Error in drawPlayerFavItemInputState: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void drawEndGameState(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(20, 20, 50)); 
        g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        g2.setFont(new Font("Serif", Font.BOLD, 38));
        g2.setColor(new Color(255, 215, 0)); 
        String title = "Steddy Valley Chronicle";
        int titleX = getXforCenteredText(title, g2);
        int topMargin = SCREEN_HEIGHT / 12;
        if (topMargin < 50) topMargin = 50;
        g2.drawString(title, titleX, topMargin);

        g2.setFont(new Font("Serif", Font.ITALIC, 20));
        g2.setColor(Color.LIGHT_GRAY);
        String subTitle = "Your Journey So Far...";
        int subTitleX = getXforCenteredText(subTitle, g2);
        g2.drawString(subTitle, subTitleX, topMargin + 35);

        int currentY_logical = topMargin + 90; 
        final int initialLogicalY = currentY_logical; 
        int lineHeight = 26;
        int sectionSpacing = lineHeight + (lineHeight / 2); 
        int indentX1 = SCREEN_WIDTH / 10;
        int valueOffsetX = 300; 
        Font statSectionFont = new Font("Serif", Font.BOLD | Font.ITALIC, 20);
        Font statLabelFont = new Font("Arial", Font.BOLD, 17);
        Font statValueFont = new Font("Arial", Font.PLAIN, 17);
        Color sectionColor = new Color(173, 216, 230); 
        Color labelColor = new Color(220, 220, 255);   
        Color valueColor = Color.WHITE;

        // --- Area Scrollable ---
        int statAreaX = indentX1 - 20;
        int statAreaY = initialLogicalY - (lineHeight / 2); 
        int statAreaWidth = SCREEN_WIDTH - (statAreaX * 2);
        this.statAreaVisibleHeight = SCREEN_HEIGHT - statAreaY - 80; 

        
        java.awt.Shape originalClip = g2.getClip();
        g2.setClip(statAreaX, statAreaY, SCREEN_WIDTH - (statAreaX * 2), this.statAreaVisibleHeight);

        g2.setFont(statSectionFont); g2.setColor(sectionColor);
        g2.drawString("Financial & Time Summary", indentX1, currentY_logical - endGameStatsScrollY);
        currentY_logical += sectionSpacing;

        g2.setFont(statLabelFont); g2.setColor(labelColor);
        g2.drawString("Total Income:", indentX1, currentY_logical - endGameStatsScrollY);
        g2.setFont(statValueFont); g2.setColor(valueColor);
        g2.drawString(Player.getTotalIncome() + "g", indentX1 + valueOffsetX, currentY_logical - endGameStatsScrollY);
        currentY_logical += lineHeight;

        g2.setFont(statLabelFont); g2.setColor(labelColor);
        g2.drawString("Total Expenditure:", indentX1, currentY_logical - endGameStatsScrollY);
        g2.setFont(statValueFont); g2.setColor(valueColor);
        g2.drawString(Player.getTotalExpenditure() + "g", indentX1 + valueOffsetX, currentY_logical - endGameStatsScrollY);
        currentY_logical += lineHeight;

        g2.setFont(statLabelFont); g2.setColor(labelColor);
        g2.drawString("Avg. Season Income:", indentX1, currentY_logical - endGameStatsScrollY);
        g2.setFont(statValueFont); g2.setColor(valueColor);
        g2.drawString(Player.getIncomePerSeason() + "g", indentX1 + valueOffsetX, currentY_logical - endGameStatsScrollY);
        currentY_logical += lineHeight;

        g2.setFont(statLabelFont); g2.setColor(labelColor);
        g2.drawString("Avg. Season Expenditure:", indentX1, currentY_logical - endGameStatsScrollY);
        g2.setFont(statValueFont); g2.setColor(valueColor);
        g2.drawString(Player.getExpenditurePerSeason() + "g", indentX1 + valueOffsetX, currentY_logical - endGameStatsScrollY);
        currentY_logical += lineHeight;

        g2.setFont(statLabelFont); g2.setColor(labelColor);
        g2.drawString("Total Days Played:", indentX1, currentY_logical - endGameStatsScrollY);
        g2.setFont(statValueFont); g2.setColor(valueColor);
        g2.drawString(String.valueOf(Player.getTotalDaysPlayed()), indentX1 + valueOffsetX, currentY_logical - endGameStatsScrollY);
        currentY_logical += sectionSpacing;


        g2.setFont(statSectionFont); g2.setColor(sectionColor);
        g2.drawString("Production Statistics", indentX1, currentY_logical - endGameStatsScrollY);
        currentY_logical += sectionSpacing;

        g2.setFont(statLabelFont); g2.setColor(labelColor);
        g2.drawString("Crops Harvested:", indentX1, currentY_logical - endGameStatsScrollY);
        g2.setFont(statValueFont); g2.setColor(valueColor);
        g2.drawString(String.valueOf(Player.getTotalCropsHarvested()), indentX1 + valueOffsetX, currentY_logical - endGameStatsScrollY);
        currentY_logical += lineHeight;
        
        g2.setFont(statLabelFont); g2.setColor(labelColor);
        g2.drawString("Total Fish Caught:", indentX1, currentY_logical - endGameStatsScrollY);
        g2.setFont(statValueFont); g2.setColor(valueColor);
        g2.drawString(String.valueOf(Player.getTotalFishCaught()), indentX1 + valueOffsetX, currentY_logical - endGameStatsScrollY);
        currentY_logical += lineHeight;
        
        g2.setFont(statValueFont); 
        Map<FishRarity, Integer> fishByRarity = Player.getFishCaughtByRarity();
        if (fishByRarity != null && !fishByRarity.isEmpty()) {
            for (FishRarity rarity : FishRarity.values()) { 
                Integer count = fishByRarity.getOrDefault(rarity, 0);
                g2.setColor(labelColor); 
                g2.drawString("  - " + rarity.toString() + " Fish:", indentX1 + 20, currentY_logical - endGameStatsScrollY);
                g2.setColor(valueColor); 
                g2.drawString(String.valueOf(count), indentX1 + valueOffsetX, currentY_logical - endGameStatsScrollY);
                currentY_logical += lineHeight;
            }
        } else {
            g2.drawString("  - No specific fish rarity data.", indentX1 + 20, currentY_logical - endGameStatsScrollY);
            currentY_logical += lineHeight;
        }
        currentY_logical += sectionSpacing;


        g2.setFont(statSectionFont); g2.setColor(sectionColor);
        g2.drawString("NPC Interactions & Relationships", indentX1, currentY_logical - endGameStatsScrollY);
        currentY_logical += lineHeight; 
        List<String> npcRelationshipDetails = Player.getNPCsRelationshipDetails();
        List<String> npcChatDetails = Player.getNPCChattingFrequencyDetails();
        List<String> npcGiftDetails = Player.getNPCGiftingFrequencyDetails();
        List<String> npcVisitDetails = Player.getNPCVisitingFrequencyDetails();

        Set<NPC> npcSet = NPC.getNpcSet(); 
        if (npcSet != null && !npcSet.isEmpty()) {
            List<NPC> sortedNpcList = new ArrayList<>(npcSet);
            Collections.sort(sortedNpcList, Comparator.comparing(NPC::getName));

            for (NPC npc : sortedNpcList) {
                g2.setFont(statLabelFont); g2.setColor(labelColor);
                g2.drawString(npc.getName() + ":", indentX1, currentY_logical - endGameStatsScrollY);
                currentY_logical += lineHeight;

                g2.setFont(statValueFont); g2.setColor(valueColor);
                
                String relInfo = String.format("  Status: %s (%d Hearts)", npc.getRelationshipStatus().toString(), npc.getHeartPoints());
                g2.drawString(relInfo, indentX1 + 15, currentY_logical - endGameStatsScrollY);
                currentY_logical += lineHeight;

                String chatInfo = String.format("  Chatted: %d times", npc.getChatCountWithPlayer()); 
                g2.drawString(chatInfo, indentX1 + 15, currentY_logical - endGameStatsScrollY);
                currentY_logical += lineHeight;

                String giftInfo = String.format("  Gifts Given To: %d times", npc.getGiftsReceivedCount());
                g2.drawString(giftInfo, indentX1 + 15, currentY_logical - endGameStatsScrollY);
                currentY_logical += lineHeight;

                String visitInfo = String.format("  Visited: %d times", npc.getTimesVisitedByPlayer()); 
                g2.drawString(visitInfo, indentX1 + 15, currentY_logical - endGameStatsScrollY);
                currentY_logical += sectionSpacing; 
            }
        } else {
             g2.setFont(statValueFont); g2.setColor(valueColor);
             g2.drawString("  No NPC data available.", indentX1 + 10, currentY_logical - endGameStatsScrollY);
             currentY_logical += lineHeight;
        }
        this.endGameStatsTotalContentHeight = currentY_logical - initialLogicalY;

        g2.setClip(originalClip);
        if (endGameStatsTotalContentHeight > statAreaVisibleHeight) {
            int scrollbarAreaX = statAreaX + statAreaWidth + 5; 
            int scrollbarTrackHeight = statAreaVisibleHeight;
            g2.setColor(new Color(100, 100, 100, 150)); 
            g2.fillRect(scrollbarAreaX, statAreaY, 8, scrollbarTrackHeight); 

            float thumbPercentageHeight = Math.max(0.1f, (float) statAreaVisibleHeight / endGameStatsTotalContentHeight); 
            int thumbHeight = (int) (scrollbarTrackHeight * thumbPercentageHeight);
            
            int maxScrollActual = endGameStatsTotalContentHeight - statAreaVisibleHeight;
            float scrollRatio = (maxScrollActual > 0) ? (float) endGameStatsScrollY / maxScrollActual : 0;
            
            int thumbY = statAreaY + (int) (scrollRatio * (scrollbarTrackHeight - thumbHeight));

            g2.setColor(new Color(200, 200, 200, 200)); 
            g2.fillRect(scrollbarAreaX, thumbY, 8, thumbHeight);
        }

        g2.setFont(new Font("Arial", Font.BOLD, 22));
        g2.setColor(Color.ORANGE);
        String continueMessage = "Press Esc to Continue Your Journey";
        int continueMsgWidth = g2.getFontMetrics().stringWidth(continueMessage);
        int continueX = (SCREEN_WIDTH - continueMsgWidth) / 2;
        
        g2.setColor(new Color(0,0,0,190)); 
        g2.fillRoundRect(continueX - 20, SCREEN_HEIGHT - 75 - g2.getFontMetrics().getAscent() + 5, continueMsgWidth + 40, g2.getFontMetrics().getHeight() + 20, 15,15);
        
        g2.setColor(Color.ORANGE);
        g2.drawString(continueMessage, continueX, SCREEN_HEIGHT - 70);
    }
    
    @Override public void onPlayerUpdated(Player player) { /* ... */ }
    @Override public void onGameStateChanged(int newState, int oldState) { /* ... */ }
}