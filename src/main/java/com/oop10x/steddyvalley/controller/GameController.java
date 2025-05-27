package com.oop10x.steddyvalley.controller;

import com.oop10x.steddyvalley.model.FarmMap;
import com.oop10x.steddyvalley.model.GameState;
import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.model.TimeManager;
import com.oop10x.steddyvalley.model.collision.CollisionChecker;
import com.oop10x.steddyvalley.model.items.Fish;
import com.oop10x.steddyvalley.model.items.Item;
import com.oop10x.steddyvalley.model.items.Seed;
import com.oop10x.steddyvalley.model.items.Food;
// Impor Tool jika ada (Hoe, WateringCan)
// import com.oop10x.steddyvalley.model.items.HoeTool;
// import com.oop10x.steddyvalley.model.items.WateringCanTool;
import com.oop10x.steddyvalley.model.map.Actionable;
import com.oop10x.steddyvalley.model.map.Land;
import com.oop10x.steddyvalley.model.objects.*;
import com.oop10x.steddyvalley.model.recipes.Recipe;
import com.oop10x.steddyvalley.model.recipes.RecipeRequirement;
import com.oop10x.steddyvalley.model.recipes.RecipeList;
import com.oop10x.steddyvalley.utils.Observer;
import com.oop10x.steddyvalley.utils.Season;
import com.oop10x.steddyvalley.utils.Weather;
import com.oop10x.steddyvalley.utils.EventType;
import com.oop10x.steddyvalley.utils.FishRarity;
import com.oop10x.steddyvalley.model.SeasonManager;
import com.oop10x.steddyvalley.model.WeatherManager;
import com.oop10x.steddyvalley.view.GamePanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.List;

public class GameController implements PlayerInputActions, Observer {

    private Player playerModel;
    private GameState gameStateModel;
    private FarmMap farmMapModel;
    private CollisionChecker collisionChecker;
    private TimeManager timeManager; // Untuk mendapatkan waktu saat ini
    private int tileSize;
    private String transitionMessage;
    private GamePanel gamePanel;

    //house punya
    private List<String> houseActions = List.of("Sleep", "Cook", "Watch TV") ;
    private int selectedHouseActionIndex = 0; 
    private List<String> recipes = List.of("Fish n' Chips", "Baguette", "Sashimi", "Fugu", "wine", "Pumpkin Pie", "Veggie Soup",
            "Fish Stew", "Spakbor Salad", "Fish Sandwich", "The Legends of Spakbor") ;
    private int selectedRecipeIndex = 0;

    private Fish currentFishingTargetFish;
    private int currentFishingTargetNumber; // Angka rahasia yang harus ditebak
    private int currentFishingTriesLeft;
    private int fishingSliderCurrentValue;
    private int fishingSliderMin;
    private int fishingSliderMax;
    private Random randomGenerator = new Random();


    private boolean moveUpActive, moveDownActive, moveLeftActive, moveRightActive;

    // Recipe data moved to com.oop10x.steddyvalley.model.recipes
    private List<Recipe> recipeList = RecipeList.RECIPES;

    // Managers for season and weather
    private SeasonManager seasonManager;
    private WeatherManager weatherManager;

    // Register as observer in constructor
    public GameController(Player player, GameState gameState, FarmMap farmMap,
                          CollisionChecker cc, TimeManager tm, int tileSize,
                          SeasonManager seasonManager, WeatherManager weatherManager, GamePanel gamePanel) {
        this.playerModel = player;
        this.gameStateModel = gameState;
        this.farmMapModel = farmMap;
        this.collisionChecker = cc;
        this.timeManager = tm;
        this.tileSize = tileSize;
        this.seasonManager = seasonManager;
        this.weatherManager = weatherManager;
        this.gamePanel = gamePanel;
        this.timeManager.addObserver(this); // Register for time events
    }

