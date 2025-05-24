package com.oop10x.steddyvalley.model.map; // Sesuaikan paket

import com.oop10x.steddyvalley.model.Game;
import com.oop10x.steddyvalley.model.GameState;
import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.model.TimeManager;
import com.oop10x.steddyvalley.model.items.Item; // Untuk getEquippedItem
import com.oop10x.steddyvalley.model.items.Seed;
import com.oop10x.steddyvalley.utils.EventType;
import com.oop10x.steddyvalley.utils.Observer;
import com.oop10x.steddyvalley.utils.Position; // Asumsi Position ada di utils

public class Land implements Actionable, Placeable, Observer {
    private final Position position; // Koordinat tile (bukan piksel)
    private LandType landType = LandType.UNTILLED;
    private Integer startPlantTimeMinutes = null; // Waktu tanam dalam menit game
    private Integer endPlantTimeMinutes = null;   // Waktu panen dalam menit game
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
    public Integer getEndPlantTimeMinutes() { return endPlantTimeMinutes; }
    // public void setEndPlantTimeMinutes(Integer endPlantTimeMinutes) { this.endPlantTimeMinutes = endPlantTimeMinutes; }
    // public Integer getStartPlantTimeMinutes() { return startPlantTimeMinutes; }
    // public void setStartPlantTimeMinutes(Integer startPlantTimeMinutes) { this.startPlantTimeMinutes = startPlantTimeMinutes; }
    public Seed getSeed() { return seed; }
    // public void setSeed(Seed seed) { this.seed = seed; }


    // --- Metode Aksi (dipanggil oleh GameController) ---
    public boolean till(Player player) {
        if (landType == LandType.UNTILLED) {
            // Cek apakah pemain punya Hoe dan energi cukup (logika ini bisa di Controller)
            // if (player.getEquippedItem() instanceof HoeTool && player.hasEnoughEnergy(COST_TILL)) {
            setLandType(LandType.TILLED);
            // player.decreaseEnergy(COST_TILL);
            System.out.println("Land at (" + getX() + "," + getY() + ") tilled.");
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
            this.startPlantTimeMinutes = currentTimeMinutes;
            // Asumsi 1 hari = 1440 menit game (24 jam * 60 menit)
            // Jika getDaysToHarvest() adalah hari game, maka perlu konversi ke menit game
            // Jika spesifikasi Anda 1 detik nyata = 5 menit game, 1 hari game (misal 16 jam game = 960 menit game)
            // Untuk sekarang, kita pakai contoh dari kode Anda: 3600 menit per hari (60 jam game?)
            // Ini perlu disesuaikan dengan definisi "hari" di TimeManager Anda
            this.endPlantTimeMinutes = this.startPlantTimeMinutes + (seedToPlant.getDaysToHarvest() * 1440); // Contoh: 1 hari = 1440 menit game
            setLandType(LandType.PLANTED);
            // player.getInventory().removeItem(seedToPlant, 1);
            // player.decreaseEnergy(COST_PLANT);
            System.out.println(seedToPlant.getName() + " planted at (" + getX() + "," + getY() + "). Harvest at: " + endPlantTimeMinutes);
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
            return true;
            // }
        }
        return false;
    }

    public Item harvest(Player player, int currentTimeMinutes) {
        if (landType == LandType.PLANTED && seed != null &&
            currentTimeMinutes >= getEndPlantTimeMinutes()) {
            // Cek energi (logika di Controller)
            // if (player.hasEnoughEnergy(COST_HARVEST)) {
            Item crop = seed.getGrowToCrop();
            // player.getInventory().addItem(crop);
            // player.decreaseEnergy(COST_HARVEST);
            System.out.println(crop.getName() + " harvested from (" + getX() + "," + getY() + ")");
            resetLand(); // Kembali ke tilled atau untilled
            return crop;
            // }
        }
        return null;
    }

    private void resetLand() {
        this.startPlantTimeMinutes = null;
        this.endPlantTimeMinutes = null;
        this.isWatered = false; // Reset status siram setelah panen
        // Kembali ke tilled setelah panen, atau untilled tergantung aturan game Anda
        setLandType(LandType.TILLED);
        this.seed = null;
    }

    private void witherCrop() {
        System.out.println("Crop withered at (" + getX() + "," + getY() + ")");
        resetLand();
        setLandType(LandType.UNTILLED); // Tanaman layu kembali ke tanah biasa
    }

    // --- Implementasi Observer ---
    @Override
    public void update(EventType eventType, Object message) {
        if (eventType == EventType.NEWDAY) {
            // int currentDayMinutes = (Integer) message; // Asumsi message adalah waktu awal hari baru
            if (getLandType() == LandType.PLANTED) {
                if (isWatered) {
                    // Tanaman tumbuh, cek apakah siap panen (sudah ditangani oleh harvest())
                    // Reset status siram untuk hari berikutnya
                    setWatered(false);
                    System.out.println("Land ("+getX()+","+getY()+") is no longer watered (new day).");
                } else {
                    // Tidak disiram, tanaman layu
                    witherCrop();
                }
            } else if (getLandType() == LandType.TILLED) {
                // Tanah yang dicangkul mungkin kembali menjadi UNTILLED jika tidak ditanami dalam sehari? (Aturan game Anda)
                // setWatered(false); // Tanah dicangkul juga kering
            }
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
