package com.oop10x.steddyvalley.controller;

import com.oop10x.steddyvalley.model.FarmMap;
import com.oop10x.steddyvalley.model.GameState;
import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.model.TimeManager;
import com.oop10x.steddyvalley.model.collision.CollisionChecker;
import com.oop10x.steddyvalley.model.items.Item;
import com.oop10x.steddyvalley.model.items.Seed;
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
import com.oop10x.steddyvalley.utils.EventType;
import com.oop10x.steddyvalley.model.SeasonManager;
import com.oop10x.steddyvalley.model.WeatherManager;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class GameController implements PlayerInputActions, Observer {

    private Player playerModel;
    private GameState gameStateModel;
    private FarmMap farmMapModel;
    private CollisionChecker collisionChecker;
    private TimeManager timeManager; // Untuk mendapatkan waktu saat ini
    private int tileSize;
    private String transitionMessage;

    //house punya
    private List<String> houseActions = List.of("Sleep", "Cook", "Watch TV") ;
    private int selectedHouseActionIndex = 0; 
    private List<String> recipes = List.of("Fish n' Chips", "Baguette", "Sashimi", "Fugu", "wine", "Pumpkin Pie", "Veggie Soup",
            "Fish Stew", "Spakbor Salad", "Fish Sandwich", "The Legends of Spakbor") ;
    private int selectedRecipeIndex = 0;


    private boolean moveUpActive, moveDownActive, moveLeftActive, moveRightActive;

    // Recipe data moved to com.oop10x.steddyvalley.model.recipes
    private List<Recipe> recipeList = RecipeList.RECIPES;

    // Managers for season and weather
    private SeasonManager seasonManager;
    private WeatherManager weatherManager;

    // Register as observer in constructor
    public GameController(Player player, GameState gameState, FarmMap farmMap,
                          CollisionChecker cc, TimeManager tm, int tileSize,
                          SeasonManager seasonManager, WeatherManager weatherManager) {
        this.playerModel = player;
        this.gameStateModel = gameState;
        this.farmMapModel = farmMap;
        this.collisionChecker = cc;
        this.timeManager = tm;
        this.tileSize = tileSize;
        this.seasonManager = seasonManager;
        this.weatherManager = weatherManager;
        this.timeManager.addObserver(this); // Register for time events
    }

    // Overloaded constructor for backward compatibility
    public GameController(Player player, GameState gameState, FarmMap farmMap,
                          CollisionChecker cc, TimeManager tm, int tileSize) {
        this(player, gameState, farmMap, cc, tm, tileSize, null, null);
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
                    selectedInventoryIndex = playerModel.getInventory().getAllItems().size(); // Loop ke bawah
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
    @Override public void setMoveLeft(boolean active) { this.moveLeftActive = active; }
    @Override public void setMoveRight(boolean active) { this.moveRightActive = active; }

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
                    ((Actionable) adjacentObject).onPlayerAction(playerModel);
                    gameStateModel.setCurrentState(GameState.FISHING_STATE);
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
                    } else if (equippedItem instanceof Seed) {
                        if (currentLand.plant((Seed) equippedItem, playerModel, timeManager.getMinutes())) actionTaken = true;
                    } else if ("Watering Can".equals(equippedItem.getName())) {
                        if (currentLand.water(playerModel)) actionTaken = true;
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
        if ((minutes % 1440) == twoAM && !gameStateModel.isSleeping()) {
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
            cookedFood = cookedFood.getFoodbyName(recipe.name);
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
        timeManager.stop();

        // 2. Check if player has a fishing rod and enough energy
        if (playerModel.getEquippedItem() == null || !"Fishing Rod".equals(playerModel.getEquippedItem().getName())) {
            transitionMessage = "You need a Fishing Rod to fish!";
            timeManager.start();
            gameStateModel.setCurrentState(GameState.PLAY_STATE);
            return;
        }
        if (playerModel.getEnergy() < 5) {
            transitionMessage = "Not enough energy to fish!";
            timeManager.start();
            gameStateModel.setCurrentState(GameState.PLAY_STATE);
            return;
        }

        // 3. Deduct energy and add 15 minutes
        playerModel.setEnergy(playerModel.getEnergy() - 5);
        timeManager.addMinutes(15);

        // 4. Determine fishing location (should be based on map/position)
        String location = getFishingLocation(); // Implement this helper to return "Pond", "Ocean", etc.
        System.out.println("DEBUG: Fishing location determined: " + location);

        // 5. Get current season and weather from managers if available
        com.oop10x.steddyvalley.utils.Season season = (seasonManager != null) ? seasonManager.getCurrentSeason() : com.oop10x.steddyvalley.utils.Season.SPRING;
        com.oop10x.steddyvalley.utils.Weather weather = (weatherManager != null) ? weatherManager.getCurrentWeather() : com.oop10x.steddyvalley.utils.Weather.SUNNY;
        int time = timeManager.getMinutes();
        System.out.println("DEBUG: Current season: " + season + ", weather: " + weather + ", time: " + time);

        // 6. Find all fish available for this location, season, weather, and time
        java.util.List<com.oop10x.steddyvalley.model.items.Fish> fishableFish = new java.util.ArrayList<>();
        for (com.oop10x.steddyvalley.model.items.Fish fish : com.oop10x.steddyvalley.model.items.Fish.getFishSet()) {
            if (fish.isInLocation(location) && fish.isInSeason(season) && fish.isInWeather(weather) && fish.isInTime(time)) {
                fishableFish.add(fish);
            }
        }
        if (fishableFish.isEmpty()) {
            transitionMessage = "No fish are biting right now.";
            timeManager.start();
            gameStateModel.setCurrentState(GameState.PLAY_STATE);
            System.out.println("DEBUG: No fish available for fishing at this time/location/season/weather.");
            return;
        }

        // 7. Randomly select a fish
        java.util.Collections.shuffle(fishableFish);
        com.oop10x.steddyvalley.model.items.Fish targetFish = fishableFish.get(0);
        com.oop10x.steddyvalley.utils.FishRarity rarity = targetFish.getRarity();
        int maxTries, upperBound;
        if (rarity == com.oop10x.steddyvalley.utils.FishRarity.COMMON) {
            maxTries = 10; upperBound = 10;
        } else if (rarity == com.oop10x.steddyvalley.utils.FishRarity.REGULAR) {
            maxTries = 10; upperBound = 100;
        } else {
            maxTries = 7; upperBound = 500;
        }
        System.out.println("DEBUG: Selected fish: " + targetFish.getName() + ", rarity: " + rarity + ", max tries: " + maxTries + ", upper bound: " + upperBound);

        // 8. Guessing game (UI integration needed)
        // TODO: Replace this with actual UI input for guessing game
        // For now, auto-success for demo
        // If integrating with UI, pause here and wait for user input, then check answer
        playerModel.addItem(targetFish);
        transitionMessage = "You caught a " + targetFish.getName() + "!";
        System.out.println("DEBUG: Caught fish: " + targetFish.getName());

        // 9. Resume world time and return to PLAY_STATE
        timeManager.start();
        gameStateModel.setCurrentState(GameState.PLAY_STATE);
    }

    // Helper to determine fishing location based on player position or map
    private String getFishingLocation() {
        // TODO: Implement actual logic based on player position and map
        // For now, return "Pond" as a default
        return "Pond";
    }
}
