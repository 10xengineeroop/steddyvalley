package com.oop10x.steddyvalley.model.recipes;

import java.util.List;
import java.util.Arrays;

public class RecipeList {
    public static final List<Recipe> RECIPES = Arrays.asList(
        new Recipe("recipe_1", "Fish n' Chips", List.of(
            new RecipeRequirement("Fish", 2, true),
            new RecipeRequirement("Wheat", 1, false),
            new RecipeRequirement("Potato", 1, false)
        ), 1),
        new Recipe("recipe_2", "Baguette", List.of(
            new RecipeRequirement("Wheat", 3, false)
        ), 1),
        new Recipe("recipe_3", "Sashimi", List.of(
            new RecipeRequirement("Salmon", 3, false)
        ), 1),
        new Recipe("recipe_4", "Fugu", List.of(
            new RecipeRequirement("Pufferfish", 1, false)
        ), 1),
        new Recipe("recipe_5", "wine", List.of(
            new RecipeRequirement("Grape", 2, false)
        ), 1),
        new Recipe("recipe_6", "Pumpkin Pie", List.of(
            new RecipeRequirement("Egg", 1, false),
            new RecipeRequirement("Wheat", 1, false),
            new RecipeRequirement("Pumpkin", 1, false)
        ), 1),
        new Recipe("recipe_7", "Veggie Soup", List.of(
            new RecipeRequirement("Cauliflower", 1, false),
            new RecipeRequirement("Parsnip", 1, false),
            new RecipeRequirement("Potato", 1, false),
            new RecipeRequirement("Tomato", 1, false)
        ), 1),
        new Recipe("recipe_8", "Fish Stew", List.of(
            new RecipeRequirement("Fish", 2, true),
            new RecipeRequirement("Hot Pepper", 1, false),
            new RecipeRequirement("Cauliflower", 2, false)
        ), 1),
        new Recipe("recipe_9", "Spakbor Salad", List.of(
            new RecipeRequirement("Melon", 1, false),
            new RecipeRequirement("Cranberry", 1, false),
            new RecipeRequirement("Blueberry", 1, false),
            new RecipeRequirement("Tomato", 1, false)
        ), 1),
        new Recipe("recipe_10", "Fish Sandwich", List.of(
            new RecipeRequirement("Fish", 1, true),
            new RecipeRequirement("Wheat", 2, false),
            new RecipeRequirement("Tomato", 1, false),
            new RecipeRequirement("Hot Pepper", 1, false)
        ), 1),
        new Recipe("recipe_11", "The Legends of Spakbor", List.of(
            new RecipeRequirement("Legend fish", 1, false),
            new RecipeRequirement("Potato", 2, false),
            new RecipeRequirement("Parsnip", 1, false),
            new RecipeRequirement("Tomato", 1, false),
            new RecipeRequirement("Eggplant", 1, false)
        ), 1)
    );
}
