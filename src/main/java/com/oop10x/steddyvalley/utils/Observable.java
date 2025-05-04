package com.oop10x.steddyvalley.utils;

public interface Observable {
    public void addObserver(Observer o);
    public void removeObserver(Observer o);
    public void notifyObservers(EventType type, String message);
}
