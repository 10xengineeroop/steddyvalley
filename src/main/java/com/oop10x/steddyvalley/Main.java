package com.oop10x.steddyvalley;

import com.oop10x.steddyvalley.model.*;
import com.oop10x.steddyvalley.model.collision.CollisionChecker;
import com.oop10x.steddyvalley.model.tile.TileManager;
import com.oop10x.steddyvalley.controller.*;
import com.oop10x.steddyvalley.view.*;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            final int TILE_SIZE = GamePanel.TILE_SIZE;

            // --- Inisialisasi Model ---

            TimeManager timeManager = TimeManager.getInstance();
            Player playerModel = new Player(TILE_SIZE * 5, TILE_SIZE * 5, 500, 100, 4);
            GameState gameStateModel = new GameState();
            // Tambahkan SeasonManager dan WeatherManager
            SeasonManager seasonManager = SeasonManager.getInstance(timeManager);
            WeatherManager weatherManager = WeatherManager.getInstance(timeManager);

            FarmMap farmMapModel = new FarmMap(timeManager);
            SeasonManager seasonModel = seasonManager;
            WeatherManager weatherModel = weatherManager;

            // TileManager masih diperlukan oleh CollisionChecker untuk tile dasar non-FarmMap
            TileManager tileManagerForCollision = new TileManager(TILE_SIZE, FarmMap.MAP_WIDTH_IN_TILES, FarmMap.MAP_HEIGHT_IN_TILES);
            CollisionChecker collisionChecker = new CollisionChecker(tileManagerForCollision, farmMapModel, TILE_SIZE);

            // --- Inisialisasi Controller ---
            GameController gameController = new GameController(playerModel, gameStateModel, farmMapModel,
                                                               collisionChecker, timeManager, TILE_SIZE, seasonModel, weatherModel, null);
            KeyHandler keyHandler = new KeyHandler(gameController);

            // --- Inisialisasi View ---
            GamePanel gamePanel = new GamePanel(playerModel, gameStateModel, gameController, farmMapModel);
            gameController.setGamePanel(gamePanel);
            // Inject managers for HUD
            gamePanel.setManagers(timeManager, seasonManager, weatherManager);
            GameWindow gameWindow = new GameWindow("Steddy Valley", gamePanel, keyHandler);

            gameWindow.addCleanShutdownHook();

            // --- Mulai Game ---
            timeManager.start();
            gameWindow.displayAndFocus();
            gamePanel.startGameThread();
        });
    }
}