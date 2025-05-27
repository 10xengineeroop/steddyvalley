package com.oop10x.steddyvalley.model.items;

import com.oop10x.steddyvalley.utils.Season;

import java.util.HashSet;
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

    public static Set<Seed> getSeedSet() {
        return seedSet;
    }

    public int getSellPrice() {
        return getPrice() / 2;
    }

    static {
        new Seed(
                "Parsnip Seeds",
                20,
                Set.of(Season.SPRING),
                1,
                "Parsnip"
        );
        new Seed(
                "Cauliflower Seeds",
                80,
                Set.of(Season.SPRING),
                5,
                "Cauliflower"
        );
        new Seed(
                "Potato Seeds",
                50,
                Set.of(Season.SPRING),
                3,
                "Potato"
        );
        new Seed(
                "Wheat Seeds",
                60,
                Set.of(Season.SPRING, Season.AUTUMN),
                1,
                "Wheat"
        );
        new Seed(
                "Blueberry Seeds",
                80,
                Set.of(Season.SUMMER),
                7,
                "Blueberry"
        );
        new Seed(
                "Tomato Seeds",
                50,
                Set.of(Season.SUMMER),
                3,
                "Tomato"
        );
        new Seed(
                "Hot Pepper Seeds",
                40,
                Set.of(Season.SUMMER),
                1,
                "Hot Pepper"
        );
        new Seed(
                "Melon Seeds",
                80,
                Set.of(Season.SUMMER),
                4,
                "Melon"
        );
        new Seed(
                "Cranberry Seeds",
                100,
                Set.of(Season.AUTUMN),
                2,
                "Cranberry"
        );
        new Seed(
                "Pumpkin Seeds",
                150,
                Set.of(Season.AUTUMN),
                7,
                "Pumpkin"
        );
        new Seed(
                "Grape Seeds",
                60,
                Set.of(Season.AUTUMN),
                3,
                "Grape"
        );
    }
}