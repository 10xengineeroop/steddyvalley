package com.oop10x.steddyvalley.model.tile; // Sesuaikan dengan paket Anda

// Tidak perlu import GamePanel lagi
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TileManager {
    private int tileSize;    // Simpan tileSize yang diterima dari luar
    public Tile[] tiles;
    public int[][] mapTileNum;

    // Ukuran dunia dalam tile, bisa di-pass atau final di sini
    public final int MAX_WORLD_COL;
    public final int MAX_WORLD_ROW;

    private final String[] tileImagePaths = {
        "/com/oop10x/steddyvalley/tiles/grass.png",    // Index 0
        "/com/oop10x/steddyvalley/tiles/water.png",
        "/com/oop10x/steddyvalley/tiles/wall.png"      // Index 1
    };
    private final String mapLayoutPath = "/com/oop10x/steddyvalley/maps/map1.txt";

    // Constructor sekarang menerima tileSize, maxWorldCol, dan maxWorldRow
    public TileManager(int tileSize, int maxWorldCol, int maxWorldRow) {
        this.tileSize = tileSize; // Simpan tileSize
        this.MAX_WORLD_COL = maxWorldCol;
        this.MAX_WORLD_ROW = maxWorldRow;

        tiles = new Tile[tileImagePaths.length];
        mapTileNum = new int[MAX_WORLD_ROW][MAX_WORLD_COL];

        loadTileImages();
        loadMap(mapLayoutPath);
    }

    private void loadTileImages() {
        try {
            InputStream stream;
            // Tile 0: Rumput (tidak solid)
            if (tileImagePaths.length > 0) {
                stream = getClass().getResourceAsStream(tileImagePaths[0]);
                if (stream == null) throw new IOException("Cannot find resource: " + tileImagePaths[0]);
                tiles[0] = new Tile(ImageIO.read(stream), false);
                stream.close();
            }
            // Tile 1: Dinding (solid)
            if (tileImagePaths.length > 1) {
                stream = getClass().getResourceAsStream(tileImagePaths[1]);
                if (stream == null) throw new IOException("Cannot find resource: " + tileImagePaths[1]);
                tiles[1] = new Tile(ImageIO.read(stream), false);
                stream.close();
            }
            // Tile 2: Air (solid)
            if (tileImagePaths.length > 2) {
                stream = getClass().getResourceAsStream(tileImagePaths[2]);
                if (stream == null) throw new IOException("Cannot find resource: " + tileImagePaths[2]);
                tiles[2] = new Tile(ImageIO.read(stream), true);
                stream.close();
            }
        } catch (IOException e) {
            System.err.println("Error loading tile images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadMap(String filePath) {
        try (InputStream is = getClass().getResourceAsStream(filePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            if (is == null) throw new IOException("Cannot find map file: " + filePath);

            for (int row = 0; row < MAX_WORLD_ROW; row++) {
                String line = br.readLine();
                if (line == null) { /* ... penanganan error ... */ break; }
                String[] numbers = line.split(" ");
                for (int col = 0; col < MAX_WORLD_COL; col++) {
                    if (col < numbers.length) {
                        try {
                            mapTileNum[row][col] = Integer.parseInt(numbers[col]);
                        } catch (NumberFormatException nfe) { /* ... penanganan error ... */ mapTileNum[row][col] = 0; }
                    } else { mapTileNum[row][col] = 0; }
                }
            }
        } catch (IOException | NullPointerException e) { /* ... penanganan error ... */ }
    }

    /**
     * Menggambar seluruh peta tile demi tile.
     * Menggunakan tileSize internal yang sudah disimpan.
     * @param g2 Graphics2D context untuk menggambar.
     */
    public void draw(Graphics2D g2) {
        for (int worldRow = 0; worldRow < MAX_WORLD_ROW; worldRow++) {
            for (int worldCol = 0; worldCol < MAX_WORLD_COL; worldCol++) {
                int tileNum = mapTileNum[worldRow][worldCol];
                if (tileNum >= 0 && tileNum < tiles.length && tiles[tileNum] != null && tiles[tileNum].image != null) {
                    int screenX = worldCol * this.tileSize; // Menggunakan this.tileSize
                    int screenY = worldRow * this.tileSize; // Menggunakan this.tileSize
                    g2.drawImage(tiles[tileNum].image, screenX, screenY, this.tileSize, this.tileSize, null);
                }
            }
        }
    }

    public Tile getTile(int worldCol, int worldRow) {
    System.out.println("DEBUG TM getTile: Requesting tile at (" + worldCol + "," + worldRow + ")");
    if (worldCol >= 0 && worldCol < MAX_WORLD_COL && worldRow >= 0 && worldRow < MAX_WORLD_ROW) {
        int tileNum = mapTileNum[worldRow][worldCol];
        System.out.println("  TileNum: " + tileNum);
        if (tileNum >= 0 && tileNum < tiles.length && tiles[tileNum] != null) {
            System.out.println("  Returning tile with collision: " + tiles[tileNum].collision);
            return tiles[tileNum];
        }
    }
        System.out.println("  Returning null (out of bounds or invalid tileNum)");
        return null;
    }
}
