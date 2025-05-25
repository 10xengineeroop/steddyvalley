package com.oop10x.steddyvalley.model.recipes;

public class RecipeRequirement {
    public String name;
    public int quantity;
    public boolean anyFish; // true if "Any Fish" is allowed

    public RecipeRequirement(String name, int quantity, boolean anyFish) {
        this.name = name;
        this.quantity = quantity;
        this.anyFish = anyFish;
    }
}
