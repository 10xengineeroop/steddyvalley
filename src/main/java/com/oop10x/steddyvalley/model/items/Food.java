package com.oop10x.steddyvalley.model.items;

import java.util.HashSet;
import java.util.Set;

public class Food extends Item {
    private static final Set<Food> foodSet = new HashSet<>();
    private final Integer energy;
    private final Integer sellPrice;
    public Food(String name, Integer energy, Integer buyPrice, Integer sellPrice) {
        super(name, buyPrice);
        this.energy = energy;
        this.sellPrice = sellPrice;
        foodSet.add(this);
    }

    public Integer getEnergy() {
        return energy;
    }

    public Integer getSellPrice() {
        return sellPrice;
    }

    public Food getFoodbyName(String name) {
        for (Food food : foodSet) {
            if (food.getName().equals(name)) {
                return food;
            }
        }
        throw new NullPointerException("No food with name " + name);
    }

    public static void addFood(Food food) {
        foodSet.add(food);
    }

    public static void removeFood(Food food) {
        foodSet.remove(food);
    }
}
