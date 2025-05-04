package com.oop10x.steddyvalley.model.items;

import com.oop10x.steddyvalley.utils.Sellable;

import java.util.ArrayList;
import java.util.List;

public class Fish extends Item implements Sellable {
    private List<String> seasonList = new ArrayList<>();
    private List<String> weatherList = new ArrayList<>();
    private List<String> locationList = new ArrayList<>();

    public Fish(String name, int price, List<String> seasonList, List<String> weatherList, List<String> locationList) {
        super(name, price);
        this.seasonList = seasonList;
        this.weatherList = weatherList;
        this.locationList = locationList;
    }

    public boolean isInSeason(String season) {
        return seasonList.contains(season);
    }

    public boolean isInWeather(String weather) {
        return weatherList.contains(weather);
    }

    public boolean isInLocation(String location) {
        return locationList.contains(location);
    }

    public int getSellPrice() {
    }
}
