package com.oop10x.steddyvalley;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.oop10x.steddyvalley.controller.GameController;
import com.oop10x.steddyvalley.controller.KeyHandler;
import com.oop10x.steddyvalley.model.FarmMap;
import com.oop10x.steddyvalley.model.GameState;
import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.model.SeasonManager;
import com.oop10x.steddyvalley.model.TimeManager;
import com.oop10x.steddyvalley.model.WeatherManager;
import com.oop10x.steddyvalley.model.collision.CollisionChecker;
import com.oop10x.steddyvalley.model.tile.TileManager;
import com.oop10x.steddyvalley.view.GamePanel;
import com.oop10x.steddyvalley.view.GameWindow;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            final int TILE_SIZE = GamePanel.TILE_SIZE;

            // --- Inisialisasi Model ---
            TimeManager timeManager = TimeManager.getInstance();
            Player playerModel = new Player(TILE_SIZE * 5, TILE_SIZE * 5, 500, 100, 4);
            GameState gameStateModel = new GameState();
            SeasonManager seasonManager = SeasonManager.getInstance(timeManager);
            WeatherManager weatherManager = WeatherManager.getInstance(timeManager);

            FarmMap farmMapModel = new FarmMap(timeManager, playerModel);
            SeasonManager seasonModel = seasonManager;
            WeatherManager weatherModel = weatherManager;

            TileManager tileManagerForCollision = new TileManager(TILE_SIZE, FarmMap.MAP_WIDTH_IN_TILES, FarmMap.MAP_HEIGHT_IN_TILES);
            CollisionChecker collisionChecker = new CollisionChecker(tileManagerForCollision, farmMapModel, TILE_SIZE);

            // --- Inisialisasi Controller ---
            GameController gameController = new GameController(playerModel, gameStateModel, farmMapModel, collisionChecker, timeManager, TILE_SIZE, seasonModel, weatherModel, null);
            KeyHandler keyHandler = new KeyHandler(gameController);

            // --- Inisialisasi View ---
            GamePanel gamePanel = new GamePanel(playerModel, gameStateModel, gameController, farmMapModel);
            gameController.setGamePanel(gamePanel);
            // Inject managers for HUD
            gamePanel.setManagers(timeManager, seasonManager, weatherManager);
            GameWindow gameWindow = new GameWindow("Steddy Valley", gamePanel, keyHandler);

            gameWindow.addCleanShutdownHook();

            // --- Mulai Game ---
            gameWindow.displayAndFocus();
            gamePanel.startGameThread();
        });
    }
}