package com.oop10x.steddyvalley.controller;

import com.oop10x.steddyvalley.model.FarmMap;
import com.oop10x.steddyvalley.model.GameState;
import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.model.TimeManager;
import com.oop10x.steddyvalley.model.collision.CollisionChecker;
import com.oop10x.steddyvalley.model.items.Equipment;
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
import com.oop10x.steddyvalley.utils.RelStatus;
import com.oop10x.steddyvalley.model.SeasonManager;
import com.oop10x.steddyvalley.model.WeatherManager;
import com.oop10x.steddyvalley.view.GamePanel;
import com.oop10x.steddyvalley.model.Store;
import com.oop10x.steddyvalley.model.NPC;
import com.oop10x.steddyvalley.model.ShippingBin;

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
    private ShippingBin activeShippingBin = null;
    private int selectedShippingInventoryIndex = 0;

    private boolean endGameStatisticsTriggered = false;

    //house punya
    private List<String> houseActions = List.of("Sleep", "Cook", "Watch TV") ;
    private int selectedHouseActionIndex = 0; 
    private List<String> recipes = List.of("Fish n' Chips", "Baguette", "Sashimi", "Fugu", "wine", "Pumpkin Pie", "Veggie Soup",
            "Fish Stew", "Spakbor Salad", "Fish Sandwich", "The Legends of Spakbor") ;
    private int selectedRecipeIndex = 0;
    private String messageTV = "";

    //Visit sama NPC bonding
    private List<String> viewVisitActions = List.of("Emily's Store", "Mayor Tadi's Manor", "Caroline's Workshop", "Perry's Atelier",
     "Dasco's Casino", "Abigail's House", "Little Lucy's House", "Forest River", "Mountain Lake", "Ocean");
    private List<String> visitActions = List.of("Emily", "Mayor Tadi", "Caroline", "Perry", "Dasco", "Abigail", "LittleLucy",
     "Forest River", "Mountain Lake", "Ocean");
    private List<Integer> proposedTime = List.of(0,0,0,0,0,0);
    private List<String> npcVisitActions = List.of("Chat", "Gift", "Propose");
    private List<String> storeOption = List.of("Meet Emily", "Shopping");
    private int selectedVisitActionIndex = 0;
    private int npcVisitIndex = 0;
    private int selectedNPCVisitActionIndex = 0; 
    private int selectedStoreOptionIndex = 0;
    private int selectedGiftIndex = 0;
    private String visiting = "";
    private String npcNow = "";
    private int npcHeartPoints = 0;
    private List<Item> giftOption;
    private String heartEarned;
    private String npcFeedbackMessage = "";
    private boolean npcFeedbackStatus = false;

    //fish punya
    private Fish currentFishingTargetFish;
    private int currentFishingTargetNumber;
    private int currentFishingTriesLeft;
    private int fishingSliderCurrentValue;
    private int fishingSliderMin;
    private int fishingSliderMax;
    private Random randomGenerator = new Random();
    private String fishingLocation = "Pond";
    
    //store punya
    private List<Item> currentShopItems;
    private int selectedShopItemIndex = 0;
    private boolean showingShopFeedback = false;
    private String shopFeedbackMessage = "";

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
        this.giftOption = new ArrayList<>(playerModel.getInventory().getAllItems().keySet());
    }
    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }
    public void scrollDisplay(int direction) {
        if (gameStateModel.isEndGame()) { // Hanya berlaku jika di ENDGAME_STATE
            if (gamePanel != null) {
                int scrollAmount = direction * 20; // Jumlah pixel untuk scroll, bisa disesuaikan
                gamePanel.adjustEndGameStatsScroll(scrollAmount);
            }
        }
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
            if (gameStateModel.getCurrentState() == GameState.VISIT_STATE) {
                selectedVisitActionIndex-- ;
                if (selectedVisitActionIndex < 0) {
                    selectedVisitActionIndex = viewVisitActions.size()-1; 
                }
            }
            if (gameStateModel.getCurrentState() == GameState.NPCVISIT_STATE) {
                selectedNPCVisitActionIndex-- ;
                if (selectedNPCVisitActionIndex < 0) {
                    selectedNPCVisitActionIndex = 2; 
                }
            }
            if (gameStateModel.getCurrentState() == GameState.GIFT_STATE) {
                selectedGiftIndex-- ;
                if (selectedGiftIndex < 0) {
                    selectedGiftIndex = getGiftOption().size()-1; 
                }
            }
            if (gameStateModel.getCurrentState() == GameState.STOREOPT_STATE) {
                selectedStoreOptionIndex-- ;
                if (selectedStoreOptionIndex < 0) {
                    selectedStoreOptionIndex = 1; 
                }
            }
            if (gameStateModel.isInShop() && !showingShopFeedback) {
                if (currentShopItems != null && !currentShopItems.isEmpty()) {
                    selectedShopItemIndex-- ;
                    if (selectedShopItemIndex < 0) {
                        selectedShopItemIndex = currentShopItems.size() - 1;
                    }
                }
            }
            if (gameStateModel.getCurrentState() == GameState.SHIPPING_STATE) {
                if (!playerModel.getInventory().getAllItems().isEmpty()) {
                    selectedShippingInventoryIndex--;
                    int invSize = playerModel.getInventory().getAllItems().size();
                    if (invSize > 0 && selectedShippingInventoryIndex >= invSize) {
                        selectedShippingInventoryIndex = 0;
                    } else if (invSize == 0) {
                        selectedShippingInventoryIndex = 0;
                    }
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
            if (gameStateModel.getCurrentState() == GameState.VISIT_STATE) {
                selectedVisitActionIndex++ ;
                if (selectedVisitActionIndex >= viewVisitActions.size()) {
                    selectedVisitActionIndex = 0; 
                }
            }
            if (gameStateModel.getCurrentState() == GameState.NPCVISIT_STATE) {
                selectedNPCVisitActionIndex++ ;
                if (selectedNPCVisitActionIndex >= 3) {
                    selectedNPCVisitActionIndex = 0; 
                }
            }
            if (gameStateModel.getCurrentState() == GameState.GIFT_STATE) {
                selectedGiftIndex++ ;
                if (selectedGiftIndex >= getGiftOption().size()) {
                    selectedGiftIndex = 0; 
                }
            }
            if (gameStateModel.getCurrentState() == GameState.STOREOPT_STATE) {
                selectedStoreOptionIndex++ ;
                if (selectedStoreOptionIndex >= 2) {
                    selectedStoreOptionIndex = 0; 
                }
            }
            if (gameStateModel.isInShop() && !showingShopFeedback) {
                if (currentShopItems != null && !currentShopItems.isEmpty()) {
                    selectedShopItemIndex++ ;
                    if (selectedShopItemIndex >= currentShopItems.size()) {
                        selectedShopItemIndex = 0;
                    }
                }
            }
            if (gameStateModel.getCurrentState() == GameState.SHIPPING_STATE) {
                if (!playerModel.getInventory().getAllItems().isEmpty()) {
                    selectedShippingInventoryIndex++;
                    int invSize = playerModel.getInventory().getAllItems().size();
                    if (invSize > 0 && selectedShippingInventoryIndex >= invSize) {
                        selectedShippingInventoryIndex = 0;
                    } else if (invSize == 0) {
                        selectedShippingInventoryIndex = 0;
                    }
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
            timeManager.start();
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
        if (currentState == GameState.SHOP_STATE) {
            if (showingShopFeedback) {
                showingShopFeedback = false;
                shopFeedbackMessage = "";
            } else {
                exitShopState();
            }
        }
        if (currentState == GameState.VISIT_STATE) {
            gameStateModel.setCurrentState(GameState.PLAY_STATE);
            resetMovementFlags();
            timeManager.start();
            selectedVisitActionIndex = 0;
        }
        else if (currentState == GameState.NPCVISIT_STATE) {
        if (npcFeedbackStatus) {
            npcFeedbackStatus = false;
            npcFeedbackMessage = "";
        } else {
                gameStateModel.setCurrentState(GameState.VISIT_STATE);
                resetMovementFlags();
                selectedNPCVisitActionIndex = 0;
        }
        }
        else if (currentState == GameState.STOREOPT_STATE) {
            gameStateModel.setCurrentState(GameState.VISIT_STATE);
            resetMovementFlags();
            selectedStoreOptionIndex = 0;
        }
        else if (currentState == GameState.GIFT_STATE) {
            gameStateModel.setCurrentState(GameState.NPCVISIT_STATE);
            resetMovementFlags();
            selectedGiftIndex = 0;
        }
        else if (currentState == GameState.GIFTED_STATE) {
            giftOption = new ArrayList<>(playerModel.getInventory().getAllItems().keySet());
            gameStateModel.setCurrentState(GameState.NPCVISIT_STATE);
            resetMovementFlags();
        }
        else if (currentState == GameState.ENDGAME_STATE) {
            gameStateModel.setCurrentState(GameState.PLAY_STATE);
            resetMovementFlags();
            timeManager.start();
        }
        else if (currentState == GameState.SHIPPING_STATE) {
            finishShippingSession();
        }
    }
    public void toggleVisit() {
        if (playerModel.getPosition().getX() == 744) {
            if (playerModel.getEnergy() >= 15) {
                gameStateModel.setCurrentState(GameState.VISIT_STATE);
                timeManager.stop();
            }
            else{
                System.out.println("Not enough energy to visit!");
            }
        }
    }

    @Override
    public void toggleInventory() {
        // kalo buka Inv jadi Pause Time
        // timeManager.stop();
        // timeManager.start();
        if (gameStateModel.isPlaying() || gameStateModel.isFishing()) {
            timeManager.stop();
            gameStateModel.setCurrentState(GameState.INVENTORY_STATE);
        } else if (gameStateModel.isInInventory()) {
            gameStateModel.setCurrentState(GameState.PLAY_STATE);
            timeManager.start();
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
            timeManager.start();
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
        else if (currentState == GameState.SHOP_STATE) {
            if(!showingShopFeedback) {
                if (currentShopItems != null && !currentShopItems.isEmpty() && selectedShopItemIndex >= 0 && selectedShopItemIndex < currentShopItems.size()) {
                    Item itemToBuy = currentShopItems.get(selectedShopItemIndex);
                    int quantityToBuy = 1; 
                    Integer itemPrice = itemToBuy.getBuyPrice();
                    if (itemPrice == null) {
                        shopFeedbackMessage = itemToBuy.getName() + " tidak dapat dibeli." ;
                    }
                    else {
                        int totalPrice = itemPrice * quantityToBuy;
                        if (playerModel.getGold() >= totalPrice) {
                            Store.getInstance().buyItem(playerModel, itemToBuy.getName(), quantityToBuy);
                            shopFeedbackMessage = "Anda membeli " + quantityToBuy + " " + itemToBuy.getName() + ".";

                        }
                        else {
                            shopFeedbackMessage = "Tidak cukup emas untuk membeli " + itemToBuy.getName() + ".";
                        }
                    }
                    showingShopFeedback = true;
                }
            }
            else {
                showingShopFeedback = false;
                shopFeedbackMessage = "";
            }
            return;
        }

        if (currentState == GameState.VISIT_STATE) {
            String selectedVisitAction = visitActions.get(selectedVisitActionIndex);
            System.out.println("Player selected visit action: " + selectedVisitAction);
            npcVisitIndex = selectedVisitActionIndex;
            handleVisitAction(selectedVisitActionIndex);
            selectedVisitActionIndex = 0;
            return;
        }

        if (currentState == GameState.NPCVISIT_STATE) {
            if (!npcFeedbackStatus) {
                giftOption = new ArrayList<>(playerModel.getInventory().getAllItems().keySet());
                handleNPCVisit(npcVisitActions.get(selectedNPCVisitActionIndex), visitActions.get(npcVisitIndex));
            }
            else{
                npcFeedbackStatus = false;
                npcFeedbackMessage = "";
            }
            selectedNPCVisitActionIndex = 0;
            return;
        }
        if (currentState == GameState.STOREOPT_STATE) {
            if(selectedStoreOptionIndex == 1) {
                enterShopState();
            }
            else if (selectedStoreOptionIndex == 0) {
                gameStateModel.setCurrentState(GameState.NPCVISIT_STATE);
            }
            playerModel.setEnergy(playerModel.getEnergy() - 10);
            timeManager.addMinutes(15);
            selectedStoreOptionIndex = 0;
            return;
        }

        if (currentState == GameState.GIFT_STATE){
            handleGifting(NPC.getNpcByName(visitActions.get(npcVisitIndex)));
            selectedGiftIndex = 0;
            return;
        }
        if (currentState == GameState.SHIPPING_STATE) {
            Map<Item, Integer> playerItemsMap = playerModel.getInventory().getAllItems();
            List<Item> playerItemList = new ArrayList<>(playerItemsMap.keySet());

            if (activeShippingBin == null) {
                transitionMessage = "Shipping Bin error! Exiting.";
                System.err.println("[GameController] CRITICAL: activeShippingBin is null in SHIPPING_STATE action.");
                finishShippingSession();
                return;
            }

            if (!playerItemList.isEmpty() && selectedShippingInventoryIndex >= 0 && selectedShippingInventoryIndex < playerItemList.size()) {
                Item itemToShip = playerItemList.get(selectedShippingInventoryIndex);
                int quantityToShip = 1;

                if (itemToShip.getSellPrice() != null && itemToShip.getSellPrice() > 0) {
                    if (activeShippingBin.putItem(itemToShip, quantityToShip)) {
                        playerModel.getInventory().removeItem(itemToShip.getName(), quantityToShip);
                        transitionMessage = "Shipped " + quantityToShip + " " + itemToShip.getName() + ".";
                        if (playerModel.getInventory().countItem(itemToShip.getName()) == 0) {
                            if (selectedShippingInventoryIndex >= playerModel.getInventory().getAllItems().size()) {
                                 selectedShippingInventoryIndex = Math.max(0, playerModel.getInventory().getAllItems().size() - 1);
                            }
                        }
                    } else {
                        transitionMessage = "Cannot ship " + itemToShip.getName() + ". Bin full of unique items or item not sellable.";
                    }
                } else {
                    transitionMessage = itemToShip.getName() + " cannot be sold or has no sell price.";
                }
            } else if (playerItemList.isEmpty()) {
                transitionMessage = "Inventory is empty. Press Esc to finish shipping.";
            } else {
                transitionMessage = "No item selected. Press Esc to finish shipping.";
            }
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
                    gameStateModel.setCurrentState(GameState.FISHING_STATE);
                    if (gamePanel != null) {
                        gamePanel.clearFishingUIState();
                    }
                    return;
                }
                else if (adjacentObject instanceof ShippingBinObject) {
                        this.activeShippingBin = ((ShippingBinObject) adjacentObject).getLogicalBin();
                    if (this.activeShippingBin != null) {
                        timeManager.stop();
                        gameStateModel.setCurrentState(GameState.SHIPPING_STATE);
                        selectedShippingInventoryIndex = 0;
                        transitionMessage = "Select item to ship (E) / Esc to Finish.";
                        System.out.println("Player entered SHIPPING_STATE.");
                    } else {
                        System.err.println("Logical Shipping Bin not found for ShippingBinObject interaction.");
                        transitionMessage = "Shipping Bin is not working.";
                    }
                    return;
                }
                else if (adjacentObject instanceof Actionable) {
                    ((Actionable) adjacentObject).onPlayerAction(playerModel);
                    return; 
                }
                
            }
            Item equippedItem = playerModel.getEquippedItem();
            if (equippedItem != null) {
                boolean isEdible = equippedItem instanceof Food ||
                                   equippedItem instanceof Fish ||
                                   equippedItem instanceof com.oop10x.steddyvalley.model.items.Crop;
    
                if (isEdible) {
                    handleEatEquippedItem();
                    return;
                }
            }

            Land currentLand = farmMapModel.getLandAt(playerTileX, playerTileY);
            if (currentLand != null) {
                boolean actionTaken = false;
                if (equippedItem != null) {
                    currentLand.harvest(playerModel, timeManager.getMinutes());
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

                if (actionTaken) {
                    System.out.println("Action performed on Land at: " + playerTileX + "," + playerTileY);
                }
                return;
            }
        }
        return ;
    }

    public void handleEatEquippedItem() {
        if (!gameStateModel.isPlaying()) {
            return;
        }

        Item equippedItem = playerModel.getEquippedItem();

        if (equippedItem == null) {
            this.transitionMessage = "Nothing equipped to eat!";
            System.out.println("Eat attempt: No item equipped.");
            return;
        }

        boolean isEdible = equippedItem instanceof Food ||
                           equippedItem instanceof Fish ||
                           equippedItem instanceof com.oop10x.steddyvalley.model.items.Crop;
        if (!isEdible) {
            this.transitionMessage = equippedItem.getName() + " is not edible!";
            System.out.println("Eat attempt: " + equippedItem.getName() + " is not edible.");
            return;
        }

        playerModel.eat(equippedItem);

        playerModel.getInventory().removeItem(equippedItem.getName(), 1);

        String itemName = equippedItem.getName();
        playerModel.setEquippedItem(null);

        timeManager.addMinutes(5);

        this.transitionMessage = "You ate " + itemName + ".";
        System.out.println("Player ate: " + itemName);
    }

    public void updateGameLogic() {
        if (gameStateModel.isEndGame() || endGameStatisticsTriggered) {
            return ;
        }
        if (gameStateModel.isSleeping()) return;
        
        if (gameStateModel.isPlaying() || gameStateModel.isInHouse()) { // Atau state aktif lainnya yang relevan
            checkAndTriggerEndGameStatistics();
            if (gameStateModel.isEndGame()) {
                return;
            }
        }
        if (playerModel.getEnergy() <= -20) {
            forceSleep();
            timeManager.stop();
            return;
        }
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
        boolean isDueToExhaustion = energyBeforeSleep == MIN_ENERGY_THRESHOLD;

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
    public List<String> getVisitActions() {
        return visitActions;
    }
    public int getSelectedRecipeIndex() {
        return selectedRecipeIndex ;
    }
    public List<String> getRecipes() {
        return recipes;
    }
    public List<Item> getGiftOption() {
        List<Item> items = new ArrayList<>();
        for (Item item : giftOption) {
            if (!(item instanceof Equipment)){
                items.add(item);
            }
        }
        return items;
    }
    public List<String> getViewVisitActions() {
        return viewVisitActions;
    }
    public List<Integer> getProposedTime() {
        return proposedTime;
    }
    public List<String> getNpcVisitActions() {
        return npcVisitActions;
    } 
    public List<String> getStoreOption() {
        return storeOption;
    }
    public int getSelectedGiftIndex() {
        return selectedGiftIndex;
    }
    public int getSelectedVisitActionIndex() {
        return selectedVisitActionIndex;
    }
    public int getSelectedNPCVisitActionIndex() {
        return selectedNPCVisitActionIndex;
    } 
    public int getSelectedStoreOptionIndex() {
        return selectedStoreOptionIndex;
    }
    public String getVisiting() {
        return visiting;
    }
    public String getNpcNow() {
        return npcNow;
    }
    public int getNpcHeartPoints() {
        return npcHeartPoints;
    }
    public String getNpcRelStatus() {
        String status = "";
        NPC npc = NPC.getNpcByName(npcNow);
        if (npc.getRelationshipStatus().equals(RelStatus.SINGLE)) {
            status = "Single";
        } else if (npc.getRelationshipStatus().equals(RelStatus.FIANCE)) {
            status = "Engaged";
        } else if (npc.getRelationshipStatus().equals(RelStatus.SPOUSE)) {
            status = "Married";
        }
        return status;
    }

    public RelStatus getNpcRelation(){
        return NPC.getNpcByName(npcNow).getRelationshipStatus();
    }
    public String getHeartMessage() {
        return heartEarned;
    }
    public void setHeartMessage(String message) {
        this.heartEarned = message;
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

        String location = fishingLocation; 
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
            Player.setTotalFishCaught(Player.getTotalFishCaught() + 1);
            Player.incrementFishCaughtByRarity(currentFishingTargetFish.getRarity());
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

    public void setFishingLocation(String location) {
        this.fishingLocation = location;
        System.out.println("Fishing location set to: " + location);
    }
    private void resetMovementFlags() {
        this.moveUpActive = false;
        this.moveDownActive = false;
        this.moveLeftActive = false;
        this.moveRightActive = false;
    }

    public void handleVisitAction(int index) {
        for (Item item : getGiftOption()) {
            if (!(item instanceof Equipment)) {
                System.out.println(item.getName());
            }
        }
        if (index == 0){
            visiting = viewVisitActions.get(selectedVisitActionIndex);
            npcNow = visitActions.get(selectedVisitActionIndex);
            npcHeartPoints = NPC.getNpcByName(npcNow).getHeartPoints();
            NPC emily = NPC.getNpcByName(npcNow);
            emily.recordPlayerVisit();
            gameStateModel.setCurrentState(GameState.STOREOPT_STATE);
        }
        else if (index >= 1 && index <= 6) {
            visiting = viewVisitActions.get(selectedVisitActionIndex);
            npcNow = visitActions.get(selectedVisitActionIndex);
            npcHeartPoints = NPC.getNpcByName(npcNow).getHeartPoints();
            NPC npc = NPC.getNpcByName(npcNow);
            npc.recordPlayerVisit();
            gameStateModel.setCurrentState(GameState.NPCVISIT_STATE);
            playerModel.setEnergy(playerModel.getEnergy() - 10);
            timeManager.addMinutes(15);
        }
        else if (index >= 7 && index <= 9){
            playerModel.setEnergy(playerModel.getEnergy() - 10);
            timeManager.addMinutes(15);
            setFishingLocation(visitActions.get(index));
            gameStateModel.setCurrentState(GameState.FISHING_STATE);
        }
        else{
            System.out.println("Unknown visit action: " + visitActions.get(index));
            }
        selectedVisitActionIndex = 0;
        }
    
    public void handleNPCVisit(String action, String name) {
        NPC npc = NPC.getNpcByName(name);
        switch (action) {
            case "Chat":
                if (playerModel.getEnergy() >= 10) {
                    npcFeedbackMessage = "Chatting with " + name + ". You had a great time.";
                    npc.chat();
                    npcHeartPoints = NPC.getNpcByName(npcNow).getHeartPoints();
                    playerModel.setEnergy(playerModel.getEnergy() - 10);
                    timeManager.addMinutes(10);
                }
                else {
                    npcFeedbackMessage = "Not enough energy to chat with " + name + ".";
                }
                npcFeedbackStatus = true;
                break;
            case "Gift":
                if (playerModel.getEnergy() >= 5) {
                    gameStateModel.setCurrentState(GameState.GIFT_STATE);
                }
                else{
                    npcFeedbackMessage = "Not enough energy to gift!";
                    npcFeedbackStatus = true;
                }
                break;
            case "Propose":
                handlePropose(npc);
                npcFeedbackStatus = true;
                break;
        } 
    }
    public boolean isShowingNPCFeedback() {
        return npcFeedbackStatus;
    }
    public String getNPCFeedbackMessage() {
        return npcFeedbackMessage;
    }

    public void handleGifting(NPC npc) {
        playerModel.setEnergy(playerModel.getEnergy() - 5);
        timeManager.addMinutes(10);
        Item giftItem = getGiftOption().get(selectedGiftIndex);
        npc.getItem(giftItem);
        System.out.println("Gifting " + getGiftOption().get(selectedGiftIndex) + " to " + npc.getName() + ".");
        if(npc.getLovedItems().contains(giftItem)) {
            heartEarned = "You earned 25 heart points with " + npc.getName() + "!";
        } else if (npc.getLikedItems().contains(giftItem)) {
            heartEarned = "You earned 20 heart points with " + npc.getName() + "!";
        } else if (npc.getHatedItems().contains(giftItem)) {
            heartEarned = "You lost 25 heart points with " + npc.getName() + ".";
        } else {
            heartEarned = "No change in heart points with " + npc.getName() + ".";
        }
        gameStateModel.setCurrentState(GameState.GIFTED_STATE);
        npcHeartPoints = NPC.getNpcByName(npcNow).getHeartPoints();
        playerModel.getInventory().removeItem(getGiftOption().get(selectedGiftIndex).getName(), 1);
    }
    
    public void handlePropose(NPC npc) {
        if(npc.getName().equals("LittleLucy")){
            npcFeedbackMessage = "Bro? Why are you trying to propose a child?";
        }
        else if (npc.getRelationshipStatus().equals(RelStatus.SPOUSE)) {
            npcFeedbackMessage = "You are already married to " + npc.getName() + ".";
        }
        else{
            if(npc.getRelationshipStatus().equals(RelStatus.FIANCE)){
                if (playerModel.getEnergy() >= 80) {
                    if (getDay(timeManager.getMinutes()) > proposedTime.get(selectedVisitActionIndex)) {
                        boolean accepted = npc.propose(playerModel);
                        npcFeedbackMessage = "Successfully married " + npc.getName() + "!";
                        playerModel.setEnergy(playerModel.getEnergy() - 80);
                        timeManager.setTimeToTenPM();
                    }
                    else{npcFeedbackMessage = "You need to wait for the next day before marrying " + npc.getName() + "!";
                    }
                }
                else{
                    npcFeedbackMessage = "Not enough energy to marry!";
                }}
            else{
                if (playerModel.getEnergy() >= 10) {
                    boolean accepted = npc.propose(playerModel);
                    if (accepted){
                            playerModel.setEnergy(playerModel.getEnergy() - 10);
                            npcFeedbackMessage = "Successfully become " + npc.getName() + "'s fiance!";
                            proposedTime.set(selectedVisitActionIndex, getDay(timeManager.getMinutes()));
                    }
                    else{
                        if (npc.getHeartPoints() < 150) {
                            npcFeedbackMessage = "Increase your heart point with " + npc.getName() + "first.";
                        }
                        else if (playerModel.getInventory().getItemByName("Proposal Ring") == null) {
                            npcFeedbackMessage = "You need a Proposal Ring to propose!";
                        }
                        playerModel.setEnergy(playerModel.getEnergy() - 20);
                    }
                    timeManager.addMinutes(60);
                }
                else{
                    npcFeedbackMessage = "Not enough energy to propose!";
                }
            }
        }
    }
    public int getDay(int time) {
        int days = (time - (time % 1440)) / 1440;
        return days;
    }
    public List<String> itemToString(List<Item> inventory) {
        List<String> newList = new ArrayList<>();
        for (Item item : inventory){
            newList.add(item.getName());
        }
        return newList;
    }
    
    public List<String> npcToString(List<NPC> npcList){
        List<String> newList = new ArrayList<>();
        for (NPC npc : npcList) {
            newList.add(npc.getName());
        }
        return newList;
    }
    
    public void enterShopState() {
        this.currentShopItems = Store.getInstance().getItems(); 
        this.selectedShopItemIndex = 0;
        this.showingShopFeedback = false;
        this.shopFeedbackMessage = "";
        this.gameStateModel.setCurrentState(GameState.SHOP_STATE);
    }
    private void exitShopState() {
        this.gameStateModel.setCurrentState(GameState.PLAY_STATE);
        this.currentShopItems = null;
    }
    public List<Item> getCurrentShopItems() {
        return currentShopItems;
    }
    public int getSelectedShopItemIndex() {
        return selectedShopItemIndex;
    }
    public boolean isShowingShopFeedback() {
        return showingShopFeedback;
    }
    public String getShopFeedbackMessage() {
        return shopFeedbackMessage;
    }

    public void checkAndTriggerEndGameStatistics() {
        if (endGameStatisticsTriggered) {
            return;
        }

        boolean goldMilestoneMet = playerModel.getGold() >= 17209;
        boolean marriageMilestoneMet = playerModel.getRelationshipStatus() == RelStatus.SPOUSE; 
        if (goldMilestoneMet || marriageMilestoneMet) {
            endGameStatisticsTriggered = true; 
             
            if (timeManager != null) {
                timeManager.stop(); 
            }
            if (gamePanel != null) {
                gamePanel.resetEndGameStatsScroll();
            }
            gameStateModel.setCurrentState(GameState.ENDGAME_STATE);

            if (gamePanel != null) gamePanel.repaint();
        }
    }
    public ShippingBin getActiveShippingBin() {
        return activeShippingBin;
    }
    public int getSelectedShippingInventoryIndex() {
        int inventorySize = (playerModel != null && playerModel.getInventory() != null) ? playerModel.getInventory().getAllItems().size() : 0;
        if (inventorySize == 0) {
            selectedShippingInventoryIndex = 0;
            return selectedShippingInventoryIndex;
        }
        if (selectedShippingInventoryIndex >= inventorySize) {
            selectedShippingInventoryIndex = inventorySize - 1;
        }
        if (selectedShippingInventoryIndex < 0) {
            selectedShippingInventoryIndex = 0;
        }
        return selectedShippingInventoryIndex;
    }
    private void finishShippingSession() {
        if (timeManager != null) {
            timeManager.addMinutes(15);
            timeManager.start();
        }
        gameStateModel.setCurrentState(GameState.PLAY_STATE);
        resetMovementFlags();
        transitionMessage = "Finished shipping for the day.";
        activeShippingBin = null;
        System.out.println("Player finished shipping session.");
    }
}