    @Override 
    public void setMoveUp(boolean active) { 
        if (active) {
            if (gameStateModel.getCurrentState() == GameState.HOUSE_STATE) {
                if (!houseActions.isEmpty()) {
                    selectedHouseActionIndex-- ;
                    if (selectedHouseActionIndex < 0) {
                        selectedHouseActionIndex = houseActions.size() - 1; // Loop ke bawah
                    }
                }
            }
            if (gameStateModel.getCurrentState() == GameState.RECIPE_STATE) {
                selectedRecipeIndex-- ;
                if (selectedRecipeIndex < 0) {
                    selectedRecipeIndex = recipes.size() - 1; 
                }
            }
            if (gameStateModel.getCurrentState() == GameState.INVENTORY_STATE) {
                selectedInventoryIndex-- ;
                if (selectedInventoryIndex < 0) {
                    selectedInventoryIndex = playerModel.getInventory().getAllItems().size()-1; // Loop ke bawah
                }
            }

        }
        if (gameStateModel.isPlaying()) {
            this.moveUpActive = active; 
        } 
        if (!active && gameStateModel.isPlaying()) {
            this.moveUpActive = false; 
        }
    }
    @Override 
    public void setMoveDown(boolean active) {
        if (active) {
            if (gameStateModel.getCurrentState() == GameState.HOUSE_STATE) {
                if (!houseActions.isEmpty()) {
                    selectedHouseActionIndex++ ;
                    if (selectedHouseActionIndex >= houseActions.size()) {
                        selectedHouseActionIndex = 0; // Loop ke atas
                    }
                }
            }
            if (gameStateModel.getCurrentState() == GameState.RECIPE_STATE) {
                selectedRecipeIndex++ ;
                if (selectedRecipeIndex >= recipes.size()) {
                    selectedRecipeIndex = 0; 
                }
            }
            if (gameStateModel.getCurrentState() == GameState.INVENTORY_STATE) {
                selectedInventoryIndex++ ;
                if (selectedInventoryIndex >= playerModel.getInventory().getAllItems().size()) {
                    selectedInventoryIndex = 0; // Loop ke atas
                }
            }
        }
        if (gameStateModel.isPlaying()) {
            this.moveDownActive = active; 
        }
        if (!active && gameStateModel.isPlaying()) {
            this.moveDownActive = false; 
        }
    }
    @Override public void setMoveLeft(boolean active) {
        if (active) { // Proses hanya saat tombol pertama kali ditekan untuk aksi slider/menu
            if (gameStateModel.isGuessingFish()) {
                adjustFishingSlider(-1); // Tombol kiri menggerakkan slider ke kiri
            } else if (gameStateModel.isPlaying()) { // Hanya set flag untuk gerakan berkelanjutan di PLAY_STATE
                this.moveLeftActive = true;
            }
            // Tambahkan logika untuk navigasi menu ke kiri jika diperlukan untuk state lain
            // else if (gameStateModel.isInSomeOtherMenu()) { /* ... */ }
        } else { // active == false (tombol dilepas)
             if (gameStateModel.isPlaying()) {
                this.moveLeftActive = false;
            }
        }
    }
    @Override public void setMoveRight(boolean active) {
        if (active) {
            if (gameStateModel.isGuessingFish()) {
                adjustFishingSlider(1); // Tombol kanan menggerakkan slider ke kanan
            } else if (gameStateModel.isPlaying()) {
                this.moveRightActive = true;
            }
            // Tambahkan logika untuk navigasi menu ke kanan jika diperlukan
            // else if (gameStateModel.isInSomeOtherMenu()) { /* ... */ }
        } else {
            if (gameStateModel.isPlaying()) {
                this.moveRightActive = false;
            }
        }
    }

    @Override
    public void togglePause() { // Tombol Escape
    int currentState = gameStateModel.getCurrentState();
    if (currentState == GameState.PLAY_STATE) {
        gameStateModel.setCurrentState(GameState.PAUSE_STATE);
    } else if (currentState == GameState.PAUSE_STATE) {
        gameStateModel.setCurrentState(GameState.PLAY_STATE);
    } else if (currentState == GameState.INVENTORY_STATE) {
        gameStateModel.setCurrentState(GameState.PLAY_STATE); // Keluar dari inventory
    } else if (currentState == GameState.HOUSE_STATE) {
        gameStateModel.setCurrentState(GameState.PLAY_STATE); // Keluar dari menu rumah
    } else if (currentState == GameState.SLEEP_STATE) {
        gameStateModel.setCurrentState(GameState.HOUSE_STATE); // Keluar dari transisi tidur
        transitionMessage = ""; // Bersihkan pesan
        timeManager.start();
    }
    else if (currentState == GameState.COOK_STATE || currentState == GameState.RECIPE_STATE) {
        gameStateModel.setCurrentState(GameState.HOUSE_STATE); // Keluar dari menu memasak

    } 
    else if (currentState == GameState.FISHING_STATE) {
        gameStateModel.setCurrentState(GameState.PLAY_STATE);
        timeManager.start(); // Mulai kembali waktu saat keluar dari fishing
    }
}

