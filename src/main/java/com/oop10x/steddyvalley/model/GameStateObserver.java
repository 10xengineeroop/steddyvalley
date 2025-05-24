package com.oop10x.steddyvalley.model;

public interface GameStateObserver {
    void onGameStateChanged(int newState, int oldState);
}
