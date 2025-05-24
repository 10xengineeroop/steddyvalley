package com.oop10x.steddyvalley.model.tile;

import java.awt.image.BufferedImage;

public class Tile {
    public BufferedImage image; // Gambar tile
    public boolean collision = false; // Apakah tile ini solid (menyebabkan tabrakan)

    public Tile(BufferedImage image, boolean collision) {
        this.image = image;
        this.collision = collision;
    }

    public Tile(BufferedImage image) {
        this(image, false); // Default tidak solid
    }
}