    @Override
    public void toggleInventory() {
        // Allow opening inventory from PLAY_STATE or FISHING_STATE
        if (gameStateModel.isPlaying() || gameStateModel.isFishing()) {
            gameStateModel.setCurrentState(GameState.INVENTORY_STATE);
        } else if (gameStateModel.isInInventory()) {
            // Return to PLAY_STATE if coming from PLAY_STATE, or FISHING_STATE if coming from FISHING_STATE
            // For simplicity, always return to PLAY_STATE (customize if you want to remember previous state)
            gameStateModel.setCurrentState(GameState.PLAY_STATE);
        }
    }

    @Override
    public void performPrimaryAction() {
        int currentState = gameStateModel.getCurrentState(); // Ambil state saat ini

        // 1. Tangani aksi spesifik untuk HOUSE_STATE terlebih dahulu
        if (currentState == GameState.HOUSE_STATE) {
            if (!houseActions.isEmpty() && selectedHouseActionIndex >= 0 && selectedHouseActionIndex < houseActions.size()) {
                String selectedAction = houseActions.get(selectedHouseActionIndex);
                System.out.println("Player selected action in house: " + selectedAction);
                handleHouseAction(selectedAction); // Ini akan memanggil setCurrentState untuk SLEEP_STATE, COOK, dll.
            }
            // Setelah aksi di menu rumah ditangani (atau tidak ada aksi valid),
            // kita hentikan proses untuk tombol 'E' ini.
            return;
        }

        // 2. Tangani aksi untuk keluar dari SLEEP_STATE jika 'E' juga berfungsi sebagai tombol "lanjut"
        // (Tombol 'Escape' Anda di togglePause() sudah menangani ini, jadi ini opsional untuk 'E')
        // if (currentState == GameState.SLEEP_STATE) {
        //     // Anda mungkin ingin kembali ke PLAY_STATE atau HOUSE_STATE
        //     gameStateModel.setCurrentState(GameState.PLAY_STATE); // atau GameState.HOUSE_STATE
        //     transitionMessage = ""; // Bersihkan pesan transisi
        //     System.out.println("Exiting sleep state via E press (Primary Action)");
        //     return;
        // }

        if (currentState == GameState.COOK_STATE) {
            gameStateModel.setCurrentState(GameState.COOK_STATE);
            return;
        }
        if (currentState == GameState.INVENTORY_STATE) {
            // Logika untuk memilih item di inventory
            Map<Item, Integer> items = playerModel.getInventory().getAllItems();
            if (items.isEmpty()) {
                System.out.println("Inventory is empty!");
                return;
            }
            if (selectedInventoryIndex < 0 || selectedInventoryIndex >= items.size()) {
                System.out.println("Invalid inventory selection index: " + selectedInventoryIndex);
                return;
            }
            Item selectedItem = (Item) items.keySet().toArray()[selectedInventoryIndex];
            System.out.println("Selected item: " + selectedItem.getName());
            playerModel.setEquippedItem(selectedItem); // Set item sebagai yang dipilih
            gameStateModel.setCurrentState(GameState.PLAY_STATE); // Kembali ke PLAY_STATE setelah memilih item
            return;
        }
        if (currentState == GameState.RECIPE_STATE) {
            String selectedRecipe = recipes.get(selectedRecipeIndex);
            cookRecipe(selectedRecipe);
            return;
        }
        else if (currentState == GameState.FISHING_STATE) {
            // Logic for fishing action (e.g., cast line, catch fish)
            // You can call a method like handleFishingAction() here
            handleFishingAction();
            return;
        }
        else if (currentState == GameState.FISH_GUESS_STATE) {
            System.out.println("Guessing game logic not implemented yet.");
            confirmFishingSliderGuess();
            return;
        }

        // 3. Logika asli untuk PLAY_STATE (berinteraksi dengan dunia, objek, tanah)
        // Pastikan ini hanya berjalan jika benar-benar dalam PLAY_STATE
        if (currentState == GameState.PLAY_STATE) {
            int playerPixelX = playerModel.getPosition().getX();
            int playerPixelY = playerModel.getPosition().getY();
            int playerTileX = playerPixelX / tileSize;
            int playerTileY = playerPixelY / tileSize;

            // A. Interaksi dengan DeployedObject yang berdekatan (atau dihadapi)
            // PERHATIAN: Apakah farmMapModel.getAdjacentInteractableDeployedObject() sudah
            // memperhitungkan ARAH HADAP pemain? Jika belum, interaksi tidak akan sesuai
            // dengan objek yang dihadapi pemain. Anda mungkin perlu menggunakan logika targetTileX/Y
            // berdasarkan playerModel.getDirection() seperti diskusi kita sebelumnya.
            DeployedObject adjacentObject = farmMapModel.getAdjacentInteractableDeployedObject(playerTileX, playerTileY);

            if (adjacentObject != null) { // Tidak perlu cek instanceof Actionable di sini jika getAdjacent... sudah memfilter
                System.out.println("DEBUG GC: Player attempting to interact with DeployedObject: " + adjacentObject.getObjectName());
                if (adjacentObject instanceof HouseObject) {
                    gameStateModel.setCurrentState(GameState.HOUSE_STATE);
                    selectedHouseActionIndex = 0; // Reset pilihan menu rumah
                    return; // Masuk rumah, selesai.
                } 
                else if (adjacentObject instanceof PondObject) {
                    Item equipped = playerModel.getEquippedItem();
                // Validasi awal sebelum masuk ke FISHING_STATE
                    if (equipped != null && "Fishing Rod".equals(equipped.getName())) { // [cite: 188]
                        if (playerModel.getEnergy() >= 5) { // [cite: 188]
                            gameStateModel.setCurrentState(GameState.FISHING_STATE);
                            if (gamePanel != null) {
                                gamePanel.clearFishingUIState(); // Bersihkan UI fishing dari sesi sebelumnya
                            }
                            System.out.println("[GC] Entered FISHING_STATE. Ready to cast.");
                        } else {
                            if (gamePanel != null) gamePanel.setFishingMessage("Not enough energy to fish!");
                        }
                    } else {
                        if (gamePanel != null) gamePanel.setFishingMessage("You need a Fishing Rod!");
                    }
                    return; // Mulai memancing
                }
                else if (adjacentObject instanceof Actionable) {
                    // Untuk objek lain seperti ShippingBin, dll.
                    ((Actionable) adjacentObject).onPlayerAction(playerModel);
                    // Tambahkan logika energi/waktu jika perlu
                    return; // Interaksi dengan objek selesai.
                }
            }

            // B. Jika tidak ada interaksi objek, cek interaksi dengan Land
            Land currentLand = farmMapModel.getLandAt(playerTileX, playerTileY);
            if (currentLand != null) {
                Item equippedItem = playerModel.getEquippedItem();
                boolean actionTaken = false;

                if (equippedItem != null) {
                    if ("Hoe".equals(equippedItem.getName())) {
                        if (currentLand.till(playerModel)) actionTaken = true;
                        playerModel.setEnergy(playerModel.getEnergy() - 5);
                    } else if (equippedItem instanceof Seed) {
                        if (currentLand.plant((Seed) equippedItem, playerModel, timeManager.getMinutes())) actionTaken = true;
                    } else if ("Watering Can".equals(equippedItem.getName())) {
                        if (currentLand.water(playerModel)) actionTaken = true;
                    }
                    else if ("Pickaxe".equals(equippedItem.getName())) {
                        if (true) {
                            actionTaken = true;
                            currentLand.resetLand();
                            playerModel.setEnergy(playerModel.getEnergy() - 5);
                        }
                    }
                }
                if (!actionTaken) { // Jika belum ada aksi dari item, coba panen
                    Item harvestedCrop = currentLand.harvest(playerModel, timeManager.getMinutes());
                    if (harvestedCrop != null) {
                        // playerModel.getInventory().addItem(harvestedCrop);
                        actionTaken = true;
                    }
                }

                if (actionTaken) {
                    System.out.println("Action performed on Land at: " + playerTileX + "," + playerTileY);
                }
                // Interaksi Land selesai (atau tidak ada aksi), bisa return atau lanjut jika ada logika lain.
                return;
            }
            // Tidak ada interaksi objek atau Land yang terjadi.
        }
        return ;
    }

