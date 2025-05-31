package com.oop10x.steddyvalley.model.items;

import com.oop10x.steddyvalley.exception.NegativePriceException;

public class Item {
    private String name;
    private Integer buyPrice;
    private Integer sellPrice;

    public Item(String name, Integer buyPrice) {
        this(name, buyPrice, null);
    }

    public Item(String name, Integer buyPrice, Integer sellPrice) {
        if (name == null) throw new NullPointerException("Name cannot be null");
        this.name = name;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
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
        return getBuyPrice();
    }

    public Integer getBuyPrice() {
        return buyPrice;
    }

    public Integer getSellPrice() {
        return sellPrice;
    }

    public void setPrice(Integer price) {
        setBuyPrice(price);
    }

    public void setBuyPrice(Integer buyPrice) {
        if (buyPrice < 0) throw new NegativePriceException("Negative buy price");
        this.buyPrice = buyPrice;
    }

    public void setSellPrice(Integer sellPrice) {
        if (sellPrice < 0) throw new NegativePriceException("Negative sell price");
        this.sellPrice = sellPrice;
    }
}
