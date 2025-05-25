package com.oop10x.steddyvalley.model.recipes;

import java.util.List;

public class Recipe {
    public String id;
    public String name;
    public List<RecipeRequirement> requirements;
    public int fuelNeeded;

    public Recipe(String id, String name, List<RecipeRequirement> reqs, int fuelNeeded) {
        this.id = id;
        this.name = name;
        this.requirements = reqs;
        this.fuelNeeded = fuelNeeded;
    }
}
