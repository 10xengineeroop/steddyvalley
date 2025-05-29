package com.oop10x.steddyvalley.model;

import com.oop10x.steddyvalley.utils.EventType;
import com.oop10x.steddyvalley.utils.Observable;
import com.oop10x.steddyvalley.utils.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class TimeManager implements Observable {

    private static final int SIXAM_MINUTES = 6 * 60;
    private static final int GAME_MINUTES_PER_REAL_SECOND = 5;
    private static final int TICK_INTERVAL_MS = 1000; // 5 game minutes per 1 real second

    private static final TimeManager INSTANCE = new TimeManager();

    private final List<Observer> observers = new ArrayList<>();
    private final AtomicInteger minutes = new AtomicInteger(SIXAM_MINUTES);
    private Timer timer;

    private TimeManager() {
        // Private constructor prevents instantiation
    }

    public static TimeManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void addObserver(Observer o) {
        if (o != null && !observers.contains(o)) {
            observers.add(o);
        }
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(EventType type, Object message) {
        List<Observer> snapshot = new ArrayList<>(observers);
        for (Observer o : snapshot) {
            o.update(type, message);
        }
    }

    public void start() {
        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                minutes.addAndGet(1);
                System.out.println("Time tick: " + minutes.get() + " minutes");
                notifyObservers(EventType.TIMETICK, minutes.get());

                int currentMinutes = minutes.get();
                if (currentMinutes % 1440 == 120) {
                    notifyObservers(EventType.TWO_AM, currentMinutes);
                }
            }
        }, 0, TICK_INTERVAL_MS);

        System.out.println("TimeManager started. Tick interval: " + TICK_INTERVAL_MS + "ms.");
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            System.out.println("TimeManager stopped.");
        }
    }

    public int getMinutes() {
        return minutes.get();
    }

    public void setTimeToSixAM() {
        stop();
        int currentMinutes = minutes.get();
        minutes.set(((currentMinutes - 360 + 1439) / 1440) * 1440 + 360);
        notifyObservers(EventType.NEWDAY, minutes.get());
        start();
    }

    public void setTimeToTenPM() {
        stop();
        int currentMinutes = minutes.get();
        minutes.set(((currentMinutes - 1320 + 1439) / 1440) * 1440 + 1320);
        notifyObservers(EventType.NEWDAY, minutes.get());
        start();
    }

    public void addMinutes(int delta) {
        minutes.addAndGet(delta);
        notifyObservers(EventType.TIMETICK, minutes.get());
    }
}
