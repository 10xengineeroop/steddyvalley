package com.oop10x.steddyvalley.model.items;

import com.oop10x.steddyvalley.utils.*;

import java.util.HashSet;

import java.util.Set;

public class Fish extends Item {
    private Set<Season> seasonList = new HashSet<>();
    private Set<Weather> weatherList = new HashSet<>();
    private Set<TimeRange> timeList = new HashSet<>();
    private Set<String> locationList = new HashSet<>();
    private final FishRarity rarity;

    private static final Set<Fish> fishSet = new HashSet<>();

    public Fish(String name, FishRarity rarity, Set<Season> seasonList, Set<TimeRange> timeList, Set<Weather> weatherList, Set<String> locationList) {
        super(name, 0);
        this.seasonList = seasonList;
        this.weatherList = weatherList;
        this.rarity = rarity;
        this.timeList = timeList;
        this.locationList = locationList;
        setPrice(getSellPrice());
        fishSet.add(this);
    }

    public boolean isInSeason(Season season) {
        return seasonList.contains(season);
    }

    public boolean isInWeather(Weather weather) { return weatherList.contains(weather); }

    public boolean isInLocation(String location) { return locationList.contains(location); }

    public boolean isInTime(Integer time) {
        for (TimeRange timeRange : timeList) {
            if (timeRange.isIn(time)) {
                return true;
            }
        }
        return false;
    }

    public int getTotalHour() {
        int total = 0;
        for (TimeRange timeRange : timeList) {
            total += (timeRange.end() - timeRange.start()) / 60;
        }
        return total;
    }

    public int getSellPrice() {
        double sellPrice = rarity.equals(FishRarity.REGULAR) ? 5.0 : rarity.equals(FishRarity.COMMON) ? 10.0 : 25.0;
        sellPrice *= 4.0 / seasonList.size();
        sellPrice *= 24.0 / getTotalHour();
        sellPrice *= 2.0 / weatherList.size();
        sellPrice *= 4.0 / locationList.size();
        return (int) sellPrice;
    }

    public FishRarity getRarity() {
        return rarity;
    }

    public static Set<Fish> getFishSet() {
        return fishSet;
    }

    public static void addFish(Fish fish) {
        fishSet.add(fish);
    }

    public static void removeFish(String fish) {
        fishSet.removeIf(f -> f.getName().equals(fish));
    }

