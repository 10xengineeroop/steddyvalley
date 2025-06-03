package com.oop10x.steddyvalley.model;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    public static final int HOUSE_STATE = 0;
    public static final int PLAY_STATE = 1;
    public static final int PAUSE_MENU_STATE  = 2;
    public static final int INVENTORY_STATE = 3;
    public static final int SLEEP_STATE = 4;
    public static final int COOK_STATE = 5;
    public static final int RECIPE_STATE = 6;
    public static final int FISHING_STATE = 7;
    public static final int FISH_GUESS_STATE = 8;
    public static final int MESSAGE_TV = 9;
    public static final int SHIPPING_STATE = 10;
    public static final int VISIT_STATE = 11;
    public static final int NPCVISIT_STATE = 12;
    public static final int STOREOPT_STATE = 13;
    public static final int GIFT_STATE = 14;
    public static final int GIFTED_STATE = 15;
    public static final int ENDGAME_STATE = 16;
    public static final int MAIN_MENU_STATE = 17;
    public static final int HELP_STATE = 18;
    public static final int CREDITS_STATE = 19;
    public static final int PLAYER_NAME_INPUT_STATE = 20;
    public static final int PLAYER_GENDER_INPUT_STATE = 21;
    public static final int PLAYER_FAV_ITEM_INPUT_STATE = 22;
    public static final int SHOP_STATE = 23;

    private int currentState;
    private final List<GameStateObserver> observers = new ArrayList<>();

    public GameState() {
        this.currentState = MAIN_MENU_STATE;
    }

    // --- Observer Methods ---
    public void addObserver(GameStateObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(GameStateObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(int oldState) {
        List<GameStateObserver> observersCopy = new ArrayList<>(observers);
        for (GameStateObserver observer : observersCopy) {
            observer.onGameStateChanged(this.currentState, oldState);
        }
    }

    // --- Getter dan Setter untuk CurrentState ---
    public int getCurrentState() {
        return currentState;
    }
    public String getCurrentStateName(int stateCode) {
        return "MAIN_MENU_STATE";
    }

    public void setCurrentState(int newState) {
        if (this.currentState != newState) {
            int oldState = this.currentState;
            this.currentState = newState;
            notifyObservers(oldState);
        }
    }

    // --- Metode helper untuk memeriksa state (opsional, tapi berguna) ---
    public boolean isPlaying() {
        return currentState == PLAY_STATE;
    }

    public boolean isPaused() {
        return currentState == PAUSE_MENU_STATE ;
    }

    public boolean isInHouse() {
        return currentState == HOUSE_STATE;
    }

    public boolean isInInventory() {
        return currentState == INVENTORY_STATE;
    }
    public boolean isSleeping() {
        return currentState == SLEEP_STATE;
    }
    public boolean isCooking() {
        return currentState == COOK_STATE;
    }
    public boolean isInRecipeMenu() {
        return currentState == RECIPE_STATE;
    }
    public boolean isFishing() {
        return currentState == FISHING_STATE;
    }
    public boolean isGuessingFish() {
        return currentState == FISH_GUESS_STATE;
    }
    public boolean isWatchingTV() {
        return currentState == MESSAGE_TV;
    }
    public boolean isInShop() {
        return currentState == SHOP_STATE;
    }
    public boolean isVisiting() {
        return currentState == VISIT_STATE;
    }
    public boolean isVisitingNPC() {
        return currentState == NPCVISIT_STATE;
    }
    public boolean isInStore() {
        return currentState == STOREOPT_STATE;
    }
    public boolean isGifting() {
        return currentState == GIFT_STATE;
    }
    public boolean isGifted() {
        return currentState == GIFTED_STATE;
    }
    public boolean isEndGame() {
        return currentState == ENDGAME_STATE;
    }
    public boolean isShipping() {
        return currentState == SHIPPING_STATE;
    }
    public boolean isMainMenuState() {
        return currentState == MAIN_MENU_STATE;
    }
    public boolean isHelpState() {
        return currentState == HELP_STATE;
    }
    public boolean isCreditsState() {
        return currentState == CREDITS_STATE;
    }
    public boolean isPlayerNameInputState() {
        return currentState == PLAYER_NAME_INPUT_STATE;
    }
    public boolean isPlayerGenderInputState() { 
        return currentState == PLAYER_GENDER_INPUT_STATE; 
    }
    public boolean isPlayerFavItemInputState() { 
        return currentState == PLAYER_FAV_ITEM_INPUT_STATE;
    }
    public boolean isInShippingMode() {
        return currentState == SHIPPING_STATE;
    }
}