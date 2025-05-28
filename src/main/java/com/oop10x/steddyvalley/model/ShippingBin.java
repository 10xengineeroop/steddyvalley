package com.oop10x.steddyvalley.model;
import com.oop10x.steddyvalley.utils.*;
import com.oop10x.steddyvalley.model.items.Item;
import com.oop10x.steddyvalley.model.map.Placeable;

import java.util.HashMap;
import java.util.Map;

public class ShippingBin implements Placeable, Observer {
    private final Position position = new Position(0, 0);
    private final Map<Item, Integer> items = new HashMap<>(); // Item and jumlah
    private final Player owner;

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

    public void putItem(Item item, int quantity) {
        items.put(item, items.getOrDefault(item, 0) + quantity);
    }

    public int getSellPrice() {
        int totalPrice = 0;
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            totalPrice += entry.getKey().getPrice() * entry.getValue();
        }
        return totalPrice;
    }

    @Override
    public void update(EventType eventType, Object message) {
        if (eventType.equals(EventType.NEWDAY)) {
            owner.setGold(owner.getGold() + getSellPrice());
            items.clear();
        }
    }

    public Player getOwner() {
        return owner;
    }
}
