package com.oop10x.steddyvalley.model; // Sesuaikan paket

import com.oop10x.steddyvalley.model.map.Land;
import com.oop10x.steddyvalley.model.objects.DeployedObject;
import com.oop10x.steddyvalley.model.objects.HouseObject;
import com.oop10x.steddyvalley.model.objects.PondObject;
import com.oop10x.steddyvalley.model.objects.ShippingBinObject;
import com.oop10x.steddyvalley.model.map.Actionable;
import com.oop10x.steddyvalley.model.map.LandType;
import java.awt.Font;
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

    private HouseObject house;
    private ShippingBin playerShippingBin;
    private ShippingBinObject shippingBinMapObject;

    public FarmMap(TimeManager timeManager, Player player) {
        this.landGrid = new Land[MAP_HEIGHT_IN_TILES][MAP_WIDTH_IN_TILES];
        this.deployedObjects = new ArrayList<>();

        for (int r = 0; r < MAP_HEIGHT_IN_TILES; r++) {
            for (int c = 0; c < MAP_WIDTH_IN_TILES; c++) {
                this.landGrid[r][c] = new Land(c, r, timeManager);
            }
        }
        placeDeployedObjects(player);
    }


    private void placeDeployedObjects(Player player) {

        int maxHouseX = MAP_WIDTH_IN_TILES - HouseObject.HOUSE_WIDTH;
        int maxHouseY = MAP_HEIGHT_IN_TILES - HouseObject.HOUSE_HEIGHT;

        int houseX = (maxHouseX > 0) ? random.nextInt(maxHouseX + 1) : 0;
        int houseY = (maxHouseY > 0) ? random.nextInt(maxHouseY + 1) : 0;
        this.house = new HouseObject(houseX, houseY);
        addDeployedObject(this.house);
        System.out.println("House placed at tile: (" + houseX + "," + houseY + ")");

        PondObject pond;
        Rectangle houseBounds = this.house.getTileBounds();
        Rectangle pondBounds;
        int maxPondX = MAP_WIDTH_IN_TILES - PondObject.POND_WIDTH;
        int maxPondY = MAP_HEIGHT_IN_TILES - PondObject.POND_HEIGHT;
        int attempts = 0;

        do {
            int pondX = (maxPondX > 0) ? random.nextInt(maxPondX + 1) : 0;
            int pondY = (maxPondY > 0) ? random.nextInt(maxPondY + 1) : 0;
            pond = new PondObject(pondX, pondY);
            pondBounds = pond.getTileBounds();
            attempts++;
            if (attempts > 100) {
                System.err.println("Could not place pond without overlapping after 100 attempts. Placing at default or skipping.");
                // Penempatan pond di posisi default atau tidak menempatkannya sama sekali
                // pond = new PondObject(0, 0);
                // pondBounds = pond.getTileBounds();
                break;
            }
        } while (houseBounds.intersects(pondBounds));
        addDeployedObject(pond);
        System.out.println("Pond placed at tile: (" + pond.getX() + "," + pond.getY() + ")");

        int binPlacementX = -1;
        int binPlacementY = -1;

        int[][] offsets = {
            {HouseObject.HOUSE_WIDTH + 1, 0},
            {-ShippingBinObject.BIN_WIDTH_IN_TILES - 1, 0},
            {0, HouseObject.HOUSE_HEIGHT + 1},
            {0, -ShippingBinObject.BIN_HEIGHT_IN_TILES - 1}
        };

        String[] offsetNames = {"Right of House", "Left of House", "Below House", "Above House"};

        for (int i = 0; i < offsets.length; i++) {
            int currentBinX = house.getX() + offsets[i][0];
            int currentBinY = house.getY() + offsets[i][1];
            
            if (currentBinX >= 0 && currentBinX + ShippingBinObject.BIN_WIDTH_IN_TILES <= MAP_WIDTH_IN_TILES &&
                currentBinY >= 0 && currentBinY + ShippingBinObject.BIN_HEIGHT_IN_TILES <= MAP_HEIGHT_IN_TILES) {

                Rectangle tempBinBounds = new Rectangle(currentBinX, currentBinY, ShippingBinObject.BIN_WIDTH_IN_TILES, ShippingBinObject.BIN_HEIGHT_IN_TILES);

                binPlacementX = currentBinX;
                binPlacementY = currentBinY;
                System.out.println("Shipping Bin will be placed " + offsetNames[i] + " at tile: (" + binPlacementX + "," + binPlacementY + ")");
                break;
            }
        }

        if (binPlacementX != -1 && binPlacementY != -1) {
            this.playerShippingBin = new ShippingBin(binPlacementX, binPlacementY, player);
            this.shippingBinMapObject = new ShippingBinObject(binPlacementX, binPlacementY, this.playerShippingBin);
            addDeployedObject(this.shippingBinMapObject);
            System.out.println("Shipping Bin object created and added to map.");
        } else {
            int fallbackBinX = 0; 
            int fallbackBinY = MAP_HEIGHT_IN_TILES - ShippingBinObject.BIN_HEIGHT_IN_TILES;
            Rectangle fallbackBinBoundsCheck = new Rectangle(fallbackBinX, fallbackBinY, ShippingBinObject.BIN_WIDTH_IN_TILES, ShippingBinObject.BIN_HEIGHT_IN_TILES);
            boolean pondExistsAndIntersectsFallback = (pond != null && pondBounds != null && fallbackBinBoundsCheck.intersects(pondBounds));
            if (!fallbackBinBoundsCheck.intersects(houseBounds) && !pondExistsAndIntersectsFallback) {
                this.playerShippingBin = new ShippingBin(fallbackBinX, fallbackBinY, player);
                this.shippingBinMapObject = new ShippingBinObject(fallbackBinX, fallbackBinY, this.playerShippingBin);
                addDeployedObject(this.shippingBinMapObject);
                System.out.println("Shipping Bin (fallback) placed at tile: (" + fallbackBinX + "," + fallbackBinY + ")");
            } else {
                this.playerShippingBin = new ShippingBin(0,0, player); 
                this.shippingBinMapObject = new ShippingBinObject(0,0, this.playerShippingBin);
                addDeployedObject(this.shippingBinMapObject);
                System.err.println("CRITICAL: Could not place Shipping Bin ideally or fallback without overlap! Placed at (0,0) as emergency.");
            }
        }
    }

    public HouseObject getHouseObject() {
        return this.house;
    }

    private void addDeployedObject(DeployedObject object) {
        deployedObjects.add(object);
        Rectangle bounds = object.getTileBounds();
        for (int r = bounds.y; r < bounds.y + bounds.height; r++) {
            for (int c = bounds.x; c < bounds.x + bounds.width; c++) {
                if (c >= 0 && c < MAP_WIDTH_IN_TILES && r >= 0 && r < MAP_HEIGHT_IN_TILES) {
                    // landGrid[r][c] = null; // Opsi 1: Hapus Land
                    // jika Land punya state:
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
                    return null;
                }
            }
            return landGrid[tileY][tileX];
        }
        return null;
    }

    public DeployedObject getAdjacentInteractableDeployedObject(int playerTileX, int playerTileY) {
        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};

        for (int i = 0; i < 4; i++) {
            int checkTileX = playerTileX + dx[i];
            int checkTileY = playerTileY + dy[i];

            if (checkTileX >= 0 && checkTileX < MAP_WIDTH_IN_TILES &&
                checkTileY >= 0 && checkTileY < MAP_HEIGHT_IN_TILES) {

                for (DeployedObject obj : deployedObjects) {

                    if (obj instanceof Actionable && obj.containsTile(checkTileX, checkTileY)) {
                        // System.out.println("DEBUG FarmMap: Found interactable " + obj.getObjectName() + " at adjacent tile (" + checkTileX + "," + checkTileY + ")");
                        return obj;
                    }
                }
            }
        }
        return null;
    }


    public void draw(Graphics2D g2, int tileSize) {
        // Gambar Land
        for (int r = 0; r < MAP_HEIGHT_IN_TILES; r++) {
            for (int c = 0; c < MAP_WIDTH_IN_TILES; c++) {
                Land land = landGrid[r][c];
                if (land != null) {
                    Color landColor = Color.decode("#A4C639");
                    if (land.getLandType() == LandType.TILLED) landColor = Color.decode("#9B7653");
                    if (land.getLandType() == LandType.PLANTED || land.getLandType() == LandType.HARVESTABLE) landColor = Color.decode("#228B22");
                    if (land.getIsWatered() && land.getLandType() != LandType.UNTILLED) landColor = landColor.darker();

                    g2.setColor(landColor);
                    g2.fillRect(land.getX() * tileSize, land.getY() * tileSize, tileSize, tileSize);
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawRect(land.getX() * tileSize, land.getY() * tileSize, tileSize, tileSize);

                    if (land.getLandType() == LandType.PLANTED && land.getSeed() != null) {
                        g2.setColor(Color.YELLOW);
                        g2.fillOval(land.getX() * tileSize + tileSize / 3, land.getY() * tileSize + tileSize / 3, tileSize / 3, tileSize / 3);
                    } else if (land.getLandType() == LandType.HARVESTABLE && land.getSeed() != null) {
                        g2.setColor(Color.BLUE);
                        g2.fillOval(land.getX() * tileSize + tileSize / 3, land.getY() * tileSize + tileSize / 3, tileSize / 3, tileSize / 3);
                    }
                }
            }
        }
    
        for (DeployedObject obj : deployedObjects) {

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

    public ShippingBin getPlayerShippingBin() {
        return playerShippingBin;
    }
}