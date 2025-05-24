package com.oop10x.steddyvalley.controller;

import com.oop10x.steddyvalley.model.FarmMap;
import com.oop10x.steddyvalley.model.GameState;
import com.oop10x.steddyvalley.model.Player;
import com.oop10x.steddyvalley.model.TimeManager;
import com.oop10x.steddyvalley.model.collision.CollisionChecker;
import com.oop10x.steddyvalley.model.items.Item;
import com.oop10x.steddyvalley.model.items.Seed;
// Impor Tool jika ada (Hoe, WateringCan)
// import com.oop10x.steddyvalley.model.items.HoeTool;
// import com.oop10x.steddyvalley.model.items.WateringCanTool;
import com.oop10x.steddyvalley.model.map.Actionable;
import com.oop10x.steddyvalley.model.map.Land;
import com.oop10x.steddyvalley.model.objects.*;
import java.util.List;

public class GameController implements PlayerInputActions {

    private Player playerModel;
    private GameState gameStateModel;
    private FarmMap farmMapModel;
    private CollisionChecker collisionChecker;
    private TimeManager timeManager; // Untuk mendapatkan waktu saat ini
    private int tileSize;
    private String transitionMessage;

    //house punya
    private List<String> houseActions = List.of("Sleep", "Cook", "Watch TV") ;
    private int selectedHouseActionIndex = 0; 

    private boolean moveUpActive, moveDownActive, moveLeftActive, moveRightActive;

    public GameController(Player player, GameState gameState, FarmMap farmMap,
                          CollisionChecker cc, TimeManager tm, int tileSize) {
        this.playerModel = player;
        this.gameStateModel = gameState;
        this.farmMapModel = farmMap;
        this.collisionChecker = cc;
        this.timeManager = tm;
        this.tileSize = tileSize;
    }

    @Override 
    public void setMoveUp(boolean active) { 
        if (active) {
            if (gameStateModel.getCurrentState() == GameState.HOUSE_STATE) {
                if (!houseActions.isEmpty()) {
                    selectedHouseActionIndex-- ;
                    if (selectedHouseActionIndex < 0) {
                        selectedHouseActionIndex = houseActions.size() - 1; // Loop ke bawah
                    }
                }
            }

        }
        if (gameStateModel.isPlaying()) {
            this.moveUpActive = active; 
        } 
        if (!active && gameStateModel.isPlaying()) {
            this.moveUpActive = false; 
        }
    }
    @Override 
    public void setMoveDown(boolean active) {
        if (active) {
            if (gameStateModel.getCurrentState() == GameState.HOUSE_STATE) {
                if (!houseActions.isEmpty()) {
                    selectedHouseActionIndex++ ;
                    if (selectedHouseActionIndex >= houseActions.size()) {
                        selectedHouseActionIndex = 0; // Loop ke atas
                    }
                }
            }
        }
        if (gameStateModel.isPlaying()) {
            this.moveDownActive = active; 
        }
        if (!active && gameStateModel.isPlaying()) {
            this.moveDownActive = false; 
        }
    }
    @Override public void setMoveLeft(boolean active) { this.moveLeftActive = active; }
    @Override public void setMoveRight(boolean active) { this.moveRightActive = active; }

    @Override
    public void togglePause() { // Tombol Escape
    int currentState = gameStateModel.getCurrentState();
    if (currentState == GameState.PLAY_STATE) {
        gameStateModel.setCurrentState(GameState.PAUSE_STATE);
    } else if (currentState == GameState.PAUSE_STATE) {
        gameStateModel.setCurrentState(GameState.PLAY_STATE);
    } else if (currentState == GameState.INVENTORY_STATE) {
        gameStateModel.setCurrentState(GameState.PLAY_STATE); // Keluar dari inventory
    } else if (currentState == GameState.HOUSE_STATE) {
        gameStateModel.setCurrentState(GameState.PLAY_STATE); // Keluar dari menu rumah
    } else if (currentState == GameState.SLEEP_STATE) {
        gameStateModel.setCurrentState(GameState.HOUSE_STATE); // Keluar dari transisi tidur
        transitionMessage = ""; // Bersihkan pesan
    }
}

