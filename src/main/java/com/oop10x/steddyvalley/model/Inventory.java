package com.oop10x.steddyvalley.model;

import com.oop10x.steddyvalley.model.items.Item;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private final Map<Item, Integer> inventory = new HashMap<>();

    public Inventory() {}

    public void addItem(Item item) {
        inventory.put(item, inventory.getOrDefault(item, 0) + 1);
    }

    public void addItem(Item item, int i) {
        inventory.put(item, inventory.getOrDefault(item, 0) + i);
    }

    public void decreaseItem(Item item) {
        decreaseItem(item, 1);
    }

    public void decreaseItem(Item item, int i) {
        inventory.put(item, inventory.getOrDefault(item, 0) - i);
    }
}
