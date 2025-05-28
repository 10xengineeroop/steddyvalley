package com.oop10x.steddyvalley.model;

import com.oop10x.steddyvalley.utils.EventType;
import com.oop10x.steddyvalley.utils.Observable;
import com.oop10x.steddyvalley.utils.Observer;
import com.oop10x.steddyvalley.utils.Season;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SeasonManager implements Observer, Observable {
    private static SeasonManager instance;

    private final Queue<Season> seasons;
    private Season currentSeason;
    private final List<Observer> observers;
    private Integer daysUntilNextSeason;

    private SeasonManager(TimeManager timeManager) {
        seasons = new LinkedList<Season>();
        populateSeasons();
        observers = new ArrayList<Observer>();
        daysUntilNextSeason = 10;
        timeManager.addObserver(this);
    }

    public static SeasonManager getInstance(TimeManager timeManager) {
        if (instance == null) {
            instance = new SeasonManager(timeManager);
        }
        return instance;
    }

    public void populateSeasons() {
        currentSeason = Season.SPRING;
        seasons.add(Season.SUMMER);
        seasons.add(Season.AUTUMN);
        seasons.add(Season.WINTER);
        seasons.add(Season.SPRING);
    }

    public Season getCurrentSeason() {
        return currentSeason;
    }

    public void nextSeason() {
        currentSeason = seasons.poll();
        seasons.add(currentSeason);
        daysUntilNextSeason = 10;
        notifyObservers(EventType.NEWSEASON, getCurrentSeason().toString());
    }

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(EventType type, Object message) {
        observers.forEach(observer -> observer.update(type, message));
    }

    @Override
    public void update(EventType eventType, Object message) {
        if (eventType.equals(EventType.NEWDAY)) {
            handleNewDay();
        }
    }

    private void handleNewDay() {
        daysUntilNextSeason--;
        if (daysUntilNextSeason == 0) {
            nextSeason();
        }
    }
}