    public void updateGameLogic() {
        if (gameStateModel.isSleeping()) return ;
        // Auto-sleep at 2:00 AM or later if not already sleeping
        int minutes = timeManager.getMinutes();
        int twoAM = 2 * 60; 
        if ((playerModel.getEnergy() < -20) || (minutes % 1440) == twoAM && !gameStateModel.isSleeping()) {
            forceSleep();
            timeManager.stop(); // Stop time progression while sleeping
            return;
        }

        if (!gameStateModel.isPlaying()) return;
        int currentX = playerModel.getPosition().getX();
        int currentY = playerModel.getPosition().getY();
        int playerSpeed = playerModel.getSpeed();
        int dx = 0;
        int dy = 0;

        if (moveUpActive) dy = -playerSpeed;
        if (moveDownActive) dy = playerSpeed;
        if (moveLeftActive) dx = -playerSpeed;
        if (moveRightActive) dx = playerSpeed;

        if (dx != 0 || dy != 0) {
            int nextX = currentX;
            int nextY = currentY;

            // Cek dan terapkan pergerakan horizontal
            if (dx != 0) {
                if (!collisionChecker.willCollide(currentX, currentY, dx, 0)) {
                    nextX += dx;
                }
            }
            // Cek dan terapkan pergerakan vertikal, DARI POSISI X YANG MUNGKIN SUDAH BERUBAH (nextX)
            // dan currentY (karena kita cek tabrakan vertikal dari posisi Y saat ini)
            if (dy != 0) {
                if (!collisionChecker.willCollide(nextX, currentY, 0, dy)) { // Cek dari nextX, currentY
                    nextY += dy;
                }
            }
            
            if (nextX != currentX || nextY != currentY) {
                playerModel.setPosition(nextX, nextY);
            }
        }
    }
    private void forceSleep() {
        int energyBeforeSleep = playerModel.getEnergy();
        int maxEnergy = 100;
        int energyRestored;
        if (energyBeforeSleep == 0) {
            playerModel.setEnergy(10); // If energy is 0, restore to 10
            energyRestored = 10;
        } else if (energyBeforeSleep < maxEnergy * 0.1) {
            playerModel.setEnergy(maxEnergy / 2); // Restore to half
            energyRestored = (maxEnergy / 2) - energyBeforeSleep;
        } else {
            playerModel.setEnergy(maxEnergy); // Restore to full
            energyRestored = maxEnergy - energyBeforeSleep;
        }
        if (playerModel.getEnergy() > maxEnergy) playerModel.setEnergy(maxEnergy);
        transitionMessage = "You slept well. Energy +" + Math.max(0, energyRestored) + ".";
        gameStateModel.setCurrentState(GameState.SLEEP_STATE);
        timeManager.setTimeToSixAM();
    }

