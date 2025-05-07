package com.oop10x.steddyvalley.model;

import com.oop10x.steddyvalley.model.items.Item;
import com.oop10x.steddyvalley.model.items.Seed;
import com.oop10x.steddyvalley.utils.*;
import com.oop10x.steddyvalley.utils.Observer;

import java.util.*;

public class FarmMap {
    private final List<Placeable> placeable = new ArrayList<>(1024);
    public FarmMap(Player p) {
        placeHouseAndBin();
        placePond();
    }

    private void placeHouseAndBin() {
        placeHouse();
        placeShippingBin();
    }

    private void placeHouse() {
        // TODO kode place house
    }

    private void placeShippingBin() {
        // TODO kode place shipping bin
    }

    private void placePond() {
        // TODO kode place pond
    }


}

class Land implements Actionable, Placeable, Observer {
    private final Position position;
    private LandType landType = LandType.UNTILLED;
    private Integer startPlantTime = null;
    private Integer endPlantTime = null;
    private boolean isWatered = false;
    private Seed seed = null;

    public Land(int x, int y) {
        position = new Position(x, y);
    }

    @Override
    public void onPlayerAction(Player player) {
        if (player.getEquippedItem().getName().equals("Watering Can")) {
            setWatered(true);
            return;
        }

        if (getLandType().equals(LandType.UNTILLED)) {
            if (player.getEquippedItem().getName().equals("Hoe")) {
                setLandType(LandType.TILLED);
            }
        } else if (getLandType().equals(LandType.TILLED)) {
            if (player.getEquippedItem() instanceof Seed) {
                plant((Seed) player.getEquippedItem());
            }
        } else if (getLandType().equals(LandType.PLANTED)) {
            if (player.getCurrentTime() >= getEndPlantTime()) {
                player.addItem(getSeed().getGrowToCrop());
                harvest();
            }
        }
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

    public LandType getLandType() {
        return landType;
    }

    public void setLandType(LandType landType) {
        this.landType = landType;
    }

    public boolean getIsWatered() {
        return isWatered;
    }

    public void setWatered(boolean watered) {
        isWatered = watered;
    }

    public Integer getEndPlantTime() {
        return endPlantTime;
    }

    public void setEndPlantTime(Integer endPlantTime) {
        this.endPlantTime = endPlantTime;
    }

    public int getStartPlantTime() {
        return startPlantTime;
    }
    public void setStartPlantTime(Integer startPlantTime) {
        this.startPlantTime = startPlantTime;
    }

    public Seed getSeed() {
        return seed;
    }

    public void setSeed(Seed seed) {
        this.seed = seed;
    }
    private void plant(Seed s) {
        setSeed(s);
        setStartPlantTime(TimeManager.getInstance().getMinutes());
        setEndPlantTime(getStartPlantTime() + (s.getDaysToHarvest() * 3600));
        setLandType(LandType.PLANTED);
    }
    private void witherCrop() {
        setStartPlantTime(null);
        setEndPlantTime(null);
        setLandType(LandType.UNTILLED);
        setSeed(null);
    }

    private void harvest() {
        witherCrop();
    }

    @Override
    public void update(EventType eventType, Object message) {
        if (eventType.equals(EventType.NEWDAY)) {
            if (getLandType().equals(LandType.PLANTED) && !isWatered) {
                witherCrop();
            }
        } else if (eventType.equals(EventType.NEWWEATHER)) {
            // TODO kalo new weather
        } else if (eventType.equals(EventType.NEWSEASON)) {
            // TODO kalo new season
        }
    }
}

enum LandType {
    UNTILLED, TILLED, PLANTED
}
