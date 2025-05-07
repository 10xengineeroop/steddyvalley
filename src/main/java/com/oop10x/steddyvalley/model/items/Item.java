package com.oop10x.steddyvalley.model.items;

import com.oop10x.steddyvalley.exception.NegativePriceException;

public class Item {
    private String name;
    private Integer price;

    public Item(String name, Integer price) {
        if (name == null) throw new NullPointerException("Name cannot be null");
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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        if (price < 0) throw new NegativePriceException("Negative price");
        this.price = price;
    }
}
