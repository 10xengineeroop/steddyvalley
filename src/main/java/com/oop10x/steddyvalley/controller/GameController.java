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
    private TimeManager timeManager;
    private int tileSize;
    private String transitionMessage;
    private GamePanel gamePanel;

    //house punya
    private List<String> houseActions = List.of("Sleep", "Cook", "Watch TV") ;
    private int selectedHouseActionIndex = 0; 
    private List<String> recipes = List.of("Fish n' Chips", "Baguette", "Sashimi", "Fugu", "wine", "Pumpkin Pie", "Veggie Soup",
            "Fish Stew", "Spakbor Salad", "Fish Sandwich", "The Legends of Spakbor") ;
    private int selectedRecipeIndex = 0;
    private String messageTV = "";

    private Fish currentFishingTargetFish;
    private int currentFishingTargetNumber;
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
        this.timeManager.addObserver(this); 
    }
    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override 
    public void setMoveUp(boolean active) { 
        if (active) {
            if (gameStateModel.getCurrentState() == GameState.HOUSE_STATE) {
                if (!houseActions.isEmpty()) {
                    selectedHouseActionIndex-- ;
                    if (selectedHouseActionIndex < 0) {
                        selectedHouseActionIndex = houseActions.size() - 1;
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
                    selectedInventoryIndex = playerModel.getInventory().getAllItems().size()-1; 
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
                        selectedHouseActionIndex = 0;
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
                    selectedInventoryIndex = 0; 
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
        if (active) { 
            if (gameStateModel.isGuessingFish()) {
                adjustFishingSlider(-1); 
            } else if (gameStateModel.isPlaying()) { 
                this.moveLeftActive = true;
            }
        } else {
             if (gameStateModel.isPlaying()) {
                this.moveLeftActive = false;
            }
        }
    }
    @Override public void setMoveRight(boolean active) {
        if (active) {
            if (gameStateModel.isGuessingFish()) {
                adjustFishingSlider(1);
            } else if (gameStateModel.isPlaying()) {
                this.moveRightActive = true;
            }
        } else {
            if (gameStateModel.isPlaying()) {
                this.moveRightActive = false;
            }
        }
    }

    @Override
    public void togglePause() {
    int currentState = gameStateModel.getCurrentState();
    if (currentState == GameState.PLAY_STATE) {
        gameStateModel.setCurrentState(GameState.PAUSE_STATE);
        timeManager.stop();
    } else if (currentState == GameState.PAUSE_STATE) {
        gameStateModel.setCurrentState(GameState.PLAY_STATE);
        resetMovementFlags();
        timeManager.start();
    } else if (currentState == GameState.INVENTORY_STATE) {
        gameStateModel.setCurrentState(GameState.PLAY_STATE);
        resetMovementFlags();
    } else if (currentState == GameState.HOUSE_STATE) {
        gameStateModel.setCurrentState(GameState.PLAY_STATE);
        resetMovementFlags();
    } else if (currentState == GameState.SLEEP_STATE) {
        gameStateModel.setCurrentState(GameState.HOUSE_STATE);
        resetMovementFlags();
        transitionMessage = "";
        timeManager.start();
    }
    else if (currentState == GameState.COOK_STATE || currentState == GameState.RECIPE_STATE) {
        gameStateModel.setCurrentState(GameState.HOUSE_STATE);
        resetMovementFlags();
    } 
    else if (currentState == GameState.FISHING_STATE) {
        gameStateModel.setCurrentState(GameState.PLAY_STATE);
        resetMovementFlags();
        timeManager.start();
    }
    else if (currentState == GameState.FISHING_STATE || currentState == GameState.FISH_GUESS_STATE) {
        endFishingSession();
    } /*  else if (currentState == GameState.SHIPPING_MODE) {
        playerFinishesShippingSession(); // Metode ini harus memanggil timeManager.start() setelah addMinutes(15)
    } */
    if (currentState == GameState.MESSAGE_TV) {
        messageTV = "";
        gameStateModel.setCurrentState(GameState.HOUSE_STATE);
        return;
    }
    
}

    @Override
    public void toggleInventory() {
        if (gameStateModel.isPlaying() || gameStateModel.isFishing()) {
            timeManager.stop();
            gameStateModel.setCurrentState(GameState.INVENTORY_STATE);
        } else if (gameStateModel.isInInventory()) {
            gameStateModel.setCurrentState(GameState.PLAY_STATE);
            timeManager.start();
            resetMovementFlags();
        }
    }

    @Override
    public void performPrimaryAction() {
        int currentState = gameStateModel.getCurrentState(); 

        if (currentState == GameState.HOUSE_STATE) {
            if (!houseActions.isEmpty() && selectedHouseActionIndex >= 0 && selectedHouseActionIndex < houseActions.size()) {
                String selectedAction = houseActions.get(selectedHouseActionIndex);
                System.out.println("Player selected action in house: " + selectedAction);
                handleHouseAction(selectedAction);
            }
            return;
        }
        if (currentState == GameState.MESSAGE_TV) {
            messageTV = "";
            gameStateModel.setCurrentState(GameState.HOUSE_STATE);
            return;
        }

        if (currentState == GameState.COOK_STATE) {
            gameStateModel.setCurrentState(GameState.COOK_STATE);
            return;
        }
        if (currentState == GameState.INVENTORY_STATE) {
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
            playerModel.setEquippedItem(selectedItem);
            gameStateModel.setCurrentState(GameState.PLAY_STATE);
            resetMovementFlags();
            return;
        }
        if (currentState == GameState.RECIPE_STATE) {
            String selectedRecipe = recipes.get(selectedRecipeIndex);
            cookRecipe(selectedRecipe);
            gameStateModel.setCurrentState(GameState.COOK_STATE);
            return;
        }
        else if (currentState == GameState.FISHING_STATE) {
            handleFishingAction();
            return;
        }
        else if (currentState == GameState.FISH_GUESS_STATE) {
            confirmFishingSliderGuess();
            return;
        }

        if (currentState == GameState.PLAY_STATE) {
            int playerPixelX = playerModel.getPosition().getX();
            int playerPixelY = playerModel.getPosition().getY();
            int playerTileX = playerPixelX / tileSize;
            int playerTileY = playerPixelY / tileSize;

            DeployedObject adjacentObject = farmMapModel.getAdjacentInteractableDeployedObject(playerTileX, playerTileY);

            if (adjacentObject != null) {
                System.out.println("DEBUG GC: Player attempting to interact with DeployedObject: " + adjacentObject.getObjectName());
                if (adjacentObject instanceof HouseObject) {
                    gameStateModel.setCurrentState(GameState.HOUSE_STATE);
                    selectedHouseActionIndex = 0;
                    return;
                } 
                else if (adjacentObject instanceof PondObject) {
                    Item equipped = playerModel.getEquippedItem();
                    if (equipped != null && "Fishing Rod".equals(equipped.getName())) {
                        if (playerModel.getEnergy() >= 5) {
                            gameStateModel.setCurrentState(GameState.FISHING_STATE);
                            if (gamePanel != null) {
                                gamePanel.clearFishingUIState();
                            }
                            System.out.println("Entered FISHING_STATE. Ready to cast.");
                        } else {
                            if (gamePanel != null) gamePanel.setFishingMessage("Not enough energy to fish!");
                        }
                    } else {
                        if (gamePanel != null) gamePanel.setFishingMessage("You need a Fishing Rod!");
                    }
                    return;
                }
                else if (adjacentObject instanceof Actionable) {
                    ((Actionable) adjacentObject).onPlayerAction(playerModel);
                    return; 
                }
            }

            Land currentLand = farmMapModel.getLandAt(playerTileX, playerTileY);
            // if (playerPixelX == 744 && playerPixelY == 744) {
            //     gameStateModel.setCurrentState(VISIT_STATE) ;
            //     handleVisitAction() ;
            // }
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
                    else if ("Pickaxe".equals(equippedItem.getName())) {
                        if (true) {
                            actionTaken = true;
                            currentLand.resetLand();
                            playerModel.setEnergy(playerModel.getEnergy() - 5);
                        }
                    }
                }
                if (!actionTaken) { 
                    Item harvestedCrop = currentLand.harvest(playerModel, timeManager.getMinutes());
                    if (harvestedCrop != null) {
                        // playerModel.getInventory().addItem(harvestedCrop);
                        actionTaken = true;
                    }
                }

                if (actionTaken) {
                    System.out.println("Action performed on Land at: " + playerTileX + "," + playerTileY);
                }
                return;
            }
        }
        return ;
    }

    public void updateGameLogic() {
        if (gameStateModel.isSleeping()) return ;
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

            if (dx != 0) {
                if (!collisionChecker.willCollide(currentX, currentY, dx, 0)) {
                    nextX += dx;
                }
            }

            if (dy != 0) {
                if (!collisionChecker.willCollide(nextX, currentY, 0, dy)) {
                    nextY += dy;
                }
            }
            
            if (nextX != currentX || nextY != currentY) {
                playerModel.setPosition(nextX, nextY);
            }
        }
    }

    private void forceSleep() {
        if (gameStateModel.isSleeping()) return;

        System.out.println("Initiating force sleep sequence...");
        timeManager.stop();

        int energyBeforeSleep = playerModel.getEnergy();
        final int MAX_ENERGY = 100;
        final int MIN_ENERGY_THRESHOLD = -20;
        int energyToSet;
        String sleepReasonMessage;

        boolean isDueToTime = (timeManager.getMinutes() % 1440) == (2 * 60);
        boolean isDueToExhaustion = energyBeforeSleep <= MIN_ENERGY_THRESHOLD;

        if (isDueToExhaustion) {
            energyToSet = MAX_ENERGY / 2;
            sleepReasonMessage = "You collapsed from exhaustion! Energy restored to half.";
        } else if (energyBeforeSleep == 0) {
            energyToSet = 10;
            sleepReasonMessage = "You were completely out of energy and went to sleep. Energy +10.";
        } else if (energyBeforeSleep < (MAX_ENERGY * 0.10)) {
            energyToSet = MAX_ENERGY / 2;
            sleepReasonMessage = "You were too tired and went to sleep. Energy restored to half.";
        } else {
            energyToSet = MAX_ENERGY;
            if (isDueToTime) {
                sleepReasonMessage = "It's late! You automatically went to sleep. Energy fully restored.";
            } else {
                sleepReasonMessage = "You slept soundly. Energy fully restored.";
            }
        }
        playerModel.setEnergy(energyToSet);
        transitionMessage = sleepReasonMessage;

        timeManager.setTimeToSixAM();

        HouseObject houseInstance = farmMapModel.getHouseObject();
        if (houseInstance != null) {
            int spawnTileX = houseInstance.getX() + (HouseObject.HOUSE_WIDTH / 2);
            int spawnTileY = houseInstance.getY() + HouseObject.HOUSE_HEIGHT;
            playerModel.setPosition(spawnTileX * tileSize, spawnTileY * tileSize);
        } else {
            System.err.println("House object not found in FarmMap. Player position not reset to house.");
            playerModel.setPosition(this.tileSize * 5, this.tileSize * 5);
        }
        gameStateModel.setCurrentState(GameState.SLEEP_STATE);
    }

    private void handleHouseAction(String action) {
        switch (action) {
            case "Sleep":
                forceSleep();
                break;
            case "Cook":
                gameStateModel.setCurrentState(GameState.COOK_STATE);
                transitionMessage = "You are cooking...";
                gameStateModel.setCurrentState(GameState.RECIPE_STATE);
                System.out.println("Opening cooking interface...");
                break;
            case "Watch TV":
                performWatchTV();
                break;
            default:
                System.out.println("Unknown house action: " + action);
        }
    }

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

        System.out.println("[DEBUG] Player energy: " + playerModel.getEnergy());
        if (playerModel.getEnergy() < 10) {
            System.out.println("[DEBUG] Not enough energy to cook.");
            transitionMessage = "Not enough energy to cook!";
            return;
        }

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

        com.oop10x.steddyvalley.model.items.Food cookedFood = null;
        try {
            cookedFood = new com.oop10x.steddyvalley.model.items.Food(recipe.name, 20, 100, 50); 
            cookedFood = Food.getFoodbyName(recipe.name);
            System.out.println("[DEBUG] Found Food object for: " + recipe.name);
        } catch (Exception e) {
            cookedFood = new com.oop10x.steddyvalley.model.items.Food(recipe.name, 20, 100, 50);
            System.out.println("[DEBUG] Created fallback Food object for: " + recipe.name);
        }
        playerModel.addItem(cookedFood);
        System.out.println("[DEBUG] Added cooked food to inventory: " + recipe.name);
        transitionMessage = "Cooked " + recipe.name + "!";
    }

    private void performWatchTV() {
        if (playerModel.getEnergy() >= 5) {
            playerModel.setEnergy(playerModel.getEnergy() - 5);
            timeManager.addMinutes(15);
    
            Weather tomorrowWeather = weatherManager.getNextDayWeather();
    
            String forecastMessage = "";
            if (tomorrowWeather != null) {
                forecastMessage += "\nTomorrow's forecast: " + tomorrowWeather.toString();
            } else {
                forecastMessage += "\nTomorrow's forecast is uncertain.";
            }
    
            messageTV = forecastMessage + ".";
            System.out.println("Watch TV: " + messageTV);
            gameStateModel.setCurrentState(GameState.MESSAGE_TV);
        } else {
            messageTV = "Not enough energy to watch TV.";
            System.out.println("Watch TV: " + messageTV);
            gameStateModel.setCurrentState(GameState.MESSAGE_TV);
        }
    }


    public String getMessageTV() {
        return messageTV;
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

    private int selectedInventoryIndex = 0;
    public int getSelectedInventoryIndex() {
        return selectedInventoryIndex;
    }

    private void handleFishingAction() {
        Item equipped = playerModel.getEquippedItem();
        if (equipped == null || !"Fishing Rod".equals(equipped.getName())) { 
            if (gamePanel != null) gamePanel.setFishingMessage2("You need a Fishing Rod equipped!");
            return;
        }
        if (playerModel.getEnergy() < 5) { 
            if (gamePanel != null) gamePanel.setFishingMessage2("Not enough energy to fish!\nPress Esc to back.");
            if(gamePanel != null) gamePanel.endFishingSliderUI(false); 
            return;
        }
        playerModel.setEnergy(playerModel.getEnergy() - 5); 
        timeManager.stop(); 
        timeManager.addMinutes(15); 

        String location = getFishingLocation(); 
        Season season = (seasonManager != null) ? seasonManager.getCurrentSeason() : Season.SPRING;
        Weather weather = (weatherManager != null) ? weatherManager.getCurrentWeather() : Weather.SUNNY;
        int timeOfDayMinutes = timeManager.getMinutes() % 1440; 

        List<Fish> fishableFish = new ArrayList<>();
        if (Fish.getFishSet() != null) { 
            for (Fish fish : Fish.getFishSet()) {
                if (fish.isInLocation(location) && fish.isInSeason(season) && fish.isInWeather(weather) && fish.isInTime(timeOfDayMinutes)) {
                    fishableFish.add(fish);
                }
            }
        }
        System.out.println("Fishable fish at " + location + "/" + season + "/" + weather + "/" + timeOfDayMinutes + ": " + fishableFish.size());

        if (fishableFish.isEmpty()) {
            if (gamePanel != null) gamePanel.setFishingMessage("No fish are biting right now.\nPress Esc to back.");
            if (gamePanel != null) gamePanel.endFishingSliderUI(false); 
            return;
        }


        Collections.shuffle(fishableFish);
        currentFishingTargetFish = fishableFish.get(0);
        FishRarity rarity = currentFishingTargetFish.getRarity();

        switch (rarity) { 
            case COMMON:     currentFishingTriesLeft = 10;  fishingSliderMax = 10;  break;
            case REGULAR:   currentFishingTriesLeft = 10;  fishingSliderMax = 100; break;
            case LEGENDARY:  currentFishingTriesLeft = 7;   fishingSliderMax = 500; break;
            default:         currentFishingTriesLeft = 10;  fishingSliderMax = 10;  break;
        }
        fishingSliderMin = 1;
        currentFishingTargetNumber = fishingSliderMin + randomGenerator.nextInt(fishingSliderMax);
        fishingSliderCurrentValue = fishingSliderMin; 

        System.out.println("Fishing for: " + currentFishingTargetFish.getName() + " (Target: " + currentFishingTargetNumber + ")");

        gameStateModel.setCurrentState(GameState.FISH_GUESS_STATE);
        if (gamePanel != null) {
            String initialMessage = "A fish is biting! It's a " + currentFishingTargetFish.getName() + " (" + rarity + ")!\n" +
                                  "Adjust slider (Range: " + fishingSliderMin + "-" + fishingSliderMax + "). Tries: " + currentFishingTriesLeft;
            gamePanel.startFishingSliderUI(initialMessage, fishingSliderMin, fishingSliderMax, fishingSliderCurrentValue, currentFishingTriesLeft);
        }
    }

    public void adjustFishingSlider(int delta) {
        if (!gameStateModel.isGuessingFish() || gamePanel == null || !gamePanel.isFishingSliderActive()) {
            return;
        }
        fishingSliderCurrentValue += delta;
        fishingSliderCurrentValue = Math.max(fishingSliderMin, Math.min(fishingSliderCurrentValue, fishingSliderMax)); 

        String currentAttemptMessage = "Current value: " + fishingSliderCurrentValue +
                                     " (Range: " + fishingSliderMin + "-" + fishingSliderMax +"). Tries left: " + currentFishingTriesLeft;
        gamePanel.updateFishingSliderDisplay(fishingSliderCurrentValue, currentAttemptMessage, currentFishingTriesLeft);
    }
     public void confirmFishingSliderGuess() {
        if (!gameStateModel.isGuessingFish() || gamePanel == null || !gamePanel.isFishingSliderActive()) {
            return;
        }
        System.out.println("Confirming guess: " + fishingSliderCurrentValue + ". Target: " + currentFishingTargetNumber);

        int guess = fishingSliderCurrentValue;
        String resultMessage;

        if (guess == currentFishingTargetNumber) {
            playerModel.addItem(currentFishingTargetFish); 
            resultMessage = "Success! You caught a " + currentFishingTargetFish.getName() + "!\nPress Esc to continue.";
            if (gamePanel != null) gamePanel.endFishingSliderUI(true); 
        } else {
            currentFishingTriesLeft--;
            if (currentFishingTriesLeft <= 0) {
                resultMessage = "Failed! The " + currentFishingTargetFish.getName() + " got away.\nPress Esc to continue.";
                if (gamePanel != null) gamePanel.endFishingSliderUI(false); 
            } else {
                String hint ;
                if (guess < currentFishingTargetNumber) {
                    hint = "Try a higher value!";
                } else {
                    hint = "Try a lower value!";
                }
                fishingSliderCurrentValue = fishingSliderMin; 
                resultMessage = "Wrong! " + hint + "\n" + currentFishingTriesLeft + " tries left.\n" +
                                "Adjust slider (Range: " + fishingSliderMin + "-" + fishingSliderMax + "). Value: " + fishingSliderCurrentValue;
                if (gamePanel != null) gamePanel.updateFishingSliderDisplay(fishingSliderCurrentValue, resultMessage, currentFishingTriesLeft);
                return; 
            }
        }
        gameStateModel.setCurrentState(GameState.FISHING_STATE); 
        if (gamePanel != null) gamePanel.setFishingMessage(resultMessage);
    }
    public void endFishingSession() {
        System.out.println("Ending fishing session completely. Returning to PLAY_STATE.");
        if (gamePanel != null) {
            gamePanel.clearFishingUIState();
        }
        gameStateModel.setCurrentState(GameState.PLAY_STATE);
        if (timeManager != null) timeManager.start(); 
    }

    private String getFishingLocation() {

        // Untuk FarmMap dan Pond:
        // Anda perlu cara yang lebih baik untuk cek "1 tile DARI Pond"
        // DeployedObject adjObj = farmMapModel.getAdjacentInteractableDeployedObject(playerTileX, playerTileY);
        // if (adjObj instanceof PondObject) {
        //     return "Pond";
        // }

        // Untuk lokasi lain dari World Map
        // if (farmMapModel.getCurrentMapName().equals("Forest River")) return "Forest River";

        System.out.println("[WARN] getFishingLocation() is defaulting to 'Pond'. Implement proper detection.");
        return "Pond"; 
    }
    private void setFishingLocation(String location) {}
    private int getUpperBoundForFish(FishRarity rarity) { 
        switch (rarity) {
            case COMMON: return 10;
            case REGULAR: return 100;
            case LEGENDARY: return 500;
            default: return 10;
        }
    }

    private void resetMovementFlags() {
        this.moveUpActive = false;
        this.moveDownActive = false;
        this.moveLeftActive = false;
        this.moveRightActive = false;
    }
}
