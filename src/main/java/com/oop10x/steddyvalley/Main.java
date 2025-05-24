package com.oop10x.steddyvalley;

import com.oop10x.steddyvalley.model.FarmMap;
import com.oop10x.steddyvalley.model.GameState;
import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.model.TimeManager;
import com.oop10x.steddyvalley.model.collision.CollisionChecker;
import com.oop10x.steddyvalley.model.items.Equipment;
import com.oop10x.steddyvalley.model.items.Item; // Untuk item awal
import com.oop10x.steddyvalley.model.items.Seed; // Untuk item awal
// import com.oop10x.steddyvalley.model.items.HoeTool; // Jika ada
import com.oop10x.steddyvalley.model.tile.TileManager;
import com.oop10x.steddyvalley.controller.GameController;
import com.oop10x.steddyvalley.controller.KeyHandler;
import com.oop10x.steddyvalley.view.GamePanel;
import com.oop10x.steddyvalley.view.GameWindow;
import com.oop10x.steddyvalley.utils.Season; // Untuk Seed

import javax.swing.SwingUtilities;
import java.util.HashSet;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            final int TILE_SIZE = GamePanel.TILE_SIZE;

            // --- Inisialisasi Model ---
            Equipment initialItem = new Equipment("Hoe") ;
            TimeManager timeManager = new TimeManager();
            Player playerModel = new Player(TILE_SIZE * 5, TILE_SIZE * 5, 500, 100, 4);
            playerModel.setEquippedItem(initialItem); // Mengatur item awal sebagai item yang dilengkapi
            GameState gameStateModel = new GameState();
            // gameStateModel.setCurrentState(GameState.MENU_STATE); // Mulai dari menu jika ada

            // Buat beberapa contoh Seed dan Item lain untuk inventory awal
            // Asumsi Crop.getCropByName() ada dan mengembalikan Crop (turunan Item)
            // Item parsnipCrop = new Item("Parsnip"); // Atau Crop.getCropByName("Parsnip")
            // Seed parsnipSeed = new Seed("Parsnip Seed", 20, Set.of(Season.SPRING), 4, parsnipCrop);
            // playerModel.addItemToInventory(parsnipSeed, 5);
            // playerModel.addItemToInventory(new HoeTool("Basic Hoe")); // Jika Hoe adalah Item
            // playerModel.setEquippedItem(playerModel.getInventory().getItemByName("Basic Hoe")); // Contoh

            FarmMap farmMapModel = new FarmMap(timeManager);

            // TileManager masih diperlukan oleh CollisionChecker untuk tile dasar non-FarmMap
            TileManager tileManagerForCollision = new TileManager(TILE_SIZE, FarmMap.MAP_WIDTH_IN_TILES, FarmMap.MAP_HEIGHT_IN_TILES);
            CollisionChecker collisionChecker = new CollisionChecker(tileManagerForCollision, farmMapModel, TILE_SIZE);

            // --- Inisialisasi Controller ---
            GameController gameController = new GameController(playerModel, gameStateModel, farmMapModel,
                                                               collisionChecker, timeManager, TILE_SIZE);
            KeyHandler keyHandler = new KeyHandler(gameController);

            // --- Inisialisasi View ---
            GamePanel gamePanel = new GamePanel(playerModel, gameStateModel, gameController, farmMapModel);
            GameWindow gameWindow = new GameWindow("Steddy Valley", gamePanel, keyHandler);

            gameWindow.addCleanShutdownHook();

            // --- Mulai Game ---
            timeManager.start();
            gameWindow.displayAndFocus();
            gamePanel.startGameThread();
        });
    }
}
