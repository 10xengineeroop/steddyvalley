package com.oop10x.steddyvalley.model; // Sesuaikan paket

import com.oop10x.steddyvalley.model.map.Land;
import com.oop10x.steddyvalley.model.objects.DeployedObject;
import com.oop10x.steddyvalley.model.objects.HouseObject;
import com.oop10x.steddyvalley.model.objects.PondObject;
import com.oop10x.steddyvalley.model.objects.ShippingBinObject;
import com.oop10x.steddyvalley.model.map.Actionable;
import com.oop10x.steddyvalley.model.map.LandType;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FarmMap {
    private final Land[][] landGrid;
    private final List<DeployedObject> deployedObjects;
    public static final int MAP_WIDTH_IN_TILES = 32;
    public static final int MAP_HEIGHT_IN_TILES = 32;
    private Random random = new Random();

    private HouseObject house; // Simpan referensi ke rumah untuk posisi bin

    public FarmMap(TimeManager timeManager) { // Terima TimeManager
        this.landGrid = new Land[MAP_HEIGHT_IN_TILES][MAP_WIDTH_IN_TILES];
        this.deployedObjects = new ArrayList<>();

        for (int r = 0; r < MAP_HEIGHT_IN_TILES; r++) {
            for (int c = 0; c < MAP_WIDTH_IN_TILES; c++) {
                this.landGrid[r][c] = new Land(c, r, timeManager);
            }
        }
        placeDeployedObjects();
    }

    // Di dalam FarmMap.java

    private void placeDeployedObjects() {
        // 1. Tempatkan Rumah (misalnya, HouseObject.HOUSE_WIDTH, HouseObject.HOUSE_HEIGHT)
        // Pastikan objek tidak keluar dari batas KANAN dan BAWAH peta
        int maxHouseX = MAP_WIDTH_IN_TILES - HouseObject.HOUSE_WIDTH; // Posisi X maksimal agar rumah tidak keluar kanan
        int maxHouseY = MAP_HEIGHT_IN_TILES - HouseObject.HOUSE_HEIGHT; // Posisi Y maksimal agar rumah tidak keluar bawah

        // Pastikan argumen untuk nextInt adalah positif
        int houseX = (maxHouseX > 0) ? random.nextInt(maxHouseX + 1) : 0; // +1 karena nextInt(n) menghasilkan 0 hingga n-1
        int houseY = (maxHouseY > 0) ? random.nextInt(maxHouseY + 1) : 0;
        this.house = new HouseObject(houseX, houseY);
        addDeployedObject(this.house);
        System.out.println("House placed at tile: (" + houseX + "," + houseY + ")");

        // 2. Tempatkan Kolam (misalnya, PondObject.POND_WIDTH, PondObject.POND_HEIGHT)
        PondObject pond;
        Rectangle houseBounds = this.house.getTileBounds();
        Rectangle pondBounds;
        int maxPondX = MAP_WIDTH_IN_TILES - PondObject.POND_WIDTH;
        int maxPondY = MAP_HEIGHT_IN_TILES - PondObject.POND_HEIGHT;
        int attempts = 0; // Untuk mencegah loop tak terbatas jika tidak ada ruang

        do {
            int pondX = (maxPondX > 0) ? random.nextInt(maxPondX + 1) : 0;
            int pondY = (maxPondY > 0) ? random.nextInt(maxPondY + 1) : 0;
            pond = new PondObject(pondX, pondY);
            pondBounds = pond.getTileBounds();
            attempts++;
            if (attempts > 100) { // Batas percobaan untuk menghindari loop tak terbatas
                System.err.println("Could not place pond without overlapping after 100 attempts. Placing at default or skipping.");
                // Anda bisa menempatkan pond di posisi default atau tidak menempatkannya sama sekali
                // pond = new PondObject(0, 0); // Contoh fallback
                // pondBounds = pond.getTileBounds();
                break;
            }
        } while (houseBounds.intersects(pondBounds));
        addDeployedObject(pond);
        System.out.println("Pond placed at tile: (" + pond.getX() + "," + pond.getY() + ")");


        // 3. Tempatkan Shipping Bin (misalnya, ShippingBinObject.BIN_WIDTH, ShippingBinObject.BIN_HEIGHT)
        // Logika penempatan bin 1 petak dari rumah lebih kompleks dan perlu validasi batas yang cermat.
        // Kita coba beberapa posisi di sekitar rumah.
        ShippingBinObject shippingBin = null;
        int[][] offsets = {
            {HouseObject.HOUSE_WIDTH + 1, 0},                             // Kanan rumah (jarak 1 petak)
            {-ShippingBinObject.BIN_WIDTH_IN_TILES - 1, 0},               // Kiri rumah (jarak 1 petak) - SUDAH BENAR
            {0, HouseObject.HOUSE_HEIGHT + 1},                             // Bawah rumah (jarak 1 petak)
            {0, -ShippingBinObject.BIN_HEIGHT_IN_TILES - 1}                // Atas rumah (jarak 1 petak) - SUDAH BENAR
        };
        String[] offsetNames = {"Right of House", "Left of House", "Below House", "Above House"};

        for (int i = 0; i < offsets.length; i++) {
            int binX = house.getX() + offsets[i][0];
            // Untuk posisi di kiri atau atas, offset X atau Y dari rumah mungkin perlu disesuaikan
            // agar bin tidak tumpang tindih dengan rumah itu sendiri.
            // Contoh: jika di kiri rumah, binX = house.getX() - ShippingBinObject.BIN_WIDTH - 1;
            // Untuk sementara, kita sederhanakan:
            if (offsets[i][0] < 0) binX = house.getX() + offsets[i][0]; // Jika offset X negatif, itu sudah posisi kiri bin
            int binY = house.getY() + offsets[i][1];
            if (offsets[i][1] < 0) binY = house.getY() + offsets[i][1];

            // Validasi batas peta
            if (binX >= 0 && binX + ShippingBinObject.BIN_WIDTH_IN_TILES <= MAP_WIDTH_IN_TILES &&
                binY >= 0 && binY + ShippingBinObject.BIN_HEIGHT_IN_TILES <= MAP_HEIGHT_IN_TILES) {

                ShippingBinObject tempBin = new ShippingBinObject(binX, binY);
                Rectangle tempBinBounds = tempBin.getTileBounds();

                // Cek tumpang tindih dengan rumah (seharusnya tidak jika offset benar) dan kolam
                if (!tempBinBounds.intersects(houseBounds) && !tempBinBounds.intersects(pondBounds)) {
                    shippingBin = tempBin;
                    System.out.println("Shipping Bin placed " + offsetNames[i] + " at tile: (" + binX + "," + binY + ")");
                    break; // Ditemukan posisi yang valid
                }
            }
        }

        if (shippingBin != null) {
            addDeployedObject(shippingBin);
        } else {
            // Fallback jika tidak ada ruang di sekitar rumah
            // Coba tempatkan di posisi default yang aman jika memungkinkan
            int fallbackBinX = 0;
            int fallbackBinY = MAP_HEIGHT_IN_TILES - ShippingBinObject.BIN_HEIGHT_IN_TILES; // Kiri bawah
            ShippingBinObject fallbackBin = new ShippingBinObject(fallbackBinX, fallbackBinY);
            Rectangle fallbackBinBounds = fallbackBin.getTileBounds();
            if (!fallbackBinBounds.intersects(houseBounds) && !fallbackBinBounds.intersects(pondBounds)) {
                addDeployedObject(fallbackBin);
                System.out.println("Shipping Bin (fallback) placed at tile: (" + fallbackBinX + "," + fallbackBinY + ")");
            } else {
                System.err.println("CRITICAL: Could not place Shipping Bin anywhere without overlap!");
            }
        }
    }

    public HouseObject getHouseObject() {
        return this.house;
    }


    private void addDeployedObject(DeployedObject object) {
        deployedObjects.add(object);
        // Tandai tile di landGrid yang ditempati oleh objek ini sebagai tidak bisa ditanami
        // atau set Land menjadi null, atau buat Land memiliki properti isOccupied.
        Rectangle bounds = object.getTileBounds();
        for (int r = bounds.y; r < bounds.y + bounds.height; r++) {
            for (int c = bounds.x; c < bounds.x + bounds.width; c++) {
                if (c >= 0 && c < MAP_WIDTH_IN_TILES && r >= 0 && r < MAP_HEIGHT_IN_TILES) {
                    // landGrid[r][c] = null; // Opsi 1: Hapus Land
                    // Atau jika Land punya state:
                    if (landGrid[r][c] != null) {
                        // landGrid[r][c].setOccupied(true, object.getObjectName());
                        // Untuk sekarang, kita bisa biarkan Land apa adanya,
                        // CollisionChecker akan menangani solid-nya DeployedObject.
                        // GamePanel akan menggambar DeployedObject di atas Land.
                    }
                }
            }
        }
    }


    public Land getLandAt(int tileX, int tileY) {
        if (tileX >= 0 && tileX < MAP_WIDTH_IN_TILES && tileY >= 0 && tileY < MAP_HEIGHT_IN_TILES) {
            // Cek apakah ada DeployedObject solid di sini dulu
            for (DeployedObject obj : deployedObjects) {
                if (obj.isSolid() && obj.containsTile(tileX, tileY)) {
                    return null; // Anggap tidak ada Land yang bisa diinteraksikan jika ada objek solid di atasnya
                }
            }
            return landGrid[tileY][tileX];
        }
        return null;
    }

    /**
     * Mendapatkan DeployedObject yang bisa diinteraksikan di sebelah pemain.
     * Ini adalah contoh sederhana, perlu disempurnakan.
     */
    public DeployedObject getAdjacentInteractableDeployedObject(int playerTileX, int playerTileY) {
        int[] dx = {0, 0, -1, 1}; // Perubahan X untuk Atas, Bawah, Kiri, Kanan
        int[] dy = {-1, 1, 0, 0}; // Perubahan Y untuk Atas, Bawah, Kiri, Kanan

        for (int i = 0; i < 4; i++) {
            int checkTileX = playerTileX + dx[i];
            int checkTileY = playerTileY + dy[i];

            // Pastikan koordinat yang dicek valid di dalam peta
            if (checkTileX >= 0 && checkTileX < MAP_WIDTH_IN_TILES &&
                checkTileY >= 0 && checkTileY < MAP_HEIGHT_IN_TILES) {

                for (DeployedObject obj : deployedObjects) {
                    // Cek apakah tile (checkTileX, checkTileY) adalah bagian dari DeployedObject ini
                    // dan apakah objek tersebut Actionable.
                    if (obj instanceof Actionable && obj.containsTile(checkTileX, checkTileY)) {
                        // System.out.println("DEBUG FarmMap: Found interactable " + obj.getObjectName() + " at adjacent tile (" + checkTileX + "," + checkTileY + ")");
                        return obj; // Kembalikan objek pertama yang ditemukan
                    }
                }
            }
        }
        return null; // Tidak ada objek yang bisa diinteraksikan di sekitar
    }


    public void draw(Graphics2D g2, int tileSize) {
        // Gambar Land
        for (int r = 0; r < MAP_HEIGHT_IN_TILES; r++) {
            for (int c = 0; c < MAP_WIDTH_IN_TILES; c++) {
                Land land = landGrid[r][c];
                if (land != null) {
                    Color landColor = Color.decode("#A4C639"); // Android Green (UNTILLED)
                    if (land.getLandType() == LandType.TILLED) landColor = Color.decode("#9B7653"); // Tilled
                    if (land.getLandType() == LandType.PLANTED) landColor = Color.decode("#228B22"); // Forest Green
                    if (land.getIsWatered() && land.getLandType() != LandType.UNTILLED) landColor = landColor.darker();

                    g2.setColor(landColor);
                    g2.fillRect(land.getX() * tileSize, land.getY() * tileSize, tileSize, tileSize);
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawRect(land.getX() * tileSize, land.getY() * tileSize, tileSize, tileSize);

                    // Jika ada tanaman, gambar sesuatu (misalnya, lingkaran kecil)
                    if (land.getLandType() == LandType.PLANTED && land.getSeed() != null) {
                        g2.setColor(Color.YELLOW); // Warna benih/tanaman muda
                        g2.fillOval(land.getX() * tileSize + tileSize / 3, land.getY() * tileSize + tileSize / 3, tileSize / 3, tileSize / 3);
                    }
                }
            }
        }
        // Gambar DeployedObject (di atas Land)
        for (DeployedObject obj : deployedObjects) {
            // Anda perlu sprite/gambar untuk setiap objek
            // Untuk sekarang, gambar sebagai kotak berwarna
            Color objColor = Color.GRAY; // Default
            if (obj.getObjectName().equals("House")) objColor = Color.RED;
            else if (obj.getObjectName().equals("Pond")) objColor = Color.CYAN;
            else if (obj.getObjectName().equals("ShippingBin")) objColor = Color.MAGENTA;

            g2.setColor(objColor);
            g2.fillRect(obj.getX() * tileSize, obj.getY() * tileSize,
                        obj.getWidthInTiles() * tileSize, obj.getHeightInTiles() * tileSize);
            g2.setColor(Color.BLACK);
            g2.drawRect(obj.getX() * tileSize, obj.getY() * tileSize,
                        obj.getWidthInTiles() * tileSize, obj.getHeightInTiles() * tileSize);
            g2.drawString(obj.getObjectName().substring(0,1), obj.getX()*tileSize + 5, obj.getY()*tileSize + 15);
        }
    }

    public List<DeployedObject> getDeployedObjects() {
        return deployedObjects;
    }
}
