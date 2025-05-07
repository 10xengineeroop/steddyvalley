package com.oop10x.steddyvalley.model.items;

import com.oop10x.steddyvalley.model.locations.Location;
import com.oop10x.steddyvalley.utils.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Fish extends Item implements Sellable {
    private int price;
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
}
