package com.oop10x.steddyvalley.model;

import com.oop10x.steddyvalley.model.items.Fish;
import com.oop10x.steddyvalley.model.map.Placeable;
import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.model.items.Fish;
import com.oop10x.steddyvalley.utils.Fishable;
import com.oop10x.steddyvalley.utils.Position;
import com.oop10x.steddyvalley.utils.RNG;

import java.util.List;

public class Pond implements Placeable, Fishable {
    private final Position position = new Position(0, 0);
    private final int fishCount = 0;

    public Pond(int x, int y) {
        setX(x);
        setY(y);
    }

    @Override
    public void setX(int x) {
        position.setX(x);
    }

    @Override
    public int getX() {
        return position.getX();
    }

    @Override
    public void setY(int y) {
        position.setY(y);
    }
    public int getY() {
        return position.getY();
    }
    public boolean isFishable() {
        return true;
    }

    @Override
    public List<Fish> getFish() {
        return List.of();
    }

}