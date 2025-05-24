package com.oop10x.steddyvalley.model.objects;

import com.oop10x.steddyvalley.model.map.Placeable;
import com.oop10x.steddyvalley.utils.Position; // Asumsi Position ada di utils
import java.awt.Rectangle; // Untuk area solid

public abstract class DeployedObject implements Placeable {
    protected Position position; // Posisi tile kiri atas
    protected int widthInTiles;  // Lebar dalam satuan tile
    protected int heightInTiles; // Tinggi dalam satuan tile
    protected String objectName;
    protected boolean isSolid;

    public DeployedObject(int tileX, int tileY, int widthInTiles, int heightInTiles, String name, boolean isSolid) {
        this.position = new Position(tileX, tileY);
        this.widthInTiles = widthInTiles;
        this.heightInTiles = heightInTiles;
        this.objectName = name;
        this.isSolid = isSolid;
    }

    @Override public void setX(int x) { position.setX(x); }
    @Override public int getX() { return position.getX(); }
    @Override public void setY(int y) { position.setY(y); }
    @Override public int getY() { return position.getY(); }

    public int getWidthInTiles() { return widthInTiles; }
    public int getHeightInTiles() { return heightInTiles; }
    public String getObjectName() { return objectName; }
    public boolean isSolid() { return isSolid; }

    /**
     * Mendapatkan area yang ditempati objek ini dalam koordinat tile.
     */
    public Rectangle getTileBounds() {
        return new Rectangle(getX(), getY(), widthInTiles, heightInTiles);
    }

    /**
     * Memeriksa apakah tile (tx, ty) berada di dalam bounds objek ini.
     */
    public boolean containsTile(int tx, int ty) {
        return tx >= getX() && tx < getX() + widthInTiles &&
               ty >= getY() && ty < getY() + heightInTiles;
    }
}
