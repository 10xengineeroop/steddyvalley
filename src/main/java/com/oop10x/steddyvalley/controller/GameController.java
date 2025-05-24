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
import com.oop10x.steddyvalley.model.objects.DeployedObject;

public class GameController implements PlayerInputActions {

    private Player playerModel;
    private GameState gameStateModel;
    private FarmMap farmMapModel;
    private CollisionChecker collisionChecker;
    private TimeManager timeManager; // Untuk mendapatkan waktu saat ini
    private int tileSize;

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

    @Override public void setMoveUp(boolean active) { this.moveUpActive = active; }
    @Override public void setMoveDown(boolean active) { this.moveDownActive = active; }
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
            ((Actionable) adjacentObject).onPlayerAction(playerModel, gameStateModel);
            // Di sini Anda bisa menambahkan logika pengurangan energi atau pemajuan waktu jika interaksi berhasil
            // playerModel.setEnergy(playerModel.getEnergy() - COST_INTERACT_HOUSE);
            // timeManager.advanceTime(MINUTES_INTERACT_HOUSE);
            return; // Interaksi dengan DeployedObject selesai, tidak perlu cek Land di bawah.
        } else {
            // System.out.println("DEBUG GC: No adjacent interactable DeployedObject found.");
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
}
