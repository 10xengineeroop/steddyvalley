package com.oop10x.steddyvalley.model.collision;

import com.oop10x.steddyvalley.model.FarmMap;
import com.oop10x.steddyvalley.model.objects.DeployedObject;
import com.oop10x.steddyvalley.model.tile.Tile;
import com.oop10x.steddyvalley.model.tile.TileManager;

public class CollisionChecker {
    private TileManager tileManager;
    private FarmMap farmMap;
    private int tileSize;

    public CollisionChecker(TileManager tileManager, FarmMap farmMap, int tileSize) {
        this.tileManager = tileManager;
        this.farmMap = farmMap;
        this.tileSize = tileSize;
    }

    public boolean willCollide(int currentPixelX, int currentPixelY, int dx, int dy) {
        int futureLeftPixelX = currentPixelX + dx;
        int futureTopPixelY = currentPixelY + dy;
        int futureRightPixelX = currentPixelX + dx + tileSize - 1;
        int futureBottomPixelY = currentPixelY + dy + tileSize - 1;

        int mapPixelWidth = 32 * tileSize;
        int mapPixelHeight = 32 * tileSize;

        if (futureLeftPixelX < 0 || futureRightPixelX >= mapPixelWidth ||
            futureTopPixelY < 0 || futureBottomPixelY >= mapPixelHeight) {
            return true;
        }

        int topLeftTileCol = futureLeftPixelX / tileSize;
        int topLeftTileRow = futureTopPixelY / tileSize;
        int bottomRightTileCol = futureRightPixelX / tileSize;
        int bottomRightTileRow = futureBottomPixelY / tileSize;

        if (dx != 0 && dy == 0) {
            System.out.println("DEBUG CC H-Move: current(" + currentPixelX + "," + currentPixelY + ") dx=" + dx);
            System.out.println("  future L=" + futureLeftPixelX + " T=" + futureTopPixelY + " R=" + futureRightPixelX + " B=" + futureBottomPixelY);
            System.out.println("  future Tiles: LCol=" + topLeftTileCol + " TRow=" + topLeftTileRow + " RCol=" + bottomRightTileCol + " BRow=" + bottomRightTileRow);
            int checkCol = (dx > 0) ? bottomRightTileCol : topLeftTileCol;
            Tile tile1 = tileManager.getTile(checkCol, topLeftTileRow);
            Tile tile2 = tileManager.getTile(checkCol, bottomRightTileRow);
            if ((tile1 != null && tile1.collision) || (tile2 != null && tile2.collision)) {
                System.out.println("Collision with basic tile (H)");
                return true;
            }
        } else if (dy != 0 && dx == 0) {
            int checkRow = (dy > 0) ? bottomRightTileRow : topLeftTileRow;
            Tile tile1 = tileManager.getTile(topLeftTileCol, checkRow);
            Tile tile2 = tileManager.getTile(bottomRightTileCol, checkRow);
            if ((tile1 != null && tile1.collision) || (tile2 != null && tile2.collision)) {
                System.out.println("Collision with basic tile (V)");
                return true;
            }
        } else if (dx != 0 && dy != 0) {
            int checkColX = (dx > 0) ? bottomRightTileCol : topLeftTileCol;
            Tile tileX1 = tileManager.getTile(checkColX, topLeftTileRow);
            Tile tileX2 = tileManager.getTile(checkColX, bottomRightTileRow);
            if ((tileX1 != null && tileX1.collision) || (tileX2 != null && tileX2.collision)) return true;

            int checkRowY = (dy > 0) ? bottomRightTileRow : topLeftTileRow;
            Tile tileY1 = tileManager.getTile(topLeftTileCol, checkRowY);
            Tile tileY2 = tileManager.getTile(bottomRightTileCol, checkRowY);
            if ((tileY1 != null && tileY1.collision) || (tileY2 != null && tileY2.collision)) return true;
        }

        java.awt.Rectangle playerTargetBounds = new java.awt.Rectangle(futureLeftPixelX, futureTopPixelY, tileSize, tileSize);

        if (this.farmMap != null && farmMap.getDeployedObjects() != null) {
            for (DeployedObject obj : farmMap.getDeployedObjects()) {
                if (obj.isSolid()) {
                    java.awt.Rectangle objectBounds = new java.awt.Rectangle(
                        obj.getX() * tileSize,
                        obj.getY() * tileSize,
                        obj.getWidthInTiles() * tileSize,
                        obj.getHeightInTiles() * tileSize
                    );
                    if (playerTargetBounds.intersects(objectBounds)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