    private void handleHouseAction(String action) {
        switch (action) {
            case "Sleep":
                forceSleep();
                break;
            case "Cook":
                transitionMessage = "You are cooking...";
                gameStateModel.setCurrentState(GameState.RECIPE_STATE);
                System.out.println("Opening cooking interface...");
                break;
            case "Watch TV":
                System.out.println("Opening TV interface...");
                break;
            default:
                System.out.println("Unknown house action: " + action);
        }
    }

    // Cooking logic
    private void cookRecipe(String recipeName) {
        System.out.println("[DEBUG] Attempting to cook: " + recipeName);
        Recipe recipe = recipeList.stream()
            .filter(r -> r.name.equalsIgnoreCase(recipeName))
            .findFirst().orElse(null);
        if (recipe == null) {
            System.out.println("[DEBUG] Recipe not found: " + recipeName);
            transitionMessage = "Unknown recipe!";
            return;
        }

        // Check energy
        System.out.println("[DEBUG] Player energy: " + playerModel.getEnergy());
        if (playerModel.getEnergy() < 10) {
            System.out.println("[DEBUG] Not enough energy to cook.");
            transitionMessage = "Not enough energy to cook!";
            return;
        }

        // Check ingredients and fuel
        var inventory = playerModel.getInventory();
        boolean hasAll = true;
        for (RecipeRequirement req : recipe.requirements) {
            int count = 0;
            if (req.anyFish) {
                count = inventory.countItemsByType("Fish");
                System.out.println("[DEBUG] Checking for any Fish, found: " + count + ", required: " + req.quantity);
            } else {
                count = inventory.countItem(req.name);
                System.out.println("[DEBUG] Checking for " + req.name + ", found: " + count + ", required: " + req.quantity);
            }
            if (count < req.quantity) {
                hasAll = false;
                transitionMessage = "Missing ingredient: " + req.name;
                System.out.println("[DEBUG] Missing ingredient: " + req.name);
                break;
            }
        }
        int fuelCount = inventory.countItem("Coal");
        System.out.println("[DEBUG] Checking for Fuel, found: " + fuelCount + ", required: " + recipe.fuelNeeded);
        if (fuelCount < recipe.fuelNeeded) {
            hasAll = false;
            transitionMessage = "Not enough fuel!";
            System.out.println("[DEBUG] Not enough fuel!");
        }

        if (!hasAll) {
            System.out.println("[DEBUG] Cannot cook: requirements not met.");
            return;
        }

        // Deduct energy, ingredients, and fuel
        playerModel.setEnergy(playerModel.getEnergy() - 10);
        System.out.println("[DEBUG] Deducted 10 energy. Remaining: " + playerModel.getEnergy());
        for (RecipeRequirement req : recipe.requirements) {
            if (req.anyFish) {
                System.out.println("[DEBUG] Removing any Fish, quantity: " + req.quantity);
                inventory.removeItemsByType("Fish", req.quantity);
            } else {
                System.out.println("[DEBUG] Removing " + req.name + ", quantity: " + req.quantity);
                inventory.removeItem(req.name, req.quantity);
            }
        }
        System.out.println("[DEBUG] Removing Fuel, quantity: " + recipe.fuelNeeded);
        inventory.removeItem("Fuel", recipe.fuelNeeded);

        // Add cooked food to inventory (simulate instant cooking)
        com.oop10x.steddyvalley.model.items.Food cookedFood = null;
        try {
            cookedFood = new com.oop10x.steddyvalley.model.items.Food(recipe.name, 20, 100, 50); // fallback if not found
            cookedFood = Food.getFoodbyName(recipe.name);
            System.out.println("[DEBUG] Found Food object for: " + recipe.name);
        } catch (Exception e) {
            // fallback: create a generic Food if not found
            cookedFood = new com.oop10x.steddyvalley.model.items.Food(recipe.name, 20, 100, 50);
            System.out.println("[DEBUG] Created fallback Food object for: " + recipe.name);
        }
        playerModel.addItem(cookedFood);
        System.out.println("[DEBUG] Added cooked food to inventory: " + recipe.name);
        transitionMessage = "Cooked " + recipe.name + "!";
    }

