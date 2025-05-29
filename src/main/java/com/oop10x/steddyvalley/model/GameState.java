package com.oop10x.steddyvalley.model;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    // Definisikan konstanta untuk berbagai state game
    public static final int HOUSE_STATE = 0;
    public static final int PLAY_STATE = 1;
    public static final int PAUSE_STATE = 2;
    public static final int INVENTORY_STATE = 3;
    public static final int SLEEP_STATE = 4; // Contoh state lain
    public static final int COOK_STATE = 5; // Jika ada menu awal
    public static final int RECIPE_STATE = 6;
    public static final int FISHING_STATE = 7;
    public static final int FISH_GUESS_STATE = 8;
<<<<<<< Updated upstream
    // Tambahkan state lain sesuai kebutuhan game Anda
=======
    public static final int MESSAGE_TV = 9;
    public static final int SHOP_STATE =10;
    public static final int VISIT_STATE = 11;
    public static final int NPCVISIT_STATE = 12;
    public static final int STOREOPT_STATE = 13;
    public static final int GIFT_STATE = 14;
>>>>>>> Stashed changes

    private int currentState;
    private final List<GameStateObserver> observers = new ArrayList<>();

    public GameState() {
        this.currentState = PLAY_STATE; // Mulai game dalam PLAY_STATE (atau MENU_STATE jika Anda punya menu)
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

    public void setCurrentState(int newState) {
        if (this.currentState != newState) {
            int oldState = this.currentState;
            this.currentState = newState;
            notifyObservers(oldState); // Beri tahu observer tentang perubahan state
        }
    }

    // --- Metode helper untuk memeriksa state (opsional, tapi berguna) ---
    public boolean isPlaying() {
        return currentState == PLAY_STATE;
    }

    public boolean isPaused() {
        return currentState == PAUSE_STATE;
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
<<<<<<< Updated upstream
=======
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
>>>>>>> Stashed changes
}
