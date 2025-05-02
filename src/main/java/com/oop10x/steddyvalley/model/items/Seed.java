package com.oop10x.steddyvalley.model.items;

import java.util.ArrayList;
import java.util.List;

public class Seed extends Item {
    private List<String> seasons = new ArrayList<>();
    private final Integer daysToHarvest;
    private final Crop growToCrop;

    public Seed(String name, int price, List<String> seasons, Integer daysToHarvest, Crop growToCrop) {
        super(name, price);
        this.seasons = seasons;
        this.daysToHarvest = daysToHarvest;
        this.growToCrop = growToCrop;
    }

    public boolean canGrowInSeason(String season) {
        return seasons.contains(season);
    }

    public Integer getDaysToHarvest() {
        return daysToHarvest;
    }

    public Crop getGrowToCrop() {
        return growToCrop;
    }
}