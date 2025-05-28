package com.oop10x.steddyvalley;

import com.oop10x.steddyvalley.model.*;
import com.oop10x.steddyvalley.model.collision.CollisionChecker;
<<<<<<< HEAD
=======
import com.oop10x.steddyvalley.model.items.Equipment;
// import com.oop10x.steddyvalley.model.items.HoeTool; // Jika ada
>>>>>>> 9a0ed4b (HUD)
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
<<<<<<< HEAD
            GameState gameStateModel = new GameState();
            // Tambahkan SeasonManager dan WeatherManager
            SeasonManager seasonManager = SeasonManager.getInstance(timeManager);
            WeatherManager weatherManager = WeatherManager.getInstance(timeManager);
=======
            playerModel.setEquippedItem(initialItem);
            GameState gameStateModel = new GameState();
            GamePanel gamePanelModel = new GamePanel(playerModel, gameStateModel, null, null);
            com.oop10x.steddyvalley.model.SeasonManager seasonManager = new com.oop10x.steddyvalley.model.SeasonManager(timeManager);
            com.oop10x.steddyvalley.model.WeatherManager weatherManager = new com.oop10x.steddyvalley.model.WeatherManager(timeManager);

            // Buat beberapa contoh Seed dan Item lain untuk inventory awal
            // Asumsi Crop.getCropByName() ada dan mengembalikan Crop (turunan Item)
            // Item parsnipCrop = new Item("Parsnip");
            // Seed parsnipSeed = new Seed("Parsnip Seed", 20, Set.of(Season.SPRING), 4, parsnipCrop);
            // playerModel.addItemToInventory(parsnipSeed, 5);
            // playerModel.addItemToInventory(new HoeTool("Basic Hoe"));
            // playerModel.setEquippedItem(playerModel.getInventory().getItemByName("Basic Hoe"));

            com.oop10x.steddyvalley.model.items.Fish fish1 = new com.oop10x.steddyvalley.model.items.Fish("FishA", com.oop10x.steddyvalley.utils.FishRarity.COMMON, new java.util.HashSet<>(), new java.util.HashSet<>(), new java.util.HashSet<>(), new java.util.HashSet<>());
            com.oop10x.steddyvalley.model.items.Fish fish2 = new com.oop10x.steddyvalley.model.items.Fish("FishB", com.oop10x.steddyvalley.utils.FishRarity.COMMON, new java.util.HashSet<>(), new java.util.HashSet<>(), new java.util.HashSet<>(), new java.util.HashSet<>());
            playerModel.addItem(fish1);
            playerModel.addItem(fish2);

            playerModel.addItem(initialItem2) ;
            playerModel.addItem(initialItem3);
            com.oop10x.steddyvalley.model.items.Crop hotPepper = new com.oop10x.steddyvalley.model.items.Crop("Hot Pepper", 10, 20, 1);
            playerModel.addItem(hotPepper);

            com.oop10x.steddyvalley.model.items.Crop cauliflower1 = new com.oop10x.steddyvalley.model.items.Crop("Cauliflower", 10, 20, 1);
            com.oop10x.steddyvalley.model.items.Crop cauliflower2 = new com.oop10x.steddyvalley.model.items.Crop("Cauliflower", 10, 20, 1);
            playerModel.addItem(cauliflower1);
            playerModel.addItem(cauliflower2);

            com.oop10x.steddyvalley.model.items.Misc firewood = com.oop10x.steddyvalley.model.items.Misc.getMisc("Firewood");
            com.oop10x.steddyvalley.model.items.Misc coal = com.oop10x.steddyvalley.model.items.Misc.getMisc("Coal");
            playerModel.addItem(firewood);
            playerModel.addItem(coal);
>>>>>>> 9a0ed4b (HUD)

            FarmMap farmMapModel = new FarmMap(timeManager);
            SeasonManager seasonModel = seasonManager;
            WeatherManager weatherModel = weatherManager;

            TileManager tileManagerForCollision = new TileManager(TILE_SIZE, FarmMap.MAP_WIDTH_IN_TILES, FarmMap.MAP_HEIGHT_IN_TILES);
            CollisionChecker collisionChecker = new CollisionChecker(tileManagerForCollision, farmMapModel, TILE_SIZE);

            // --- Inisialisasi Controller ---
            GameController gameController = new GameController(playerModel, gameStateModel, farmMapModel,
                                                               collisionChecker, timeManager, TILE_SIZE, seasonModel, weatherModel, gamePanelModel);
            KeyHandler keyHandler = new KeyHandler(gameController);

            // --- Inisialisasi View ---
            GamePanel gamePanel = new GamePanel(playerModel, gameStateModel, gameController, farmMapModel);
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