    @Override
    public void toggleInventory() {
        if (gameStateModel.isPlaying()) gameStateModel.setCurrentState(GameState.INVENTORY_STATE);
        else if (gameStateModel.isInInventory()) gameStateModel.setCurrentState(GameState.PLAY_STATE);
    }

    @Override
    public void performPrimaryAction() {
        int currentState = gameStateModel.getCurrentState(); // Ambil state saat ini sekali

        // 1. Tangani aksi spesifik untuk HOUSE_STATE terlebih dahulu
        if (currentState == GameState.HOUSE_STATE) {
            if (!houseActions.isEmpty() && selectedHouseActionIndex >= 0 && selectedHouseActionIndex < houseActions.size()) {
                String selectedAction = houseActions.get(selectedHouseActionIndex);
                System.out.println("Player selected action in house: " + selectedAction);
                handleHouseAction(selectedAction); // Ini akan memanggil setCurrentState untuk SLEEP_STATE, COOK, dll.
            }
            // Setelah aksi di menu rumah ditangani (atau tidak ada aksi valid),
            // kita hentikan proses untuk tombol 'E' ini.
            return;
        }

        // 2. Tangani aksi untuk keluar dari SLEEP_STATE jika 'E' juga berfungsi sebagai tombol "lanjut"
        // (Tombol 'Escape' Anda di togglePause() sudah menangani ini, jadi ini opsional untuk 'E')
        if (currentState == GameState.SLEEP_STATE) {
            // Anda mungkin ingin kembali ke PLAY_STATE atau HOUSE_STATE
            gameStateModel.setCurrentState(GameState.PLAY_STATE); // atau GameState.HOUSE_STATE
            transitionMessage = ""; // Bersihkan pesan transisi
            System.out.println("Exiting sleep state via E press (Primary Action)");
            return;
        }

        // 3. Logika asli untuk PLAY_STATE (berinteraksi dengan dunia, objek, tanah)
        // Pastikan ini hanya berjalan jika benar-benar dalam PLAY_STATE
        if (currentState == GameState.PLAY_STATE) {
            int playerPixelX = playerModel.getPosition().getX();
            int playerPixelY = playerModel.getPosition().getY();
            int playerTileX = playerPixelX / tileSize;
            int playerTileY = playerPixelY / tileSize;

            // A. Interaksi dengan DeployedObject yang berdekatan (atau dihadapi)
            // PERHATIAN: Apakah farmMapModel.getAdjacentInteractableDeployedObject() sudah
            // memperhitungkan ARAH HADAP pemain? Jika belum, interaksi tidak akan sesuai
            // dengan objek yang dihadapi pemain. Anda mungkin perlu menggunakan logika targetTileX/Y
            // berdasarkan playerModel.getDirection() seperti diskusi kita sebelumnya.
            DeployedObject adjacentObject = farmMapModel.getAdjacentInteractableDeployedObject(playerTileX, playerTileY);

            if (adjacentObject != null) { // Tidak perlu cek instanceof Actionable di sini jika getAdjacent... sudah memfilter
                System.out.println("DEBUG GC: Player attempting to interact with DeployedObject: " + adjacentObject.getObjectName());
                if (adjacentObject instanceof HouseObject) {
                    gameStateModel.setCurrentState(GameState.HOUSE_STATE);
                    selectedHouseActionIndex = 0; // Reset pilihan menu rumah
                    return; // Masuk rumah, selesai.
                } 
                else if (adjacentObject instanceof Actionable) {
                    // Untuk objek lain seperti ShippingBin, Pond, dll.
                    ((Actionable) adjacentObject).onPlayerAction(playerModel);
                    // Tambahkan logika energi/waktu jika perlu
                    return; // Interaksi dengan objek selesai.
                }
            }

            // B. Jika tidak ada interaksi objek, cek interaksi dengan Land
            Land currentLand = farmMapModel.getLandAt(playerTileX, playerTileY);
            if (currentLand != null) {
                Item equippedItem = playerModel.getEquippedItem();
                boolean actionTaken = false;

                if (equippedItem != null) {
                    if ("Hoe".equals(equippedItem.getName())) {
                        if (currentLand.till(playerModel)) actionTaken = true;
                    } else if (equippedItem instanceof Seed) {
                        if (currentLand.plant((Seed) equippedItem, playerModel, timeManager.getMinutes())) actionTaken = true;
                    } else if ("Watering Can".equals(equippedItem.getName())) {
                        if (currentLand.water(playerModel)) actionTaken = true;
                    }
                }
                if (!actionTaken) { // Jika belum ada aksi dari item, coba panen
                    Item harvestedCrop = currentLand.harvest(playerModel, timeManager.getMinutes());
                    if (harvestedCrop != null) {
                        // playerModel.getInventory().addItem(harvestedCrop);
                        actionTaken = true;
                    }
                }

                if (actionTaken) {
                    System.out.println("Action performed on Land at: " + playerTileX + "," + playerTileY);
                }
                // Interaksi Land selesai (atau tidak ada aksi), bisa return atau lanjut jika ada logika lain.
                return;
            }
            // Tidak ada interaksi objek atau Land yang terjadi.
        }
        return ;
    }

