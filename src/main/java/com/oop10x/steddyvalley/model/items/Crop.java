package com.oop10x.steddyvalley.model.items;

import com.oop10x.steddyvalley.utils.Sellable;

public class Crop extends Item implements Sellable {
    private final int sellPrice;
    private final int energy;
    public Crop(String name, int price, int energy, int sellPrice) {
        super(name,price);
        this.sellPrice = sellPrice;
        this.energy = energy;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public int getEnergy() {
        return energy;
    }
}