    public void update(EventType type, Object message) {
        if (type == EventType.TWO_AM && !gameStateModel.isSleeping()) {
            forceSleep();
            timeManager.stop();
        }
    }

    public List<String> getHouseActions() {
        return houseActions;
    }
    public int getSelectedHouseActionIndex() {
        return selectedHouseActionIndex;
    }
    public String getTransitionMessage() {
        return transitionMessage;
    }

    public int getSelectedRecipeIndex() {
        return selectedRecipeIndex ;
    }
    public List<String> getRecipes() {
        return recipes;
    }

    // Inventory selection index for UI
    private int selectedInventoryIndex = 0;
    public int getSelectedInventoryIndex() {
        return selectedInventoryIndex;
    }

    // Handle fishing action in FISHING_STATE
    private void handleFishingAction() {
        // 1. Stop world time
        Item equipped = playerModel.getEquippedItem();
        if (equipped == null || !"Fishing Rod".equals(equipped.getName())) { // [cite: 188]
            if (gamePanel != null) gamePanel.setFishingMessage("You need a Fishing Rod equipped!");
            // Tetap di FISHING_STATE, tunggu pemain ganti item atau keluar (via Esc)
            return;
        }
        if (playerModel.getEnergy() < 5) { // [cite: 188]
            if (gamePanel != null) gamePanel.setFishingMessage("Not enough energy to fish!\nPress Enter/Esc to continue.");
            // Biarkan pemain di FISHING_STATE untuk melihat pesan, lalu keluar via Enter/Esc
            if(gamePanel != null) gamePanel.endFishingSliderUI(false); // Non-aktifkan input slider jika sempat aktif
            return;
        }
        playerModel.setEnergy(playerModel.getEnergy() - 5); // [cite: 188]
        timeManager.stop(); // [cite: 203]
        timeManager.addMinutes(15); // [cite: 203] (efek waktu berlalu selama persiapan)

        // 3. Tentukan ikan yang bisa ditangkap
        String location = getFishingLocation(); // Implementasi ini penting
        Season season = (seasonManager != null) ? seasonManager.getCurrentSeason() : Season.SPRING;
        Weather weather = (weatherManager != null) ? weatherManager.getCurrentWeather() : Weather.SUNNY;
        int timeOfDayMinutes = timeManager.getMinutes() % 1440; // Waktu saat ini dalam format 0-1439 menit

        List<Fish> fishableFish = new ArrayList<>();
        if (Fish.getFishSet() != null) { // Pastikan fishSet tidak null
            for (Fish fish : Fish.getFishSet()) {
                if (fish.isInLocation(location) && fish.isInSeason(season) && fish.isInWeather(weather) && fish.isInTime(timeOfDayMinutes)) {
                    fishableFish.add(fish);
                }
            }
        }
        System.out.println("[GC] Fishable fish at " + location + "/" + season + "/" + weather + "/" + timeOfDayMinutes + ": " + fishableFish.size());

        if (fishableFish.isEmpty()) {
            if (gamePanel != null) gamePanel.setFishingMessage("No fish are biting right now.\nPress Enter/Esc to continue.");
            if (gamePanel != null) gamePanel.endFishingSliderUI(false); // Pastikan UI tebakan tidak aktif
            // Tetap di FISHING_STATE untuk menampilkan pesan. Pemain akan keluar via Enter/Esc (memanggil endFishingSession).
            return;
        }

        // 4. Pilih ikan acak dan siapkan parameter mini-game
        Collections.shuffle(fishableFish);
        currentFishingTargetFish = fishableFish.get(0);
        FishRarity rarity = currentFishingTargetFish.getRarity();

        switch (rarity) { // [cite: 204, 205]
            case COMMON:     currentFishingTriesLeft = 10;  fishingSliderMax = 10;  break;
            case REGULAR:   currentFishingTriesLeft = 10;  fishingSliderMax = 100; break;
            case LEGENDARY:  currentFishingTriesLeft = 7;   fishingSliderMax = 500; break;
            default:         currentFishingTriesLeft = 10;  fishingSliderMax = 10;  break;
        }
        fishingSliderMin = 1;
        currentFishingTargetNumber = fishingSliderMin + randomGenerator.nextInt(fishingSliderMax);
        fishingSliderCurrentValue = fishingSliderMin; // Atau di tengah: fishingSliderMin + (fishingSliderMax - fishingSliderMin) / 2;

        System.out.println("[GC] Fishing for: " + currentFishingTargetFish.getName() + " (Target: " + currentFishingTargetNumber + ")");

        // 5. Ubah state ke FISH_GUESS_STATE dan update UI
        gameStateModel.setCurrentState(GameState.FISH_GUESS_STATE);
        if (gamePanel != null) {
            String initialMessage = "A fish is biting! It's a " + currentFishingTargetFish.getName() + " (" + rarity + ")!\n" +
                                  "Adjust slider (Range: " + fishingSliderMin + "-" + fishingSliderMax + "). Tries: " + currentFishingTriesLeft;
            gamePanel.startFishingSliderUI(initialMessage, fishingSliderMin, fishingSliderMax, fishingSliderCurrentValue, currentFishingTriesLeft);
        }
    }

