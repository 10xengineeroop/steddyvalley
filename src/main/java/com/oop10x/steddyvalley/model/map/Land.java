package com.oop10x.steddyvalley.model.map; // Sesuaikan paket

import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.model.TimeManager;
import com.oop10x.steddyvalley.model.WeatherManager;
import com.oop10x.steddyvalley.model.items.Crop;
import com.oop10x.steddyvalley.model.items.Item; // Untuk getEquippedItem
import com.oop10x.steddyvalley.model.items.Seed;
import com.oop10x.steddyvalley.utils.EventType;
import com.oop10x.steddyvalley.utils.Observer;
import com.oop10x.steddyvalley.utils.Position; // Asumsi Position ada di utils
import com.oop10x.steddyvalley.utils.Weather;

public class Land implements Actionable, Placeable, Observer {
    private final Position position; // Koordinat tile (bukan piksel)
    private LandType landType = LandType.UNTILLED;
    private Integer startPlantDate = null; // Waktu tanam dalam menit game
    private Integer endPlantDate = null;   // Waktu panen dalam menit game
    private boolean isWatered = false;
    private Seed seed = null;
    // private TimeManager timeManager; // Tidak perlu disimpan jika waktu di-pass saat aksi

    public Land(int tileX, int tileY, TimeManager timeManager) { // Terima TimeManager untuk mendaftar
        this.position = new Position(tileX, tileY);
        if (timeManager != null) {
            timeManager.addObserver(this); // Daftarkan diri ke TimeManager
        } else {
            System.err.println("Warning: TimeManager is null for Land at (" + tileX + "," + tileY + "). NEWDAY events won't be received.");
        }
    }

    // --- Implementasi Placeable ---
    @Override public void setX(int x) { position.setX(x); }
    @Override public int getX() { return position.getX(); }
    @Override public void setY(int y) { position.setY(y); }
    @Override public int getY() { return position.getY(); }

    // --- Getters dan Setters untuk State Land ---
    public LandType getLandType() { return landType; }
    public void setLandType(LandType landType) { this.landType = landType; }
    public boolean getIsWatered() { return isWatered; }
    public void setWatered(boolean watered) { isWatered = watered; }
    public Integer getEndPlantDate() { return endPlantDate; }
    // public void setEndPlantTimeMinutes(Integer endPlantTimeMinutes) { this.endPlantTimeMinutes = endPlantTimeMinutes; }
    // public Integer getStartPlantTimeMinutes() { return startPlantTimeMinutes; }
    // public void setStartPlantTimeMinutes(Integer startPlantTimeMinutes) { this.startPlantTimeMinutes = startPlantTimeMinutes; }
    public Seed getSeed() { return seed; }
    // public void setSeed(Seed seed) { this.seed = seed; }

    public void checkAndSetWatered() {
        Weather currentWeather = WeatherManager.getInstance(TimeManager.getInstance()).getCurrentWeather();
        if (currentWeather == Weather.RAINY) {
            setWatered(true);
        }
    }
    // --- Metode Aksi (dipanggil oleh GameController) ---
    public boolean till(Player player) {
        if (landType == LandType.UNTILLED) {
            // Cek apakah pemain punya Hoe dan energi cukup (logika ini bisa di Controller)
            // if (player.getEquippedItem() instanceof HoeTool && player.hasEnoughEnergy(COST_TILL)) {
            setLandType(LandType.TILLED);
            // player.decreaseEnergy(COST_TILL);
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
            // Cek apakah pemain punya benih tersebut dan energi cukup (logika ini bisa di Controller)
            // if (player.getInventory().hasItem(seedToPlant) && player.hasEnoughEnergy(COST_PLANT)) {
            this.seed = seedToPlant;
            this.startPlantDate = (currentTimeMinutes / 1440) + 1;
            // Asumsi 1 hari = 1440 menit game (24 jam * 60 menit)
            // Jika getDaysToHarvest() adalah hari game, maka perlu konversi ke menit game
            // Jika spesifikasi Anda 1 detik nyata = 5 menit game, 1 hari game (misal 16 jam game = 960 menit game)
            // Untuk sekarang, kita pakai contoh dari kode Anda: 3600 menit per hari (60 jam game?)
            // Ini perlu disesuaikan dengan definisi "hari" di TimeManager Anda
            this.endPlantDate = this.startPlantDate + seedToPlant.getDaysToHarvest(); // Contoh: 1 hari = 1440 menit game
            setLandType(LandType.PLANTED);
            // player.getInventory().removeItem(seedToPlant, 1);
            // player.decreaseEnergy(COST_PLANT);
            player.getInventory().removeItem(seedToPlant.getName(), 1);
            checkAndSetWatered();
            player.setEnergy(player.getEnergy() - 5);
            TimeManager.getInstance().addMinutes(5);
            return true;
            // }
        }
        return false;
    }

    public boolean water(Player player) {
        if (landType == LandType.PLANTED || landType == LandType.TILLED) {
            // Cek apakah pemain punya WateringCan dan energi (logika di Controller)
            // if (player.getEquippedItem() instanceof WateringCanTool && player.hasEnoughEnergy(COST_WATER)) {
            setWatered(true);
            // player.decreaseEnergy(COST_WATER);
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
            resetLand();
            return true;
        }
        else {
            return false; // Tidak bisa panen jika tidak ada tanaman atau belum waktunya
        }
    }

    public void resetLand() {
        this.startPlantDate = null;
        this.endPlantDate = null;
        this.isWatered = false; // Reset status siram setelah panen
        // Kembali ke tilled setelah panen, atau untilled tergantung aturan game Anda
        setLandType(LandType.UNTILLED);
        checkAndSetWatered();
        this.seed = null;
    }

    private void witherCrop() {
        System.out.println("Crop withered at (" + getX() + "," + getY() + ")");
        resetLand();
    }

    // --- Implementasi Observer ---
    @Override
    public void update(EventType eventType, Object message) {
        if (eventType == EventType.NEWDAY) {
            checkAndSetWatered();
            if (getLandType() == LandType.PLANTED) {
                if (isWatered) {
                    setLandType(LandType.HARVESTABLE);
                    System.out.println("Land ("+getX()+","+getY()+") is now harvestable.");
                    setWatered(false);
                    System.out.println("Land ("+getX()+","+getY()+") is no longer watered (new day).");
                } else {
                    witherCrop();
                }
            } else if (getLandType() == LandType.TILLED || getLandType() == LandType.UNTILLED) {
                // Reset tanah yang tidak ditanami atau sudah ditanami tapi tidak disiram
                if (isWatered) {
                    setWatered(false);
                }
            }
            checkAndSetWatered();
        }
        // Implementasi untuk NEWWEATHER, NEWSEASON jika diperlukan
        
    }
    public void onPlayerAction(Player player) {
            Item equippedItem = player.getEquippedItem();
            if (equippedItem == null) {
                System.out.println("Player has no equipped item.");
            }
    }
}
