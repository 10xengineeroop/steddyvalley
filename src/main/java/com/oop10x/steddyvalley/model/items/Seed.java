package com.oop10x.steddyvalley.model.items;

import com.oop10x.steddyvalley.utils.Season;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Seed extends Item {
    private Set<Season> seasons = new HashSet<>();
    private final Integer daysToHarvest;
    private final Crop growToCrop;
    private static final Set<Seed> seedSet = new HashSet<>();

    public Seed(String name, Integer price, Set<Season> seasons, Integer daysToHarvest, String growToCrop) {
        super(name, price);
        this.seasons = seasons;
        this.daysToHarvest = daysToHarvest;
        this.growToCrop = Crop.getCropByName(growToCrop);
        seedSet.add(this);
    }

    public boolean canGrowInSeason(String season) {
        for (Season s : seasons) {
            if (s.name().equals(season)) {
                return true;
            }
        }
        return false;
    }

    public Integer getDaysToHarvest() {
        return daysToHarvest;
    }

    public Crop getGrowToCrop() {
        return growToCrop;
    }

    public static Seed getSeedByName(String name) {
        for (Seed seed : seedSet) {
            if (seed.getName().equals(name)) {
                return seed;
            }
        }
        throw new IllegalArgumentException("No seed with name " + name);
    }
}