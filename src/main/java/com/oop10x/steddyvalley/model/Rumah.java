package com.oop10x.steddyvalley.model;

import com.oop10x.steddyvalley.model.map.Placeable;
import com.oop10x.steddyvalley.utils.Position;

public class Rumah implements Placeable {
    private final Position position = new Position(0,0);

    public Rumah(int x, int y) {
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

    @Override
    public int getY() {
        return position.getY();
    }
}
