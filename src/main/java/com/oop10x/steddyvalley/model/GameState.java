package com.oop10x.steddyvalley.model;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    // Definisikan konstanta untuk berbagai state game
    public static final int HOUSE_STATE = 0;
    public static final int PLAY_STATE = 1;
    public static final int PAUSE_STATE = 2;
    public static final int INVENTORY_STATE = 3;
    public static final int DIALOGUE_STATE = 4; // Contoh state lain
    // Tambahkan state lain sesuai kebutuhan game Anda

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
    // ... tambahkan metode isX() lainnya ...
}