    public void updateGameLogic() {
        if (!gameStateModel.isPlaying()) return;

        int currentX = playerModel.getPosition().getX();
        int currentY = playerModel.getPosition().getY();
        int playerSpeed = playerModel.getSpeed();
        int dx = 0;
        int dy = 0;

        if (moveUpActive) dy = -playerSpeed;
        if (moveDownActive) dy = playerSpeed;
        if (moveLeftActive) dx = -playerSpeed;
        if (moveRightActive) dx = playerSpeed;

        if (dx != 0 || dy != 0) {
            int nextX = currentX;
            int nextY = currentY;

            // Cek dan terapkan pergerakan horizontal
            if (dx != 0) {
                if (!collisionChecker.willCollide(currentX, currentY, dx, 0)) {
                    nextX += dx;
                }
            }
            // Cek dan terapkan pergerakan vertikal, DARI POSISI X YANG MUNGKIN SUDAH BERUBAH (nextX)
            // dan currentY (karena kita cek tabrakan vertikal dari posisi Y saat ini)
            if (dy != 0) {
                if (!collisionChecker.willCollide(nextX, currentY, 0, dy)) { // Cek dari nextX, currentY
                    nextY += dy;
                }
            }
            
            if (nextX != currentX || nextY != currentY) {
                playerModel.setPosition(nextX, nextY);
            }
        }
    }
    private void handleHouseAction(String action) {
        switch (action) {
            case "Sleep":
                int energyBeforeSleep = playerModel.getEnergy();
                int maxEnergy = 100 ;
                int energyRestored;
            if (energyBeforeSleep < maxEnergy * 0.1) { // Jika energi < 10%
                playerModel.setEnergy(maxEnergy / 2); // Hanya pulih setengah
                energyRestored = (maxEnergy / 2) - energyBeforeSleep;
            } else {
                playerModel.setEnergy(maxEnergy); // Pulih penuh
                energyRestored = maxEnergy - energyBeforeSleep;
            }
            // Pastikan energi tidak melebihi maxEnergy jika ada logika penambahan
            if (playerModel.getEnergy() > maxEnergy) playerModel.setEnergy(maxEnergy);


            // Logika memajukan waktu ke pagi berikutnya (perlu implementasi di TimeManager)
            // timeManager.skipToNextMorning(); // Ini akan memicu NEWDAY
            System.out.println("Player is sleeping... (Time skipping logic TODO)");

            transitionMessage = "You slept well. Energy +" + Math.max(0, energyRestored) + ".";
            gameStateModel.setCurrentState(GameState.SLEEP_STATE);
            break;
            case "Cook":
                // Logika memasak, misalnya buka UI memasak
                System.out.println("Opening cooking interface...");
                // gameStateModel.setCurrentState(GameState.COOKING_STATE); // Jika ada state khusus
                break;
            case "Watch TV":
                // Logika menonton TV, misalnya buka UI TV
                System.out.println("Opening TV interface...");
                // gameStateModel.setCurrentState(GameState.TV_STATE); // Jika ada state khusus
                break;
            default:
                System.out.println("Unknown house action: " + action);
        }
    }
    public List<String> getHouseActions() {
        return houseActions;
    }
    public int getSelectedHouseActionIndex() {
        return selectedHouseActionIndex;
    }
    public String getTransitionMessage() {
        return transitionMessage;
    }
}
