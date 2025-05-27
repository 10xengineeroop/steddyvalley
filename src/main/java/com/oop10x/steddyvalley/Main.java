package com.oop10x.steddyvalley;

import com.oop10x.steddyvalley.model.FarmMap;
import com.oop10x.steddyvalley.model.GameState;
import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.model.TimeManager;
import com.oop10x.steddyvalley.model.WeatherManager;
import com.oop10x.steddyvalley.model.collision.CollisionChecker;
import com.oop10x.steddyvalley.model.items.Equipment;
// import com.oop10x.steddyvalley.model.items.HoeTool; // Jika ada
import com.oop10x.steddyvalley.model.tile.TileManager;
import com.oop10x.steddyvalley.controller.GameController;
import com.oop10x.steddyvalley.controller.KeyHandler;
import com.oop10x.steddyvalley.view.GamePanel;
import com.oop10x.steddyvalley.view.GameWindow;
import com.oop10x.steddyvalley.model.SeasonManager;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            final int TILE_SIZE = GamePanel.TILE_SIZE;

            // --- Inisialisasi Model ---
            Equipment initialItem = new Equipment("Hoe") ;
            Equipment initialItem2 = new Equipment("Fishing Rod");
            Equipment initialItem3 = new Equipment ("Pickaxe") ;
            
            TimeManager timeManager = new TimeManager();
            Player playerModel = new Player(TILE_SIZE * 5, TILE_SIZE * 5, 500, 100, 4);
            playerModel.setEquippedItem(initialItem); // Mengatur item awal sebagai item yang dilengkapi
            GameState gameStateModel = new GameState();
            // Tambahkan SeasonManager dan WeatherManager
            com.oop10x.steddyvalley.model.SeasonManager seasonManager = new com.oop10x.steddyvalley.model.SeasonManager(timeManager);
            com.oop10x.steddyvalley.model.WeatherManager weatherManager = new com.oop10x.steddyvalley.model.WeatherManager(timeManager);

            // Buat beberapa contoh Seed dan Item lain untuk inventory awal
            // Asumsi Crop.getCropByName() ada dan mengembalikan Crop (turunan Item)
            // Item parsnipCrop = new Item("Parsnip"); // Atau Crop.getCropByName("Parsnip")
            // Seed parsnipSeed = new Seed("Parsnip Seed", 20, Set.of(Season.SPRING), 4, parsnipCrop);
            // playerModel.addItemToInventory(parsnipSeed, 5);
            // playerModel.addItemToInventory(new HoeTool("Basic Hoe")); // Jika Hoe adalah Item
            // playerModel.setEquippedItem(playerModel.getInventory().getItemByName("Basic Hoe")); // Contoh

            // Tambahkan bahan Fish Stew ke inventory player
            // Fish
            com.oop10x.steddyvalley.model.items.Fish fish1 = new com.oop10x.steddyvalley.model.items.Fish("FishA", com.oop10x.steddyvalley.utils.FishRarity.COMMON, new java.util.HashSet<>(), new java.util.HashSet<>(), new java.util.HashSet<>(), new java.util.HashSet<>());
            com.oop10x.steddyvalley.model.items.Fish fish2 = new com.oop10x.steddyvalley.model.items.Fish("FishB", com.oop10x.steddyvalley.utils.FishRarity.COMMON, new java.util.HashSet<>(), new java.util.HashSet<>(), new java.util.HashSet<>(), new java.util.HashSet<>());
            playerModel.addItem(fish1);
            playerModel.addItem(fish2);
            // Hot Pepper
            playerModel.addItem(initialItem2) ;
            playerModel.addItem(initialItem3);
            com.oop10x.steddyvalley.model.items.Crop hotPepper = new com.oop10x.steddyvalley.model.items.Crop("Hot Pepper", 10, 20, 1);
            playerModel.addItem(hotPepper);
            // Cauliflower
            com.oop10x.steddyvalley.model.items.Crop cauliflower1 = new com.oop10x.steddyvalley.model.items.Crop("Cauliflower", 10, 20, 1);
            com.oop10x.steddyvalley.model.items.Crop cauliflower2 = new com.oop10x.steddyvalley.model.items.Crop("Cauliflower", 10, 20, 1);
            playerModel.addItem(cauliflower1);
            playerModel.addItem(cauliflower2);
            // Fuel (Misc: Firewood & Coal)
            com.oop10x.steddyvalley.model.items.Misc firewood = com.oop10x.steddyvalley.model.items.Misc.getMisc("Firewood");
            com.oop10x.steddyvalley.model.items.Misc coal = com.oop10x.steddyvalley.model.items.Misc.getMisc("Coal");
            playerModel.addItem(firewood);
            playerModel.addItem(coal);

            FarmMap farmMapModel = new FarmMap(timeManager);
            SeasonManager seasonModel = new SeasonManager(timeManager);
            WeatherManager weatherModel = new WeatherManager(timeManager);

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
