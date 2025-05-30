package com.oop10x.steddyvalley.controller;

public interface PlayerInputActions {
    void setMoveUp(boolean moveUp);
    void setMoveDown(boolean moveDown); 
    void setMoveLeft(boolean moveLeft);
    void setMoveRight(boolean moveRight);
    //void setInteract(boolean interact);
    void togglePause();
    void toggleInventory();
    void performPrimaryAction();
    void toggleVisit();
}
