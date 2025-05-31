package com.oop10x.steddyvalley.model.map; // Sesuaikan paket

import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.model.SeasonManager;
import com.oop10x.steddyvalley.model.TimeManager;
import com.oop10x.steddyvalley.model.WeatherManager;
import com.oop10x.steddyvalley.model.items.Crop;
import com.oop10x.steddyvalley.model.items.Item; // Untuk getEquippedItem
import com.oop10x.steddyvalley.model.items.Seed;
import com.oop10x.steddyvalley.utils.EventType;
import com.oop10x.steddyvalley.utils.Observer;
import com.oop10x.steddyvalley.utils.Position;
import com.oop10x.steddyvalley.utils.Season;
import com.oop10x.steddyvalley.utils.Weather;

public class Land implements Actionable, Placeable, Observer {
    private final Position position; 
    private LandType landType = LandType.UNTILLED;
    private Integer startPlantDate = null; // Waktu tanam dalam menit game
    private Integer endPlantDate = null;   // Waktu panen dalam menit game
    private boolean isWatered = false;
    private Seed seed = null;

    public Land(int tileX, int tileY, TimeManager timeManager) { 
        this.position = new Position(tileX, tileY);
        if (timeManager != null) {
            timeManager.addObserver(this); 
        } else {
        }
    }

    // --- Implementasi Placeable ---
    @Override public void setX(int x) { position.setX(x); }
    @Override public int getX() { return position.getX(); }
    @Override public void setY(int y) { position.setY(y); }
    @Override public int getY() { return position.getY(); }


    public LandType getLandType() { return landType; }
    public void setLandType(LandType landType) { this.landType = landType; }
    public boolean getIsWatered() { return isWatered; }
    public void setWatered(boolean watered) { isWatered = watered; }
    public Integer getEndPlantDate() { return endPlantDate; }

    public Seed getSeed() { return seed; }


    public void checkAndSetWatered() {
        Weather currentWeather = WeatherManager.getInstance(TimeManager.getInstance()).getCurrentWeather();
        if (currentWeather == Weather.RAINY) {
            setWatered(true);
        }
    }

    public boolean till(Player player) {
        if (landType == LandType.UNTILLED) {

            setLandType(LandType.TILLED);
            System.out.println("Land at (" + getX() + "," + getY() + ") tilled.");
            player.setEnergy(player.getEnergy() - 5);
            TimeManager.getInstance().addMinutes(5);
            checkAndSetWatered();
            return true;
            // }
        }
        return false;
    }

    public boolean plant(Seed seedToPlant, Player player, int currentTimeMinutes) {
        if (landType == LandType.TILLED && seedToPlant != null) {

            this.seed = seedToPlant;
            this.startPlantDate = (currentTimeMinutes / 1440) + 1;

            this.endPlantDate = this.startPlantDate + seedToPlant.getDaysToHarvest(); 
            setLandType(LandType.PLANTED);

            player.getInventory().removeItem(seedToPlant.getName(), 1);
            checkAndSetWatered();
            player.setEnergy(player.getEnergy() - 5);
            TimeManager.getInstance().addMinutes(5);
            return true;
            
        }
        return false;
    }

    public boolean water(Player player) {
        if ((landType == LandType.PLANTED || landType == LandType.TILLED) && !isWatered) {

            setWatered(true);

            System.out.println("Land at (" + getX() + "," + getY() + ") watered.");
            player.setEnergy(player.getEnergy() - 5);
            TimeManager.getInstance().addMinutes(5);
            return true;
            // }
        }
        return false;
    }

    public boolean harvest(Player player, int currentTimeMinutes) {
        if ((landType == LandType.HARVESTABLE)) {
            System.out.println("Current time: " + (currentTimeMinutes / 1440) + 1 + ", End plant date: " + getEndPlantDate());
            Crop crop = seed.getGrowToCrop();
            int amount = seed.getHarvestAmount();
            player.getInventory().addItem(crop, amount);
            player.setEnergy(player.getEnergy() - 5);
            TimeManager.getInstance().addMinutes(5);
            Player.setTotalCropsHarvested(Player.getTotalCropsHarvested() + amount);
            resetLand();
            return true;
        }
        else {
            return false; 
        }
    }

    public void resetLand() {
        this.startPlantDate = null;
        this.endPlantDate = null;
        this.seed = null;
        setLandType(LandType.UNTILLED);
        checkAndSetWatered();

    }

    private void witherCrop() {
        System.out.println("Crop withered at (" + getX() + "," + getY() + ")");
        this.startPlantDate = null;
        this.endPlantDate = null;
        this.seed = null;
        setLandType(LandType.TILLED);
        checkAndSetWatered();
    }

    // --- Implementasi Observer ---
    @Override
    public void update(EventType eventType, Object message) {
        if (eventType == EventType.NEWDAY) {
            checkAndSetWatered();
            Season currentSeason = SeasonManager.getInstance(TimeManager.getInstance()).getCurrentSeason();
            if (currentSeason == Season.WINTER && (getLandType() == LandType.PLANTED) || (getLandType() == LandType.HARVESTABLE)) {
                witherCrop(); 
                return;
            }
            if (getLandType() == LandType.UNTILLED) {
                return; // Tidak ada yang perlu dilakukan jika tanah belum diolah
            }
            if (getLandType() == LandType.TILLED) {
                if (isWatered) {
                    setWatered(false);
                    return;
                }
            }
            if (getLandType() == LandType.PLANTED) {
                if (isWatered) {
                    int currentDay = (TimeManager.getInstance().getMinutes() / 1440) + 1;
                    if (currentDay >= endPlantDate) {
                        setLandType(LandType.HARVESTABLE);
                    }
                } else {
                    witherCrop();
                }
            }
            checkAndSetWatered();        
        }
    }
    public void onPlayerAction(Player player) {
            Item equippedItem = player.getEquippedItem();
            if (equippedItem == null) {
                System.out.println("Player has no equipped item.");
            }
    }
}
