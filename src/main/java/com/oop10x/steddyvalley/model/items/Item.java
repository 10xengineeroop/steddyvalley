package com.oop10x.steddyvalley.model.items;

import com.oop10x.steddyvalley.exception.NegativePriceException;

public class Item {
    private String name;
    private int price;

    public Item(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new NullPointerException("Name cannot be null or empty");
        }
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        if (price < 0) throw new NegativePriceException("Negative price");
        this.price = price;
    }
}