    // Helper to determine fishing location based on player position or map
    public void adjustFishingSlider(int delta) {
        if (!gameStateModel.isGuessingFish() || gamePanel == null || !gamePanel.isFishingSliderActive()) {
            return;
        }
        fishingSliderCurrentValue += delta;
        fishingSliderCurrentValue = Math.max(fishingSliderMin, Math.min(fishingSliderCurrentValue, fishingSliderMax)); // Clamp value

        String currentAttemptMessage = "Current value: " + fishingSliderCurrentValue +
                                     " (Range: " + fishingSliderMin + "-" + fishingSliderMax +"). Tries left: " + currentFishingTriesLeft;
        gamePanel.updateFishingSliderDisplay(fishingSliderCurrentValue, currentAttemptMessage, currentFishingTriesLeft);
    }
     public void confirmFishingSliderGuess() {
        if (!gameStateModel.isGuessingFish() || gamePanel == null || !gamePanel.isFishingSliderActive()) {
            return;
        }
        System.out.println("[GC] Confirming guess: " + fishingSliderCurrentValue + ". Target: " + currentFishingTargetNumber);

        int guess = fishingSliderCurrentValue;
        String resultMessage;

        if (guess == currentFishingTargetNumber) {
            playerModel.addItem(currentFishingTargetFish); // [cite: 206]
            resultMessage = "Success! You caught a " + currentFishingTargetFish.getName() + "!\nPress Enter/Esc to continue.";
            if (gamePanel != null) gamePanel.endFishingSliderUI(true); // Sukses, nonaktifkan input slider
        } else {
            currentFishingTriesLeft--;
            if (currentFishingTriesLeft <= 0) {
                resultMessage = "Failed! The " + currentFishingTargetFish.getName() + " got away.\nPress Enter/Esc to continue.";
                if (gamePanel != null) gamePanel.endFishingSliderUI(false); // Gagal total, nonaktifkan input slider
            } else {
                // Masih ada percobaan, UI slider tetap aktif
                fishingSliderCurrentValue = fishingSliderMin; // Reset slider untuk tebakan berikutnya (opsional)
                resultMessage = "Wrong! " + currentFishingTriesLeft + " tries left.\n" +
                                "Adjust slider (Range: " + fishingSliderMin + "-" + fishingSliderMax + "). Value: " + fishingSliderCurrentValue;
                // GamePanel akan diupdate oleh adjustFishingSlider jika pemain menggerakkan lagi,
                // atau di sini jika ingin langsung update pesan dan nilai slider
                if (gamePanel != null) gamePanel.updateFishingSliderDisplay(fishingSliderCurrentValue, resultMessage, currentFishingTriesLeft);
                return; // Jangan pindah state dulu jika masih ada percobaan
            }
        }
        gameStateModel.setCurrentState(GameState.FISHING_STATE); // Kembali ke state fishing umum untuk menampilkan hasil
        if (gamePanel != null) gamePanel.setFishingMessage(resultMessage);
    }
    public void endFishingSession() {
        System.out.println("[GC] Ending fishing session completely. Returning to PLAY_STATE.");
        if (gamePanel != null) {
            gamePanel.clearFishingUIState();
        }
        gameStateModel.setCurrentState(GameState.PLAY_STATE);
        if (timeManager != null) timeManager.start(); // Lanjutkan waktu dunia [cite: 206]
    }

