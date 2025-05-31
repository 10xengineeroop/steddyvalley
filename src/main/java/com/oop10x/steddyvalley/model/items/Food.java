package com.oop10x.steddyvalley.model.items;

import java.util.HashSet;
import java.util.Set;

public class Food extends Item {
    private static final Set<Food> foodSet = new HashSet<>();
    private final Integer energy;
    public Food(String name, Integer energy, Integer buyPrice, Integer sellPrice) {
        super(name, buyPrice, sellPrice);
        this.energy = energy;
        foodSet.add(this);
    }

    public Integer getEnergy() {
        return energy;
    }

    public static Food getFoodbyName(String name) {
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

    public static Set<Food> getFoodSet() {
        return foodSet;
}
    public static void removeFood(Food food) {
        foodSet.remove(food);
    }

    
    static {
        new Food(
                "Fish n' Chips",
                50,
                150,
                135
        );
        new Food(
                "Baguette",
                25,
                100,
                80
        );
        new Food(
                "Sashimi",
                70,
                300,
                275
        );
        new Food(
                "Fugu",
                50,
                null,
                135
        );
        new Food(
                "Wine",
                20,
                100,
                90
        );
        new Food(
                "Pumpkin Pie",
                35,
                120,
                100
        );
        new Food(
                "Veggie Soup",
                40,
                140,
                120
        );
        new Food(
                "Fish Stew",
                70,
                280,
                260
        );
        new Food(
                "Spakbor Salad",
                70,
                null,
                250
        );
        new Food(
                "Fish Sandwich",
                50,
                200,
                180
        );
        new Food(
                "The Legends of Spakbor",
                100,
                null,
                2000
        );
        new Food(
                "Cooked Pig's Head",
                100,
                1000,
                0
        );
    }
}
