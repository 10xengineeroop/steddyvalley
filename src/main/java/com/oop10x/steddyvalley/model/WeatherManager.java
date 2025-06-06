package com.oop10x.steddyvalley.model;

import com.oop10x.steddyvalley.utils.EventType;
import com.oop10x.steddyvalley.utils.Observable;
import com.oop10x.steddyvalley.utils.Observer;
import com.oop10x.steddyvalley.utils.Weather;

import java.util.*;

public class WeatherManager implements Observable, Observer {

    private static WeatherManager instance;

    private final List<Observer> observers;
    private final Queue<Weather> weatherSchedule;
    private Weather currentWeather;

    private WeatherManager(TimeManager timeManager) {
        if (timeManager == null) {
            throw new NullPointerException("timeManager");
        }
        weatherSchedule = new LinkedList<>();
        populateWeather();
        observers = new LinkedList<>();
        timeManager.addObserver(this);
    }

    public static WeatherManager getInstance(TimeManager timeManager) {
        if (instance == null) {
            instance = new WeatherManager(timeManager);
        }
        return instance;
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(EventType type, Object message) {
        for (Observer observer : observers) {
            observer.update(type, message);
        }
    }

    @Override
    public void update(EventType eventType, Object message) {
        if (eventType == EventType.NEWDAY) {
            startNewDay();
        }
    }

    public void populateWeather() {
        List<Weather> forecast = new ArrayList<>();
        forecast.add(Weather.RAINY);
        forecast.add(Weather.RAINY);

        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            Weather randomWeather = random.nextBoolean() ? Weather.RAINY : Weather.SUNNY;
            forecast.add(randomWeather);
        }

        Collections.shuffle(forecast);
        weatherSchedule.addAll(forecast);
        currentWeather = weatherSchedule.poll();
    }

    public Weather getCurrentWeather() {
        if (currentWeather == null) {
            startNewDay();
        }
        return currentWeather;
    }

    public void startNewDay() {
        if (weatherSchedule.isEmpty()) {
            populateWeather();
        } else {
            currentWeather = weatherSchedule.poll();
        }
        notifyObservers(EventType.NEWWEATHER, currentWeather.toString());
    }
    public Weather getNextDayWeather() {
        if (weatherSchedule.isEmpty()) {
            populateWeather();
        }
        return weatherSchedule.peek();
    }
}