    private String getFishingLocation() {
        // TODO: Implementasi untuk menentukan lokasi memancing berdasarkan posisi pemain
        // Misalnya, cek apakah pemain berada di dekat PondObject di FarmMap,
        // atau jika di WorldMap, cek nama lokasi saat ini.
        // Untuk FarmMap dan Pond:
        // Anda perlu cara yang lebih baik untuk cek "1 tile DARI Pond" [cite: 201]
        // Ini mungkin melibatkan pengecekan 4 tile di sekitar pemain, atau tile yang dihadapi.
        // DeployedObject adjObj = farmMapModel.getAdjacentInteractableDeployedObject(playerTileX, playerTileY);
        // if (adjObj instanceof PondObject) {
        //     return "Pond";
        // }
        // Untuk lokasi lain dari World Map [cite: 202] (jika sudah ada)
        // if (farmMapModel.getCurrentMapName().equals("Forest River")) return "Forest River";

        System.out.println("[WARN] getFishingLocation() is defaulting to 'Pond'. Implement proper detection.");
        return "Pond"; // Default sementara
    }
    private int getUpperBoundForFish(FishRarity rarity) { // Helper untuk konsistensi
        switch (rarity) {
            case COMMON: return 10;
            case REGULAR: return 100;
            case LEGENDARY: return 500;
            default: return 10;
        }
    }


}
