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

    public int countItem(String name) {
        int total = 0;
        for (Map.Entry<Item, Integer> entry : inventory.entrySet()) {
            if (entry.getKey().getName().equalsIgnoreCase(name)) {
                total += entry.getValue();
            }
        }
        return total;
    }

    public int countItemsByType(String type) {
        int total = 0;
        for (Map.Entry<Item, Integer> entry : inventory.entrySet()) {
            if (type.equalsIgnoreCase("Fish")) {
                if (entry.getKey().getClass().getSimpleName().equals("Fish")) {
                    total += entry.getValue();
                }
            }
            // Add more types if needed
        }
        return total;
    }

    public void removeItem(String name, int quantity) {
        for (Map.Entry<Item, Integer> entry : inventory.entrySet()) {
            if (entry.getKey().getName().equalsIgnoreCase(name)) {
                int current = entry.getValue();
                if (current > quantity) {
                    inventory.put(entry.getKey(), current - quantity);
                } else {
                    inventory.remove(entry.getKey());
                }
                break;
            }
        }
    }

    public void removeItemsByType(String type, int quantity) {
        if (type.equalsIgnoreCase("Fish")) {
            int toRemove = quantity;
            for (Map.Entry<Item, Integer> entry : new HashMap<>(inventory).entrySet()) {
                if (entry.getKey().getClass().getSimpleName().equals("Fish")) {
                    int available = entry.getValue();
                    if (available >= toRemove) {
                        inventory.put(entry.getKey(), available - toRemove);
                        if (available - toRemove == 0) inventory.remove(entry.getKey());
                        break;
                    } else {
                        inventory.remove(entry.getKey());
                        toRemove -= available;
                    }
                }
            }
        }
        // Add more types if needed
    }

    public Item getItemByName(String name) {
        for (Item item : inventory.keySet()) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    public Map<Item, Integer> getAllItems() {
        return new HashMap<>(inventory);
    }
}
