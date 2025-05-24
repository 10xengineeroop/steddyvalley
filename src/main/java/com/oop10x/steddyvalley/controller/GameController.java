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
    public void togglePause() {
        if (gameStateModel.isPlaying()) gameStateModel.setCurrentState(GameState.PAUSE_STATE);
        else if (gameStateModel.isPaused()) gameStateModel.setCurrentState(GameState.PLAY_STATE);
        else if (gameStateModel.isInInventory()) gameStateModel.setCurrentState(GameState.PLAY_STATE); // Esc dari inventory
    }

    @Override
    public void toggleInventory() {
        if (gameStateModel.isPlaying()) gameStateModel.setCurrentState(GameState.INVENTORY_STATE);
        else if (gameStateModel.isInInventory()) gameStateModel.setCurrentState(GameState.PLAY_STATE);
    }

    @Override
    public void performPrimaryAction() {
        if (gameStateModel.isInHouse()) {
            gameStateModel.setCurrentState(GameState.PLAY_STATE);
            return;
        }
        if (!gameStateModel.isPlaying()) return;

        int playerPixelX = playerModel.getPosition().getX();
        int playerPixelY = playerModel.getPosition().getY();
        int playerTileX = playerPixelX / tileSize;
        int playerTileY = playerPixelY / tileSize;
        // String playerDirection = playerModel.getDirection(); // Jika Player punya arah hadap

        DeployedObject adjacentObject = farmMapModel.getAdjacentInteractableDeployedObject(playerTileX, playerTileY);

        if (adjacentObject != null && adjacentObject instanceof Actionable) {
            System.out.println("DEBUG GC: Player attempting to interact with adjacent DeployedObject: " + adjacentObject.getObjectName());
            if (adjacentObject instanceof HouseObject) {
                gameStateModel.setCurrentState(GameState.HOUSE_STATE);
                selectedHouseActionIndex = 0;
                //updateCurrentHouseActionView() ;
                return;
            }
            else if (adjacentObject instanceof Actionable){
                ((Actionable) adjacentObject).onPlayerAction(playerModel);
            }
            // Di sini Anda bisa menambahkan logika pengurangan energi atau pemajuan waktu jika interaksi berhasil
            // playerModel.setEnergy(playerModel.getEnergy() - COST_INTERACT_HOUSE);
            // timeManager.advanceTime(MINUTES_INTERACT_HOUSE);
            return; // Interaksi dengan DeployedObject selesai, tidak perlu cek Land di bawah.
        } 
        if (gameStateModel.getCurrentState() == GameState.HOUSE_STATE) {
            if (!houseActions.isEmpty() && selectedHouseActionIndex >= 0 && selectedHouseActionIndex < houseActions.size()) {
                String selectedAction = houseActions.get(selectedHouseActionIndex);
                System.out.println("Player selected action in house: " + selectedAction);
                handleHouseAction(selectedAction); // Metode baru untuk menangani aksi rumah
            }
            return; // Setelah aksi di rumah, jangan proses aksi lain
        }

        // 2. Cek interaksi dengan Land di bawah pemain
        Land currentLand = farmMapModel.getLandAt(playerTileX, playerTileY);
        if (currentLand != null) {
            Item equippedItem = playerModel.getEquippedItem(); // Asumsi Player punya metode ini
            boolean actionTaken = false;

            if (equippedItem != null) { // Pastikan ada item yang dipegang
                // Contoh sederhana, Anda perlu kelas Tool atau cara lain untuk identifikasi
                if ("Hoe".equals(equippedItem.getName())) { // Ganti dengan instanceof HoeTool jika ada
                    if (currentLand.till(playerModel)) { // Metode till di Land sekarang mengembalikan boolean
                        // playerModel.useEnergy(5); // Energi dikurangi di Controller
                        actionTaken = true;
                    }
                } else if (equippedItem instanceof Seed) {
                    if (currentLand.plant((Seed) equippedItem, playerModel, timeManager.getMinutes())) {
                        // playerModel.getInventory().removeItem(equippedItem, 1);
                        // playerModel.useEnergy(2);
                        actionTaken = true;
                    }
                } else if ("Watering Can".equals(equippedItem.getName())) { // Ganti dengan instanceof
                    if (currentLand.water(playerModel)) {
                        // playerModel.useEnergy(1);
                        actionTaken = true;
                    }
                }
            }
            // Interaksi panen (mungkin tidak perlu item khusus)
            if (!actionTaken) { // Jika belum ada aksi dari item, coba panen
                Item harvestedCrop = currentLand.harvest(playerModel, timeManager.getMinutes());
                if (harvestedCrop != null) {
                    // playerModel.getInventory().addItem(harvestedCrop);
                    // playerModel.useEnergy(0); // Panen mungkin tidak butuh energi
                    actionTaken = true;
                }
            }

            if (actionTaken) {
                // playerModel.notifyObservers(); // Jika aksi mengubah state player yang perlu di-render ulang
                System.out.println("Action performed on Land at: " + playerTileX + "," + playerTileY);
            }
        }
        // TODO: 3. Cek interaksi tepi peta untuk visiting
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
                // Logika tidur, misalnya set waktu ke pagi
                //timeManager.setTimeToMorning();
                gameStateModel.setCurrentState(GameState.PLAY_STATE);
                System.out.println("Player slept and time is now morning.");
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
}
