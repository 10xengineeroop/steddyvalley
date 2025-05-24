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
    private final List<Observer> observers;
    private final AtomicInteger minutes = new AtomicInteger();
    private Timer timer; // Tidak final agar bisa di-recreate jika perlu
    private final int SIXAM_MINUTES = 6 * 60; // 6 AM = 360 menit dari tengah malam
    private final int GAME_MINUTES_PER_REAL_SECOND = 5; // 1 detik nyata = 5 menit game
    private final int REAL_MILLISECONDS_PER_TICK = 1000 / GAME_MINUTES_PER_REAL_SECOND; // 1000ms / 5 = 200ms per 1 menit game
                                                                                      // Atau jika 1 detik nyata = 5 menit game, maka 1000ms untuk 5 menit game
                                                                                      // Jadi 1 menit game = 1000ms / 5 = 200ms.
                                                                                      // Jika ingin 1 menit game per tick:
    private final int TICK_INTERVAL_MS = 200; // Setiap 200ms nyata, 1 menit game berlalu
                                              // Jika ingin 5 menit game per tick 1000ms:
    // private final int TICK_INTERVAL_MS = 1000; // Setiap 1 detik nyata, 5 menit game berlalu


    // Hapus static instance tm
    // private static TimeManager tm;

    // Constructor sekarang publik
    public TimeManager() {
        observers = new ArrayList<>();
        // timer = new Timer(true); // Buat sebagai daemon thread
        minutes.set(SIXAM_MINUTES); // Mulai dari jam 6 pagi
        // tm = this; // Hapus
    }

    @Override
    public void addObserver(Observer o) {
        if (o != null && !observers.contains(o)) observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(EventType type, Object message) {
        // Buat salinan untuk menghindari ConcurrentModificationException
        List<Observer> observersCopy = new ArrayList<>(observers);
        for (Observer o : observersCopy) {
            o.update(type, message);
        }
    }

    public void start() {
        if (timer != null) { // Hentikan timer lama jika ada
            timer.cancel();
        }
        timer = new Timer(true); // Buat timer baru sebagai daemon thread
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Jika ingin 5 menit game per tick interval:
                // minutes.addAndGet(5);
                // Jika ingin 1 menit game per tick interval:
                minutes.addAndGet(1);

                notifyObservers(EventType.TIMETICK, minutes.get()); // Kirim Integer

                // Cek apakah hari baru (setiap 24 jam game = 1440 menit game)
                // (minutes.get() - SIXAM_MINUTES) memastikan kita cek relatif terhadap awal hari pertama
                if ((minutes.get() - SIXAM_MINUTES) > 0 && (minutes.get() - SIXAM_MINUTES) % (24 * 60) == 0) {
                    System.out.println("NEW DAY Event Fired at minutes: " + minutes.get());
                    notifyObservers(EventType.NEWDAY, minutes.get()); // Kirim Integer
                }
            }
        }, 0, TICK_INTERVAL_MS); // Tick setiap TICK_INTERVAL_MS
        System.out.println("TimeManager started. Tick interval: " + TICK_INTERVAL_MS + "ms.");
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null; // Set null agar bisa di-start lagi
            System.out.println("TimeManager stopped.");
        }
    }

    public int getMinutes() {
        return minutes.get();
    }

    // Hapus getInstance(), tidak lagi Singleton
    // public static TimeManager getInstance() { ... }
}
