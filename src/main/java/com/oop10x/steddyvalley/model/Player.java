package com.oop10x.steddyvalley.model;

import com.oop10x.steddyvalley.model.items.*;
import com.oop10x.steddyvalley.utils.Position;
import com.oop10x.steddyvalley.utils.RelStatus;

import java.util.*;


public class Player {
    private int gold;
    private int energy;
    private final Position position;
    private Item equippedItem;
    private final Inventory inventory = new Inventory();
    private int currentTime;
    private int speed;
    private RelStatus relationshipStatus = RelStatus.SINGLE;

    public RelStatus getRelationshipStatus() {
        return relationshipStatus;
    }
    public void setRelationshipStatus(RelStatus relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
        notifyObservers();
    }

    private final transient List<PlayerObserver> observers = new ArrayList<>();

    public Player(int x, int y, int gold, int energy, int speed) {
        this.position = new Position(x,y);
        this.gold = gold;
        this.energy = energy;
        this.currentTime = 0;
        this.speed = speed;
        // Menambahkan peralatan awal
        Set<Equipment> initialEquipment = Equipment.getEquipmentSet();
        initialEquipment.forEach(equipment -> addItem(equipment));
        addItem(Seed.getSeedByName("Parsnip Seeds"), 15);
        setEquippedItem(initialEquipment.stream()
                .filter(equipment -> equipment.getName().equals("Hoe"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No Hoe equipment found")));
    }
    public void addObserver(PlayerObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }
    public void removeObserver(PlayerObserver observer) {
        observers.remove(observer);
    }
    private void notifyObservers() {
        List<PlayerObserver> observersCopy = new ArrayList<>(observers);
        for (PlayerObserver observer : observersCopy) {
            observer.onPlayerUpdated(this);
        }
    }
    public int getGold() {
        return gold;
    }
    public int getSpeed() {
        return speed;
    }

    public void setGold(int gold) {
        this.gold = gold;
        notifyObservers();
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
        if (this.energy < -20) {
            this.energy = -20;
        } else if (this.energy > 100) {
            this.energy = 100;
        }
        notifyObservers();
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(int x, int y) {
        boolean positionChanged = false;
        if (this.position.getX() != x) {
            this.position.setX(x);
            positionChanged = true;
        }
        if (this.position.getY() != y) {
            this.position.setY(y);
            positionChanged = true;
        }
        if (positionChanged) {
            notifyObservers();
        }
    }
    public void move(int deltaX, int deltaY) {
        if (deltaX != 0 || deltaY != 0) {
            this.position.setX(this.position.getX() + deltaX);
            this.position.setY(this.position.getY() + deltaY);
            // Mungkin kurangi energi di sini juga jika bergerak mengurangi energi
            // setEnergy(this.energy - 1); // Ini akan memanggil notifyObservers() juga
            notifyObservers();
        }
    }

    public Item getEquippedItem() {
        return equippedItem;
    }

    public void setEquippedItem(Item equippedItem) {
        this.equippedItem = equippedItem;
        notifyObservers();
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
        notifyObservers();
    }

    public void addItem(Item item) {
        inventory.addItem(item);
    }

    public void addItem(Item item, Integer amount) {
        inventory.addItem(item, amount);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void eat(Item item) {
        if (item != null) {
            if (item instanceof Food food) {
                setEnergy(getEnergy() + food.getEnergy());
            } else if (item instanceof Fish) {
                setEnergy(getEnergy() + 1);
            } else if (item instanceof Crop) {
                setEnergy(getEnergy() + 3);
            }
        }
    }
}
