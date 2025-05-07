package com.oop10x.steddyvalley.model;

import com.oop10x.steddyvalley.utils.EventType;
import com.oop10x.steddyvalley.utils.Observable;
import com.oop10x.steddyvalley.utils.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TimeManager implements Observable {
    private final List<Observer> observers;
    private int minutes;
    private final Timer timer;
    private final int SIXAM = 360;
    private static TimeManager tm;

    private TimeManager() {
        observers = new ArrayList<>();
        timer = new Timer();
        minutes = SIXAM;
        tm = this;
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
    public void notifyObservers(EventType type, String message) {
        for (Observer o : observers) {
            o.update(type, message);
        }
    }

    public void start() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                minutes += 5;
                notifyObservers(EventType.TIMETICK, String.valueOf(minutes));
                if ((minutes - SIXAM) % 1440 == 0) {
                    notifyObservers(EventType.NEWDAY, String.valueOf(minutes));
                }
            }
        }, 0, 1000);
    }

    public void reset() {
        minutes = 0;
    }

    public void stop() {
        timer.cancel();
    }

    public Timer getTimer() {
        return timer;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        stop();
        this.minutes = minutes;
        start();
    }

    public static TimeManager getInstance() {
        if (tm == null) {
            tm = new TimeManager();
        }
        return tm;
    }
}
