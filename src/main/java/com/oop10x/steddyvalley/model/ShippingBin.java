package com.oop10x.steddyvalley.model;
import com.oop10x.steddyvalley.utils.*;
import com.oop10x.steddyvalley.model.items.Item;
import com.oop10x.steddyvalley.model.map.Placeable;

import java.util.LinkedHashMap;
import java.util.Map;

public class ShippingBin implements Placeable, Observer {
    private final Position position = new Position(0, 0);
    private final Map<Item, Integer> items = new LinkedHashMap<>();
    private final Player owner;
    private static final int MaxSlots = 16;

    public ShippingBin(int x, int y, Player owner) {
        setX(x);
        setY(y);
        this.owner = owner;
    }

    @Override
    public void setX(int x) {
        position.setX(x);
    }

    @Override
    public int getX() {
        return position.getX();
    }

    @Override
    public void setY(int y) {
        position.setY(y);
    }

    @Override
    public int getY() {
        return position.getY();
    }

    public boolean putItem(Item item, int quantity) {
        if (item == null || quantity <= 0 || item.getSellPrice() == null || item.getSellPrice() <= 0) {
            System.out.println("Cannot ship this item (null, invalid quantity, or not sellable).");
            return false;
        }
        if (!items.containsKey(item) && items.size() >= MaxSlots) {
            System.out.println("Shipping Bin is full (max " + MaxSlots + " unique item slots).");
            return false;
        }
        items.put(item, items.getOrDefault(item, 0) + quantity);
        System.out.println("Placed " + quantity + "x " + item.getName() + " into Shipping Bin.");
        return true;
    }

    public int getSellPrice() {
        int totalPrice = 0;
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            Item item = entry.getKey();
            Integer quantity = entry.getValue();
            if (item.getSellPrice() != null) {
                totalPrice += item.getSellPrice() * quantity;
            }
        }
        return totalPrice;
    }

    public Map<Item, Integer> getItemsInBin() {
        return new LinkedHashMap<>(items);
    }

    @Override
    public void update(EventType eventType, Object message) {
        if (eventType == EventType.NEWDAY) {
            int totalIncome = getSellPrice();
            if (totalIncome > 0) {
                owner.setGold(owner.getGold() + totalIncome);
                System.out.println("End of day: Sold items from Shipping Bin for " + totalIncome + "g. Player gold: " + owner.getGold());
            } else {
                System.out.println("End of day: No items sold from Shipping Bin.");
            }
            items.clear();
        }
    }

    public Player getOwner() {
        return owner;
    }

}
