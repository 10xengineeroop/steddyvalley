package com.oop10x.steddyvalley.controller;

public interface PlayerInputActions {
    void setMoveUp(boolean moveUp);
    void setMoveDown(boolean moveDown); 
    void setMoveLeft(boolean moveLeft);
    void setMoveRight(boolean moveRight);
    void togglePause();
    void toggleInventory();
    void performPrimaryAction();
    void toggleVisit();
    void scrollDisplay(int direction);
}