    public static Fish getFishbyName(String name) {
        for (Fish fish : fishSet) {
            if (fish.getName().equals(name)) {
                return fish;
            }
        }
        throw new IllegalArgumentException("No Fish with name " + name + " found");
    }
    static {
    new Fish(
                "Bullhead",
                FishRarity.COMMON,
                Set.of(Season.SPRING,Season.SUMMER,Season.AUTUMN,Season.WINTER),
                Set.of(new TimeRange(0,24*60)),
                Set.of(Weather.RAINY,Weather.SUNNY),
                Set.of("Mountain Lake")
        );
        new Fish(
                "Carp",
                FishRarity.COMMON,
                Set.of(Season.SPRING,Season.SUMMER,Season.AUTUMN,Season.WINTER),
                Set.of(new TimeRange(0,24*60)),
                Set.of(Weather.RAINY,Weather.SUNNY),
                Set.of("Mountain Lake", "Pond")
        );
        new Fish(
                "Chub",
                FishRarity.COMMON,
                Set.of(Season.SPRING,Season.SUMMER,Season.AUTUMN,Season.WINTER),
                Set.of(new TimeRange(0,24*60)),
                Set.of(Weather.RAINY,Weather.SUNNY),
                Set.of("Forest River", "Mountain Lake")
        );

    //    REGULAR FISH
        new Fish(
                "Largemouth Bass",
                FishRarity.REGULAR,
                Set.of(Season.SPRING,Season.SUMMER,Season.AUTUMN,Season.WINTER),
                Set.of(new TimeRange(6 * 60, 18 * 60)),
                Set.of(Weather.RAINY,Weather.SUNNY),
                Set.of("Mountain Lake")
        );
        new Fish(
                "Rainbow Trout",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER),
                Set.of(new TimeRange(6 * 60, 18 * 60)),
                Set.of(Weather.SUNNY),
                Set.of("Forest River", "Mountain Lake")
        );
        new Fish(
                "Sturgeon",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER, Season.WINTER),
                Set.of(new TimeRange(6 * 60, 18 * 60)),
                Set.of(Weather.SUNNY, Weather.RAINY),
                Set.of("Mountain Lake")
        );
        new Fish(
                "Midnight Carp",
                FishRarity.REGULAR,
                Set.of(Season.WINTER, Season.AUTUMN),
                Set.of(new TimeRange(0, 2 * 60), new TimeRange(20 * 60, 24 * 60)),
                Set.of(Weather.SUNNY, Weather.RAINY),
                Set.of("Mountain Lake", "Pond")
        );
        new Fish(
                "Flounder",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER, Season.SPRING),
                Set.of(new TimeRange(6 * 60, 22 * 60)),
                Set.of(Weather.SUNNY, Weather.RAINY),
                Set.of("Ocean")
        );
        new Fish(
                "Halibut",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER, Season.WINTER, Season.AUTUMN, Season.SPRING),
                Set.of(new TimeRange(6 * 60, 11 * 60), new TimeRange(19 * 60, 24 * 60), new TimeRange(0, 2 * 60)),
                Set.of(Weather.SUNNY, Weather.RAINY),
                Set.of("Ocean")
        );
        new Fish(
                "Octopus",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER),
                Set.of(new TimeRange(6 * 60, 22 * 60)),
                Set.of(Weather.SUNNY, Weather.RAINY),
                Set.of("Ocean")
        );
        new Fish(
                "Pufferfish",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER),
                Set.of(new TimeRange(0, 16 * 60)),
                Set.of(Weather.SUNNY),
                Set.of("Ocean")
        );
        new Fish(
                "Sardine",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER, Season.SPRING, Season.WINTER, Season.AUTUMN),
                Set.of(new TimeRange(6 * 60, 18 * 60)),
                Set.of(Weather.SUNNY, Weather.RAINY),
                Set.of("Ocean")
        );
        new Fish(
                "Super Cucumber",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER, Season.WINTER, Season.AUTUMN),
                Set.of(new TimeRange(18 * 60, 24 * 60), new TimeRange(0, 2 * 60)),
                Set.of(Weather.SUNNY, Weather.RAINY),
                Set.of("Ocean")
        );
        new Fish(
                "Catfish",
                FishRarity.REGULAR,
                Set.of(Season.SUMMER, Season.SPRING, Season.AUTUMN),
                Set.of(new TimeRange(6 * 60, 22 * 60)),
                Set.of(Weather.RAINY),
                Set.of("Forest River", "Pond")
        );
        new Fish(
                "Salmon",
                FishRarity.REGULAR,
                Set.of(Season.AUTUMN),
                Set.of(new TimeRange(6 * 60, 18 * 60)),
                Set.of(Weather.RAINY, Weather.SUNNY),
                Set.of("Forest River")
        );

        //    LEGENDARY FISH
        new Fish(
                "Angler",
                FishRarity.LEGENDARY,
                Set.of(Season.AUTUMN),
                Set.of(new TimeRange(8 * 60, 20 * 60)),
                Set.of(Weather.RAINY, Weather.SUNNY),
                Set.of("Pond")
        );
        new Fish(
                "Crimsonfish",
                FishRarity.LEGENDARY,
                Set.of(Season.SUMMER),
                Set.of(new TimeRange(8 * 60, 20 * 60)),
                Set.of(Weather.RAINY, Weather.SUNNY),
                Set.of("Ocean")
        );
        new Fish(
                "Glacier Fish",
                FishRarity.LEGENDARY,
                Set.of(Season.WINTER),
                Set.of(new TimeRange(8 * 60, 20 * 60)),
                Set.of(Weather.RAINY, Weather.SUNNY),
                Set.of("Forest River")
        );
        new Fish(
                "Legend",
                FishRarity.LEGENDARY,
                Set.of(Season.SPRING),
                Set.of(new TimeRange(8 * 60, 20 * 60)),
                Set.of(Weather.RAINY),
                Set.of("Mountain Lake")
        );
    }
}
