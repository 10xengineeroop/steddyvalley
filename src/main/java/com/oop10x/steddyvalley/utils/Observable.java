package com.oop10x.steddyvalley.utils;

public interface Observable {
    void addObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers(EventType type, Object message);
}
