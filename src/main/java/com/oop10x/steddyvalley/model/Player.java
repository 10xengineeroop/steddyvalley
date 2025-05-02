package com.oop10x.steddyvalley.model;

import com.oop10x.steddyvalley.model.items.Item;
import com.oop10x.steddyvalley.utils.Position;

public class Player {
    private static Player instance;

    private int gold;
    private int energy;
    private final Position position;
    private Item equippedItem;
    private final Inventory inventory = new Inventory();
    private int currentTime;


    private Player() {
        this.position = new Position(0,0);
        this.gold = 0;
        this.energy = 0;
        instance = this;
    }
    public static Player getInstance() {
        if (instance == null) {
            instance = new Player();
        }
        return instance;
    }
    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(int x, int y) {
        position.setX(x);
        position.setY(y);
    }

    public Item getEquippedItem() {
        return equippedItem;
    }

    public void setEquippedItem(Item equippedItem) {
        this.equippedItem = equippedItem;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    public void addItem(Item item) {
        inventory.addItem(item);
    }
}
